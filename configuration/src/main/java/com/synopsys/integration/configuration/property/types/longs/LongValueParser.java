package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;

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
