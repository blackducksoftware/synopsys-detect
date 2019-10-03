package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectables.bazel.BazelClasspathFileReader;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRules;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.BazelPipelineJsonProcessor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.BazelPipelineLoader;
import com.synopsys.integration.exception.IntegrationException;

public class BazelPipelineLoaderTest {

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException, IOException {
        doTest(null);
    }


    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        doTest("maven_install");
    }

    private void doTest(final String providedBazelDependencyRule) throws IOException, IntegrationException {
        final List<Step> testSteps = generateSteps();
        final BazelPipelineJsonProcessor bazelPipelineJsonProcessor = Mockito.mock(BazelPipelineJsonProcessor.class);
        Mockito.when(bazelPipelineJsonProcessor.fromJsonString(Mockito.anyString())).thenReturn(testSteps);
        final BazelClasspathFileReader bazelClasspathFileReader = Mockito.mock(BazelClasspathFileReader.class);
        Mockito.when(bazelClasspathFileReader.readFileFromClasspathToString("/bazel/pipeline/maven_install.json")).thenReturn("this value is ignored");
        final BazelPipelineLoader bazelPipelineLoader = new BazelPipelineLoader(bazelClasspathFileReader, bazelPipelineJsonProcessor);
        final WorkspaceRules workspaceRules = Mockito.mock(WorkspaceRules.class);
        Mockito.when(workspaceRules.getDependencyRule()).thenReturn("maven_install");

        final List<Step> loadedSteps = bazelPipelineLoader.loadPipelineSteps(workspaceRules, providedBazelDependencyRule);

        assertEquals(7, loadedSteps.size());
        assertEquals("edit", loadedSteps.get(6).getType());
    }

    @NotNull
    private List<Step> generateSteps() {
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
