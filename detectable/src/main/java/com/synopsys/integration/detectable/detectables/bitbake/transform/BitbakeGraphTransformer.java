package com.synopsys.integration.detectable.detectables.bitbake.transform;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphNodeLabelParser;

public class BitbakeGraphTransformer {
    private final GraphNodeLabelParser graphNodeLabelParser;

    public BitbakeGraphTransformer(GraphNodeLabelParser graphNodeLabelParser) {
        this.graphNodeLabelParser = graphNodeLabelParser;
    }

    public BitbakeGraph transform(GraphParser graphParser, Set<String> layerNames) {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();

        for (GraphNode graphNode : graphParser.getNodes().values()) {
            String name = parseNameFromNode(graphNode);
            Optional<String> layer = parseLayerFromNode(graphNode, layerNames);
            parseVersionFromNode(graphNode).ifPresent(ver -> bitbakeGraph.addNode(name, ver, layer.orElse(null)));
        }

        for (GraphEdge graphEdge : graphParser.getEdges().values()) {
            String parent = parseNameFromNode(graphEdge.getNode1());
            String child = parseNameFromNode(graphEdge.getNode2());
            if (!parent.equals(child)) {
                bitbakeGraph.addChild(parent, child);
            }
        }

        return bitbakeGraph;
    }

    private String parseNameFromNode(GraphNode graphNode) {
        String[] nodeIdPieces = graphNode.getId().split(".do_");
        return nodeIdPieces[0].replace("\"", "");
    }

    private Optional<String> parseVersionFromNode(GraphNode graphNode) {
        Optional<String> labelValue = getLabelAttribute(graphNode);
        if (labelValue.isPresent()) {
            return graphNodeLabelParser.parseVersionFromLabel(labelValue.get());
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> parseLayerFromNode(GraphNode graphNode, Set<String> knownLayerNames) {
        Optional<String> labelAttribute = getLabelAttribute(graphNode);
        if (labelAttribute.isPresent()) {
            return graphNodeLabelParser.parseLayerFromLabel(labelAttribute.get(), knownLayerNames);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getLabelAttribute(GraphNode graphNode) {
        String labelValue = (String) graphNode.getAttribute("label");
        Optional<String> result = Optional.empty();

        if (StringUtils.isNotBlank(labelValue)) {
            result = Optional.of(labelValue);
        }
        return result;
    }
}
