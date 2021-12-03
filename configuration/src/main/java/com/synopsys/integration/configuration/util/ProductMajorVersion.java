package com.synopsys.integration.configuration.util;

import org.jetbrains.annotations.NotNull;

public class ProductMajorVersion {
    private final Integer intValue;

    public ProductMajorVersion(Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    @NotNull
    public String getDisplayValue() {
        return String.format("%s.0.0", getIntValue());
    }
}