package com.synopsys.integration.detectable.detectables.bazel.pipeline.step.model;

import com.synopsys.integration.util.Stringable;

public class AttributeItem extends Stringable {
    private String name;
    private String stringValue;

    public String getName() {
        return name;
    }

    public String getStringValue() {
        return stringValue;
    }
}
