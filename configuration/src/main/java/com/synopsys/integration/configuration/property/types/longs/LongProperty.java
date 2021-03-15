/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class LongProperty extends ValuedProperty<Long> {
    public LongProperty(@NotNull final String key, Long defaultValue) {
        super(key, new LongValueParser(), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Nullable
    @Override
    public String describeType() {
        return "Long";
    }
}
