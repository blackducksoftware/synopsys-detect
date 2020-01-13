package com.synopsys.integration.configuration.property.types.bool

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser
import org.apache.commons.lang3.BooleanUtils

class BooleanValueParser : ValueParser<Boolean>() {
    override fun parse(value: String) : Boolean {
        val trimmed = value.toLowerCase().trim()
        return if (trimmed.isBlank()) {
            true
        } else {
            BooleanUtils.toBooleanObject(trimmed) ?: throw ValueParseException(value, "boolean", "Unknown boolean format. Supported values include yes and false.")
        }
    }
}