package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class PathValue {
    private final String value;

    public PathValue(String value) {
        this.value = value;
    }

    public Path resolvePath(@NotNull PathResolver pathResolver) {
        return pathResolver.resolvePath(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PathValue pathValue = (PathValue) o;

        return Objects.equals(value, pathValue.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}