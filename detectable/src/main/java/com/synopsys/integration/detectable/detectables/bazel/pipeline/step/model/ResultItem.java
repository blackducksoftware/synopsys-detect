package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import com.synopsys.integration.util.Stringable;

public class ResultItem extends Stringable {
    private Target target;

    public Target getTarget() {
        return target;
    }
}
