package com.synopsys.integration.detect.configuration.validation;

import java.util.List;

import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;

public class ValueDeprecation {
    private final String propertyKey;
    private final List<DeprecatedValueUsage> deprecatedValueUsages;

    public ValueDeprecation(String propertyKey, List<DeprecatedValueUsage> deprecatedValueUsages) {
        this.propertyKey = propertyKey;
        this.deprecatedValueUsages = deprecatedValueUsages;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public List<DeprecatedValueUsage> getDeprecatedValueUsages() {
        return deprecatedValueUsages;
    }
}