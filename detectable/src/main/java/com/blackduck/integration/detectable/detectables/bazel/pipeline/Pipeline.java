package com.blackduck.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;
import com.blackduck.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.blackduck.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class Pipeline {
    private final List<IntermediateStep> intermediateSteps;
    private final FinalStep finalStep;

    public Pipeline(List<IntermediateStep> intermediateSteps, FinalStep finalStep) {
        this.intermediateSteps = intermediateSteps;
        this.finalStep = finalStep;
    }

    public List<Dependency> run() throws DetectableException, ExecutableFailedException {
        // Execute pipeline steps (like linux cmd piping with '|'); each step processes the output of the previous step
        List<String> pipelineData = new ArrayList<>();
        for (IntermediateStep pipelineStep : intermediateSteps) {
            pipelineData = pipelineStep.process(pipelineData);
        }
        return finalStep.finish(pipelineData);
    }
}
