package com.synopsys.integration.configuration.property.types.integer

import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class IntegerPropertiesTests {
    @Test
    fun testList() {
        val property = IntegerListProperty("integer.list", emptyList());
        val config = configOf("integer.list" to "2,3")
        Assertions.assertEquals(listOf(2, 3), config.getValue(property));
    }

    @Test
    fun testNullable() {
        val property = NullableIntegerProperty("integer.nullable");
        val config = configOf("integer.nullable" to "2")
        Assertions.assertEquals(2, config.getValue(property));
    }

    @Test
    fun testValued() {
        val property = IntegerProperty("integer.valued", 2);
        val config = configOf("integer.valued" to "5")
        Assertions.assertEquals(5, config.getValue(property));
    }
}