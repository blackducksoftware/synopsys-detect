/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumList;

public class ExcludeIncludeEnumFilter<T extends Enum<T>> {
    private final FilterableEnumList<T> excluded;
    private final FilterableEnumList<T> included;

    public ExcludeIncludeEnumFilter(FilterableEnumList<T> excluded, FilterableEnumList<T> included) {
        this.excluded = excluded;
        this.included = included;
    }

    private boolean willExclude(T value) {
        if (excluded.containsAll()) {
            return true;
        } else if (excluded.containsNone()) {
            return false;
        } else {
            return excluded.containsValue(value);
        }
    }

    private boolean willInclude(T value) {
        if (included.isEmpty()) {
            return true;
        } else if (included.containsAll()) {
            return true;
        } else if (included.containsNone()) {
            return false;
        } else {
            return included.containsValue(value);
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