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
    public FilterableEnumList<T> parse(@NotNull String value) throws ValueParseException {
        return new FilterableEnumList<>(listValueParser.parse(value), enumClass);
    }
}
