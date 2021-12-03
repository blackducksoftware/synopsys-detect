package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import com.synopsys.integration.exception.IntegrationException;

public interface IntermediateStep {

    List<String> process(List<String> input) throws IntegrationException;
}
