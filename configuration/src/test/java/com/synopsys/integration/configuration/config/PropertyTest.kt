package com.synopsys.integration.configuration.config

open class PropertyTest {
    fun emptyConfig(): PropertyConfiguration {
        val propertyMap = emptyMap<String, String>()
        val propertySource = MapPropertySource("map", propertyMap)
        val propertySources = listOf(propertySource)
        return PropertyConfiguration(propertySources)
    }

    fun configOf(pair: Pair<String, String>): PropertyConfiguration {
        val propertyMap = mapOf(pair)
        val propertySource = MapPropertySource("map", propertyMap)
        val propertySources = listOf(propertySource)
        return PropertyConfiguration(propertySources)
    }
}