package com.synopsys.integration.configuration.property.types.enumlist

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableEnumListProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : NullableProperty<List<T>>(key, EnumListValueParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}

class EnumListProperty<T>(key: String, default: List<T>, valueOf: (String) -> T?, val values: List<T>) : ValuedProperty<List<T>>(key, EnumListValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}