package com.synopsys.integration.configuration.property.types.enums

import com.synopsys.integration.configuration.parse.ValueParser

class EnumValueParser<T>(val valueOf: (String) -> T?) : ValueParser<T>() {
    private val parser = ValueOfParser(valueOf);
    override fun parse(value: String) : T {
        return parser.parse(value)
    }
}