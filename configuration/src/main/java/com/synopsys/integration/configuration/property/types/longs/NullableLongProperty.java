package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullableLongProperty extends NullableProperty<Long> {
    public NullableLongProperty(@NotNull String key) {
        super(key, new LongValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Long";
    }
}
