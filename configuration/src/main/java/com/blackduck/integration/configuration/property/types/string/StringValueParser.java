package com.blackduck.integration.configuration.property.types.string;

import com.blackduck.integration.configuration.parse.ValueParser;
import org.jetbrains.annotations.NotNull;

class StringValueParser extends ValueParser<String> {

    @NotNull
    @Override
    public String parse(@NotNull String value) {
        return value;
    }
}
