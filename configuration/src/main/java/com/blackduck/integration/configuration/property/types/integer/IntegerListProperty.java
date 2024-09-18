package com.blackduck.integration.configuration.property.types.integer;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.PropertyUtils;

public class IntegerListProperty extends ValuedAlikeListProperty<Integer> {
    public IntegerListProperty(@NotNull String key, @NotNull List<Integer> defaultValue) {
        super(key, new ListValueParser<>(new IntegerValueParser()), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Integer List";
    }
}
