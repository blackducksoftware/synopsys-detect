package com.blackduck.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.configuration.property.base.ValuedAlikeProperty;

public class PathProperty extends ValuedAlikeProperty<PathValue> {
    public PathProperty(@NotNull String key, PathValue defaultValue) {
        super(key, new PathValueParser(), defaultValue);
    }

    @Override
    public String describeType() {
        return "Path";
    }

    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }
}