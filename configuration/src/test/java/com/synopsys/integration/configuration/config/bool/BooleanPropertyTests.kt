package com.synopsys.integration.configuration.config.bool

import com.synopsys.integration.configuration.config.InvalidPropertyException
import com.synopsys.integration.configuration.config.configOf
import com.synopsys.integration.configuration.config.emptyConfig
import com.synopsys.integration.configuration.property.types.bool.BooleanProperty
import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BooleanPropertyTests {
    private val exampleKey = "example.key"
    private val defaultTrue = BooleanProperty(exampleKey, true)
    private val defaultFalse = BooleanProperty(exampleKey, false)
    private val nullable = NullableBooleanProperty(exampleKey)

    @Test
    fun emptyDefaultsTrue() {
        Assertions.assertTrue(emptyConfig().getValue(defaultTrue))
    }

    @Test
    fun emptyDefaultsFalse() {
        Assertions.assertFalse(emptyConfig().getValue(defaultFalse))
    }

    @Test
    fun emptyDefaultsNull() {
        Assertions.assertNull(emptyConfig().getValue(nullable))
    }

    @Test
    fun providedFalseOverridesTrueDefault() {
        val config = configOf(exampleKey to "false")
        Assertions.assertFalse(config.getValue(defaultTrue))
    }

    @Test
    fun providedTrueOverridesFalseDefault() {
        val config = configOf(exampleKey to "yes")
        Assertions.assertTrue(config.getValue(defaultFalse))
    }

    @Test
    fun providesValue() {
        val config = configOf(exampleKey to "false")
        val value = config.getValue(nullable)
        Assertions.assertTrue(value != null && value == false)
    }

    @Test()
    fun unknownValueThrows() {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to "unknown").getValue(defaultTrue)
        }
    }

    @Test()
    fun unknownValueGivesDefault() {
        Assertions.assertTrue(configOf("example.key" to "unknown").getValueOrDefault(defaultTrue))
    }
}