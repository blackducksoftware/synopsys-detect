package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackageId;

public class PnpmLockYamlParser {
    private static final String DEPENDENCIES = "dependencies";
    private static final String DEV_DEPENDENCIES = "devDependencies";
    //TODO- optioinalDependencies
    private static final String PACKAGES = "packages";
    public static final String STRING_ID_NAME_VERSION_SEPARATOR = "@";

    public CodeLocation parse(File pnpmLockYamlFile, boolean includeDevDependencies) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> yarnLockYaml = yaml.load(new FileReader(pnpmLockYamlFile));

        // Use objects, no casting!
        // store dependencies, dev dependencies, optiioina dependencies as "root ids"
        // iterate through packages, if matches a root id, add to root, then make relationships for all its children

        List<PnpmPackageId> dependencies = extractPackages((Map<String, Object>) yarnLockYaml.get(DEPENDENCIES));
        List<PnpmPackageId> devDependencies = extractPackages((Map<String, Object>) yarnLockYaml.get(DEV_DEPENDENCIES));
        Map<String, Object> packages = (Map<String, Object>) yarnLockYaml.get(PACKAGES);

        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder(); //TODO- do we need lazy graph builder?

        addDependenciesToGraph(graphBuilder, dependencies, packages);
        if (includeDevDependencies) {
            addDependenciesToGraph(graphBuilder, devDependencies, packages);
        }

        return null;
    }

    private void addDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, List<PnpmPackageId> dependencies, Map<String, Object> packages) {
        for (PnpmPackageId pnpmPackage : dependencies) {
            addToGraph(graphBuilder, packages, pnpmPackage, null);
        }
    }

    private void addToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, Object> packages, PnpmPackageId pnpmPackage, PnpmPackageId parent) {
        if (parent == null) {
            graphBuilder.addChildToRoot(generatePackageDependencyId(pnpmPackage.getName(), pnpmPackage.getVersion()));
        } else {

        }
    }

    private List<PnpmPackageId> extractPackages(Map<String, Object> deps) {
        return deps.entrySet().stream()
                   .map(this::convertRawEntryToPackage)
                   .collect(Collectors.toList());
    }

    private PnpmPackageId convertRawEntryToPackage(Map.Entry<String, Object> entry) {
        //TODO- make sure to trim/normalize raw def (account for 's around name)
        return new PnpmPackageId(entry.getKey(), (String) entry.getValue());
    }

    private StringDependencyId generatePackageDependencyId(String name, String version) {
        return new StringDependencyId(name + STRING_ID_NAME_VERSION_SEPARATOR + version);
    }
}
