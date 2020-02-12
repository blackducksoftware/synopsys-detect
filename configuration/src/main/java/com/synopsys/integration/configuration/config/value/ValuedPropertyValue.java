package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.parse.ValueParseException;

public class ValuedPropertyValue<T> extends PropertyValue<T> {
    @NotNull
    private T value;
    @NotNull
    private PropertyResolutionInfo propertyResolutionInfo;

    public ValuedPropertyValue(@NotNull T value, @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        Assert.notNull(value, "Value cannot be null.");
        Assert.notNull(propertyResolutionInfo, "PropertyResolutionInfo cannot be null.");
        this.value = value;
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.of(value);
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.empty();
    }
}
