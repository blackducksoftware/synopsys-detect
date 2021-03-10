/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.dot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class SbtGraphParserTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SbtDotGraphNodeParser sbtDotGraphNodeParser;

    public SbtGraphParserTransformer(final SbtDotGraphNodeParser sbtDotGraphNodeParser) {
        this.sbtDotGraphNodeParser = sbtDotGraphNodeParser;
    }

    public DependencyGraph transformDotToGraph(GraphParser graphParser, String projectNodeId) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (GraphEdge graphEdge : graphParser.getEdges().values()) {
            Dependency parent = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode1().getId());
            Dependency child = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode2().getId());
            if (projectNodeId.equals(graphEdge.getNode1().getId())) {
                graph.addChildToRoot(child);
            } else {
                graph.addChildWithParent(child, parent);
            }
        }

        return graph;
    }
}
