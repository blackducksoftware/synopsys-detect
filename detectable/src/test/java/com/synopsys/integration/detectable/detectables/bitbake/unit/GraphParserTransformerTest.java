/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("name", "name\\n:version\\n/some/path/to.bb", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(1, bitbakeGraph.getNodes().size());
        Assertions.assertEquals("version", bitbakeGraph.getNodes().get(0).getVersion().get());
    }

    @Test
    public void parsedRelationship() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("parent", "name\\n:parent.version\\n/some/path/to.bb", nodes, edges);
        addNode("child", "name\\n:child.version\\n/some/path/to.bb", nodes, edges);
        addEdge("edge1", "parent", "child", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(2, bitbakeGraph.getNodes().size());
        Assertions.assertEquals(1, bitbakeGraph.getNodes().get(0).getChildren().size());
        Assertions.assertTrue(bitbakeGraph.getNodes().get(0).getChildren().contains("child"), "Parent node children must contain child");
    }

    @Test
    public void removedQuotesFromName() {
        final HashMap<String, GraphEdge> edges = new HashMap<>();
        final HashMap<String, GraphNode> nodes = new HashMap<>();

        addNode("quotes\"removed", "example\\n:example\\n/example", nodes, edges);
        final BitbakeGraph bitbakeGraph = buildGraph(nodes, edges);

        Assertions.assertEquals(1, bitbakeGraph.getNodes().size());
        Assertions.assertEquals("quotesremoved", bitbakeGraph.getNodes().get(0).getName());
    }

    private BitbakeGraph buildGraph(final HashMap<String, GraphNode> nodes, final HashMap<String, GraphEdge> edges) {
        final GraphParserTransformer graphParserTransformer = new GraphParserTransformer();
        final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(mockParser(nodes, edges));
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
        final GraphNode node1 = nodeMap.values().stream().filter(it -> it.getId().equals(nodeName1)).findFirst().get();
        final GraphNode node2 = nodeMap.values().stream().filter(it -> it.getId().equals(nodeName2)).findFirst().get();
        final GraphEdge edge = new GraphEdge(edgeId, node1, node2);
        edgeMap.put(edgeId, edge);
    }

}
