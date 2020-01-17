package com.synopsys.integration.configuration.property.types.enumfilterable

// An enum that can be ALL, NONE or ENUM
// Useful for properties that want to be used with the ExcludeIncludeEnumFilter.
sealed class FilterableEnumValue<T>

class All<T>() : FilterableEnumValue<T>() {
    override fun toString(): String = "ALL"
}

class None<T>() : FilterableEnumValue<T>() {
    override fun toString(): String = "NONE"
}

class Value<T>(val value: T) : FilterableEnumValue<T>() {
    override fun toString(): String = value.toString()
}

