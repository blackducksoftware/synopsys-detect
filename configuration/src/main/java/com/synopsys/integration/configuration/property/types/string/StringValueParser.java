package com.synopsys.integration.configuration.property.types.string;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

class StringValueParser extends ValueParser<String> {

    @NotNull
    @Override
    public String parse(@NotNull String value) {
        return value;
    }
}
