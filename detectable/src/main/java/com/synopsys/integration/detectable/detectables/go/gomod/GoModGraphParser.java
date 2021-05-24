/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class GoModGraphParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final String incompatibleSuffix = "+incompatible";

    public GoModGraphParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    DependencyGraph parseGoModGraph(List<String> goModGraph, String rootModule, Set<String> moduleExclusionList) {
        MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();

        for (String line : goModGraph) {
            //example: github.com/gomods/athens cloud.google.com/go@v0.26.0
            if (line.endsWith(incompatibleSuffix)) {
                // Trim incompatible suffix so that KB can match component
                line = line.substring(0, line.length() - incompatibleSuffix.length());
            }
            addDependencyToGraph(mutableDependencyGraph, line, rootModule, moduleExclusionList);
        }

        return mutableDependencyGraph;
    }

    private void addDependencyToGraph(MutableDependencyGraph mutableDependencyGraph, String line, String rootModule, Set<String> moduleExclusionList) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            logger.warn("Unknown graph line format: {}", line);
            return;
        }
        String fromModule = parts[0];
        String toModule = parts[1];
        Dependency to = parseDependency(toModule);

        Predicate<String> includeModule = moduleName -> !moduleExclusionList.contains(moduleName);
        boolean includeToDependency = includeModule.test(to.getName());
        boolean addToRoot = rootModule.equals(fromModule) && includeToDependency;
        if (addToRoot) {
            mutableDependencyGraph.addChildToRoot(to);
        } else {
            Dependency from = parseDependency(fromModule);
            boolean includeFromDependency = includeModule.test(from.getName());
            boolean addChildToParent = includeToDependency && includeFromDependency;
            if (addChildToParent) {
                mutableDependencyGraph.addChildWithParent(to, from);
            }
        }
    }

    private Dependency parseDependency(String dependencyPart) {
        if (dependencyPart.contains("@")) {
            String[] parts = dependencyPart.split("@");
            if (parts.length != 2) {
                logger.warn("Unknown graph dependency format, using entire line as name: {}", dependencyPart);
                return new Dependency(dependencyPart, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyPart, null));
            } else {
                String name = parts[0];
                String version = parts[1];
                if (version.contains("-")) { //The KB only supports the git hash, unfortunately we must strip out the rest. This gets just the commit has from a go.mod psuedo version.
                    String[] versionPieces = version.split("-");
                    version = versionPieces[versionPieces.length - 1];
                }
                return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, name, version));
            }
        } else {
            return new Dependency(dependencyPart, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyPart, null));
        }
    }

    public List<CodeLocation> parseListAndGoModGraph(List<String> listOutput, List<String> modGraphOutput, Set<String> moduleExclusionList) {
        List<CodeLocation> codeLocations = new ArrayList<>();
        for (String module : listOutput) {
            DependencyGraph graph = parseGoModGraph(modGraphOutput, module, moduleExclusionList);
            codeLocations.add(new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, module, null)));
        }
        return codeLocations;
    }
}
