package com.synopsys.integration.configuration.deprecation

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

//This is a simple 'Deprecation Aware' way to access values.
class DeprecationAware(private val propertyConfiguration: PropertyConfiguration) {
    fun <T> getValue(property: ValuedProperty<T>): T {
        return getValueOrNull(property) ?: property.default
    }

    fun <T> getValueOrNull(property: ValuedProperty<T>): T? {
        return if (propertyConfiguration.wasPropertyProvided(property)) {
            propertyConfiguration.getValue(property);
        } else {
            val replacement = property.replacesDeprecatedProperty
            if (replacement != null) {
                getValueOrNull(replacement)
            } else {
                null
            }
        }
    }

    fun <T> getValue(property: NullableProperty<T>): T? {
        return if (propertyConfiguration.wasPropertyProvided(property)) {
            propertyConfiguration.getValue(property);
        } else {
            val replacement = property.replacesDeprecatedProperty
            if (replacement != null) {
                getValue(replacement)
            } else {
                null
            }
        }
    }
}
