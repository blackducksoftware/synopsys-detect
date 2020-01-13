package com.synopsys.integration.configuration.property.types.longs

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableLongProperty(key: String) : NullableProperty<Long>(key, LongValueParser()) {}
class LongProperty(key: String, default: Long) : ValuedProperty<Long>(key, LongValueParser(), default) {
    override fun describeDefault(): String? = default.toString()
}
