package com.synopsys.integration.configuration.property;

import java.util.List;
import java.util.stream.Collectors;

public class Properties {
    private final List<Property> properties;

    public Properties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getPropertyKeys() {
        return properties.stream()
            .map(Property::getKey)
            .collect(Collectors.toList());
    }

    public List<String> getSortedPropertyKeys() {
        return getPropertyKeys().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<Property> getProperties() {
        return properties;
    }
}
