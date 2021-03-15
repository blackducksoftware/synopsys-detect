/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.bool;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class BooleanListProperty extends ValuedListProperty<Boolean> {
    public BooleanListProperty(@NotNull final String key, @NotNull final List<Boolean> defaultValue) {
        super(key, new ListValueParser<Boolean>(new BooleanValueParser()), defaultValue);
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Nullable
    @Override
    public String describeType() {
        return "Boolean List";
    }
}
