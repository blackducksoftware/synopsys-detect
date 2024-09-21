package com.blackduck.integration.detectable.detectables.bazel.pipeline.step.model;

import com.blackduck.integration.util.Stringable;

public class ResultItem extends Stringable {
    private Target target;

    public Target getTarget() {
        return target;
    }
}
