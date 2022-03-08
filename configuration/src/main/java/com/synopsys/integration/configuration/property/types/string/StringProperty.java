package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;

public class StringProperty extends ValuedAlikeProperty<String> {
    public StringProperty(@NotNull String key, @NotNull String defaultValue) {
        super(key, new StringValueParser(), defaultValue);
    }

    public static PropertyBuilder<StringProperty> newBuilder(@NotNull String key, @NotNull String defaultValue) {
        return new PropertyBuilder<StringProperty>().setCreator(() -> new StringProperty(key, defaultValue));
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue();
    }

    @Nullable
    @Override
    public String describeType() {
        return "String";
    }
}
