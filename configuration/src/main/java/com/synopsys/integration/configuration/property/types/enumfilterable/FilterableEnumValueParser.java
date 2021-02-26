/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumfilterable;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.types.enums.EnumValueParser;

public class FilterableEnumValueParser<T extends Enum<T>> extends ValueParser<FilterableEnumValue<T>> {
    private EnumValueParser<T> enumValueParser;

    public FilterableEnumValueParser(@NotNull Class<T> enumClass) {
        this.enumValueParser = new EnumValueParser<>(enumClass);
    }

    @NotNull
    @Override
    public FilterableEnumValue<T> parse(@NotNull final String value) throws ValueParseException {
        String trimmedValue = value.toLowerCase().trim();
        if ("none".equals(trimmedValue)) {
            return FilterableEnumValue.noneValue();
        } else if ("all".equals(trimmedValue)) {
            return FilterableEnumValue.allValue();
        } else {
            return FilterableEnumValue.value(enumValueParser.parse(value));
        }
    }
}
