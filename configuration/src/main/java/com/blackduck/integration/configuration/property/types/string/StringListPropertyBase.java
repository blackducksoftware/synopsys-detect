package com.blackduck.integration.configuration.property.types.string;

import java.util.List;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.PropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StringListPropertyBase extends ValuedAlikeListProperty<String> {
    public StringListPropertyBase(@NotNull String key, @NotNull List<String> defaultValue) {
        super(key, new ListValueParser<>(new StringValueParser()), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Nullable
    @Override
    public String describeType() {
        return "String List";
    }
}
