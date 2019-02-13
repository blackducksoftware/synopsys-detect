/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.lifecycle.boot;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.phonehome.OnlinePhoneHomeManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.response.CurrentVersionView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.ConnectionResult;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ConnectivityChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConnectivityResult determineConnectivity(DetectConfiguration detectConfiguration, DetectOptionManager detectOptionManager, DetectInfo detectInfo, Gson gson, ObjectMapper objectMapper, EventSystem eventSystem)
        throws DetectUserFriendlyException {

        logger.info("Detect will check if it can communicate with the Black Duck Server.");
        Slf4jIntLogger blackduckLogger = new Slf4jIntLogger(logger);
        BlackDuckServerConfig blackDuckServerConfig = detectOptionManager.createBlackduckServerConfig();

        logger.info("Attempting connection to the Black Duck server");

        ConnectionResult connectionResult = blackDuckServerConfig.attemptConnection(blackduckLogger);

        if (connectionResult.isFailure()) {
            logger.error("Failed to connect to the Black Duck server");
            logger.debug(String.format("The Black Duck server responded with a status code of %d", connectionResult.getHttpStatusCode()));
            return ConnectivityResult.failure(connectionResult.getErrorMessage().orElse("Could not reach the Black Duck server or the credentials were invalid."));
        }

        logger.info("Connection to the Black Duck server was successful");//TODO: Get a detailed reason of why canConnect failed.

        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckServerConfig.createBlackDuckServicesFactory(gson, objectMapper, blackduckLogger);

        try {
            final BlackDuckService blackDuckService = blackDuckServicesFactory.createBlackDuckService();
            final CurrentVersionView currentVersion = blackDuckService.getResponse(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE);
            logger.info(String.format("Successfully connected to BlackDuck (version %s)!", currentVersion.getVersion()));
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException("Could not determine which version of Black Duck detect connected to.", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        }

        Map<String, String> additionalMetaData = detectConfiguration.getPhoneHomeProperties();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper = BlackDuckPhoneHomeHelper.createAsynchronousPhoneHomeHelper(blackDuckServicesFactory, executorService);
        PhoneHomeManager phoneHomeManager = new OnlinePhoneHomeManager(additionalMetaData, detectInfo, gson, eventSystem, blackDuckPhoneHomeHelper);

        return ConnectivityResult.success(blackDuckServicesFactory, phoneHomeManager, blackDuckServerConfig);
    }
}
