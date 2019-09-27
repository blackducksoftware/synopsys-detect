package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.exception.IntegrationException;

public class StepExecutorEdit implements StepExecutor {
    @Override
    public boolean applies(final String stepType) {
        if ("edit".equalsIgnoreCase(stepType)) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> process(final Step step, final List<String> input) throws IntegrationException {
        final List<String> results = new ArrayList<>();
        for (final String inputItem : input) {
            results.add(inputItem.replaceAll(step.getArgs().get(0), step.getArgs().get(1)));
        }
        return results;
    }
}
