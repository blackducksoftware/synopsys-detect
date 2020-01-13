package com.synopsys.integration.configuration.property.types.bool

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableBooleanProperty(key: String) : NullableProperty<Boolean>(key, BooleanValueParser()) {
    override fun listExampleValues(): List<String>? = listOf("true", "false")
}
class BooleanProperty(key: String, default: Boolean) : ValuedProperty<Boolean>(key, BooleanValueParser(), default) {
    override fun listExampleValues(): List<String>? = listOf("true", "false")
    override fun describeDefault(): String? = default.toString()
}