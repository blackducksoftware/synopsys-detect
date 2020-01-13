package com.synopsys.integration.configuration.property.types.longs

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser

class LongValueParser : ValueParser<Long>() {
    override fun parse(value: String) : Long {
        return try {
            value.toLong()
        } catch (e: NumberFormatException) {
            throw ValueParseException(value, "integer", innerException = e)
        }
    }
}