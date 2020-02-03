package com.synopsys.integration.detect.configuration

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.Companion.factoryOf
import com.synopsys.integration.detect.configuration.DetectConfigurationFactoryTestUtils.Companion.spyFactoryOf
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito

class DetectConfigurationFactoryTests {

    //#region Proxy
    @Test
    fun proxyUsesCredentials() {
        val factory = factoryOf(
                DetectProperties.BLACKDUCK_PROXY_HOST to "host",
                DetectProperties.BLACKDUCK_PROXY_PORT to "20",
                DetectProperties.BLACKDUCK_PROXY_USERNAME to "username",
                DetectProperties.BLACKDUCK_PROXY_PASSWORD to "password"
        )
        val result = factory.createBlackDuckProxyInfo().proxyCredentials.get()
        Assertions.assertEquals(result.username.get(), "username")
        Assertions.assertEquals(result.password.get(), "password")
    }
    //#endregion Proxy

    //#region Parallel Processors
    @Test
    fun parallelProcessorsDefaultsToRuntime() {
        val factory = spyFactoryOf()
        val result = factory.findParallelProcessors()
        Mockito.verify(factory).findRuntimeProcessors()
    }

    @Test
    fun parallelProcessorsPrefersProperty() {
        val factory = factoryOf(DetectProperties.DETECT_PARALLEL_PROCESSORS to "3")
        Assertions.assertEquals(3, factory.findParallelProcessors())
    }

    @Test
    fun parallelProcessorsPrefersNewProperty() {
        val factory = factoryOf(
                DetectProperties.DETECT_PARALLEL_PROCESSORS to "5",
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS to "4"
        )
        Assertions.assertEquals(5, factory.findParallelProcessors())
    }

    @Test
    fun parallelProcessorsFallsBackToOldProperty() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS to "5"
        )
        Assertions.assertEquals(5, factory.findParallelProcessors())
    }
    //#endregion Parallel Processors

    //#region Snippet Matching
    @Test
    fun snippetMatchingDeprecatedPropertyEnablesSnippets() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE to "true"
        )
        Assertions.assertEquals(SnippetMatching.SNIPPET_MATCHING, factory.findSnippetMatching())
    }

    @Test
    fun snippetMatchingPrefersNewerProperty() {
        val factory = factoryOf(
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE to "true",
                DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING to SnippetMatching.FULL_SNIPPET_MATCHING_ONLY.name
        )
        Assertions.assertEquals(SnippetMatching.FULL_SNIPPET_MATCHING_ONLY, factory.findSnippetMatching())
    }
    //#endregion Snippet Matching

}