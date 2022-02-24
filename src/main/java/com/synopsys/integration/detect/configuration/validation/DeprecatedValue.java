package com.synopsys.integration.detect.configuration.validation;

public class DeprecatedValue {
    private final String valueDescription;
    private final String message;

    public DeprecatedValue(String valueDescription, String message) {
        this.valueDescription = valueDescription;
        this.message = message;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public String getMessage() {
        return message;
    }
}
