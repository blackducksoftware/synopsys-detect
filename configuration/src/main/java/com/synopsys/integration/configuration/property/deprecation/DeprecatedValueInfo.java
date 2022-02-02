package com.synopsys.integration.configuration.property.deprecation;

public class DeprecatedValueInfo {
    private final String valueDescription;
    private final String reason;

    public DeprecatedValueInfo(String value, String reason) {
        this.valueDescription = value;
        this.reason = reason;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public String getReason() {
        return reason;
    }
}
