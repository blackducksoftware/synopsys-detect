package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullableStringProperty extends NullableProperty<String> {
    public NullableStringProperty(@NotNull String key) {
        super(key, new StringValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional String";
    }
}
