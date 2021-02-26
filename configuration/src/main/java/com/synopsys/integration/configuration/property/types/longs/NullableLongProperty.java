/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.longs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullableLongProperty extends NullableProperty<Long> {
    public NullableLongProperty(@NotNull final String key) {
        super(key, new LongValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Long";
    }
}
