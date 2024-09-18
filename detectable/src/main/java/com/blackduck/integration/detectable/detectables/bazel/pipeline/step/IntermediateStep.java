package com.blackduck.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.ExecutableFailedException;

public interface IntermediateStep {

    List<String> process(List<String> input) throws DetectableException, ExecutableFailedException;
}
