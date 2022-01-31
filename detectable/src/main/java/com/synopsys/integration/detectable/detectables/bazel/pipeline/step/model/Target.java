package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import com.synopsys.integration.util.Stringable;

public class Target extends Stringable {
    private String type;
    private Rule rule;

    public String getType() {
        return type;
    }

    public Rule getRule() {
        return rule;
    }
}
