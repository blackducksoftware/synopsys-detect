package com.synopsys.integration.configuration.property.types.bool

import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class BooleanPropertiesTests {
    @Test
    fun testList() {
        val property = BooleanListProperty("boolean.list", emptyList());
        val config = configOf("boolean.list" to "true, true")
        Assertions.assertEquals(listOf(true, true), config.getValue(property));
    }

    @Test
    fun testNullable() {
        val property = NullableBooleanProperty("boolean.nullable");
        val config = configOf("boolean.nullable" to "true")
        Assertions.assertEquals(true, config.getValue(property));
    }

    @Test
    fun testValued() {
        val property = BooleanProperty("boolean.valued", false);
        val config = configOf("boolean.valued" to "true")
        Assertions.assertEquals(true, config.getValue(property));
    }
}