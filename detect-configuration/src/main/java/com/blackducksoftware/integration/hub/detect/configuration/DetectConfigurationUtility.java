/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

public class DetectConfigurationUtility {
    private final DetectConfiguration detectConfiguration;
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);

    public DetectConfigurationUtility(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setHost(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_HOST));
        proxyInfoBuilder.setPort(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PORT));
        proxyInfoBuilder.setUsername(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_USERNAME));
        proxyInfoBuilder.setPassword(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PASSWORD));
        proxyInfoBuilder.setIgnoredProxyHosts(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_IGNORED_HOSTS));
        proxyInfoBuilder.setNtlmDomain(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN));
        proxyInfoBuilder.setNtlmWorkstation(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION));
        ProxyInfo proxyInfo = ProxyInfo.NO_PROXY_INFO;
        try {
            proxyInfo = proxyInfoBuilder.build();
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
        return proxyInfo;
    }

    public UnauthenticatedRestConnection createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
        restConnectionBuilder.setBaseUrl(url);
        restConnectionBuilder.setTimeout(detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT));
        restConnectionBuilder.applyProxyInfo(getHubProxyInfo());
        restConnectionBuilder.setLogger(new Slf4jIntLogger(logger));
        restConnectionBuilder.setAlwaysTrustServerCertificate(detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_TRUST_CERT));

        return restConnectionBuilder.build();
    }
}
