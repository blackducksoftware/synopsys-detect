package com.synopsys.integration.detectable.detectables.sbt.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotGraphNodeParser;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtRootNodeFinder;

public class SbtRootNodeFinderTest {

    private GraphParser createGraphParser(String actualGraph) {
        String simpleGraph = "digraph \"dependency-graph\" {\n"
            + "    graph[rankdir=\"LR\"]\n"
            + "    edge [\n"
            + "        arrowtail=\"none\"\n"
            + "    ]\n"
            + actualGraph + "\n"
            + "\n"
            + "}";
        InputStream stream = new ByteArrayInputStream(simpleGraph.getBytes(StandardCharsets.UTF_8));
        return new GraphParser(stream);
    }

    private String node(String org, String name, String version) {
        return "    \"" + org + ":" + name + ":" + version + "\"[label=<" + org + "<BR/><B>" + name + "</B><BR/>" + version + "> style=\"\"]\n";
    }

    private String edge(String fromOrg, String fromName, String fromVersion, String toOrg, String toName, String toVersion) {
        return "    \"" + fromOrg + ":" + fromName + ":" + fromVersion + "\" -> \"" + toOrg + ":" + toName + ":" + toVersion + "\"\n";
    }

    @Test
    public void projectFoundFromSingleNode() throws DetectableException {
        GraphParser graphParser = createGraphParser(node("one-org", "one-name", "one-version"));
        SbtRootNodeFinder projectMatcher = new SbtRootNodeFinder(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        Set<String> projectId = projectMatcher.determineRootIDs(graphParser);
        Assertions.assertEquals(1, projectId.size());
        Assertions.assertEquals("\"one-org:one-name:one-version\"", projectId.stream().findFirst().get());
    }

    @Test
    public void projectFoundFromTwoNodesWhereProjectIsSecond() throws DetectableException {
        GraphParser graphParser = createGraphParser(node("two-org", "two-name", "two-version") +
            node("one-org", "one-name", "one-version") +
            edge("one-org", "one-name", "one-version", "two-org", "two-name", "two-version"));
        SbtRootNodeFinder projectMatcher = new SbtRootNodeFinder(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        Set<String> projectId = projectMatcher.determineRootIDs(graphParser);
        Assertions.assertEquals(1, projectId.size());
        Assertions.assertEquals("\"one-org:one-name:one-version\"", projectId.stream().findFirst().get());
    }

    @Test
    public void multipleFoundWithNoEdges() throws DetectableException {
        GraphParser graphParser = createGraphParser(node("one-org", "one-name", "one-version") +
            node("one-org", "one-name", "two-version"));
        SbtRootNodeFinder projectMatcher = new SbtRootNodeFinder(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        Set<String> projectId = projectMatcher.determineRootIDs(graphParser);
        Assertions.assertEquals(2, projectId.size());
        Assertions.assertTrue(projectId.contains("\"one-org:one-name:one-version\""));
        Assertions.assertTrue(projectId.contains("\"one-org:one-name:two-version\""));
    }

    @Test
    public void multipleFoundWithEdge() throws DetectableException {
        GraphParser graphParser = createGraphParser(node("one-org", "one-name", "one-version") +
            node("one-org", "one-name", "two-version") +
            node("one-org", "one-name", "three-version") + //should not be reported
            edge("one-org", "one-name", "one-version", "one-org", "one-name", "three-version"));
        SbtRootNodeFinder projectMatcher = new SbtRootNodeFinder(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        Set<String> projectId = projectMatcher.determineRootIDs(graphParser);
        Assertions.assertEquals(2, projectId.size());
        Assertions.assertTrue(projectId.contains("\"one-org:one-name:one-version\""));
        Assertions.assertTrue(projectId.contains("\"one-org:one-name:two-version\""));
    }

}
