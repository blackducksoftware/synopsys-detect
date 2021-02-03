package com.synopsys.integration.detectable.detectables.gradle.functional;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

@FunctionalTest
public class BuildGradleParserTest {
    @Test
    public void testComplexBuildGradle() throws IOException {
        BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        DependencyGraph dependencyGraph;
        try (InputStream buildGradle = FunctionalTestFiles.asInputStream("/gradle/complexBuild.gradle")) {
            dependencyGraph = buildGradleParser.parse(buildGradle).get();
        }

        Assertions.assertTrue(dependencyGraph.getRootDependencies().size() > 0);

    }

    @Test
    public void testSimpleBuildGradle() throws IOException {
        BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        DependencyGraph dependencyGraph;
        try (InputStream buildGradle = FunctionalTestFiles.asInputStream("/gradle/intCommonBuild.gradle")) {
            dependencyGraph = buildGradleParser.parse(buildGradle).get();
        }

        Assertions.assertEquals(13, dependencyGraph.getRootDependencies().size());
    }

}
