package com.synopsys.integration.configuration.property.types.enums

import com.synopsys.integration.configuration.parse.ValueParseException

class ValueOfParser<T>(val valueOf: (String) -> T?) {
    @Throws(ValueParseException::class)
    fun parse(value: String) : T {
        try {
            return valueOf(value) ?: throw ValueParseException(value, "enum", additionalMessage = "Enum value was null.")
        } catch (e:Exception){
            throw ValueParseException(value, "enum", innerException = e)
        }
    }
}

class ValueOfOrNullParser<T>(val valueOf: (String) -> T?) {
    fun parse(value: String) : T? {
        return try {
            valueOf(value)
        } catch (e:Exception){
            null
        }
    }
}
