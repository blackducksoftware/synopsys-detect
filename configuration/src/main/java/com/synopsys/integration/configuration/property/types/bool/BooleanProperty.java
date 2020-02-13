package com.synopsys.integration.configuration.property.types.bool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class BooleanProperty extends ValuedProperty<Boolean> {
    public BooleanProperty(@NotNull final String key, @NotNull final Boolean defaultValue) {
        super(key, new BooleanValueParser(), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Nullable
    @Override
    public String describeType() {
        return "Boolean";
    }
}
