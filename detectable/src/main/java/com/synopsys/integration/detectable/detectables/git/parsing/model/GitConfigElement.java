package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.Map;
import java.util.Optional;

public class GitConfigElement {
    private final String elementType;
    private final String name;
    private final Map<String, String> properties;

    public GitConfigElement(final String elementType, final String name, final Map<String, String> properties) {
        this.elementType = elementType;
        this.name = name;
        this.properties = properties;
    }

    public String getElementType() {
        return elementType;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public String getProperty(final String propertyKey) {
        return properties.get(propertyKey);
    }

    public boolean containsKey(final String key) {
        return properties.containsKey(key);
    }
}
