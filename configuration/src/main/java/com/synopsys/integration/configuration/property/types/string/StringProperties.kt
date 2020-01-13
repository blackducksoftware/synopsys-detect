package com.synopsys.integration.configuration.property.types.string

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableStringProperty(key: String) : NullableProperty<String>(key, StringValueParser()) {}
class StringProperty(key: String, default: String) : ValuedProperty<String>(key, StringValueParser(), default) {
    override fun describeDefault(): String? = default
}
