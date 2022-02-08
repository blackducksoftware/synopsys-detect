package com.synopsys.integration.configuration.property.deprecation;

public class DeprecatedValueUsage {
    private final String value;
    private final DeprecatedValueInfo info;

    public DeprecatedValueUsage(String value, DeprecatedValueInfo info) {
        this.value = value;
        this.info = info;
    }

    public String getValue() {
        return value;
    }

    public DeprecatedValueInfo getInfo() {
        return info;
    }
}
