package com.blackduck.integration.configuration.config.value;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import com.blackduck.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.blackduck.integration.configuration.parse.ValueParseException;

public class ExceptionPropertyValue<T> extends ResolvedPropertyValue<T> {
    @NotNull
    private final ValueParseException exception;

    public ExceptionPropertyValue(@NotNull ValueParseException exception, @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        super(propertyResolutionInfo);
        Assert.notNull(exception, "Exception cannot be null.");
        Assert.notNull(propertyResolutionInfo, "PropertyResolutionInfo cannot be null.");
        this.exception = exception;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.of(exception);
    }
}
