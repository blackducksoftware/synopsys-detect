package com.synopsys.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullablePathProperty extends NullableProperty<PathValue> {
    public NullablePathProperty(@NotNull String key) {
        super(key, new PathValueParser());
    }

    @Override
    public String describeType() {
        return "Optional Path";
    }
}
