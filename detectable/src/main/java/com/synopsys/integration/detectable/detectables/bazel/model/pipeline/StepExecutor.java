package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.util.List;

import com.synopsys.integration.exception.IntegrationException;

public interface StepExecutor {

    boolean applies(final String stepType);
    List<String> process(final Step step, final List<String> input) throws IntegrationException;
}
