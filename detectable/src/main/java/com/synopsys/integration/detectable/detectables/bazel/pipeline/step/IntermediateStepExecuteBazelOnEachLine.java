package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;

public class IntermediateStepExecuteBazelOnEachLine implements IntermediateStep {
    private final BazelCommandExecutor bazelCommandExecutor;
    private final BazelVariableSubstitutor bazelVariableSubstitutor;
    private final List<String> bazelCommandArgs;
    private final boolean inputIsExpected;

    public IntermediateStepExecuteBazelOnEachLine(
        BazelCommandExecutor bazelCommandExecutor,
        BazelVariableSubstitutor bazelVariableSubstitutor,
        List<String> bazelCommandArgs,
        boolean inputIsExpected
    ) {
        this.bazelCommandExecutor = bazelCommandExecutor;
        this.bazelVariableSubstitutor = bazelVariableSubstitutor;
        this.bazelCommandArgs = bazelCommandArgs;
        this.inputIsExpected = inputIsExpected;
    }

    @Override
    public List<String> process(List<String> input) throws DetectableException, ExecutableFailedException {
        List<String> results = new ArrayList<>();
        if (inputIsExpected && input.isEmpty()) {
            return results;
        }
        List<String> adjustedInput;
        if (input.isEmpty()) {
            // Empty pipeline is normal when this is first step in pipeline, but we need one item to enter the loop below
            adjustedInput = new ArrayList<>(1);
            adjustedInput.add(null);
        } else {
            adjustedInput = input;
        }
        for (String inputItem : adjustedInput) {
            List<String> finalizedArgs = bazelVariableSubstitutor.substitute(bazelCommandArgs, inputItem);
            Optional<String> cmdOutput = bazelCommandExecutor.executeToString(finalizedArgs);
            cmdOutput.ifPresent(results::add);
        }
        return results;
    }
}
