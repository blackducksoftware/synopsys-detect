package com.blackduck.integration.configuration.property.types.integer;

import com.blackduck.integration.configuration.property.PropertyBuilder;
import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullableIntegerProperty extends NullableAlikeProperty<Integer> {
    public NullableIntegerProperty(@NotNull String key) {
        super(key, new IntegerValueParser());
    }

    public static PropertyBuilder<NullableIntegerProperty> newBuilder(@NotNull String key) {
        return new PropertyBuilder<NullableIntegerProperty>().setCreator(() -> new NullableIntegerProperty(key));
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Integer";
    }
}
