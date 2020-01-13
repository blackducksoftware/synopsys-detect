package com.synopsys.integration.configuration.property.types.integer

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser

class IntegerValueParser : ValueParser<Int>() {
    override fun parse(value: String) : Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            throw ValueParseException(value, "integer", innerException = e)
        }
    }
}