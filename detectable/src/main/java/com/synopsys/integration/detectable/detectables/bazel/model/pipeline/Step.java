package com.synopsys.integration.detectable.detectables.bazel.model.pipeline;

import java.util.List;

import com.synopsys.integration.util.Stringable;

public class Step extends Stringable {
    private final String type;
    private final List<String> args;

    public Step(final String type, final List<String> args) {
        this.type = type;
        this.args = args;
    }

    public String getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }
}
