package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class StringProperty extends ValuedAlikeProperty<String> {
    public StringProperty(@NotNull String key, String defaultValue) {
        super(key, new StringValueParser(), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue();
    }

    @Nullable
    @Override
    public String describeType() {
        return "String";
    }
}
