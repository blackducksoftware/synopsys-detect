package com.synopsys.integration.configuration.property.types.integer

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableIntegerProperty(key: String) : NullableProperty<Int>(key, IntegerValueParser()) {}
class IntegerProperty(key: String, default: Int) : ValuedProperty<Int>(key, IntegerValueParser(), default) {
    override fun describeDefault(): String? = default.toString()
}