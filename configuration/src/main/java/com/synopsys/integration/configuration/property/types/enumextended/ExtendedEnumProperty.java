/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class ExtendedEnumProperty<E extends Enum<E>, B extends Enum<B>> extends ValuedProperty<ExtendedEnumValue<E, B>> {
    private final List<String> allOptions;
    private final Class<B> bClass;

    public ExtendedEnumProperty(@NotNull final String key, @NotNull final ExtendedEnumValue<E, B> defaultValue, @NotNull final Class<E> eClass, @NotNull final Class<B> bClass) {
        super(key, new ExtendedEnumValueParser<>(eClass, bClass), defaultValue);
        allOptions = new ArrayList<>();
        allOptions.addAll(EnumPropertyUtils.getEnumNames(eClass));
        allOptions.addAll(EnumPropertyUtils.getEnumNames(bClass));
        this.bClass = bClass;
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return allOptions;
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return getDefaultValue().toString();
    }

    @Nullable
    @Override
    public String describeType() {
        return bClass.getSimpleName();
    }
}
