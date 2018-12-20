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
package com.blackducksoftware.integration.hub.detect.hub;

import java.util.Map;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.response.CurrentVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.rest.BlackDuckRestConnection;
import com.synopsys.integration.blackduck.service.BinaryScannerService;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.BlackDuckRegistrationService;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeService;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.ResourceUtil;

public class HubServiceManager {
    private final Logger logger = LoggerFactory.getLogger(HubServiceManager.class);

    private final DetectConfiguration detectConfiguration;
    private final ConnectionManager connectionManager;
    private final Gson gson;
    private final ObjectMapper objectMapper;

    private Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger);
    private BlackDuckServerConfig hubServerConfig;
    private BlackDuckServicesFactory hubServicesFactory;

    public HubServiceManager(final DetectConfiguration detectConfiguration, final ConnectionManager connectionManager, final Gson gson, final ObjectMapper objectMapper) {
        this.detectConfiguration = detectConfiguration;
        this.connectionManager = connectionManager;
        this.gson = gson;
        this.objectMapper = objectMapper;
    }

    public void init() throws IntegrationException, DetectUserFriendlyException {
        try {
            hubServerConfig = createBlackDuckServerConfig(slf4jIntLogger);
            hubServicesFactory = createBlackDuckServicesFactory(slf4jIntLogger, hubServerConfig);
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Not able to process Black Duck connection: %s", e.getMessage()), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        }
        final BlackDuckService hubService = createBlackDuckService();
        final CurrentVersionView currentVersion = hubService.getResponse(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE);
        logger.info(String.format("Successfully connected to BlackDuck (version %s)!", currentVersion.getVersion()));
    }

    public boolean testBlackDuckConnection(final IntLogger intLogger) {
        try {
            assertBlackDuckConnection(intLogger);
            return true;
        } catch (final IntegrationException e) {
            intLogger.error(String.format("Could not reach the Black Duck server or the credentials were invalid: %s", e.getMessage()), e);
        }
        return false;
    }

    public void assertBlackDuckConnection(final IntLogger intLogger) throws IntegrationException {
        logger.info("Attempting connection to the Black Duck server");

        try {
            //FIXME need to actually test connection
            final BlackDuckServerConfig hubServerConfig = createBlackDuckServerConfig(intLogger);
            hubServerConfig.createRestConnection(intLogger);
            logger.info("Connection to the Black Duck server was successful");
        } catch (final IllegalArgumentException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    public BinaryScannerService createBinaryScannerService() {
        return hubServicesFactory.createBinaryScannerService();
    }

    public BlackDuckService createBlackDuckService() {
        return hubServicesFactory.createBlackDuckService();
    }

    public BlackDuckRegistrationService createBlackDuckRegistrationService() {
        return hubServicesFactory.createBlackDuckRegistrationService();
    }

    public ProjectService createProjectService() {
        return hubServicesFactory.createProjectService();
    }

    public BlackDuckPhoneHomeHelper createBlackDuckPhoneHomeHelper() {
        return BlackDuckPhoneHomeHelper.createAsynchronousPhoneHomeHelper(hubServicesFactory, Executors.newSingleThreadExecutor());
    }

    public CodeLocationService createCodeLocationService() {
        return hubServicesFactory.createCodeLocationService();
    }

    public CodeLocationCreationService createCodeLocationCreationService() {
        return hubServicesFactory.createCodeLocationCreationService();
    }

    public BdioUploadService createBdioUploadService() {
        return hubServicesFactory.createBdioUploadService();
    }

    public ReportService createReportService() throws IntegrationException {
        return hubServicesFactory.createReportService(detectConfiguration.getLongProperty(DetectProperty.DETECT_API_TIMEOUT, PropertyAuthority.None));
    }

    private BlackDuckServicesFactory createBlackDuckServicesFactory(final IntLogger slf4jIntLogger, final BlackDuckServerConfig hubServerConfig) throws IntegrationException {
        final BlackDuckRestConnection restConnection = hubServerConfig.createRestConnection(slf4jIntLogger);

        return new BlackDuckServicesFactory(gson, objectMapper, restConnection, slf4jIntLogger);
    }

    private BlackDuckServerConfig createBlackDuckServerConfig(final IntLogger slf4jIntLogger) {
        final BlackDuckServerConfigBuilder hubServerConfigBuilder = new BlackDuckServerConfigBuilder();
        hubServerConfigBuilder.setLogger(slf4jIntLogger);

        final Map<String, String> blackduckBlackDuckProperties = detectConfiguration.getBlackduckProperties();
        hubServerConfigBuilder.setFromProperties(blackduckBlackDuckProperties);

        return hubServerConfigBuilder.build();
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return hubServerConfig;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return hubServicesFactory;
    }

    public IntEnvironmentVariables getEnvironmentVariables() {
        try {
            return (IntEnvironmentVariables) BlackDuckServicesFactory.class.getDeclaredField("intEnvironmentVariables").get(hubServicesFactory);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return new IntEnvironmentVariables();
        }
    }

}
