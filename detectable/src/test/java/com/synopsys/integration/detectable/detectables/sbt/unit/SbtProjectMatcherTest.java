package com.synopsys.integration.detectable.detectables.sbt.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotGraphNodeParser;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtProjectMatcher;

public class SbtProjectMatcherTest {

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
        SbtProjectMatcher projectMatcher = new SbtProjectMatcher(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        String projectId = projectMatcher.determineProjectNodeID(graphParser);
        Assertions.assertEquals("\"one-org:one-name:one-version\"", projectId);
    }

    @Test
    public void projectFoundFromTwoNodesWhereProjectIsSecond() throws DetectableException {
        GraphParser graphParser = createGraphParser(node("two-org", "two-name", "two-version") +
                                                        node("one-org", "one-name", "one-version") +
                                                        edge("one-org", "one-name", "one-version", "two-org", "two-name", "two-version"));
        SbtProjectMatcher projectMatcher = new SbtProjectMatcher(new SbtDotGraphNodeParser(new ExternalIdFactory()));
        String projectId = projectMatcher.determineProjectNodeID(graphParser);
        Assertions.assertEquals("\"one-org:one-name:one-version\"", projectId);
    }

}
