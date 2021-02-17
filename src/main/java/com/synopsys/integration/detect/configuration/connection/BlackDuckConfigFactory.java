/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckConfigFactory {
    private final BlackDuckConnectionDetails blackDuckConnectionDetails;
    private final DetectInfo detectInfo;
    private static final String BLACK_DUCK_SERVER_CONFIG_BUILDER_TIMEOUT_KEY = "blackduck.timeout";

    public BlackDuckConfigFactory(DetectInfo detectInfo, BlackDuckConnectionDetails blackDuckConnectionDetails) {
        this.detectInfo = detectInfo;
        this.blackDuckConnectionDetails = blackDuckConnectionDetails;
    }

    public BlackDuckServerConfig createServerConfig(IntLogger intLogger) throws DetectUserFriendlyException {
        IntLogger logger;
        if (intLogger == null) {
            logger = new SilentIntLogger();
        } else {
            logger = intLogger;
        }
        ConnectionDetails connectionDetails = blackDuckConnectionDetails.getConnectionDetails();

        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = new BlackDuckServerConfigBuilder()
                                                                        .setExecutorService(Executors.newFixedThreadPool(blackDuckConnectionDetails.getParallelProcessors()))
                                                                        .setLogger(logger);

        blackDuckServerConfigBuilder.setProperties(blackDuckConnectionDetails.getBlackduckProperties().entrySet());
        blackDuckServerConfigBuilder.setProperty(BLACK_DUCK_SERVER_CONFIG_BUILDER_TIMEOUT_KEY, blackDuckConnectionDetails.getConnectionDetails().getTimeout().toString());
        blackDuckServerConfigBuilder.setSolutionDetails(new NameVersion("synopsys_detect", detectInfo.getDetectVersion()));
        Optional<Boolean> shouldIgnore = blackDuckConnectionDetails.getBlackDuckUrl().map(blackduckUrl -> ProxyUtil.shouldIgnoreUrl(blackduckUrl, connectionDetails.getIgnoredProxyHostPatterns(), logger));
        if (shouldIgnore.isPresent() && Boolean.TRUE.equals(shouldIgnore.get())) {
            blackDuckServerConfigBuilder.setProxyInfo(ProxyInfo.NO_PROXY_INFO);
        } else {
            blackDuckServerConfigBuilder.setProxyInfo(connectionDetails.getProxyInformation());
        }

        try {
            return blackDuckServerConfigBuilder.build();
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException("Failed to configure Black Duck server connection: " + e.getMessage(), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
