package com.synopsys.integration.detectable.detectables.sbt.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.sbt.dot.SbtDotGraphNodeParser;

public class SbtDotGraphNodeParserTest {

    @Test
    public void canParseSimpleGraph() {
        String simpleGraph = "digraph \"dependency-graph\" {\n"
            + "    graph[rankdir=\"LR\"]\n"
            + "    edge [\n"
            + "        arrowtail=\"none\"\n"
            + "    ]\n"
            + "    \"org.scalameta:scalafmtroot_2.13:2.7.5-SNAPSHOT\"[label=<org.scalameta<BR/><B>scalafmtroot_2.13</B><BR/>2.7.5-SNAPSHOT> style=\"\"]\n"
            + "\n"
            + "}";
        InputStream stream = new ByteArrayInputStream(simpleGraph.getBytes(StandardCharsets.UTF_8));
        GraphParser graphParser = new GraphParser(stream);

        Map.Entry<String, GraphNode> node = graphParser.getNodes().entrySet().stream().findFirst().get();
        SbtDotGraphNodeParser nodeParser = new SbtDotGraphNodeParser(new ExternalIdFactory());
        Dependency dependencyFromKey = nodeParser.nodeToDependency(node.getKey());
        Dependency dependencyFromId = nodeParser.nodeToDependency(node.getValue().getId());

        assertDependency(dependencyFromId, "org.scalameta", "scalafmtroot_2.13", "2.7.5-SNAPSHOT");
        assertDependency(dependencyFromKey, "org.scalameta", "scalafmtroot_2.13", "2.7.5-SNAPSHOT");
    }

    @Test
    public void canRemoveQuotes() {
        SbtDotGraphNodeParser nodeParser = new SbtDotGraphNodeParser(new ExternalIdFactory());
        Dependency dependency = nodeParser.nodeToDependency("\"net.databinder.dispatch:dispatch-core_2.10:0.11.2\"");
        assertDependency(dependency, "net.databinder.dispatch", "dispatch-core_2.10", "0.11.2");
    }

    @Test
    public void canRemoveTrailingQuotes() {
        SbtDotGraphNodeParser nodeParser = new SbtDotGraphNodeParser(new ExternalIdFactory());
        Dependency dependency = nodeParser.nodeToDependency("net.databinder.dispatch:dispatch-core_2.10:0.11.2\"");
        assertDependency(dependency, "net.databinder.dispatch", "dispatch-core_2.10", "0.11.2");
    }

    @Test
    public void canHandleCommits() {
        SbtDotGraphNodeParser nodeParser = new SbtDotGraphNodeParser(new ExternalIdFactory());
        Dependency dependency = nodeParser.nodeToDependency("org.foundweekends:sbt-bintray:HEAD+20210303-1347");
        assertDependency(dependency, "org.foundweekends", "sbt-bintray", "HEAD+20210303-1347");
    }

    private void assertDependency(Dependency dependency, String group, String name, String version) {
        Assertions.assertEquals(name, dependency.getName());
        Assertions.assertEquals(version, dependency.getVersion());

        ExternalId externalId = dependency.getExternalId();
        Assertions.assertEquals(group, externalId.getGroup());
        Assertions.assertEquals(name, externalId.getName());
        Assertions.assertEquals(version, externalId.getVersion());

    }
}
