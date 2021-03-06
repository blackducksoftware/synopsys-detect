/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.parsing.parse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilePhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BuildGradleParser {
    private final Logger logger = LoggerFactory.getLogger(BuildGradleParser.class);
    private final ExternalIdFactory externalIdFactory;

    public BuildGradleParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Optional<DependencyGraph> parse(final InputStream inputStream) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        try {
            final String sourceContents = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final AstBuilder astBuilder = new AstBuilder();
            final List<ASTNode> nodes = astBuilder.buildFromString(CompilePhase.CONVERSION, sourceContents);

            final DependenciesVisitor dependenciesVisitor = new DependenciesVisitor(externalIdFactory);
            for (final ASTNode node : nodes) {
                node.visit(dependenciesVisitor);
            }

            final List<Dependency> dependencies = dependenciesVisitor.getDependencies();
            dependencyGraph.addChildrenToRoot(dependencies);

            return Optional.of(dependencyGraph);
        } catch (final IOException e) {
            logger.error("Could not get the build file contents: " + e.getMessage());
        }

        return Optional.empty();
    }

}
