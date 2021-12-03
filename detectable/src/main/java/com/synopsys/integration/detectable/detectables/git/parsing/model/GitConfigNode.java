package com.synopsys.integration.detectable.detectables.git.parsing.model;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class GitConfigNode {
    private final String type;
    @Nullable
    private final String name;
    private final Map<String, String> properties;

    public GitConfigNode(String type, Map<String, String> properties) {
        this(type, null, properties);
    }

    public GitConfigNode(String type, @Nullable String name, Map<String, String> properties) {
        this.type = type;
        this.name = name;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getProperty(String propertyKey) {
        return Optional.ofNullable(properties.get(propertyKey));
    }
}
