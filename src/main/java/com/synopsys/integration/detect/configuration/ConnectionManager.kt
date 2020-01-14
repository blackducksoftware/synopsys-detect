/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.exitcode.ExitCodeType
import com.synopsys.integration.detect.util.ProxyUtil
import com.synopsys.integration.log.Slf4jIntLogger
import com.synopsys.integration.rest.client.IntHttpClient
import com.synopsys.integration.rest.credentials.Credentials
import com.synopsys.integration.rest.credentials.CredentialsBuilder
import com.synopsys.integration.rest.proxy.ProxyInfo
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder
import org.apache.commons.lang3.math.NumberUtils
import org.slf4j.LoggerFactory
import java.net.MalformedURLException
import java.net.URL

class ConnectionManager(private val detectConfiguration: PropertyConfiguration) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    val blackDuckProxyInfo: ProxyInfo
        @Throws(DetectUserFriendlyException::class)
        get() {
            val proxyCredentialsBuilder = CredentialsBuilder()
            proxyCredentialsBuilder.username = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_USERNAME)
            proxyCredentialsBuilder.password = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_PASSWORD)
            val proxyCredentials: Credentials
            try {
                proxyCredentials = proxyCredentialsBuilder.build()
            } catch (e: IllegalArgumentException) {
                throw DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
            }

            val proxyInfoBuilder = ProxyInfoBuilder()

            proxyInfoBuilder.credentials = proxyCredentials
            proxyInfoBuilder.host = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_HOST)
            val proxyPortFromConfiguration = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_PORT)
            val proxyPort = NumberUtils.toInt(proxyPortFromConfiguration, 0)
            proxyInfoBuilder.port = proxyPort
            proxyInfoBuilder.ntlmDomain = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN)
            proxyInfoBuilder.ntlmWorkstation = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION)
            try {
                return proxyInfoBuilder.build()
            } catch (e: IllegalArgumentException) {
                throw DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
            }

        }

    @Throws(DetectUserFriendlyException::class)
    fun createUnauthenticatedRestConnection(url: String): IntHttpClient {
        val ignoredProxyHostPatterns = ProxyUtil.getIgnoredProxyHostPatterns(detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_IGNORED_HOSTS))
        val ignoreProxy: Boolean
        try {
            ignoreProxy = ProxyUtil.shouldIgnoreHost(URL(url).host, ignoredProxyHostPatterns)
        } catch (e: MalformedURLException) {
            throw DetectUserFriendlyException("Unable to decide if proxy should be used for a given host.", e, ExitCodeType.FAILURE_CONFIGURATION)
        }

        val intLogger = Slf4jIntLogger(logger)
        val timeout = detectConfiguration.getValue(DetectProperties.BLACKDUCK_TIMEOUT)
        val alwaysTrust = detectConfiguration.getValue(DetectProperties.BLACKDUCK_TRUST_CERT)

        return if (ignoreProxy) {
            IntHttpClient(intLogger, timeout, alwaysTrust, ProxyInfo.NO_PROXY_INFO)
        } else {
            IntHttpClient(intLogger, timeout, alwaysTrust, blackDuckProxyInfo)
        }
    }
}
