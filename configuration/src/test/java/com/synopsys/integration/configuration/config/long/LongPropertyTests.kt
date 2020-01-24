package com.synopsys.integration.configuration.config.long

import com.synopsys.integration.configuration.config.InvalidPropertyException
import com.synopsys.integration.configuration.config.PropertyTest
import com.synopsys.integration.configuration.property.types.integer.IntegerProperty
import com.synopsys.integration.configuration.property.types.integer.NullableIntegerProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LongPropertyTests : PropertyTest() {
    private val exampleKey = "example.key"
    private val defaultPos1 = IntegerProperty(exampleKey, 1)
    private val defaultNeg1 = IntegerProperty(exampleKey, -1)
    private val nullable = NullableIntegerProperty(exampleKey)

    @Test
    fun emptyDefaultPos1() {
        Assertions.assertEquals(1, emptyConfig().getValue(defaultPos1))
    }

    @Test
    fun emptyDefaultNeg1() {
        Assertions.assertEquals(-1, emptyConfig().getValue(defaultNeg1))
    }

    @Test
    fun emptyDefaultsNull() {
        Assertions.assertNull(emptyConfig().getValue(nullable))
    }

    @Test
    fun providedOverridesDefault() {
        val config = configOf(exampleKey to "-5")
        Assertions.assertEquals(-5, config.getValue(defaultPos1))
    }

    @Test
    fun providesValue() {
        val config = configOf(exampleKey to "5")
        val value = config.getValue(nullable)
        Assertions.assertNotNull(value, "Config should not have returned null.")
        Assertions.assertEquals(5, value)
    }

    @Test()
    fun unknownValueThrows() {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to "unknown").getValue(defaultPos1)
        }
    }

    @Test()
    fun unknownValueGivesDefault() {
        Assertions.assertEquals(1, configOf("example.key" to "unknown").getValueOrDefault(defaultPos1))
    }
}