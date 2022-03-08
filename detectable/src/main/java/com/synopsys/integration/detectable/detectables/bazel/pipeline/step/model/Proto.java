package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import java.util.List;

import com.synopsys.integration.util.Stringable;

// Model for output produced by: bazel cquery ... --output jsonproto
public class Proto extends Stringable {
    private List<ResultItem> results;

    public List<ResultItem> getResults() {
        return results;
    }
}
