package com.blackduck.integration.configuration.property.types.path;

import com.blackduck.integration.configuration.property.PropertyBuilder;
import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import org.jetbrains.annotations.NotNull;

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
