package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;

public class LongProperty extends ValuedAlikeProperty<Long> {
    public LongProperty(@NotNull String key, @NotNull Long defaultValue) {
        super(key, new LongValueParser(), defaultValue);
    }

    public static PropertyBuilder<LongProperty> newBuilder(@NotNull String key, @NotNull Long defaultValue) {
        return new PropertyBuilder<LongProperty>().setCreator(() -> new LongProperty(key, defaultValue));
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Nullable
    @Override
    public String describeType() {
        return "Long";
    }
}
