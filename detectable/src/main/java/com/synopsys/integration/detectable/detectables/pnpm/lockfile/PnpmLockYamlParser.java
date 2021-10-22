package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmLockYaml;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmPackage;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockYamlParser {
    private final ExternalIdFactory externalIdFactory;

    public PnpmLockYamlParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(File pnpmLockYamlFile, boolean includeDevDependencies) throws IOException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(PnpmLockYaml.class), representer);
        PnpmLockYaml pnpmLockYaml = yaml.load(new FileReader(pnpmLockYamlFile));

        List<String> rootPackageIds = extractRootPackageIds(pnpmLockYaml, includeDevDependencies);
        Map<String, PnpmPackage> packageMap = pnpmLockYaml.packages;

        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        buildGraph(dependencyGraph, rootPackageIds, packageMap, includeDevDependencies);

        return dependencyGraph;
    }

    private void buildGraph(MutableDependencyGraph graphBuilder, List<String> rootPackageIds, Map<String, PnpmPackage> packageMap, boolean includeDevDependencies) {
        for (Map.Entry<String, PnpmPackage> packageEntry : packageMap.entrySet()) {
            String packageId = packageEntry.getKey();
            if (rootPackageIds.contains(packageId)) {
                graphBuilder.addChildToRoot(buildDependencyFromPackageId(packageId));
            }
            PnpmPackage pnpmPackage = packageEntry.getValue();
            if ((!pnpmPackage.isDev() || includeDevDependencies) && pnpmPackage.hasDependencies()) {
                for (Map.Entry<String, String> dependency : pnpmPackage.dependencies.entrySet()) {
                    String dependencyPackageId = convertRawEntryToPackageId(dependency);
                    Dependency child = buildDependencyFromPackageId(dependencyPackageId);
                    graphBuilder.addChildWithParent(child, buildDependencyFromPackageId(packageId));
                }
            }
        }
    }

    private List<String> extractRootPackageIds(PnpmLockYaml pnpmLockYaml, boolean includeDevDependencies) {
        Map<String, String> rawPackageInfo = new HashMap<>(pnpmLockYaml.dependencies);
        if (includeDevDependencies) {
            rawPackageInfo.putAll(pnpmLockYaml.devDependencies);
        }
        return rawPackageInfo.entrySet().stream()
                   .map(this::convertRawEntryToPackageId)
                   .collect(Collectors.toList());
    }

    private String convertRawEntryToPackageId(Map.Entry<String, String> entry) {
        String name = entry.getKey();
        if (name.startsWith("'") && name.endsWith("'")) {
            name = name.substring(1, name.length() - 1);
        }
        String version = entry.getValue();
        return String.format("/%s/%s", name, version);
    }

    private NameVersion parseNameVersionFromId(String id) {
        // ids follow format: /name/version, where name often contains slashes
        int indexOfLastSlash = id.lastIndexOf("/");
        String name = id.substring(1, indexOfLastSlash);
        String version = id.substring(indexOfLastSlash + 1);

        return new NameVersion(name, version);
    }

    private Dependency buildDependencyFromPackageId(String packageId) {
        NameVersion nameVersion = parseNameVersionFromId(packageId);
        return new Dependency(externalIdFactory.createNameVersionExternalId(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion()));
    }
}
