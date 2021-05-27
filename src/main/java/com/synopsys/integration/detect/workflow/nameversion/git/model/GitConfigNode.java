/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.git.model;

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
