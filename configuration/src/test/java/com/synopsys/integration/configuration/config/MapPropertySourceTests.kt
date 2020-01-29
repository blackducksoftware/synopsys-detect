package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.source.MapPropertySource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MapPropertySourceTests {
    @Test
    fun testNormalizesKeys() {
        val source = MapPropertySource("test", mapOf("CAPITAL_UNDERSCORE" to "value"))
        val keys = source.getKeys()
        Assertions.assertEquals(setOf("capital.underscore"), keys)
    }

    @Test
    fun returnsKey() {
        val source = MapPropertySource("test", mapOf("property.key" to "value"))
        Assertions.assertEquals("value", source.getValue("property.key"));
        Assertions.assertEquals("test", source.getOrigin("property.key"));
        Assertions.assertEquals("test", source.getName());
    }
}