package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PoetryLockParserTest {

    @Test
    public void testParseNameAndVersionSimple() {

        String input = String.join(System.lineSeparator(), Arrays.asList(
            "[[package]]",
            "name = \"pytest-cov\"",
            "version = \"2.8.1\"",
            "",
            "[[package]]",
            "name = \"pytest-mock\"",
            "version = \"2.0.0\""
        ));
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("pytest-cov", "2.8.1");
        graphAssert.hasRootDependency("pytest-mock", "2.0.0");
    }

    @Test
    public void testParseDependencies() {
        String input = String.join(System.lineSeparator(), Arrays.asList(
            "[[package]]",
            "name = \"pytest-cov\"",
            "python-versions = \">=2.7, !=3.0.*, !=3.1.*, !=3.2.*, !=3.3.*\"",
            "version = \"2.8.1\"",
            "",
            "[package.dependencies]",
            "coverage = \">=4.4\"",
            "pytest = \">=3.6\"",
            "",
            "[[package]]",
            "name = \"coverage\"",
            "version = \"4.4\"",
            "",
            "[[package]]",
            "name = \"pytest\"",
            "version = \"3.7\""
        ));
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("pytest-cov", "2.8.1");
        graphAssert.hasParentChildRelationship("pytest-cov", "2.8.1", "coverage", "4.4");
        graphAssert.hasParentChildRelationship("pytest-cov", "2.8.1", "pytest", "3.7");
    }

    @Test
    public void testEmptyGraphWhenNoPackageObjects() throws IOException {
        String input = String.join(System.lineSeparator(), Arrays.asList(
            "package",
            "name = \"pytest\"",
            "version = \"1.0.0\"",
            "",
            "package",
            "name = \"python\"",
            "version = \"3.0\""
        ));
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testParseComplexDependencyHierarchy() throws IOException {
        String input = String.join(System.lineSeparator(), Arrays.asList(
            "[[package]]",
            "name = \"test1\"",
            "version = \"1.0.0\"",
            "",
            "[package.dependencies]",
            "test2 = \">=1.0\"",
            "",
            "[[package]]",
            "name = \"test2\"",
            "version = \"2.0\"",
            "",
            "[package.dependencies]",
            "test4 = \"<4.4\"",
            "",
            "[[package]]",
            "name = \"test3\"",
            "version = \"3.0\"",
            "",
            "[[package]]",
            "name = \"test4\"",
            "version = \"4.0\"",
            "",
            "[package.dependencies]",
            "test1 = \"~1.0.0\""
        ));
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("test3", "3.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "test2", "2.0");
        graphAssert.hasParentChildRelationship("test2", "2.0", "test4", "4.0");
        graphAssert.hasParentChildRelationship("test4", "4.0", "test1", "1.0.0");
    }
}
