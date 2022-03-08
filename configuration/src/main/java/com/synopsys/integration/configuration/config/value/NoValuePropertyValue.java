package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.parse.ValueParseException;

public class NoValuePropertyValue<T> extends PropertyValue<T> {
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
        return Optional.empty();
    }
}
