/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import java.util.List;

import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;

public class ExcludeIncludeEnumFilter<T extends Enum<T>> {
    private final List<FilterableEnumValue<T>> excluded;
    private final List<FilterableEnumValue<T>> included;

    public ExcludeIncludeEnumFilter(List<FilterableEnumValue<T>> excluded, List<FilterableEnumValue<T>> included) {
        this.excluded = excluded;
        this.included = included;
    }

    private boolean willExclude(T value) {
        if (FilterableEnumUtils.containsAll(excluded)) {
            return true;
        } else if (FilterableEnumUtils.containsNone(excluded)) {
            return false;
        } else {
            return FilterableEnumUtils.containsValue(excluded, value);
        }
    }

    private boolean willInclude(T value) {
        if (included.isEmpty()) {
            return true;
        } else if (FilterableEnumUtils.containsAll(included)) {
            return true;
        } else if (FilterableEnumUtils.containsNone(included)) {
            return false;
        } else {
            return FilterableEnumUtils.containsValue(included, value);
        }
    }

    public boolean shouldInclude(T value) {
        if (willExclude(value)) {
            return false;
        } else {
            return willInclude(value);
        }
    }
}