package com.blackduck.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;

public class NullableLongProperty extends NullableAlikeProperty<Long> {
    public NullableLongProperty(@NotNull String key) {
        super(key, new LongValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Long";
    }
}
