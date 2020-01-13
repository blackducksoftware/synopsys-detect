package com.synopsys.integration.configuration.property.base

import com.synopsys.integration.configuration.parse.ValueParser

abstract class ValuedProperty<T>(key: String, parser: ValueParser<T>, val default: T) : TypedProperty<T>(key, parser) {
    // This is a property with a key and with a default value, it will always have a value.
}