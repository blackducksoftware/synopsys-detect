/*
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.hub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.exception.EncryptionException
import com.blackducksoftware.integration.exception.IntegrationException
import com.blackducksoftware.integration.hub.api.generated.discovery.ApiDiscovery
import com.blackducksoftware.integration.hub.api.generated.response.CurrentVersionView
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.CLIDataService
import com.blackducksoftware.integration.hub.dataservice.CodeLocationDataService
import com.blackducksoftware.integration.hub.dataservice.HubDataService
import com.blackducksoftware.integration.hub.dataservice.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.ReportDataService
import com.blackducksoftware.integration.hub.dataservice.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubDataServicesFactory
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.SilentLogger
import com.blackducksoftware.integration.log.Slf4jIntLogger

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HubServiceWrapper {
    private final Logger logger = LoggerFactory.getLogger(HubServiceWrapper.class)

    @Autowired
    DetectConfiguration detectConfiguration

    Slf4jIntLogger slf4jIntLogger
    HubServerConfig hubServerConfig
    HubDataServicesFactory hubDataServicesFactory

    void init() {
        try {
            slf4jIntLogger = new Slf4jIntLogger(logger)
            hubServerConfig = createHubServerConfig(slf4jIntLogger)
            hubDataServicesFactory = createHubDataServicesFactory(slf4jIntLogger, hubServerConfig)
        } catch (IllegalStateException | EncryptionException e) {
            throw new DetectUserFriendlyException("Not able to initialize Hub connection: ${e.message}", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        }
        HubDataService hubDataService = createHubDataService()
        CurrentVersionView currentVersion = hubDataService.getResponseFromLinkResponse(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE)
        logger.info(String.format("Successfully connected to Hub (version %s)!", currentVersion.version))
    }

    public boolean testHubConnection() {
        testHubConnection(true);
    }

    public boolean testHubConnection(boolean detailedLog) {
        logger.info("Attempting connection to the Hub")
        try {
            IntLogger slf4jIntLogger;
            if (detailedLog) {
                slf4jIntLogger = new Slf4jIntLogger(logger);
            } else {
                slf4jIntLogger = new SilentLogger();
            }
            HubServerConfig hubServerConfig = createHubServerConfig(slf4jIntLogger);

            final RestConnection connection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)
            connection.connect()
            logger.info("Connection to the Hub was successful")
            return true;
        } catch (IllegalStateException e) {
            if (detailedLog) {
                logger.error("Failed to build the server configuration: ${e.message}", e)
            }
        } catch (IntegrationException e) {
            if (detailedLog) {
                logger.error("Could not reach the Hub server or the credentials were invalid: ${e.message}", e)
            }
        }
        return false;
    }

    HubDataService createHubDataService() {
        hubDataServicesFactory.createHubDataService()
    }

    ProjectDataService createProjectDataService() {
        hubDataServicesFactory.createProjectDataService()
    }

    PhoneHomeDataService createPhoneHomeDataService() {
        hubDataServicesFactory.createPhoneHomeDataService()
    }


    CodeLocationDataService createCodeLocationDataService() {
        hubDataServicesFactory.createCodeLocationDataService()
    }

    ScanStatusDataService createScanStatusDataService() {
        hubDataServicesFactory.createScanStatusDataService(detectConfiguration.getApiTimeout())
    }

    PolicyStatusDataService createPolicyStatusDataService() {
        hubDataServicesFactory.createPolicyStatusDataService()
    }

    ReportDataService createReportDataService() {
        hubDataServicesFactory.createReportDataService(detectConfiguration.getApiTimeout())
    }

    CLIDataService createCliDataService() {
        hubDataServicesFactory.createCLIDataService(120000L)
    }

    private HubDataServicesFactory createHubDataServicesFactory(IntLogger slf4jIntLogger, HubServerConfig hubServerConfig) {
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        new HubDataServicesFactory(restConnection)
    }

    private HubServerConfig createHubServerConfig(IntLogger slf4jIntLogger) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(detectConfiguration.getHubUrl())
        hubServerConfigBuilder.setTimeout(detectConfiguration.getHubTimeout())
        hubServerConfigBuilder.setUsername(detectConfiguration.getHubUsername())
        hubServerConfigBuilder.setPassword(detectConfiguration.getHubPassword())

        hubServerConfigBuilder.setProxyHost(detectConfiguration.getHubProxyHost())
        hubServerConfigBuilder.setProxyPort(detectConfiguration.getHubProxyPort())
        hubServerConfigBuilder.setProxyUsername(detectConfiguration.getHubProxyUsername())
        hubServerConfigBuilder.setProxyPassword(detectConfiguration.getHubProxyPassword())
        hubServerConfigBuilder.setAlwaysTrustServerCertificate(detectConfiguration.getHubTrustCertificate())
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder.build()
    }
}
