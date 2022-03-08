package com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse;

import java.util.Optional;

public class GemspecDependency {
    private final String name;
    private final String version;
    private final GemspecDependencyType gemspecDependencyType;

    public GemspecDependency(String name, GemspecDependencyType gemspecDependencyType) {
        this.name = name;
        this.version = null;
        this.gemspecDependencyType = gemspecDependencyType;
    }

    public GemspecDependency(String name, String version, GemspecDependencyType gemspecDependencyType) {
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
