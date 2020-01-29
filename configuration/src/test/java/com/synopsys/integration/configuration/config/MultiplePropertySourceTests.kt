package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.types.string.NullableStringProperty
import com.synopsys.integration.configuration.util.configOf
import com.synopsys.integration.configuration.util.propertySourceOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MultiplePropertySourceTests {
    private val secondaryProperty = NullableStringProperty("second.key")
    private val sharedProperty = NullableStringProperty("shared.key")

    private val secondarySource = propertySourceOf("secondary", "second.key" to "secondValue", "shared.key" to "base")
    private val primarySource = propertySourceOf("primary", "shared.key" to "override")
    private val configuration = configOf(primarySource, secondarySource)

    @Test
    fun primaryOverridesSecondary() {
        Assertions.assertEquals("override", configuration.getValue(sharedProperty))
        Assertions.assertEquals("primary", configuration.getPropertySource(sharedProperty))
    }

    @Test
    fun fallbackToSecondary() {
        Assertions.assertEquals("secondValue", configuration.getValue(secondaryProperty))
        Assertions.assertEquals("secondary", configuration.getPropertySource(secondaryProperty))
    }

    @Test
    fun containsKeysFromBothSources() {
        Assertions.assertEquals(setOf("second.key", "shared.key"), configuration.getKeys())
    }

    @Test
    fun rawValues() {
        Assertions.assertEquals(mapOf(
                "second.key" to "secondValue",
                "shared.key" to "override"
        ), configuration.getRaw())
    }
}