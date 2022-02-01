package com.synopsys.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.PropertyBuilder;
import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;

public class NullablePathProperty extends NullableAlikeProperty<PathValue> {
    public NullablePathProperty(@NotNull String key) {
        super(key, new PathValueParser());
    }

    public static PropertyBuilder<NullablePathProperty> newBuilder(@NotNull String key) {
        return new PropertyBuilder<NullablePathProperty>().setCreator(() -> new NullablePathProperty(key));
    }

    @Override
    public String describeType() {
        return "Optional Path";
    }
}
