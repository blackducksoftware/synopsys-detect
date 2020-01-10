/**
 * detect-configuration
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
package com.synopsys.integration.detect.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.util.ProxyUtil;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

public class ConnectionManager {
    private final DetectConfiguration detectConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConnectionManager(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public ProxyInfo getBlackDuckProxyInfo() throws DetectUserFriendlyException {
        final CredentialsBuilder proxyCredentialsBuilder = new CredentialsBuilder();
        proxyCredentialsBuilder.setUsername(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_USERNAME, PropertyAuthority.NONE));
        proxyCredentialsBuilder.setPassword(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PASSWORD, PropertyAuthority.NONE));
        final Credentials proxyCredentials;
        try {
            proxyCredentials = proxyCredentialsBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }

        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();

        proxyInfoBuilder.setCredentials(proxyCredentials);
        proxyInfoBuilder.setHost(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_HOST, PropertyAuthority.NONE));
        final String proxyPortFromConfiguration = detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PORT, PropertyAuthority.NONE);
        final int proxyPort = NumberUtils.toInt(proxyPortFromConfiguration, 0);
        proxyInfoBuilder.setPort(proxyPort);
        proxyInfoBuilder.setNtlmDomain(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN, PropertyAuthority.NONE));
        proxyInfoBuilder.setNtlmWorkstation(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION, PropertyAuthority.NONE));
        try {
            return proxyInfoBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
    }

    public IntHttpClient createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        final List<Pattern> ignoredProxyHostPatterns = ProxyUtil.getIgnoredProxyHostPatterns(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_IGNORED_HOSTS, PropertyAuthority.NONE));
        final boolean ignoreProxy;
        try {
            ignoreProxy = ProxyUtil.shouldIgnoreHost(new URL(url).getHost(), ignoredProxyHostPatterns);
        } catch (final MalformedURLException e) {
            throw new DetectUserFriendlyException("Unable to decide if proxy should be used for a given host.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        final IntLogger intLogger = new Slf4jIntLogger(logger);
        final int timeout = detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT, PropertyAuthority.NONE);
        final boolean alwaysTrust = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_TRUST_CERT, PropertyAuthority.NONE);

        if (ignoreProxy) {
            return new IntHttpClient(intLogger, timeout, alwaysTrust, ProxyInfo.NO_PROXY_INFO);
        } else {
            return new IntHttpClient(intLogger, timeout, alwaysTrust, getBlackDuckProxyInfo());
        }
    }
}
