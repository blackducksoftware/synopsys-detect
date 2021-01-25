package com.synopsys.integration.configuration.help;

import com.synopsys.integration.configuration.property.Property;

public class PropertyInfo {
    private String key;
    private String value;
    private Property property;

    public PropertyInfo(final String key, final String value, final Property property) {
        this.key = key;
        this.value = value;
        this.property = property;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Property getProperty() {
        return property;
    }
}
