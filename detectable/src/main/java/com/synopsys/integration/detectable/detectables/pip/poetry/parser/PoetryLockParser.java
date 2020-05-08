package com.synopsys.integration.detectable.detectables.pip.poetry.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pip.poetry.model.DependencyList;
import com.synopsys.integration.detectable.detectables.pip.poetry.model.Package;
import com.synopsys.integration.detectable.detectables.pip.poetry.model.PoetryLock;

public class PoetryLockParser {

    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private final Map<String, Dependency> packageMap = new HashMap<>();

    public DependencyGraph parseLockFile(InputStream poetryInputStream) {
        final PoetryLock poetryLock = new Toml().read(poetryInputStream).to(PoetryLock.class);
        if (poetryLock.packages != null) {
            return parseDependencies(poetryLock.packages);
        }

        return new MutableMapDependencyGraph();
    }

    private DependencyGraph parseDependencies(final List<Package> lockPackages) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        Set<String> rootPackages = determineRootPackages(lockPackages);

        for (final String rootPackage : rootPackages) {
            graph.addChildToRoot(packageMap.get(rootPackage));
        }
        //TODO - once we can parse package.dependencies
        /*
        for (final Package lockPackage : lockPackages) {
            List<String> dependencies = extractFromDependencyList(lockPackage.getDependencies());
            if (dependencies == null) {
                continue;
            }
            List<String> trimmedDependencies = trimDependencies(dependencies);
            for (final String dependency : trimmedDependencies) {
                Dependency child = packageMap.get(dependency);
                Dependency parent = packageMap.get(lockPackage.getName());
                if (child != null && parent != null) {
                    graph.addChildWithParent(child, parent);
                }
            }
        }

         */
        return graph;
    }

    private Set<String> determineRootPackages(List<Package> lockPackages) {
        Set<String> rootPackages = new HashSet<>();
        Set<String> dependencyPackages = new HashSet<>();

        for (final Package lockPackage : lockPackages) {
            if (lockPackage != null) {
                final String projectName = lockPackage.getName();
                final String projectVersion = lockPackage.getVersion();

                packageMap.put(projectName, createPoetryDependency(projectName, projectVersion));
                rootPackages.add(projectName);
                //TODO - once we can parse package.dependencies
                /*
                if (lockPackage.getDependencies() != null) {
                    List<String> dependencies = extractFromDependencyList(lockPackage.getDependencies());
                    dependencyPackages.addAll(trimDependencies(dependencies));
                }
                 */

            }
        }
        rootPackages.removeAll(dependencyPackages);

        return rootPackages;
    }

    private List<String> trimDependencies(List<String> rawDependencies) {
        List<String> trimmedDependencies = new ArrayList<>();

        for (String rawDependency : rawDependencies) {
            String trimmedDependency = rawDependency.split(" ")[0];
            trimmedDependencies.add(trimmedDependency);
        }
        return trimmedDependencies;
    }

    private List<String> extractFromDependencyList(DependencyList dependencyList) {
        return new ArrayList<>();
    }

    private Dependency createPoetryDependency(final String name, final String version) {
        final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);
        return new Dependency(name, version, dependencyExternalId);
    }
}
