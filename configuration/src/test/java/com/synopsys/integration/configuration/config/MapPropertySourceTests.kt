package com.synopsys.integration.configuration.config

import org.junit.jupiter.api.Assertions

class MapPropertySourceTests {
    fun testNormalizesKeys() {
        val source = MapPropertySource("test", mapOf("CAPITAL_UNDERSCORE" to "value"))
        val keys = source.getKeys()
        Assertions.assertEquals(setOf("capital.underscore"), keys)
    }
}