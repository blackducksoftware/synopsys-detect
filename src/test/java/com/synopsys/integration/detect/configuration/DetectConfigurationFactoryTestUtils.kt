package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.Property
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver
import com.synopsys.integration.configuration.source.MapPropertySource
import org.mockito.Mockito

class DetectConfigurationFactoryTestUtils {
    companion object {
        fun spyFactoryOf(vararg properties: Pair<Property, String>): DetectConfigurationFactory {
            return Mockito.spy(factoryOf(*properties));
        }

        fun factoryOf(vararg properties: Pair<Property, String>): DetectConfigurationFactory {
            val propertyMap = properties.toMap().mapKeys { it.key.key }
            val inMemoryPropertySource = MapPropertySource("test", propertyMap)
            val propertyConfiguration = PropertyConfiguration(listOf(inMemoryPropertySource))
            return DetectConfigurationFactory(propertyConfiguration, SimplePathResolver())
        }
    }
}