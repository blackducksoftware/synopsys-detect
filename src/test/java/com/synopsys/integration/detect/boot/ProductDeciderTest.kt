package com.synopsys.integration.detect.boot

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.synopsys.integration.configuration.config.MapPropertySource
import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.config.PropertySource
import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.configuration.DetectProperties
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider
import com.synopsys.integration.detect.util.filter.DetectToolFilter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

//TODO: Consider separating configuration.
class ProductDeciderTest {
    @Test
    fun shouldRunPolaris() {
        val userHome = mock<File>()
        val detectConfiguration = configuration(mapOf(
                "POLARIS_ACCESS_TOKEN" to "access token text",
                "POLARIS_URL" to "http://polaris.com"
        ))
        val productDecider = ProductDecider(detectConfiguration)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)
        val productDecision = productDecider.decide(userHome, detectToolFilter)

        // TODO: Fix this tests. Not sure how this ever passed because the decision requires a valid PolarisServerConfig.
        Assertions.assertTrue(productDecision.polarisDecision.shouldRun())
    }

    @Test
    fun shouldRunPolarisWhenExcluded() {
        val detectConfiguration = configuration(mapOf(
                "POLARIS_ACCESS_TOKEN" to "access token text",
                "POLARIS_URL" to "http://polaris.com"
        ))
        val productDecider = ProductDecider(detectConfiguration)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(false)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertFalse(productDecision.polarisDecision.shouldRun())
    }

    @Test
    fun shouldRunBlackDuckOffline() {
        val detectConfiguration = configuration(mapOf(
                DetectProperties.BLACKDUCK_OFFLINE_MODE.key to "true"
        ))

        val productDecider = ProductDecider(detectConfiguration)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertTrue(productDecision.blackDuckDecision.shouldRun())
        Assertions.assertTrue(productDecision.blackDuckDecision.isOffline)
    }

    @Test
    fun shouldRunBlackDuckOnline() {
        val detectConfiguration = configuration(mapOf(
                DetectProperties.BLACKDUCK_URL.key to "some-url"
        ))

        val productDecider = ProductDecider(detectConfiguration)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertTrue(productDecision.blackDuckDecision.shouldRun())
        Assertions.assertFalse(productDecision.blackDuckDecision.isOffline)
    }

    @Test
    fun decidesNone() {
        val detectConfiguration = configuration(mapOf(
                DetectProperties.BLACKDUCK_OFFLINE_MODE.key to "false"
        ))

        val productDecider = ProductDecider(detectConfiguration)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertFalse(productDecision.willRunAny())
    }

    private fun configuration(propertyMap: Map<String, String>): PropertyConfiguration {
        val mapPropertySource: PropertySource = MapPropertySource("testPropertyMap", propertyMap)
        val propertySources: List<PropertySource> = listOf(mapPropertySource)

        return PropertyConfiguration(propertySources)
    }
}