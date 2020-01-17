package com.synopsys.integration.configuration.property.types.enumextended

// An enum that can be either the E or the B type.
// Useful for enums that extend a base type. For example we want an UNSPECIFIED value on an existing enum that does not have it and does not make sense as an enum value on the existing type.
sealed class ExtendedEnumValue<E, B>

class ExtendedValue<E, B>(val value: E) : ExtendedEnumValue<E, B>() {
    override fun toString(): String = value.toString()
}

class BaseValue<E, B>(val value: B) : ExtendedEnumValue<E, B>() {
    override fun toString(): String = value.toString()
}
