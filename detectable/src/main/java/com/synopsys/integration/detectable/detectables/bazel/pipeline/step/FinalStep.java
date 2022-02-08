package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.List;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface FinalStep {
    List<Dependency> finish(List<String> input) throws DetectableException;
}
