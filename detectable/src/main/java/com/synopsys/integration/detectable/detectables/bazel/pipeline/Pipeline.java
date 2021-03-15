/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.exception.IntegrationException;

public class Pipeline {
    private final List<IntermediateStep> intermediateSteps;
    private final FinalStep finalStep;

    public Pipeline(List<IntermediateStep> intermediateSteps, FinalStep finalStep) {
        this.intermediateSteps = intermediateSteps;
        this.finalStep = finalStep;
    }

    public List<Dependency> run() throws IntegrationException {
        // Execute pipeline steps (like linux cmd piping with '|'); each step processes the output of the previous step
        List<String> pipelineData = new ArrayList<>();
        for (IntermediateStep pipelineStep : intermediateSteps) {
            pipelineData = pipelineStep.process(pipelineData);
        }
        return finalStep.finish(pipelineData);
    }
}
