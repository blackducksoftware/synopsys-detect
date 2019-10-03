package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

        final List<Step> testSteps = new ArrayList<>();
        testSteps.add(new Step("executeBazelOnEach", Arrays.asList("cquery", "--noimplicit_deps", "kind(j.*import, deps(${detect.bazel.target}))", "--output", "build")));
        testSteps.add(new Step("splitEach", Arrays.asList("\n")));
        testSteps.add(new Step("filter", Arrays.asList(".*maven_coordinates=.*")));
        testSteps.add(new Step("edit", Arrays.asList("^\\s*tags\\s*", "")));
        testSteps.add(new Step("edit", Arrays.asList("^\\s*=\\s*\\[\\s*\"", "")));
        testSteps.add(new Step("edit", Arrays.asList("maven_coordinates=", "")));
        testSteps.add(new Step("edit", Arrays.asList("\".*", "")));

        // TODO the value of pipelineStepsString is ignored thanks to the use of Mockito.anyString()
        final File pipelineStepsFile = new File("src/test/resources/detectables/functional/bazel/pipeline/maven_install.json");
        final String pipelineStepsString = FileUtils.readFileToString(pipelineStepsFile, StandardCharsets.UTF_8);

        final BazelPipelineJsonProcessor bazelPipelineJsonProcessor = Mockito.mock(BazelPipelineJsonProcessor.class);
        // bazelPipelineJsonProcessor.fromJsonString(jsonString)
        Mockito.when(bazelPipelineJsonProcessor.fromJsonString(Mockito.anyString())).thenReturn(testSteps);

        final BazelClasspathFileReader bazelClasspathFileReader = Mockito.mock(BazelClasspathFileReader.class);
        Mockito.when(bazelClasspathFileReader.readFileFromClasspathToString("/bazel/pipeline/maven_install.json")).thenReturn(pipelineStepsString);
        final BazelPipelineLoader bazelPipelineLoader = new BazelPipelineLoader(bazelClasspathFileReader, bazelPipelineJsonProcessor);

        final WorkspaceRules workspaceRules = Mockito.mock(WorkspaceRules.class);
        // workspaceRules.getDependencyRule()
        Mockito.when(workspaceRules.getDependencyRule()).thenReturn("maven_install");

        final List<Step> loadedSteps = bazelPipelineLoader.loadPipelineSteps(workspaceRules, null);

        assertEquals(7, loadedSteps.size());
    }

    @Test
    public void testProvidedBazelDependencyRule() {

//        final BazelPipelineJsonProcessor bazelPipelineJsonProcessor = Mockito.mock(BazelPipelineJsonProcessor.class);
//        final BazelPipelineLoader bazelPipelineLoader = new BazelPipelineLoader(bazelPipelineJsonProcessor);
//
//        bazelPipelineLoader.loadPipelineSteps(workspaceRules, "maven_install");

        fail("not implemented yet");
    }
}
