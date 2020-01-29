package com.synopsys.integration.configuration.property.types.enum

import com.synopsys.integration.configuration.property.types.enums.NullableEnumProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class EnumPropertiesTests {
    enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    fun testExampleValues() {
        val property = NullableEnumProperty("example.list", Example::class.java)
        Assertions.assertEquals(listOf("THING", "ANOTHER", "THIRD"), property.listExampleValues());
    }
}