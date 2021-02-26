/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.path;

import java.nio.file.Path;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PathValue {
    private final String value;

    public PathValue(final String value) {
        this.value = value;
    }

    public Path resolvePath(@NotNull final PathResolver pathResolver) {
        return pathResolver.resolvePath(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PathValue pathValue = (PathValue) o;

        return Objects.equals(value, pathValue.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}