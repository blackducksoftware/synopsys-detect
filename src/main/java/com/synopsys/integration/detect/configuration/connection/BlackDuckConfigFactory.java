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
package com.synopsys.integration.detect.configuration.connection;

import java.util.Optional;
import java.util.concurrent.Executors;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.common.util.ProxyUtil;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class BlackDuckConfigFactory {
    private final BlackDuckConnectionDetails blackDuckConnectionDetails;
    private static final String BLACKDUCK_SERVER_CONFIG_BUILDER_TIMEOUT_KEY = "blackduck.timeout";

    public BlackDuckConfigFactory(final BlackDuckConnectionDetails blackDuckConnectionDetails) {
        this.blackDuckConnectionDetails = blackDuckConnectionDetails;
    }

    public BlackDuckServerConfig createServerConfig(final IntLogger intLogger) throws DetectUserFriendlyException {
        final IntLogger logger;
        if (intLogger == null) {
            logger = new SilentIntLogger();
        } else {
            logger = intLogger;
        }
        final ConnectionDetails connectionDetails = blackDuckConnectionDetails.getConnectionDetails();

        final BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = new BlackDuckServerConfigBuilder()
                                                                              .setExecutorService(Executors.newFixedThreadPool(blackDuckConnectionDetails.getParallelProcessors()))
                                                                              .setLogger(logger);

        blackDuckServerConfigBuilder.setProperties(blackDuckConnectionDetails.getBlackduckProperties().entrySet());
        blackDuckServerConfigBuilder.setProperty(BLACKDUCK_SERVER_CONFIG_BUILDER_TIMEOUT_KEY, blackDuckConnectionDetails.getConnectionDetails().getTimeout().toString());

        final Optional<Boolean> shouldIgnore = blackDuckConnectionDetails.getBlackDuckUrl().map(blackduckUrl -> ProxyUtil.shouldIgnoreUrl(blackduckUrl, connectionDetails.getIgnoredProxyHostPatterns(), logger));
        if (shouldIgnore.isPresent() && Boolean.TRUE.equals(shouldIgnore.get())) {
            blackDuckServerConfigBuilder.setProxyInfo(ProxyInfo.NO_PROXY_INFO);
        } else {
            blackDuckServerConfigBuilder.setProxyInfo(connectionDetails.getProxyInformation());
        }

        try {
            return blackDuckServerConfigBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException("Failed to configure Black Duck server connection: " + e.getMessage(), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}