/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

import java.util.Optional;

public class GemspecDependency {
    private final String name;
    private final String version;
    private final GemspecDependencyType gemspecDependencyType;

    public GemspecDependency(final String name, final GemspecDependencyType gemspecDependencyType) {
        this.name = name;
        this.version = null;
        this.gemspecDependencyType = gemspecDependencyType;
    }

    public GemspecDependency(final String name, final String version, final GemspecDependencyType gemspecDependencyType) {
        this.name = name;
        this.version = version;
        this.gemspecDependencyType = gemspecDependencyType;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public GemspecDependencyType getGemspecDependencyType() {
        return gemspecDependencyType;
    }
}
