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
        List<String> pubDepsLines = FunctionalTestFiles.asListOfStrings("/dart/pubDeps.txt");

        PubDepsParser pubDepsParser = new PubDepsParser();

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("charcode", "1.1.3");
        graphAssert.hasRootDependency("pedantic", "1.9.2");
        graphAssert.hasRootDependency("test", "1.15.3");
        graphAssert.hasRootDependency("typed_data", "1.2.0");

        graphAssert.hasParentChildRelationship("analyzer", "0.39.17", "_fe_analyzer_shared", "7.0.0");
        graphAssert.hasParentChildRelationship("glob", "1.2.0", "node_io", "1.1.1");
        graphAssert.hasParentChildRelationship("node_interop", "1.1.1", "js", "1.1.0");
        graphAssert.hasParentChildRelationship("js", "1.1.0", "async", "2.2.1");
        graphAssert.hasParentChildRelationship("async", "2.2.1", "charcode", "1.1.3");
        graphAssert.hasParentChildRelationship("typed_data", "1.2.0", "webkit_inspection_protocol", "0.7.3");

    }

    @Test
    public void test_3_1() {
        List<String> pubDepsLines = FunctionalTestFiles.asListOfStrings("/dart/pubDeps_3_1.txt");

        PubDepsParser pubDepsParser = new PubDepsParser();

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("collection", "1.18.0");
        graphAssert.hasRootDependency("source_span", "1.10.0");
        graphAssert.hasRootDependency("string_scanner", "1.2.0");
        graphAssert.hasRootDependency("typed_data", "1.3.2");

        graphAssert.hasParentChildRelationship("source_span", "1.10.0", "collection", "1.18.0");
        graphAssert.hasParentChildRelationship("source_span", "1.10.0", "path", "1.8.3");
        graphAssert.hasParentChildRelationship("source_span", "1.10.0", "term_glyph", "1.2.1");
        graphAssert.hasParentChildRelationship("string_scanner", "1.2.0", "source_span", "1.10.0");
        graphAssert.hasParentChildRelationship("typed_data", "1.3.2", "collection", "1.18.0");

    }

    @Test
    public void test_2_19() {
        List<String> pubDepsLines = FunctionalTestFiles.asListOfStrings("/dart/pubDeps_2_19.txt");

        PubDepsParser pubDepsParser = new PubDepsParser();

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("http", "0.12.2");
        graphAssert.hasRootDependency("markdown", "2.1.8");
        graphAssert.hasRootDependency("mustache", "1.1.1");
        graphAssert.hasRootDependency("tavern", "3.2.1+1");

        graphAssert.hasParentChildRelationship("http", "0.12.2", "http_parser", "3.1.4");
        graphAssert.hasParentChildRelationship("http", "0.12.2", "pedantic", "1.11.0");
        graphAssert.hasParentChildRelationship("typed_data", "1.3.0", "collection", "1.15.0");
        graphAssert.hasParentChildRelationship("source_span", "1.8.1", "collection", "1.15.0");
        graphAssert.hasParentChildRelationship("string_scanner", "1.1.0", "source_span", "1.8.1");
        graphAssert.hasParentChildRelationship("yaml", "2.2.1", "charcode", "1.2.0");

    }

    @Test
    public void test_2_18() {
        List<String> pubDepsLines = FunctionalTestFiles.asListOfStrings("/dart/pubDeps_2_18.txt");

        PubDepsParser pubDepsParser = new PubDepsParser();

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("http", "0.12.2");
        graphAssert.hasRootDependency("markdown", "2.1.8");
        graphAssert.hasRootDependency("mustache", "1.1.1");
        graphAssert.hasRootDependency("tavern", "3.2.1+1");

        graphAssert.hasParentChildRelationship("http", "0.12.2", "http_parser", "3.1.4");
        graphAssert.hasParentChildRelationship("http", "0.12.2", "pedantic", "1.11.0");
        graphAssert.hasParentChildRelationship("typed_data", "1.3.0", "collection", "1.15.0");
        graphAssert.hasParentChildRelationship("source_span", "1.8.1", "collection", "1.15.0");
        graphAssert.hasParentChildRelationship("string_scanner", "1.1.0", "source_span", "1.8.1");
        graphAssert.hasParentChildRelationship("yaml", "2.2.1", "charcode", "1.2.0");

    }
}
