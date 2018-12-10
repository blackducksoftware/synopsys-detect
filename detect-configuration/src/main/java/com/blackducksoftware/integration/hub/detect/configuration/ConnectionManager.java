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
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

public class ConnectionManager {
    private final DetectConfiguration detectConfiguration;
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);

    public ConnectionManager(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        CredentialsBuilder proxyCredentialsBuilder = new CredentialsBuilder();
        proxyCredentialsBuilder.setUsername(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_USERNAME, PropertyAuthority.None));
        proxyCredentialsBuilder.setPassword(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PASSWORD, PropertyAuthority.None));
        Credentials proxyCredentials = null;
        try {
            proxyCredentials = proxyCredentialsBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }

        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setCredentials(proxyCredentials);
        proxyInfoBuilder.setHost(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_HOST, PropertyAuthority.None));
        proxyInfoBuilder.setPort(detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_PROXY_PORT, PropertyAuthority.None));
        proxyInfoBuilder.setNtlmDomain(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN, PropertyAuthority.None));
        proxyInfoBuilder.setNtlmWorkstation(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION, PropertyAuthority.None));
        try {
            return proxyInfoBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
    }

    public RestConnection createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        IntLogger intLogger = new Slf4jIntLogger(logger);
        int timeout = detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT, PropertyAuthority.None);
        boolean alwaysTrust = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_TRUST_CERT, PropertyAuthority.None);
        ProxyInfo proxyInfo = getHubProxyInfo();
        return new RestConnection(intLogger, timeout, alwaysTrust, proxyInfo);
    }

}
