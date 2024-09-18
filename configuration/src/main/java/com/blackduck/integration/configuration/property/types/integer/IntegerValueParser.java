package com.blackduck.integration.configuration.property.types.integer;

import com.blackduck.integration.configuration.parse.ValueParseException;
import com.blackduck.integration.configuration.parse.ValueParser;
import org.jetbrains.annotations.NotNull;

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
