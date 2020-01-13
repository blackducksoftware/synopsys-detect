package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.property.types.enumfilterable.All
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue
import com.synopsys.integration.configuration.property.types.enumfilterable.None
import com.synopsys.integration.configuration.property.types.enumfilterable.Value


class ExcludeIncludeEnumFilter<T>(val excluded: List<FilterableEnumValue<T>>, val included: List<FilterableEnumValue<T>>) {
    fun containsAll(list: List<FilterableEnumValue<T>>): Boolean {
        return list.any {
            when (it) {
                is All -> true
                else -> false
            }
        }
    }

    fun containsNone(list: List<FilterableEnumValue<T>>): Boolean {
        return list.any {
            when (it) {
                is None -> true
                else -> false
            }
        }
    }

    fun containsElement(list: List<FilterableEnumValue<T>>, value: T): Boolean {
        return list.any {
            when (it) {
                is Value -> it.value == value
                else -> false
            }
        }
    }

    fun willExclude(value: T): Boolean {
        if (containsAll(excluded)) {
            return true;
        } else if (containsNone(excluded)) {
            return false;
        } else {
            return containsElement(excluded, value);
        }
    }

    fun willInclude(value: T): Boolean {
        if (included.isEmpty()) {
            return true
        } else if (containsAll(included)) {
            return true;
        } else if (containsNone(included)) {
            return false;
        } else {
            return containsElement(included, value);
        }
    }

    fun shouldInclude(value: T): Boolean {
        return if (willExclude(value)) {
            false
        } else {
            willInclude(value)
        }
    }
}