package com.synopsys.integration.detectable.detectables.bazel.functional.bazel.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bazel.model.Step;
import com.synopsys.integration.detectable.detectables.bazel.model.StepType;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.PipelineChooser;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.Pipelines;
import com.synopsys.integration.exception.IntegrationException;

public class PipelineChooserTest {

    @Test
    public void testDerivedBazelDependencyRule() throws IntegrationException, IOException {
        final List<Step> loadedSteps = run(null);
        assertEquals(5, loadedSteps.size());
        assertEquals(StepType.EDIT, loadedSteps.get(4).getType());
    }

    @Test
    public void testProvidedBazelDependencyRule() throws IOException, IntegrationException {
        final List<Step> loadedSteps = run("maven_install");
        assertEquals(5, loadedSteps.size());
        assertEquals(StepType.EDIT, loadedSteps.get(4).getType());
    }

    private List<Step> run(final String providedBazelDependencyRule) throws IOException, IntegrationException {
        final PipelineChooser pipelineChooser = new PipelineChooser();
        final Pipelines builtinPipelines = new Pipelines();
        final List<Step> loadedSteps = pipelineChooser.choose(builtinPipelines, WorkspaceRule.MAVEN_INSTALL, providedBazelDependencyRule);
        return loadedSteps;
    }
}
