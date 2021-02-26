/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.util.PropertyUtils;

/**
 * This is a property with a key and with a default value, it will always have a value.
 */
// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
public abstract class ValuedListProperty<T> extends ValuedProperty<List<T>> {
    public ValuedListProperty(@NotNull final String key, @NotNull final ValueParser<List<T>> valueParser, final List<T> defaultValue) {
        super(key, valueParser, defaultValue);
    }

    @Override
    public boolean isCommaSeparated() {
        return true;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }
}