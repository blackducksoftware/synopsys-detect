package com.synopsys.integration.detectable.detectables.gradle.functional;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;

public class BuildGradleParserTest {
    @Test
    public void testComplexBuildGradle() {
        BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        InputStream buildGradle = BuildGradleParserTest.class.getResourceAsStream("/detectables/functional/gradle/complexBuild.gradle");
        DependencyGraph dependencyGraph = buildGradleParser.parse(buildGradle).get();

        Assertions.assertTrue(dependencyGraph.getRootDependencies().size() > 0);

    }

    @Test
    public void testSimpleBuildGradle() {
        BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        InputStream buildGradle = BuildGradleParserTest.class.getResourceAsStream("/detectables/functional/gradle/intCommonBuild.gradle");
        DependencyGraph dependencyGraph = buildGradleParser.parse(buildGradle).get();

        Assertions.assertEquals(13, dependencyGraph.getRootDependencies().size());
    }

}
