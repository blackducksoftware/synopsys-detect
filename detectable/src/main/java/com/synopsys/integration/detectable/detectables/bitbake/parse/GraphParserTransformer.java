/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;

public class GraphParserTransformer {
    public BitbakeGraph transform(GraphParser graphParser) {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();

        for (GraphNode graphNode : graphParser.getNodes().values()) {
            String name = getNameFromNode(graphNode);
            getVersionFromNode(graphNode).ifPresent(
                    version -> bitbakeGraph.addNode(name, version)
            );
        }

        for (GraphEdge graphEdge : graphParser.getEdges().values()) {
            String parent = getNameFromNode(graphEdge.getNode1());
            String child = getNameFromNode(graphEdge.getNode2());
            if (!parent.equals(child)) {
                bitbakeGraph.addChild(parent, child);
            }
        }

        return bitbakeGraph;
    }

    private String getNameFromNode(GraphNode graphNode) {
        String[] nodeIdPieces = graphNode.getId().split(".do_");
        return nodeIdPieces[0].replace("\"", "");
    }

    private Optional<String> getVersionFromNode(GraphNode graphNode) {
        Optional<String> attribute = getLabelAttribute(graphNode);
        return attribute.map(this::getVersionFromLabel);
    }

    private Optional<String> getLabelAttribute(GraphNode graphNode) {
        String attribute = (String) graphNode.getAttribute("label");
        Optional<String> result = Optional.empty();

        if (StringUtils.isNotBlank(attribute)) {
            result = Optional.of(attribute);
        }

        return result;
    }

    private String getVersionFromLabel(String label) {
        String[] components = label.split("\\\\n:|\\\\n");
        return components[1];
    }
}
