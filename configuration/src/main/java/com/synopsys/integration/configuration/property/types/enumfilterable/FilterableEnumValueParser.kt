package com.synopsys.integration.configuration.property.types.enumfilterable

import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.ValueOfParser

class FilterableEnumListValueParser<T>(val valueOf: (String) -> T?) : ValueParser<List<FilterableEnumValue<T>>>() {
    private val parser = ValueOfParser(valueOf)
    override fun parse(value: String): List<FilterableEnumValue<T>> {
        return value.split(",").map {
            when {
                it.toLowerCase() == "none" -> None<T>()
                it.toLowerCase() == "all" -> All<T>()
                else -> Value(parser.parse(value))
            }
        }
    }
}
