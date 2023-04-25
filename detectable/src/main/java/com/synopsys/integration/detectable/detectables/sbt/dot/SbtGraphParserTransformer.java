package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphElement;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class SbtGraphParserTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SbtDotGraphNodeParser sbtDotGraphNodeParser;

    public SbtGraphParserTransformer(SbtDotGraphNodeParser sbtDotGraphNodeParser) {
        this.sbtDotGraphNodeParser = sbtDotGraphNodeParser;
    }

    public DependencyGraph transformDotToGraph(GraphParser graphParser, String projectNodeId) {
        DependencyGraph graph = new BasicDependencyGraph();

        Set<String> evictedIds = graphParser.getEdges().values().stream()
        .filter(
            edge -> edge.getAttribute("label") != null
            && edge.getAttribute("label").toString().toLowerCase().contains("evicted")
            )
            .map(GraphEdge::getNode1)
            .map(GraphElement::getId)
            .collect(Collectors.toSet());

        for (GraphEdge graphEdge : graphParser.getEdges().values()) {
            Dependency parent = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode1().getId());
            Dependency child = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode2().getId());
           
            if (projectNodeId.equals(graphEdge.getNode1().getId())) {
                graph.addChildToRoot(child);
            } else {
                if (!evictedIds.contains(graphEdge.getNode2().getId())) {
                    graph.addChildWithParent(child, parent);
                }
            }
        }

        return graph;
    }

    public DependencyGraph transformDotToGraph(GraphParser graphParser, Set<String> projectNodeIds) {
        DependencyGraph graph = new BasicDependencyGraph();

        for (GraphEdge graphEdge : graphParser.getEdges().values()) {
            Dependency parent = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode1().getId());
            Dependency child = sbtDotGraphNodeParser.nodeToDependency(graphEdge.getNode2().getId());
            if (projectNodeIds.contains(graphEdge.getNode1().getId())) {
                graph.addChildToRoot(parent);
            }
            graph.addChildWithParent(child, parent);
        }

        return graph;
    }
}
