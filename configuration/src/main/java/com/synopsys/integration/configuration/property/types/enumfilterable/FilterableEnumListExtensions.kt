package com.synopsys.integration.configuration.property.types.enumfilterable

import com.synopsys.integration.configuration.property.types.enumsoft.ActualValue


fun <T> List<FilterableEnumValue<T>>.containsNone(): Boolean {
    return this.any {
        when (it) {
            is None -> true
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.containsAll(): Boolean {
    return this.any {
        when (it) {
            is None -> true
            else -> false
        }
    }

}

fun <T> List<FilterableEnumValue<T>>.containsValue(value: T): Boolean {
    return this.any {
        when (it) {
            is Value<*> -> it.value == value
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.toValueList(clazz: Class<T>): List<T> {
    return this.flatMap {
        when (it) {
            is Value<*> -> listOf(clazz.cast(it.value))
            else -> emptyList()
        }
    }.toList()
}

fun <T> List<FilterableEnumValue<T>>.populatedValues(allValues: Array<T>, clazz: Class<T>): List<T> {
    if (this.containsNone()) {
        return emptyList()
    } else if (this.containsAll()) {
        return allValues.toList()
    } else {
        return this.toValueList(clazz)
    }
}