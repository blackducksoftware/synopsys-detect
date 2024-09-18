package com.blackduck.integration.configuration.property.types.longs;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
