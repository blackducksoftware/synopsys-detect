package com.synopsys.integration.configuration.config

open class PropertyTest {
    fun emptyConfig(): DetectConfig {
        val propertyMap = emptyMap<String, String>()
        val propertySource = MapPropertySource("map", propertyMap)
        val propertySources = listOf(propertySource)
        return DetectConfig(propertySources)
    }

    fun configOf(pair: Pair<String, String>): DetectConfig {
        val propertyMap = mapOf(pair)
        val propertySource = MapPropertySource("map", propertyMap)
        val propertySources = listOf(propertySource)
        return DetectConfig(propertySources)
    }
}