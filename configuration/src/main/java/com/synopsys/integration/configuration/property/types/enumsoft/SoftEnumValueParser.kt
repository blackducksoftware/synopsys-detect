package com.synopsys.integration.configuration.property.types.enumsoft

import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.ValueOfOrNullParser

class SoftEnumValueParser<T>(val valueOf: (String) -> T?) : ValueParser<SoftEnumValue<T>>() {
    var parser = ValueOfOrNullParser(valueOf)
    override fun parse(value: String): SoftEnumValue<T> {
        return when (val enumValue = parser.parse(value)) {
            null -> StringValue<T>(value)
            else -> ActualValue<T>(enumValue)
        }
    }
}