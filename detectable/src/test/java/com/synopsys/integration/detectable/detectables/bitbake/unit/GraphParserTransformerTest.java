package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;

@UnitTest
public class GraphParserTransformerTest {
    @Test
    public void parsedVersionFromLabel() {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("name", "name\\n:version\\n/some/path/to.bb", nodes, edges);
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(1, bitbakeGraph.getNodes().size());
        Assertions.assertEquals("version", bitbakeGraph.getNodes().get(0).getVersion().get());
    }

    @Test
    public void parsedRelationship() {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("parent", "name\\n:parent.version\\n/some/path/to.bb", nodes, edges);
        addNode("child", "name\\n:child.version\\n/some/path/to.bb", nodes, edges);
        addEdge("edge1", "parent", "child", nodes, edges);
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(2, bitbakeGraph.getNodes().size());
        Assertions.assertEquals(1, bitbakeGraph.getNodes().get(0).getChildren().size());
        Assertions.assertTrue(bitbakeGraph.getNodes().get(0).getChildren().contains("child"), "Parent node children must contain child");
    }

    @Test
    public void removedQuotesFromName() {
        HashMap<String, GraphEdge> edges = new HashMap<>();
        HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("quotes\"removed", "example\\n:example\\n/example", nodes, edges);
        BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(1, bitbakeGraph.getNodes().size());
        Assertions.assertEquals("quotesremoved", bitbakeGraph.getNodes().get(0).getName());
    }

    private BitbakeGraph buildGraph(HashMap<String, GraphNode> nodes, HashMap<String, GraphEdge> edges) {
        GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        BitbakeGraph bitbakeGraph = graphParserTransformer.transform(mockParser(nodes, edges));
        return bitbakeGraph;
    }

    private GraphParser mockParser(HashMap<String, GraphNode> nodeMap, HashMap<String, GraphEdge> edgeMap) {
        GraphParser parser = Mockito.mock(GraphParser.class);
        Mockito.when(parser.getNodes()).thenReturn(nodeMap);
        Mockito.when(parser.getEdges()).thenReturn(edgeMap);
        return parser;
    }

    private void addNode(String id, String labelValue, HashMap<String, GraphNode> nodeMap, HashMap<String, GraphEdge> edgeMap) {
        GraphNode graphNode = new GraphNode(id);
        graphNode.setAttribute("label", labelValue);
        nodeMap.put(id, graphNode);
    }

    private void addEdge(String edgeId, String nodeName1, String nodeName2, HashMap<String, GraphNode> nodeMap, HashMap<String, GraphEdge> edgeMap) {
        GraphNode node1 = nodeMap.values().stream().filter(it -> it.getId().equals(nodeName1)).findFirst().get();
        GraphNode node2 = nodeMap.values().stream().filter(it -> it.getId().equals(nodeName2)).findFirst().get();
        GraphEdge edge = new GraphEdge(edgeId, node1, node2);
        edgeMap.put(edgeId, edge);
    }

}
