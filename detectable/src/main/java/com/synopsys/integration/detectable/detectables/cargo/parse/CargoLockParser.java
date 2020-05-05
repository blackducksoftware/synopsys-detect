/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.cargo.parse;

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
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.model.Package;

public class CargoLockParser {

    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private final Map<String, Dependency> packageMap = new HashMap<>();

    public DependencyGraph parseLockFile(final InputStream cargoLockInputStream) {
        final CargoLock cargoLock = new Toml().read(cargoLockInputStream).to(CargoLock.class);
        if (cargoLock.packages != null) {
            return parseDependencies(cargoLock.packages);
        }
        return new MutableMapDependencyGraph();
    }

    private DependencyGraph parseDependencies(final List<Package> lockPackages) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        Set<String> rootPackages = determineRootPackages(lockPackages);

        for (final String rootPackage : rootPackages) {
            graph.addChildToRoot(packageMap.get(rootPackage));
        }

        for (final Package lockPackage : lockPackages) {
            if (lockPackage.getDependencies() == null) {
                continue;
            }
            List<String> trimmedDependencies = trimDependencies(lockPackage.getDependencies());
            for (final String dependency : trimmedDependencies) {
                Dependency child = packageMap.get(dependency);
                Dependency parent = packageMap.get(lockPackage.getName());
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

        for (final Package lockPackage : lockPackages) {
            if (lockPackage != null) {
                final String projectName = lockPackage.getName();
                final String projectVersion = lockPackage.getVersion();

                packageMap.put(projectName, createCargoDependency(projectName, projectVersion));
                rootPackages.add(projectName);
                if (lockPackage.getDependencies() != null) {
                    dependencyPackages.addAll(trimDependencies(lockPackage.getDependencies()));
                }

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

    private Dependency createCargoDependency(final String name, final String version) {
        final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(null, name, version);
        return new Dependency(name, version, dependencyExternalId);
    }
}
