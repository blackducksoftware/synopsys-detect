package com.synopsys.integration.detect.configuration.config

import com.synopsys.integration.detect.config.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class PropertyTests {

    fun emptyConfig(): DetectConfig {
        return DetectConfig(emptyList())
    }

    fun mapConfig(propertyValues: Map<String, String>): DetectConfig {
        return DetectConfig(listOf(MapPropertySource("test", propertyValues)));
    }

    @Test
    fun requiredBooleanGivesDefaultTrue() {
        val requiredBooleanProperty = RequiredBooleanProperty("example.key", true);
        val value = emptyConfig().getValue(requiredBooleanProperty)
        Assertions.assertTrue(value)
    }

    @Test
    fun requiredBooleanGivesDefaultFalse() {
        val requiredBooleanProperty = RequiredBooleanProperty("example.key", true);
        val value = emptyConfig().getValue(requiredBooleanProperty)
        Assertions.assertTrue(value)
    }

    @Test
    fun requiredBooleanGivesProvidedFalse() {
        val requiredBooleanProperty = RequiredBooleanProperty("example.key", true);
        val config = mapConfig(mapOf("example.key" to "false"));
        val value = config.getValue(requiredBooleanProperty)
        Assertions.assertFalse(value)
    }

    @Test()
    fun requiredBooleanThrowsWhenUnknown() {
        val requiredBooleanProperty = RequiredBooleanProperty("example.key", true);
        val config = mapConfig(mapOf("example.key" to "unknown"));
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            config.getValue(requiredBooleanProperty)
        }
    }

    @Test()
    fun requiredBooleanGivesDefaultWhenUnknown() {
        val requiredBooleanProperty = RequiredBooleanProperty("example.key", true);
        val config = mapConfig(mapOf("example.key" to "unknown"));
        Assertions.assertTrue(config.getValueOrDefault(requiredBooleanProperty))
    }
}