package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import java.util.List;

import com.synopsys.integration.util.Stringable;

public class Rule extends Stringable {
    private List<AttributeItem> attribute;

    public List<AttributeItem> getAttribute() {
        return attribute;
    }
}
