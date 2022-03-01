package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;

public class NullableStringProperty extends NullableAlikeProperty<String> {
    public NullableStringProperty(@NotNull String key) {
        super(key, new StringValueParser());
    }

    public static PropertyBuilder<NullableStringProperty> newBuilder(@NotNull String key) {
        return new PropertyBuilder<NullableStringProperty>().setCreator(() -> new NullableStringProperty(key));
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional String";
    }
}
