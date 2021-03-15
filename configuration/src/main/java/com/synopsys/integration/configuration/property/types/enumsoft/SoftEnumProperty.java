/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumsoft;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class SoftEnumProperty<E extends Enum<E>> extends ValuedProperty<SoftEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public SoftEnumProperty(@NotNull final String key, SoftEnumValue<E> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new SoftEnumValueParser<>(enumClass), defaultValue);
        this.enumClass = enumClass;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNames(enumClass);
    }

    @Override
    public boolean isOnlyExampleValues() {
        return false;
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName();
    }
}