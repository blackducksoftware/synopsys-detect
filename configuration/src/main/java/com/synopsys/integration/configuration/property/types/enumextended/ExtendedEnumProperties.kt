package com.synopsys.integration.configuration.property.types.enumextended

import com.synopsys.integration.configuration.property.base.ValuedProperty

class ExtendedEnumProperty<E, B>(key: String, default: ExtendedEnumValue<E, B>, valueOfE: (String) -> E?, valueOfB: (String) -> B?, val valuesExtended: List<E>, val valuesBase: List<B>) : ValuedProperty<ExtendedEnumValue<E, B>>(key, ExtendedEnumValueOfParser(valueOfE, valueOfB), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = valuesExtended.map { it.toString() } + "," + valuesBase.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.toString() //TODO: No way this is going to work!
}