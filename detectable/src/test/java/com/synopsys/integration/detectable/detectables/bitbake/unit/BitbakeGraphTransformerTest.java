package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphNodeLabelParser;
import com.synopsys.integration.detectable.detectables.bitbake.transform.BitbakeGraphTransformer;
import com.synopsys.integration.exception.IntegrationException;

@UnitTest
public class BitbakeGraphTransformerTest {
    @Test
    public void parsedVersionFromLabel() {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("name", "name\\n:version\\n/some/meta/path/to.bb", nodes);
        Set<String> knownLayers = new HashSet<>(Arrays.asList("aaa", "meta", "bbb"));
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, knownLayers);

        assertEquals(1, bitbakeGraph.getNodes().size());
        assertTrue(bitbakeGraph.getNodes().get(0).getVersion().isPresent());
        assertEquals("version", bitbakeGraph.getNodes().get(0).getVersion().get());
        assertTrue(bitbakeGraph.getNodes().get(0).getLayer().isPresent());
        assertEquals("meta", bitbakeGraph.getNodes().get(0).getLayer().get());
    }

    @Test
    public void parsedRelationship() throws IntegrationException {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("parent", "name\\n:parent.version\\n/some/meta/path/to.bb", nodes);
        addNode("child", "name\\n:child.version\\n/some/meta/path/to.bb", nodes);
        addEdge("edge1", "parent", "child", nodes, edges);
        Set<String> knownLayers = new HashSet<>(Arrays.asList("aaa", "meta", "bbb"));
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, knownLayers);

        assertEquals(2, bitbakeGraph.getNodes().size());
        assertEquals(1, bitbakeGraph.getNodes().get(0).getChildren().size());
        assertTrue(bitbakeGraph.getNodes().get(0).getChildren().contains("child"), "Parent node children must contain child");
    }

    @Test
    public void removedQuotesFromName() throws IntegrationException {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("quotes\"removed", "example\\n:example\\n/example/meta/some.bb", nodes);
        Set<String> knownLayers = new HashSet<>(Arrays.asList("aaa", "meta", "bbb"));
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, knownLayers);

        assertEquals(1, bitbakeGraph.getNodes().size());
        assertEquals("quotesremoved", bitbakeGraph.getNodes().get(0).getName());
    }

    private BitbakeGraph buildGraph(HashMap<String, GraphNode> nodes, HashMap<String, GraphEdge> edges, Set<String> knownLayers) {
        BitbakeGraphTransformer bitbakeGraphTransformer = new BitbakeGraphTransformer(new GraphNodeLabelParser());
        return bitbakeGraphTransformer.transform(mockParser(nodes, edges), knownLayers);
    }

    private GraphParser mockParser(HashMap<String, GraphNode> nodeMap, HashMap<String, GraphEdge> edgeMap) {
        GraphParser parser = Mockito.mock(GraphParser.class);
        Mockito.when(parser.getNodes()).thenReturn(nodeMap);
        Mockito.when(parser.getEdges()).thenReturn(edgeMap);
        return parser;
    }

    private void addNode(String id, String labelValue, HashMap<String, GraphNode> nodeMap) {
        GraphNode graphNode = new GraphNode(id);
        graphNode.setAttribute("label", labelValue);
        nodeMap.put(id, graphNode);
    }

    private void addEdge(String edgeId, String nodeName1, String nodeName2, HashMap<String, GraphNode> nodeMap, HashMap<String, GraphEdge> edgeMap) throws IntegrationException {
        GraphNode node1 = nodeMap.values().stream()
            .filter(it -> it.getId().equals(nodeName1))
            .findFirst()
            .orElseThrow(() -> new IntegrationException("Failed to find Node " + nodeName1 + " in the graph to be able to add an Edge to " + nodeName2));
        GraphNode node2 = nodeMap.values().stream()
            .filter(it -> it.getId().equals(nodeName2))
            .findFirst()
            .orElseThrow(() -> new IntegrationException("Failed to find Node " + nodeName2 + " in the graph to be able to add an Edge to " + nodeName1));
        GraphEdge edge = new GraphEdge(edgeId, node1, node2);
        edgeMap.put(edgeId, edge);
    }

}
