package com.blackduck.integration.detectable.detectables.bazel.pipeline.step.model;

import java.util.List;

import com.blackduck.integration.util.Stringable;

public class Results extends Stringable {
    private List<ResultItem> resultItems;

    public List<ResultItem> getResultItems() {
        return resultItems;
    }
}
