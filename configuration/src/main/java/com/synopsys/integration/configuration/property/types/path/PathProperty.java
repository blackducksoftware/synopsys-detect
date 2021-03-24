/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.path;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.base.ValuedProperty;

public class PathProperty extends ValuedProperty<PathValue> {
    public PathProperty(@NotNull final String key, final PathValue defaultValue) {
        super(key, new PathValueParser(), defaultValue);
    }

    @Override
    public String describeType() {
        return "Path";
    }

    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }
}