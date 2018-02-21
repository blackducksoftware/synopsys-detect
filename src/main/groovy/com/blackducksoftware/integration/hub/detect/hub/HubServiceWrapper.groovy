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
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.CodeLocationService
import com.blackducksoftware.integration.hub.service.HubService
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.hub.service.PhoneHomeService
import com.blackducksoftware.integration.hub.service.ProjectService
import com.blackducksoftware.integration.hub.service.ReportService
import com.blackducksoftware.integration.hub.service.ScanStatusService
import com.blackducksoftware.integration.hub.service.SignatureScannerService
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
    HubServicesFactory hubServicesFactory

    void init() {
        try {
            slf4jIntLogger = new Slf4jIntLogger(logger)
            hubServerConfig = createHubServerConfig(slf4jIntLogger)
            hubServicesFactory = createHubServicesFactory(slf4jIntLogger, hubServerConfig)
        } catch (IllegalStateException | EncryptionException e) {
            throw new DetectUserFriendlyException("Not able to initialize Hub connection: ${e.message}", e, ExitCodeType.FAILURE_HUB_CONNECTIVITY)
        }
        HubService hubService = createHubService()
        CurrentVersionView currentVersion = hubService.getResponseFromPath(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE)
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

            final RestConnection connection = hubServerConfig.createRestConnection(slf4jIntLogger)
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

    HubService createHubService() {
        hubServicesFactory.createHubService()
    }

    ProjectService createProjectService() {
        hubServicesFactory.createProjectService()
    }

    PhoneHomeService createPhoneHomeService() {
        hubServicesFactory.createPhoneHomeService()
    }


    CodeLocationService createCodeLocationService() {
        hubServicesFactory.createCodeLocationService()
    }

    ScanStatusService createScanStatusService() {
        hubServicesFactory.createScanStatusService(detectConfiguration.getApiTimeout())
    }

    ReportService createReportService() {
        hubServicesFactory.createReportService(detectConfiguration.getApiTimeout())
    }

    SignatureScannerService createSignatureScannerService() {
        hubServicesFactory.createSignatureScannerService(120000L)
    }

    private HubServicesFactory createHubServicesFactory(IntLogger slf4jIntLogger, HubServerConfig hubServerConfig) {
        RestConnection restConnection = hubServerConfig.createRestConnection(slf4jIntLogger)

        new HubServicesFactory(restConnection)
    }

    private HubServerConfig createHubServerConfig(IntLogger slf4jIntLogger) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(detectConfiguration.getHubUrl())
        hubServerConfigBuilder.setTimeout(detectConfiguration.getHubTimeout())
        hubServerConfigBuilder.setUsername(detectConfiguration.getHubUsername())
        hubServerConfigBuilder.setPassword(detectConfiguration.getHubPassword())
        hubServerConfigBuilder.setApiToken(detectConfiguration.getHubApiToken())

        hubServerConfigBuilder.setProxyHost(detectConfiguration.getHubProxyHost())
        hubServerConfigBuilder.setProxyPort(detectConfiguration.getHubProxyPort())
        hubServerConfigBuilder.setProxyUsername(detectConfiguration.getHubProxyUsername())
        hubServerConfigBuilder.setProxyPassword(detectConfiguration.getHubProxyPassword())
        hubServerConfigBuilder.setAlwaysTrustServerCertificate(detectConfiguration.getHubTrustCertificate())
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder.build()
    }
}
