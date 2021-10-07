/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property.types.enumfilterable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterableEnumList<T extends Enum<T>> {
    private final List<FilterableEnumValue<T>> providedValues;
    private final Class<T> enumClass;

    public FilterableEnumList(List<FilterableEnumValue<T>> providedValues, Class<T> enumClass) {
        this.providedValues = providedValues;
        this.enumClass = enumClass;
    }

    public boolean containsNone() {
        return FilterableEnumUtils.containsNone(providedValues);
    }

    public boolean containsAll() {
        return FilterableEnumUtils.containsAll(providedValues);
    }

    public boolean containsValue(T value) {
        return FilterableEnumUtils.containsValue(providedValues, value);
    }

    public boolean isEmpty() {
        return providedValues.isEmpty();
    }

    public List<T> toPresentValues() {
        return FilterableEnumUtils.toPresentValues(providedValues);
    }

    public List<T> representedValues() {
        return FilterableEnumUtils.representedValues(providedValues, enumClass);
    }

    public Set<T> representedValueSet() {
        return new HashSet<>(representedValues());
    }

    public List<FilterableEnumValue<T>> toFilterableValues() {
        return providedValues;
    }
}
