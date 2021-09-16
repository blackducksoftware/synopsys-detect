/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.carthage;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class CarthageExtractor {
    private CartfileResolvedDependencyDeclarationParser dependencyDeclarationParser;

    public CarthageExtractor(CartfileResolvedDependencyDeclarationParser dependencyDeclarationParser) {
        this.dependencyDeclarationParser = dependencyDeclarationParser;
    }

    public Extraction extract(File cartfileResolved) {
        try {
            List<String> dependencyDeclarations = Files.readAllLines(cartfileResolved.toPath());

            DependencyGraph dependencyGraph = dependencyDeclarationParser.parseDependencies(dependencyDeclarations);
            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            // No project info - hoping git can help with that.
            return new Extraction.Builder().success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().failure(String.format("There was a problem extracting dependencies from %s", cartfileResolved.getAbsolutePath())).build();
        }
    }
}
