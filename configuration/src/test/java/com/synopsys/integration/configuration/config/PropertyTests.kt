package com.synopsys.integration.configuration.config

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BooleanValueParserTests() {
    @ParameterizedTest()
    @ValueSource(strings = ["unknown", "we ird tef ", "243354323"])
    fun parseUnknownThrows() {
        Assertions.assertThrows(ValueParseException::class.java) {
            BooleanValueParser().parse("unknown")
        }
    }

    @ParameterizedTest()
    @ValueSource(strings = ["tRuE", "true ", " true", "    ", "", "t"])
    fun booleanParsesTrue(value: String) {
        Assertions.assertTrue(BooleanValueParser().parse(value))
    }

    @ParameterizedTest()
    @ValueSource(strings = ["false", "f"])
    fun booleanParsesFalse(value: String) {
        Assertions.assertFalse(BooleanValueParser().parse(value))
    }
}

class BooleanPropertyTest : PropertyTest() {
    private val exampleKey = "example.key"
    private val requiredBooleanPropertyDefaultTrue = RequiredBooleanProperty(exampleKey, true)
    private val requiredBooleanPropertyDefaultFalse = RequiredBooleanProperty(exampleKey, false)
    private val optionalBooleanProperty = OptionalBooleanProperty(exampleKey)

    @Test
    fun emptyDefaultsToTrue() {
        Assertions.assertTrue(emptyConfig().getValue(requiredBooleanPropertyDefaultTrue))
    }

    @Test
    fun emptyDefaultsToFalse() {
        Assertions.assertFalse(emptyConfig().getValue(requiredBooleanPropertyDefaultFalse))
    }

    @Test
    fun emptyDefaultsToNull() {
        Assertions.assertNull(emptyConfig().getValue(optionalBooleanProperty))
    }

    @Test
    fun providedFalseOverridesTrueDefault() {
        val config = configOf(exampleKey to "false")
        Assertions.assertFalse(config.getValue(requiredBooleanPropertyDefaultTrue))
    }

    @Test
    fun providesValue() {
        val config = configOf(exampleKey to "false")
        val value = config.getValue(optionalBooleanProperty)
        Assertions.assertTrue(value != null && value == false)
    }

    @Test()
    fun unknownValueThrowsRequired() {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to "unknown").getValue(requiredBooleanPropertyDefaultTrue)
        }
    }

    @Test()
    fun unknownValueThrowsOptional() {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to "unknown").getValue(optionalBooleanProperty)
        }
    }

    @Test()
    fun unknownValueGivesDefault() {
        Assertions.assertTrue(configOf("example.key" to "unknown").getValueOrDefault(requiredBooleanPropertyDefaultTrue))
    }
}
