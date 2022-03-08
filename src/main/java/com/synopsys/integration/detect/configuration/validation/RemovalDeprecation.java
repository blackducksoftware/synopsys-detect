package com.synopsys.integration.detect.configuration.validation;

public class RemovalDeprecation {
    private final String propertyKey;
    private final String deprecationText;

    public RemovalDeprecation(String propertyKey, String deprecationText) {
        this.propertyKey = propertyKey;
        this.deprecationText = deprecationText;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public String getDeprecationText() {
        return deprecationText;
    }
}
