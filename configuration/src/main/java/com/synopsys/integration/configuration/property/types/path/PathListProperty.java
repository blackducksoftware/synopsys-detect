/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.path;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;

public class PathListProperty extends ValuedListProperty<PathValue> {
    public PathListProperty(@NotNull final String key,
        final List<PathValue> defaultValue) {
        super(key, new ListValueParser<>(new PathValueParser()), defaultValue);
    }

    @Override
    public String describeType() {
        return "Path List";
    }
}
