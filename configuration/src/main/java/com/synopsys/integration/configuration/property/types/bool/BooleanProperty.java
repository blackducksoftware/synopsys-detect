package com.synopsys.integration.configuration.property.types.bool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;

public class BooleanProperty extends ValuedAlikeProperty<Boolean> {
    public BooleanProperty(@NotNull String key, @NotNull Boolean defaultValue) {
        super(key, new BooleanValueParser(), defaultValue);
    }

    public static PropertyBuilder<BooleanProperty> newBuilder(@NotNull String key, @NotNull Boolean defaultValue) {
        return new PropertyBuilder<BooleanProperty>().setCreator(() -> new BooleanProperty(key, defaultValue));
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
