package com.blackduck.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;

public class LongValueParser extends ValueParser<Long> {
    @NotNull
    @Override
    public Long parse(@NotNull String value) throws ValueParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValueParseException(value, "long", e);
        }
    }
}
