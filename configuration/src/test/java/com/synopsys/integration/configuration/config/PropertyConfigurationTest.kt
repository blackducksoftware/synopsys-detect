package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PropertyConfigurationTest {
    companion object {
        const val UNKNOWN_VALUE = "-1"
    }

    class TestValueParser : ValueParser<String>() {
        override fun parse(value: String): String {
            if (value == UNKNOWN_VALUE) {
                throw ValueParseException(value, "String", "Will parse any value to String, except for '-1' for the test.")
            }
            return value
        }
    }

    class NullableTestProperty(key: String) : NullableProperty<String>(key, TestValueParser())
    class ValuedTestProperty(key: String, default: String) : ValuedProperty<String>(key, TestValueParser(), default)

    private val exampleKey = "example.key"
    private val propertyWithDefault = ValuedTestProperty(exampleKey, "defaultValue")
    private val nullableProperty = NullableTestProperty(exampleKey)

    @Test
    fun emptyDefaultsToDefault() {
        Assertions.assertEquals("defaultValue", emptyConfig().getValue(propertyWithDefault))
    }

    @Test
    fun emptyDefaultsToNull() {
        Assertions.assertNull(emptyConfig().getValue(nullableProperty))
    }

    @Test
    fun providedOverridesDefault() {
        val config = configOf(exampleKey to "overridden")
        Assertions.assertEquals("overridden", config.getValue(propertyWithDefault))
    }

    @Test
    fun providesValue() {
        val config = configOf(exampleKey to "providedValue")
        val value = config.getValue(nullableProperty)
        Assertions.assertNotNull(value)
        Assertions.assertEquals("providedValue", value)
    }

    @Test()
    fun unknownValueThrows() {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to UNKNOWN_VALUE).getValue(propertyWithDefault)
        }
    }

    @Test()
    fun unknownValueGivesDefault() {
        Assertions.assertEquals("defaultValue", configOf("example.key" to UNKNOWN_VALUE).getValueOrDefault(propertyWithDefault))
    }
}