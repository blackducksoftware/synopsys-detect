package com.synopsys.integration.configuration.property.types.string;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedAlikeListProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class StringListProperty extends ValuedAlikeListProperty<String> {
    public StringListProperty(@NotNull String key, List<String> defaultValue) {
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
