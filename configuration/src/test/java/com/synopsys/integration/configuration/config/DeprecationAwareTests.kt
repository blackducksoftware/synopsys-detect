package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.deprecation.DeprecationAware
import com.synopsys.integration.configuration.property.types.string.StringProperty
import org.junit.Test
import org.junit.jupiter.api.Assertions

class DeprecationAwareTests {
    @Test
    fun defaultIsFromReplacement() {
        val deprecated = StringProperty("key.old", "default.old")
        val replacement = StringProperty("key.new", "default.new").replacesDeprecatedProperty(deprecated)

        val config = PropertyConfiguration(emptyList())
        val deprecation = DeprecationAware(config)

        val value = deprecation.getValue(replacement)
        Assertions.assertEquals("default.new", value)
    }

    @Test
    fun prefersReplacement() {
        val deprecated = StringProperty("key.old", "default.old")
        val replacement = StringProperty("key.new", "default.new").replacesDeprecatedProperty(deprecated)

        val source = MapPropertySource("test", mapOf(deprecated.key to "old.value", replacement.key to "new.value"))
        val config = PropertyConfiguration(listOf(source))
        val deprecation = DeprecationAware(config)

        val value = deprecation.getValue(replacement)
        Assertions.assertEquals("new.value", value)
    }

    @Test
    fun usesDeprecatedWhenNoReplacement() {
        val deprecated = StringProperty("key.old", "default.old")
        val replacement = StringProperty("key.new", "default.new").replacesDeprecatedProperty(deprecated)

        val source = MapPropertySource("test", mapOf(deprecated.key to "given"))
        val config = PropertyConfiguration(listOf(source))
        val deprecation = DeprecationAware(config)

        val value = deprecation.getValue(replacement)
        Assertions.assertEquals("given", value)
    }

    @Test
    fun followsReplacementWhenChained() {
        val grandfather = StringProperty("key.grandfather", "default.grandfather")
        val father = StringProperty("key.father", "default.father").replacesDeprecatedProperty(grandfather)
        val child = StringProperty("key.child", "default.child").replacesDeprecatedProperty(father)

        val source = MapPropertySource("test", mapOf(grandfather.key to "given"))
        val config = PropertyConfiguration(listOf(source))
        val deprecation = DeprecationAware(config)

        val value = deprecation.getValue(child)
        Assertions.assertEquals("given", value)
    }
}