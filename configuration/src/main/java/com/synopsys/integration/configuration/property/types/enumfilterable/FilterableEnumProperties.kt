package com.synopsys.integration.configuration.property.types.enumfilterable

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableFilterableEnumListProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : NullableProperty<List<FilterableEnumValue<T>>>(key, FilterableEnumListValueParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? {
        val base = values.map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }

    override fun isOnlyExampleValues(): Boolean = true
}

class FilterableEnumListProperty<T>(key: String, default: List<FilterableEnumValue<T>>, valueOf: (String) -> T?, val values: List<T>) : ValuedProperty<List<FilterableEnumValue<T>>>(key, FilterableEnumListValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun listExampleValues(): List<String>? {
        val base = values.map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }

    override fun isOnlyExampleValues(): Boolean = true
}