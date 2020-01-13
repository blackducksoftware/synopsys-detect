package com.synopsys.integration.configuration.property.types.string

import com.synopsys.integration.configuration.parse.ValueParser

class StringValueParser : ValueParser<String>() {
    override fun parse(value: String) : String = value
}