package com.synopsys.integration.configuration.property.types.enumextended

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.ValueOfOrNullParser

class ExtendedEnumValueOfParser<E, B>(private val valueOfE: (String) -> E?, private val valueOfB: (String) -> B?) : ValueParser<ExtendedEnumValue<E, B>>() {
    var extendedParser = ValueOfOrNullParser(valueOfE)
    var baseParser = ValueOfOrNullParser(valueOfB)

    override fun parse(value: String): ExtendedEnumValue<E, B> {
        val eValue = extendedParser.parse(value);
        if (eValue != null) {
            return ExtendedValue(eValue)
        }
        val bValue = baseParser.parse(value);
        if (bValue != null) {
            return BaseValue(bValue)
        }
        throw ValueParseException(value, "either enum", additionalMessage = "Value was not a member of either enum set.")//TODO: Mention enum types?
    }
}
