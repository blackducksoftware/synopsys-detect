package com.blackduck.integration.configuration.property.types.path;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.PropertyBuilder;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;

public class PathListProperty extends ValuedAlikeListProperty<PathValue> {
    public PathListProperty(@NotNull String key, @NotNull List<PathValue> defaultValue) {
        super(key, new ListValueParser<>(new PathValueParser()), defaultValue);
    }

    public static PropertyBuilder<PathListProperty> newBuilder(@NotNull String key, @NotNull List<PathValue> defaultValue) {
        return new PropertyBuilder<PathListProperty>().setCreator(() -> new PathListProperty(key, defaultValue));
    }

    @Override
    public String describeType() {
        return "Path List";
    }
}
