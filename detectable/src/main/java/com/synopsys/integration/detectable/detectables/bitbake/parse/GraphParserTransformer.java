package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;

public class GraphParserTransformer {
    public BitbakeGraph transform(GraphParser graphParser, Set<String> layerNames) {
        BitbakeGraph bitbakeGraph = new BitbakeGraph();

        for (GraphNode graphNode : graphParser.getNodes().values()) {
            String name = getNameFromNode(graphNode);
            Optional<String> layer = getLayerFromNode(graphNode, layerNames);
            // TODO refactor
            Optional<String> version = getVersionFromNode(graphNode);
            if (version.isPresent()) {
                bitbakeGraph.addNode(name, version.get(), layer.orElse(null));
            }
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

    private Optional<String> getLayerFromNode(GraphNode graphNode, Set<String> knownLayerNames) {
        Optional<String> labelAttribute = getLabelAttribute(graphNode);
        // TODO refactor
        if (labelAttribute.isPresent()) {
            return getLayerFromLabel(labelAttribute.get(), knownLayerNames);
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getLabelFromNode(GraphNode graphNode, Set<String> knownLayers) {
        Optional<String> attribute = getLabelAttribute(graphNode);
        // TODO refactor
        if (attribute.isPresent()) {
            return getLayerFromLabel(attribute.get(), knownLayers);
        } else {
            return Optional.empty();
        }
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
        String[] components = getLabelParts(label);
        return components[1];
    }

    private Optional<String> getLayerFromLabel(String label, Set<String> knownLayerNames) {
        String[] components = getLabelParts(label);
        if (components.length == 3) {
            String bbPath = components[2];
            for (String candidateLayerName : knownLayerNames) {
                String possibleLayerPathSubstring = "/" + candidateLayerName + "/";
                if (bbPath.contains(possibleLayerPathSubstring)) {
                    return Optional.of(candidateLayerName);
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    private String[] getLabelParts(final String label) {
        return label.split("\\\\n:|\\\\n");
    }
}
