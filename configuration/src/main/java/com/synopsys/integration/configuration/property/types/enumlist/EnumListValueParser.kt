package com.synopsys.integration.configuration.property.types.enumlist

import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.ValueOfParser

class EnumListValueParser<T>(val valueOf: (String) -> T?) : ValueParser<List<T>>() {
    private val parser = ValueOfParser(valueOf);
    override fun parse(value: String) : List<T> {
        return value.split(",").map { parser.parse(it) }
    }
}

