package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorSplit implements StepExecutor {
    @Override
    public boolean applies(final String stepType) {
        return "splitEach".equalsIgnoreCase(stepType);
    }

    @Override
    public List<String> process(final Step step, final List<String> input) throws IntegrationException {
        final List<String> results = new ArrayList<>();
        for (final String inputItem : input) {
            final String[] splitLines = inputItem.split(step.getArgs().get(0));
            results.addAll(Arrays.asList(splitLines));
        }
        return results;
    }
}
