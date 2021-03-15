/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cargo.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.model.Package;

public class CargoLockParser {

    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private final Map<String, Dependency> packageMap = new HashMap<>();

    public DependencyGraph parseLockFile(String lockFile) throws DetectableException {
        try {
            CargoLock cargoLock = new Toml().read(lockFile).to(CargoLock.class);
            if (cargoLock.getPackages().isPresent()) {
                return parseDependencies(cargoLock.getPackages().get());
            }
        } catch (IllegalStateException e) {
            throw new DetectableException("Illegal syntax was detected in Cargo.lock file", e);
        }
        return new MutableMapDependencyGraph();
    }

    private DependencyGraph parseDependencies(List<Package> lockPackages) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        Set<String> rootPackages = determineRootPackages(lockPackages);

        for (String rootPackage : rootPackages) {
            graph.addChildToRoot(packageMap.get(rootPackage));
        }

        for (Package lockPackage : lockPackages) {
            if (!lockPackage.getDependencies().isPresent()) {
                continue;
            }
            List<String> trimmedDependencies = extractDependencyNames(lockPackage.getDependencies().get());
            for (String dependency : trimmedDependencies) {
                Dependency child = packageMap.get(dependency);
                Dependency parent = packageMap.get(lockPackage.getName().orElse(""));
                if (child != null && parent != null) {
                    graph.addChildWithParent(child, parent);
                }
            }
        }
        return graph;
    }

    private Set<String> determineRootPackages(List<Package> lockPackages) {
        Set<String> rootPackages = new HashSet<>();
        Set<String> dependencyPackages = new HashSet<>();

        for (Package lockPackage : lockPackages) {
            String projectName = lockPackage.getName().orElse("");
            String projectVersion = lockPackage.getVersion().orElse("");

            packageMap.put(projectName, createCargoDependency(projectName, projectVersion));
            rootPackages.add(projectName);
            lockPackage.getDependencies()
                .map(this::extractDependencyNames)
                .ifPresent(dependencyPackages::addAll);
        }
        rootPackages.removeAll(dependencyPackages);

        return rootPackages;
    }

    private List<String> extractDependencyNames(List<String> rawDependencies) {
        return rawDependencies.stream()
                   .map(dependency -> dependency.split(" ")[0])
                   .collect(Collectors.toList());
    }

    private Dependency createCargoDependency(String name, String version) {
        ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.CRATES, name, version);
        return new Dependency(name, version, dependencyExternalId);
    }
}
