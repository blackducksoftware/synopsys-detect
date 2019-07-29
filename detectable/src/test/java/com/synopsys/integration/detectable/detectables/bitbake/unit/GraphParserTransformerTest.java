package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;

@UnitTest
public class GraphParserTransformerTest {
    @Test
    public void parsedVersionFromLabel_RecipeDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("name", "name\\n:version\\n/some/path/to.bb", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.RECIPE_DEPENDS);

        Assert.assertEquals(1, bitbakeGraph.getNodes().size());
        Assert.assertEquals("version", bitbakeGraph.getNodes().get(0).getVersion().get());
    }

    @Test
    public void parsedVersionFromLabel_PackageDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("name", "name :version\\n/some/path/to.bb", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.PACKAGE_DEPENDS);

        Assert.assertEquals(1, bitbakeGraph.getNodes().size());
        Assert.assertEquals("version", bitbakeGraph.getNodes().get(0).getVersion().get());
    }

    @Test
    public void parsedRelationship_RecipeDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("parent", "name\\n:parent.version\\n/some/path/to.bb", nodes, edges);
        addNode("child", "name\\n:child.version\\n/some/path/to.bb", nodes, edges);
        addEdge("edge1", "parent", "child", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.RECIPE_DEPENDS);

        Assert.assertEquals(2, bitbakeGraph.getNodes().size());
        Assert.assertEquals(1, bitbakeGraph.getNodes().get(0).getChildren().size());
        Assert.assertTrue("Parent node children must contain child", bitbakeGraph.getNodes().get(0).getChildren().contains("child"));
    }

    @Test
    public void parsedRelationship_PackageDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("parent", "name :parent.version\\n/some/path/to.bb", nodes, edges);
        addNode("child", "name :child.version\\n/some/path/to.bb", nodes, edges);
        addEdge("edge1", "parent", "child", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.PACKAGE_DEPENDS);

        Assert.assertEquals(2, bitbakeGraph.getNodes().size());
        Assert.assertEquals(1, bitbakeGraph.getNodes().get(0).getChildren().size());
        Assert.assertTrue("Parent node children must contain child", bitbakeGraph.getNodes().get(0).getChildren().contains("child"));
    }

    @Test
    public void removedQuotesFromName_RecipeDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("quotes\"removed", "example\\n:example\\n/example", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.RECIPE_DEPENDS);

        Assert.assertEquals(1, bitbakeGraph.getNodes().size());
        Assert.assertEquals("quotesremoved", bitbakeGraph.getNodes().get(0).getName());
    }

    @Test
    public void removedQuotesFromName_PackageDepends() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("quotes\"removed", "example :example\\n/example", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges, BitbakeFileType.PACKAGE_DEPENDS);

        Assert.assertEquals(1, bitbakeGraph.getNodes().size());
        Assert.assertEquals("quotesremoved", bitbakeGraph.getNodes().get(0).getName());
    }

    private BitbakeGraph buildGraph(final HashMap<String, GraphNode> nodes, final HashMap<String, GraphEdge> edges, final BitbakeFileType bitbakeFileType) {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(mockParser(nodes, edges), bitbakeFileType);
        return bitbakeGraph;
    }

    private GraphParser mockParser(final HashMap<String, GraphNode> nodeMap, final HashMap<String, GraphEdge> edgeMap) {
        final GraphParser parser = Mockito.mock(GraphParser.class);
        Mockito.when(parser.getNodes()).thenReturn(nodeMap);
        Mockito.when(parser.getEdges()).thenReturn(edgeMap);
        return parser;
    }

    private void addNode(final String id, final String labelValue, final HashMap<String, GraphNode> nodeMap, final HashMap<String, GraphEdge> edgeMap) {
        final GraphNode graphNode = new GraphNode(id);
        graphNode.setAttribute("label", labelValue);
        nodeMap.put(id, graphNode);
    }

    private void addEdge(final String edgeId, final String nodeName1, final String nodeName2, final HashMap<String, GraphNode> nodeMap, final HashMap<String, GraphEdge> edgeMap) {
        final GraphNode node1 = nodeMap.entrySet().stream().map(it -> it.getValue()).filter(it -> it.getId().equals(nodeName1)).findFirst().get();
        final GraphNode node2 = nodeMap.entrySet().stream().map(it -> it.getValue()).filter(it -> it.getId().equals(nodeName2)).findFirst().get();
        final GraphEdge edge = new GraphEdge(edgeId, node1, node2);
        edgeMap.put(edgeId, edge);
    }
}
