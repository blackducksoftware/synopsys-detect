package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.types.string.NullableStringProperty
import com.synopsys.integration.configuration.source.SpringConfigurationPropertySource
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.springframework.mock.env.MockEnvironment

class SpringConfigurationPropertySourceTests {
    @Test
    fun verifySpringReturnsValue() {
        val m = MockEnvironment()
        m.setProperty("example.key", "value")

        val sources = SpringConfigurationPropertySource.fromConfigurableEnvironment(m)
        val config = PropertyConfiguration(sources)

        val property = NullableStringProperty("example.key")
        Assertions.assertEquals("value", config.getValue(property))
        Assertions.assertEquals("mockProperties", config.getPropertySource(property))
    }
}