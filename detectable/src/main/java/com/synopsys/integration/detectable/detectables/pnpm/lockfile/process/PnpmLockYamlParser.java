package com.synopsys.integration.detectable.detectables.pnpm.lockfile.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYamlv5;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYamlv6;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {

    private final EnumListFilter<PnpmDependencyType> dependencyFilter;

    public PnpmLockYamlParser(EnumListFilter<PnpmDependencyType> dependencyFilter) {
        this.dependencyFilter = dependencyFilter;
    }

    public List<CodeLocation> parse(File pnpmLockYamlFile, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver)
        throws IOException, IntegrationException {
        PnpmLockYaml pnpmLockYaml = parseYamlFile(pnpmLockYamlFile);
        
        if (pnpmLockYaml instanceof PnpmLockYamlv6) {
            PnpmYamlTransformerv6 pnpmYamlTransformer = new PnpmYamlTransformerv6(dependencyFilter);
            PnpmLockYamlParserv6 pnpmYamlParser = new PnpmLockYamlParserv6(pnpmYamlTransformer);
            return pnpmYamlParser.parse(pnpmLockYamlFile.getParentFile(), (PnpmLockYamlv6) pnpmLockYaml, linkedPackageResolver, projectNameVersion);
        } else {
            PnpmYamlTransformerv5 pnpmYamlTransformer = new PnpmYamlTransformerv5(dependencyFilter);
            PnpmLockYamlParserv5 pnpmYamlParser = new PnpmLockYamlParserv5(pnpmYamlTransformer);
            return pnpmYamlParser.parse(pnpmLockYamlFile.getParentFile(), (PnpmLockYamlv5) pnpmLockYaml, linkedPackageResolver, projectNameVersion);
        }
    }

    /**
     * This method reads the pnpm-lock.yaml. It first tries to read it in v6 format
     * and then tries v5 if that fails. This is usually faster than first cracking
     * open the yaml file, checking what version it is, and then calling the
     * appropriate reader.
     * 
     * @param pnpmLockYamlFile the File path to the pnpm-lock.yaml file
     * @return a memory representation of the lock file.
     * @throws FileNotFoundException
     */
    private PnpmLockYaml parseYamlFile(File pnpmLockYamlFile) throws FileNotFoundException {
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        LoaderOptions loaderOptions = new LoaderOptions();
        try {
            // Try to read the lockfile into the v6 Yaml classes. It's more common and 
            // should hopefully work more of the time.
            Yaml yaml = new Yaml(new Constructor(PnpmLockYamlv6.class, loaderOptions), representer);
            return yaml.load(new FileReader(pnpmLockYamlFile));
        } catch (ConstructorException e) {
            // If the v6 reading fails try to read a v5 Yaml. 
            Yaml yaml = new Yaml(new Constructor(PnpmLockYamlv5.class, loaderOptions), representer);
            return yaml.load(new FileReader(pnpmLockYamlFile));
        }
    }
}
