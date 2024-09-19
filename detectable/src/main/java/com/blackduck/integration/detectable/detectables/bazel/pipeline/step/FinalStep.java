package com.blackduck.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface FinalStep {
    List<Dependency> finish(List<String> input) throws DetectableException;
}
