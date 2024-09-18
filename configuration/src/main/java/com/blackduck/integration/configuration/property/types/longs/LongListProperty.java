package com.blackduck.integration.configuration.property.types.longs;

import java.util.List;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.property.base.ValuedAlikeListProperty;
import com.blackduck.integration.configuration.util.PropertyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
