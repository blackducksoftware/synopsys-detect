package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.ClasspathFileReader;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipeline;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.exception.IntegrationException;

public class PipelineTest {

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException, IOException {
        final List<Step> loadedSteps = run(null);
        assertEquals(5, loadedSteps.size());
        assertEquals("edit", loadedSteps.get(4).getType());
    }


    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        final List<Step> loadedSteps = run("maven_install");
        assertEquals(5, loadedSteps.size());
        assertEquals("edit", loadedSteps.get(4).getType());
    }

    private List<Step> run(final String providedBazelDependencyRule) throws IOException, IntegrationException {
        final ClasspathFileReader classpathFileReader = Mockito.mock(ClasspathFileReader.class);
        Mockito.when(classpathFileReader.readFileFromClasspathToString("file:custom-pipeline.json")).thenReturn("this value is ignored");
        final Pipeline pipeline = new Pipeline();
        final Pipelines builtinPipelines = new Pipelines();
        final List<Step> loadedSteps = pipeline.choose(builtinPipelines, WorkspaceRule.MAVEN_INSTALL, providedBazelDependencyRule);
        return loadedSteps;
    }

    @NotNull
    private List<Step> generateCustomPipeline() {
        final List<Step> testSteps = new ArrayList<>();
        testSteps.add(new Step("executeBazelOnEach", Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(${detect.bazel.target}))", "--output", "build")));
        testSteps.add(new Step("splitEach", Arrays.asList("\n")));
        testSteps.add(new Step("filter", Arrays.asList(".*maven_coordinates=.*")));
        testSteps.add(new Step("edit", Arrays.asList("^\\s*tags\\s*", "")));
        testSteps.add(new Step("edit", Arrays.asList("^\\s*=\\s*\\[\\s*\"", "")));
        testSteps.add(new Step("edit", Arrays.asList("maven_coordinates=", "")));
        testSteps.add(new Step("edit", Arrays.asList("\".*", "")));
        return testSteps;
    }
}
