package com.blackduck.integration.configuration.config.value;

import java.util.Optional;

import com.blackduck.integration.configuration.config.resolution.PropertyResolutionInfo;
import org.jetbrains.annotations.NotNull;

public abstract class ResolvedPropertyValue<T> extends PropertyValue<T> {
    @NotNull
    final PropertyResolutionInfo propertyResolutionInfo;

    protected ResolvedPropertyValue(@NotNull PropertyResolutionInfo propertyResolutionInfo) {
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }
}
