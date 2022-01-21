package com.synopsys.integration.detectable.detectables.bazel.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelCommandExecutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.BazelVariableSubstitutor;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.FinalStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStep;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepExecuteBazelOnEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepReplaceInEach;
import com.synopsys.integration.detectable.detectables.bazel.pipeline.step.IntermediateStepSplitEach;

public class PipelineBuilder {
    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;
    private final List<IntermediateStep> intermediateSteps = new ArrayList<>();
    private FinalStep finalStep;

    public PipelineBuilder(final BazelCommandExecutor bazelCommandExecutor, BazelVariableSubstitutor bazelVariableSubstitutor) {
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
    }

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

    //TODO: Add helper step methods.
    public PipelineBuilder replaceInEachLine(String from, String to) {
        return addIntermediateStep(new IntermediateStepReplaceInEach(from, to));
    }

    public PipelineBuilder splitEachLine(String splitOn) {
        return addIntermediateStep(new IntermediateStepSplitEach(splitOn));
    }

    public PipelineBuilder executeBazelOnEachLine(List<String> bazelArguments, boolean inputIsExpected) {
        return addIntermediateStep(new IntermediateStepExecuteBazelOnEach(bazelCommandExecutor, bazelVariableSubstitutor, bazelArguments, inputIsExpected));
    }


}
