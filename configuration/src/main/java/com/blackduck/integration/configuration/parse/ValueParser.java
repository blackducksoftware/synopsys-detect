package com.blackduck.integration.configuration.parse;

import org.jetbrains.annotations.NotNull;

public abstract class ValueParser<T> {
    @NotNull
    public abstract T parse(@NotNull String value) throws ValueParseException;
}
