package com.blackduck.integration.configuration.property.types.bool;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.PropertyUtils;

public class BooleanListProperty extends ValuedAlikeListProperty<Boolean> {
    public BooleanListProperty(@NotNull String key, @NotNull List<Boolean> defaultValue) {
        super(key, new ListValueParser<>(new BooleanValueParser()), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Boolean List";
    }
}
