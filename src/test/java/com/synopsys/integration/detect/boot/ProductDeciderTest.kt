/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.boot

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.configuration.BlackDuckConnectionDetails
import com.synopsys.integration.detect.configuration.ConnectionDetails
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions
import com.synopsys.integration.detect.util.filter.DetectToolFilter
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder
import com.synopsys.integration.rest.proxy.ProxyInfo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ProductDeciderTest {
    @Test
    fun shouldRunPolaris() {
        val detectConfigurationFactory = mock<DetectConfigurationFactory>()
        mockPolaris(detectConfigurationFactory)
        mockBlackDuck(detectConfigurationFactory)

        val productDecider = ProductDecider(detectConfigurationFactory)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)
        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertTrue(productDecision.polarisDecision.shouldRun())
    }

    @Test
    fun shouldRunPolarisWhenExcluded() {
        val detectConfigurationFactory = mock<DetectConfigurationFactory>()
        mockPolaris(detectConfigurationFactory)
        mockBlackDuck(detectConfigurationFactory)

        val productDecider = ProductDecider(detectConfigurationFactory)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(false)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertFalse(productDecision.polarisDecision.shouldRun())
    }

    @Test
    fun shouldRunBlackDuckOffline() {
        val detectConfigurationFactory = mock<DetectConfigurationFactory>()
        mockPolaris(detectConfigurationFactory)
        mockBlackDuck(detectConfigurationFactory, offlineMode = true)

        val productDecider = ProductDecider(detectConfigurationFactory)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertTrue(productDecision.blackDuckDecision.shouldRun())
        Assertions.assertTrue(productDecision.blackDuckDecision.isOffline)
    }

    @Test
    fun shouldRunBlackDuckOnline() {
        val detectConfigurationFactory = mock<DetectConfigurationFactory>()
        mockPolaris(detectConfigurationFactory)
        mockBlackDuck(detectConfigurationFactory, blackDuckUrl = "some-url")


        val productDecider = ProductDecider(detectConfigurationFactory)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertTrue(productDecision.blackDuckDecision.shouldRun())
        Assertions.assertFalse(productDecision.blackDuckDecision.isOffline)
    }

    @Test
    fun decidesNone() {
        val detectConfigurationFactory = mock<DetectConfigurationFactory>()
        mockPolaris(detectConfigurationFactory, accessToken = null, polarisUrl = null)
        mockBlackDuck(detectConfigurationFactory)

        val productDecider = ProductDecider(detectConfigurationFactory)
        val detectToolFilter = mock<DetectToolFilter>()
        whenever(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(true)

        val productDecision = productDecider.decide(mock(), detectToolFilter)

        Assertions.assertFalse(productDecision.willRunAny())
    }

    private fun mockBlackDuck(detectConfigurationFactory: DetectConfigurationFactory, offlineMode: Boolean = false, blackDuckUrl: String? = null) {
        whenever(detectConfigurationFactory.createBlackDuckConnectionDetails()).thenReturn(
                BlackDuckConnectionDetails(offlineMode, blackDuckUrl, mapOf(), 1,
                        ConnectionDetails(ProxyInfo.NO_PROXY_INFO, listOf(), 300, false)
                )
        )
        whenever(detectConfigurationFactory.createBlackDuckSignatureScannerOptions()).thenReturn(
                BlackDuckSignatureScannerOptions(listOf(), listOf(), listOf(), null, null, null, 1024, 1, false, null, false, null, null, null, 1)
        )
    }

    private fun mockPolaris(detectConfigurationFactory: DetectConfigurationFactory, accessToken: String? = "access token text", polarisUrl: String? = "http://polaris.com") {
        whenever(detectConfigurationFactory.createPolarisServerConfigBuilder(any())).thenReturn(
                PolarisServerConfigBuilder()
                        .setAccessToken(accessToken)
                        .setUrl(polarisUrl)
        )
    }
}
