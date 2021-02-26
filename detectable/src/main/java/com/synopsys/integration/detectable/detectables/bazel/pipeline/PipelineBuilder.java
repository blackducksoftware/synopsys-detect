/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;

public class PipelineBuilder {
    private final List<IntermediateStep> intermediateSteps = new ArrayList<>();
    private FinalStep finalStep;

    public PipelineBuilder addIntermediateStep(final IntermediateStep intermediateStep) {
        intermediateSteps.add(intermediateStep);
        return this;
    }

    public PipelineBuilder setFinalStep(final FinalStep finalStep) {
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
