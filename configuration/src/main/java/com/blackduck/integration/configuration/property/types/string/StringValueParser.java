package com.blackduck.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.configuration.parse.ValueParser;

class StringValueParser extends ValueParser<String> {

    @NotNull
    @Override
    public String parse(@NotNull String value) {
        return value;
    }
}
