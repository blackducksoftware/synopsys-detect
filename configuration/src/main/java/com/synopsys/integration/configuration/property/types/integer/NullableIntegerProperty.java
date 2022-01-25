package com.synopsys.integration.configuration.property.types.integer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;
import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullableIntegerProperty extends NullableAlikeProperty<Integer> {
    public NullableIntegerProperty(@NotNull String key) {
        super(key, new IntegerValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Integer";
    }
}
