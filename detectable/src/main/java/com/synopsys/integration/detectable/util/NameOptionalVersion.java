package com.synopsys.integration.detectable.util;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.util.Stringable;

public class NameOptionalVersion extends Stringable {
    private final String name;
    @Nullable
    private final String version;

    public NameOptionalVersion(String name, @Nullable String version) {
        this.name = name;
        this.version = version;
    }

    public NameOptionalVersion(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }
}
