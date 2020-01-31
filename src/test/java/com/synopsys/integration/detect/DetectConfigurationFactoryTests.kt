package com.synopsys.integration.detect

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext
import com.synopsys.integration.configuration.property.Property
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver
import com.synopsys.integration.configuration.source.MapPropertySource
import com.synopsys.integration.configuration.util.configOf
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory
import com.synopsys.integration.detect.configuration.DetectProperties
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito

class DetectConfigurationFactoryTests {
    @Test
    fun testFallsbackToRuntime() {
        val factory = spyFactoryOf()
        val result = factory.findParallelProcessors()
        Mockito.verify(factory).findAvailableProcessors()
    }

    @Test
    fun testsParallelChosen() {
        val factory = factoryOf(DetectProperties.DETECT_PARALLEL_PROCESSORS to "3")
        Assertions.assertEquals(3, factory.findParallelProcessors())
    }

    @Test
    fun testsPrefersNewer() {
        val factory = factoryOf(
                DetectProperties.DETECT_PARALLEL_PROCESSORS to "5",
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS to "4"
        )
        Assertions.assertEquals(5, factory.findParallelProcessors())
    }

    @Test
    fun testsWillUseOld() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS to "5"
        )
        Assertions.assertEquals(5, factory.findParallelProcessors())
    }

    @Test
    fun testDeprecatedSnippingTrueUsesSnippetMatchingMode() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE to "true"
        )
        Assertions.assertEquals(SnippetMatching.SNIPPET_MATCHING, factory.findSnippetMatching())
    }

    @Test
    fun testsNewSnippetModePreferred() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE to "true",
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING to SnippetMatching.FULL_SNIPPET_MATCHING_ONLY.name
        )
        Assertions.assertEquals(SnippetMatching.FULL_SNIPPET_MATCHING_ONLY, factory.findSnippetMatching())
    }


    private fun spyFactoryOf(vararg properties: Pair<Property, String>): DetectConfigurationFactory {
        return Mockito.spy(factoryOf(*properties));
    }

    private fun factoryOf(vararg properties: Pair<Property, String>): DetectConfigurationFactory {
        val configuration = configOf(DetectProperties.BLACKDUCK_OFFLINE_MODE.key to "unknown");
        val helpContext = PropertyConfigurationHelpContext(configuration)
        Assertions.assertEquals(1, helpContext.findPropertyParseErrors(DetectProperties.properties).size)

        val propertyMap = properties.toMap().mapKeys { it.key.key }
        val inMemoryPropertySource = MapPropertySource("test", propertyMap)
        val propertyConfiguration = PropertyConfiguration(listOf(inMemoryPropertySource))
        return DetectConfigurationFactory(propertyConfiguration, SimplePathResolver())
    }
}