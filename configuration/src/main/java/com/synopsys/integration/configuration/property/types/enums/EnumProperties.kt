package com.synopsys.integration.configuration.property.types.enums

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableEnumProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : NullableProperty<T>(key, EnumValueParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}

class EnumProperty<T>(key: String, default: T, valueOf: (String) -> T?, val values: List<T>) : ValuedProperty<T>(key, EnumValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.toString()
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}