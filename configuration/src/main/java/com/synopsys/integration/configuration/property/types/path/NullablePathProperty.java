/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.base.NullableProperty;

public class NullablePathProperty extends NullableProperty<PathValue> {
    public NullablePathProperty(@NotNull final String key) {
        super(key, new PathValueParser());
    }

    @Override
    public String describeType() {
        return "Optional Path";
    }
}
