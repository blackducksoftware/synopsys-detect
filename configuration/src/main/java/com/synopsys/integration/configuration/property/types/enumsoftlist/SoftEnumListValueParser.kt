package com.synopsys.integration.configuration.property.types.enumsoftlist

import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.ValueOfOrNullParser
import com.synopsys.integration.configuration.property.types.enumsoft.ActualValue
import com.synopsys.integration.configuration.property.types.enumsoft.SoftEnumValue
import com.synopsys.integration.configuration.property.types.enumsoft.StringValue

class SoftEnumListValueParser<T>(val valueOf: (String) -> T?) : ValueParser<List<SoftEnumValue<T>>>() {
    var parser = ValueOfOrNullParser(valueOf)
    override fun parse(value: String): List<SoftEnumValue<T>> {
        return value.split(",").map {
            when (val enumValue = parser.parse(value)) { //TODO: Catch exception here? What happens when valueOf throws?
                null -> StringValue<T>(value)
                else -> ActualValue<T>(enumValue)
            }
        }
    }
}
