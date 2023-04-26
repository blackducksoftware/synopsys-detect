package com.synopsys.integration.detectable.detectables.pnpm.lockfile.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmProjectPackage;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {
    private static final Predicate<String> isNodeRoot = "."::equals;

    private final PnpmYamlTransformer pnpmTransformer;

    public PnpmLockYamlParser(PnpmYamlTransformer pnpmTransformer) {
        this.pnpmTransformer = pnpmTransformer;
    }

    public List<CodeLocation> parse(File pnpmLockYamlFile, @Nullable NameVersion projectNameVersion, PnpmLinkedPackageResolver linkedPackageResolver)
        throws IOException, IntegrationException {
        PnpmLockYaml pnpmLockYaml = parseYamlFile(pnpmLockYamlFile);
        List<CodeLocation> codeLocationsFromImports = createCodeLocationsFromImports(pnpmLockYamlFile.getParentFile(), pnpmLockYaml, linkedPackageResolver, projectNameVersion);
        if (codeLocationsFromImports.isEmpty()) {
            return createCodeLocationsFromRoot(pnpmLockYamlFile.getParentFile(), pnpmLockYaml, projectNameVersion, linkedPackageResolver);
        }
        return codeLocationsFromImports;
    }

    private List<CodeLocation> createCodeLocationsFromRoot(
        File sourcePath,
        PnpmLockYaml pnpmLockYaml,
        @Nullable NameVersion projectNameVersion,
        PnpmLinkedPackageResolver linkedPackageResolver
    ) throws IntegrationException {
        CodeLocation codeLocation = pnpmTransformer.generateCodeLocation(sourcePath, pnpmLockYaml, projectNameVersion, linkedPackageResolver);
        return Collections.singletonList(codeLocation);
    }

    private List<CodeLocation> createCodeLocationsFromImports(
        File sourcePath,
        PnpmLockYaml pnpmLockYaml,
        PnpmLinkedPackageResolver linkedPackageResolver,
        @Nullable NameVersion projectNameVersion
    ) throws IntegrationException {
        if (MapUtils.isEmpty(pnpmLockYaml.importers)) {
            return Collections.emptyList();
        }

        List<CodeLocation> codeLocations = new LinkedList<>();
        for (Map.Entry<String, PnpmProjectPackage> projectPackageInfo : pnpmLockYaml.importers.entrySet()) {
            String projectKey = projectPackageInfo.getKey();
            PnpmProjectPackage projectPackage = projectPackageInfo.getValue();
            NameVersion extractedNameVersion = extractProjectInfo(projectPackageInfo, linkedPackageResolver, projectNameVersion);

            String reportingProjectPackagePath = null;
            if (!isNodeRoot.evaluate(projectKey)) {
                reportingProjectPackagePath = projectKey;
            }
            File generatedSourcePath = generateCodeLocationSourcePath(sourcePath, reportingProjectPackagePath);

            codeLocations.add(pnpmTransformer.generateCodeLocation(
                generatedSourcePath,
                projectPackage,
                reportingProjectPackagePath,
                extractedNameVersion,
                pnpmLockYaml.packages,
                linkedPackageResolver
            ));
        }

        return codeLocations;
    }

    private NameVersion extractProjectInfo(
        Map.Entry<String, PnpmProjectPackage> projectPackageInfo,
        PnpmLinkedPackageResolver linkedPackageResolver,
        @Nullable NameVersion projectNameVersion
    ) {
        if (isNodeRoot.evaluate(projectPackageInfo.getKey()) && projectNameVersion != null && projectNameVersion.getName() != null) {
            // resolve "." package to project root
            return projectNameVersion;
        }

        String projectPackageName = projectPackageInfo.getKey();
        String projectPackageVersion = linkedPackageResolver.resolveVersionOfLinkedPackage(null, projectPackageName);
        return new NameVersion(projectPackageName, projectPackageVersion);
    }

    private PnpmLockYaml parseYamlFile(File pnpmLockYamlFile) throws FileNotFoundException {
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(PnpmLockYaml.class, loaderOptions), representer);
        return yaml.load(new FileReader(pnpmLockYamlFile));
    }

    private File generateCodeLocationSourcePath(File sourcePath, @Nullable String reportingProjectPackagePath) {
        if (StringUtils.isNotEmpty(reportingProjectPackagePath)) {
            return new File(sourcePath, reportingProjectPackagePath);
        }
        return sourcePath;
    }
}
