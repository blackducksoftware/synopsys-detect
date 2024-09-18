package com.blackduck.integration.configuration.property.types.string;

import com.blackduck.integration.configuration.property.PropertyBuilder;
import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
