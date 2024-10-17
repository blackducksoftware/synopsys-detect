package com.blackduck.integration.configuration.config.value;

import java.util.Optional;

import com.blackduck.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.blackduck.integration.configuration.parse.ValueParseException;

public abstract class PropertyValue<T> {
    public abstract Optional<T> getValue();

    public abstract Optional<PropertyResolutionInfo> getResolutionInfo();

    public abstract Optional<ValueParseException> getException();
}
