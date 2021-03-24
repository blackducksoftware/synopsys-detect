/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.integer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullableIntegerProperty extends NullableProperty<Integer> {
    public NullableIntegerProperty(@NotNull final String key) {
        super(key, new IntegerValueParser());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional Integer";
    }
}
