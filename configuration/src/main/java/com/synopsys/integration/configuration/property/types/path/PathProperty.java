package com.synopsys.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class PathProperty extends ValuedProperty<PathValue> {
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