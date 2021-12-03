package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;

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
