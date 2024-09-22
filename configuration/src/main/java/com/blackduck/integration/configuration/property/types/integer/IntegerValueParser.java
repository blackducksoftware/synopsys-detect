package com.blackduck.integration.configuration.property.types.integer;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;

class IntegerValueParser extends ValueParser<Integer> {
    @NotNull
    @Override
    public Integer parse(@NotNull String value) throws ValueParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ValueParseException(value, "integer", e);
        }
    }
}
