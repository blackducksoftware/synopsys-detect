package com.synopsys.integration.configuration.property.types.stringlist

import com.synopsys.integration.configuration.parse.ValueParser

class StringListValueParser : ValueParser<List<String>>() {
    override fun parse(value: String) : List<String> {
        return value.split(",").toList()
    }
}