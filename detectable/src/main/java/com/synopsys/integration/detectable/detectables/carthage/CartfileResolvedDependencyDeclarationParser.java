/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.carthage;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CartfileResolvedDependencyDeclarationParser {
    private static String GITHUB_ORIGIN_ID = "github";

    private ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    public DependencyGraph parseDependencies(List<String> dependencyDeclarations) {
        // Each line in a Cartfile.resolved file is a dependency declaration: <origin> <name/resource> <version>
        // eg. github "realm/realm-cocoa" "v10.7.2"
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (String dependencyDeclaration : dependencyDeclarations) {
            String[] dependencyDeclarationPieces = dependencyDeclaration.split("\\s+");
            String origin = dependencyDeclarationPieces[0];
            // Carthage supports declarations of dependencies via github org/repo, a URL, or a local path
            // As of now, Detect only supports dependencies with github origins
            // The KB does not have mappings for binaries, or resources that are not open source.  It has some mappings, though, for GitHub repos
            if (origin.equals(GITHUB_ORIGIN_ID)) {
                String name = dependencyDeclarationPieces[1].replace("\"", "");
                String version = dependencyDeclarationPieces[2].replace("\"", "");

                // Because the dependency is hosted on GitHub, we must use the github forge in order for KB to match it
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, name, version);
                // As of Carthage 0.38.0 the dependencies in Cartfile.resolved are produced as a flat list
                dependencyGraph.addChildToRoot(new Dependency(name, version, externalId));
            }
        }
        return dependencyGraph;
    }
}
