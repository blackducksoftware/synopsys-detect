/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumfilterable;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

public class FilterableEnumListParser<T extends Enum<T>> extends ValueParser<FilterableEnumList<T>> {
    private ListValueParser<FilterableEnumValue<T>> listValueParser;
    private final Class<T> enumClass;

    public FilterableEnumListParser(@NotNull ListValueParser<FilterableEnumValue<T>> listValueParser, @NotNull Class<T> enumClass) {
        this.listValueParser = listValueParser;
        this.enumClass = enumClass;
    }

    public FilterableEnumListParser(@NotNull Class<T> enumClass) {
        this(new ListValueParser<>(new FilterableEnumValueParser<>(enumClass)), enumClass);
    }

    @NotNull
    @Override
    public FilterableEnumList<T> parse(@NotNull final String value) throws ValueParseException {
        return new FilterableEnumList<T>(listValueParser.parse(value), enumClass);
    }
}
