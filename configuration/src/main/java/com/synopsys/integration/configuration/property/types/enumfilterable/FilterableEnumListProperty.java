/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumfilterable;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class FilterableEnumListProperty<E extends Enum<E>> extends ValuedProperty<FilterableEnumList<E>> {
    @NotNull
    private final Class<E> enumClass;

    public FilterableEnumListProperty(@NotNull String key, @NotNull List<FilterableEnumValue<E>> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new FilterableEnumListParser<>(enumClass), new FilterableEnumList<>(defaultValue, enumClass));
        this.enumClass = enumClass;
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @Override
    public boolean isCommaSeparated() {
        return true;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNamesAnd(enumClass, "ALL", "NONE");
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue().toFilterableValues());
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName() + " List";
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }
}
