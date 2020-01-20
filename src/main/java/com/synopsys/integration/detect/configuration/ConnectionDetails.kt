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
package com.synopsys.integration.detect.configuration

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.exitcode.ExitCodeType
import com.synopsys.integration.detect.util.ProxyUtil
import com.synopsys.integration.log.IntLogger
import com.synopsys.integration.log.SilentIntLogger
import com.synopsys.integration.rest.client.IntHttpClient
import com.synopsys.integration.rest.proxy.ProxyInfo
import java.util.concurrent.Executors
import java.util.regex.Pattern

class ConnectionDetails(
        val proxyInformation: ProxyInfo, //Not null because of NO_PROXY_INFO value.
        val ignoredProxyHostPatterns: List<Pattern>,
        val timeout: Long,
        val alwaysTrust: Boolean
)

class BlackDuckConnectionDetails(
        val offline: Boolean,
        val blackduckUrl: String?,
        val blackduckProperties: Map<String, String>,
        val parallelProcessors: Int,
        val connectionDetails: ConnectionDetails
)

class BlackDuckConfigFactory(private val blackDuckConnectionDetails: BlackDuckConnectionDetails) {
    fun createServerConfig(logger: IntLogger = SilentIntLogger()): BlackDuckServerConfig? {
        val connectionDetails = blackDuckConnectionDetails.connectionDetails;
        val blackDuckServerConfigBuilder = BlackDuckServerConfigBuilder()
                .setExecutorService(Executors.newFixedThreadPool(blackDuckConnectionDetails.parallelProcessors))
                .setLogger(logger)

        blackDuckServerConfigBuilder.setProperties(blackDuckConnectionDetails.blackduckProperties.entries)

        if (blackDuckConnectionDetails.blackduckUrl != null && ProxyUtil.shouldIgnoreUrl(blackDuckConnectionDetails.blackduckUrl, connectionDetails.ignoredProxyHostPatterns, logger)) {
            blackDuckServerConfigBuilder.setProxyInfo(connectionDetails.proxyInformation);
        }

        try {
            return blackDuckServerConfigBuilder.build();
        } catch (e: IllegalArgumentException) {
            throw DetectUserFriendlyException("Failed to configure Black Duck server connection: " + e.message, e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}

class ConnectionFactory(private val connectionDetails: ConnectionDetails) {
    fun createConnection(url: String, logger: IntLogger = SilentIntLogger()): IntHttpClient {
        return if (ProxyUtil.shouldIgnoreUrl(url, connectionDetails.ignoredProxyHostPatterns, logger)) {
            IntHttpClient(logger, connectionDetails.timeout.toInt(), connectionDetails.alwaysTrust, ProxyInfo.NO_PROXY_INFO)
        } else {
            IntHttpClient(logger, connectionDetails.timeout.toInt(), connectionDetails.alwaysTrust, connectionDetails.proxyInformation)
        }
    }
}