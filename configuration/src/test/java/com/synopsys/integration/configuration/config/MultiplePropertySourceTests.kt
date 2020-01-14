package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.types.string.NullableStringProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MultiplePropertySourceTests {
    val secondaryProperty = NullableStringProperty("second.key")
    val sharedProperty = NullableStringProperty("shared.key")

    val secondarySource = MapPropertySource("secondary", mapOf("second.key" to "secondValue", "shared.key" to "base"))
    val primarySource = MapPropertySource("primary", mapOf("shared.key" to "override"))
    val configuration = PropertyConfiguration(listOf(primarySource, secondarySource))

    @Test
    fun primaryOverridesSecondary() {
        Assertions.assertEquals("override", configuration.getValue(sharedProperty))
        Assertions.assertEquals("primary", configuration.getPropertySource(sharedProperty))
    }

    @Test
    fun fallbackToSecondary() {
        Assertions.assertEquals("secondValue", configuration.getValue(secondaryProperty))
    }

    @Test
    fun containsKeysFromBothSources() {
        Assertions.assertEquals(setOf("second.key", "shared.key"), configuration.getKeys())
    }
}