package com.blackduck.integration.configuration.property.types.string;

import java.util.List;

import com.blackduck.integration.configuration.property.PropertyBuilder;
import org.jetbrains.annotations.NotNull;

public class StringListProperty extends StringListPropertyBase {
    public StringListProperty(@NotNull String key, @NotNull List<String> defaultValue) {
        super(key, defaultValue);
    }

    public static PropertyBuilder<StringListProperty> newBuilder(@NotNull String key, @NotNull List<String> defaultValue) {
        return new PropertyBuilder<StringListProperty>().setCreator(() -> new StringListProperty(key, defaultValue));
    }
}
