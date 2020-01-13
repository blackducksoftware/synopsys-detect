package com.synopsys.integration.configuration.property.types.enumsoftlist

import com.synopsys.integration.configuration.property.base.ValuedProperty
import com.synopsys.integration.configuration.property.types.enumsoft.SoftEnumValue

class SoftEnumListProperty<T>(key: String, default: List<SoftEnumValue<T>>, valueOf: (String) -> T?, val values: List<T>) : ValuedProperty<List<SoftEnumValue<T>>>(key, SoftEnumListValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.joinToString { "," }
}