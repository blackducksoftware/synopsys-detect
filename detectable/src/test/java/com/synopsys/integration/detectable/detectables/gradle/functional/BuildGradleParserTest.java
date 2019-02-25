package com.synopsys.integration.detectable.detectables.gradle.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.Optional;

import org.junit.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.parsing.parse.BuildGradleParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class BuildGradleParserTest {
    @Test
    public void testGettingGraphFromSimpleBuildGradle() {
        final InputStream buildGradleInputStream = FunctionalTestFiles.asInputStream("/gradle/simple_build.gradle.txt");

        final BuildGradleParser buildGradleParser = new BuildGradleParser(new ExternalIdFactory());
        final Optional<DependencyGraph> dependencyGraph = buildGradleParser.parse(buildGradleInputStream);

        assertEquals(9, dependencyGraph.get().getRootDependencies().size());
    }
}
