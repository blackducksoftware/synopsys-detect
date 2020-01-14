package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.base.PassthroughProperty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PassthroughTests {
    @Test
    fun passthroughFindsTwoProperties() {
        val passthrough = PassthroughProperty("pass")

        val secondarySource = MapPropertySource("secondary", mapOf("pass.two" to "two value", "ignore" to "ignore value"))
        val primarySource = MapPropertySource("primary", mapOf("pass.one" to "one value"))
        val configuration = PropertyConfiguration(listOf(primarySource, secondarySource))

        Assertions.assertEquals(mapOf("pass.one" to "one value", "pass.two" to "two value"), configuration.getRaw(passthrough))
    }
}