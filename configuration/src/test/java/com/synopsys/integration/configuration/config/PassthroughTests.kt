package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.base.PassthroughProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PassthroughTests {
    private val passthrough = PassthroughProperty("pass")

    @Test
    fun findsTwoProperties() {
        val secondarySource = propertySourceOf("secondary", "pass.two" to "two value", "ignore" to "ignore value")
        val primarySource = propertySourceOf("primary", "pass.one" to "one value")
        val configuration = configOf(primarySource, secondarySource)

        Assertions.assertEquals(mapOf("one" to "one value", "two" to "two value"), configuration.getRaw(passthrough))
    }

    @Test
    fun prefersPrimary() {
        val secondarySource = propertySourceOf("secondary", "pass.shared" to "secondaryValue")
        val primarySource = propertySourceOf("primary", "pass.shared" to "primaryValue")
        val configuration = configOf(primarySource, secondarySource)

        Assertions.assertEquals(mapOf("shared" to "primaryValue"), configuration.getRaw(passthrough))
    }
}