package com.blackduck.integration.configuration.property.types.bool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;

public class NullableBooleanProperty extends NullableAlikeProperty<Boolean> {
    public NullableBooleanProperty(@NotNull String key) {
        super(key, new BooleanValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Boolean";
    }
}
