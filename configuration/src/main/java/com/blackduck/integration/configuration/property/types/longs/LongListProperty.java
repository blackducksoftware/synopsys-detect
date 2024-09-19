package com.blackduck.integration.configuration.property.types.longs;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.PropertyUtils;

public class LongListProperty extends ValuedAlikeListProperty<Long> {
    public LongListProperty(@NotNull String key, List<Long> defaultValue) {
        super(key, new ListValueParser<>(new LongValueParser()), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Long List";
    }
}
