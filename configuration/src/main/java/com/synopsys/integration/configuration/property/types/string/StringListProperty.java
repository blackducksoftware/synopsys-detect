package com.synopsys.integration.configuration.property.types.string;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.PropertyBuilder;

public class StringListProperty extends StringListPropertyBase {
    public StringListProperty(@NotNull String key, @NotNull List<String> defaultValue) {
        super(key, defaultValue);
    }

    public static PropertyBuilder<StringListProperty> newBuilder(@NotNull String key, @NotNull List<String> defaultValue) {
        return new PropertyBuilder<StringListProperty>().setCreator(() -> new StringListProperty(key, defaultValue));
    }
}
