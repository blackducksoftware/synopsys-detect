/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;

public abstract class ResolvedPropertyValue<T> extends PropertyValue<T> {
    @NotNull
    final PropertyResolutionInfo propertyResolutionInfo;

    protected ResolvedPropertyValue(@NotNull final PropertyResolutionInfo propertyResolutionInfo) {
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }
}
