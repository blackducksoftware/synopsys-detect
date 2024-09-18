package com.blackduck.integration.configuration.property.types.string;

import java.util.Collections;
import java.util.List;

import com.blackduck.integration.configuration.property.PropertyBuilder;
import org.jetbrains.annotations.NotNull;

public class CaseSensitiveStringListProperty extends StringListPropertyBase {
    public CaseSensitiveStringListProperty(@NotNull String key, @NotNull List<String> defaultValue) {
        super(key, defaultValue);
    }

    public static PropertyBuilder<CaseSensitiveStringListProperty> newBuilder(@NotNull String key, @NotNull List<String> defaultValue) {
        return new PropertyBuilder<CaseSensitiveStringListProperty>().setCreator(() -> new CaseSensitiveStringListProperty(key, defaultValue));
    }

    public static PropertyBuilder<CaseSensitiveStringListProperty> newBuilder(@NotNull String key) {
        return newBuilder(key, Collections.emptyList());
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
