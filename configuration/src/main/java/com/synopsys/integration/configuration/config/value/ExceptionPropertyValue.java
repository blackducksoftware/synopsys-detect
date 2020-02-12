package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.parse.ValueParseException;

public class ExceptionPropertyValue<T> extends PropertyValue<T> {
    @NotNull
    private ValueParseException exception;

    public ExceptionPropertyValue(final ValueParseException exception) {
        Assert.notNull(exception, "Exception cannot be null.");
        this.exception = exception;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.empty();
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.of(exception);
    }
}
