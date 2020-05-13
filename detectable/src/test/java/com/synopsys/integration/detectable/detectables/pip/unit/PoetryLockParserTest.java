package com.synopsys.integration.detectable.detectables.pip.unit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PoetryLockParserTest {

    @Test
    public void testParseNameAndVersionSimple() throws IOException {

        Path input = Paths.get(this.getClass().getClassLoader().getResource("detectables/functional/pip/poetry/simpleNameVersion.lock").getPath());
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("pytest-cov", "2.8.1");
        graphAssert.hasRootDependency("pytest-mock", "2.0.0");
    }

    @Test
    public void testParseDependencies() throws IOException {
        Path input = Paths.get(this.getClass().getClassLoader().getResource("detectables/functional/pip/poetry/packageWithDependencies.lock").getPath());
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
        Path input = Paths.get(this.getClass().getClassLoader().getResource("detectables/functional/pip/poetry/noPackageObjects.lock").getPath());
        PoetryLockParser poetryLockParser = new PoetryLockParser();
        DependencyGraph graph = poetryLockParser.parseLockFile(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootSize(0);
    }

    @Test
    public void testParseComplexDependencyHierarchy() throws IOException{
        Path input = Paths.get(this.getClass().getClassLoader().getResource("detectables/functional/pip/poetry/complexDependencyHierarchy.lock").getPath());
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
