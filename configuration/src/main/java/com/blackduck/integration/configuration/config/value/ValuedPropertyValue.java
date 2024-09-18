package com.blackduck.integration.configuration.config.value;

import java.util.Optional;

import com.blackduck.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.blackduck.integration.configuration.parse.ValueParseException;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class ValuedPropertyValue<T> extends ResolvedPropertyValue<T> {
    @NotNull
    private final T value;

    public ValuedPropertyValue(@NotNull T value, @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        super(propertyResolutionInfo);
        Assert.notNull(value, "Value cannot be null.");
        Assert.notNull(propertyResolutionInfo, "PropertyResolutionInfo cannot be null.");
        this.value = value;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.of(value);
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.empty();
    }
}
