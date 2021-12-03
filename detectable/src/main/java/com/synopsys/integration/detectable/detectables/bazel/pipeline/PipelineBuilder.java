package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;

public class PipelineBuilder {
    private final List<IntermediateStep> intermediateSteps = new ArrayList<>();
    private FinalStep finalStep;

    public PipelineBuilder addIntermediateStep(IntermediateStep intermediateStep) {
        intermediateSteps.add(intermediateStep);
        return this;
    }

    public PipelineBuilder setFinalStep(FinalStep finalStep) {
        this.finalStep = finalStep;
        return this;
    }

    public Pipeline build() {
        if (finalStep == null) {
            throw new UnsupportedOperationException("A final step is required");
        }
        return new Pipeline(intermediateSteps, finalStep);
    }
}
