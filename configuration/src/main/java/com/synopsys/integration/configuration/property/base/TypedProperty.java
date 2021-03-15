/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.Property;

/**
 * A property that is associated with a single type.
 *
 * It can't itself be retrieved from a Configuration but provides a shared parent class
 * for Nullable and Valued properties, both of which need a property with a single type.
 * @param <T> the type this property returns when it is retrieved from a Configuration.
 */
public abstract class TypedProperty<T> extends Property {
    @NotNull
    private final ValueParser<T> valueParser;

    public TypedProperty(@NotNull final String key, @NotNull ValueParser<T> valueParser) {
        super(key);
        this.valueParser = valueParser;
    }

    @NotNull
    public ValueParser<T> getValueParser() {
        return valueParser;
    }
}
