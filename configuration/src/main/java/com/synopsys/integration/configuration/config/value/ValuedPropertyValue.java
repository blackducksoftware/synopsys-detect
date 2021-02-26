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
import org.springframework.util.Assert;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.parse.ValueParseException;

public class ValuedPropertyValue<T> extends ResolvedPropertyValue<T> {
    @NotNull
    private final T value;

    public ValuedPropertyValue(@NotNull final T value, @NotNull final PropertyResolutionInfo propertyResolutionInfo) {
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
