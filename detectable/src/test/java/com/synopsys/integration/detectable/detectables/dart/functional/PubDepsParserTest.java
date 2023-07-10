package com.synopsys.integration.detectable.detectables.dart.functional;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PubDepsParserTest {
    @Test
    public void test() {
        String pubDepsLines = FunctionalTestFiles.asString("/dart/pubDeps.json");

        PubDepsParser pubDepsParser = new PubDepsParser();

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("lints", "2.0.1");
        graphAssert.hasRootDependency("test", "1.22.1");
        graphAssert.hasRootDependency("http", "0.13.5");

        graphAssert.hasParentChildRelationship("analyzer", "5.4.0", "_fe_analyzer_shared", "52.0.0");
        graphAssert.hasParentChildRelationship("source_span", "1.9.1", "path", "1.8.3");
        graphAssert.hasParentChildRelationship("js", "0.6.5", "meta", "1.8.0");

    }
}
