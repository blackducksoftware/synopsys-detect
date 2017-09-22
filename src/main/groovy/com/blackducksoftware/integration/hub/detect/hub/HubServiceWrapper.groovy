/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.IntLogger
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
            throw new DetectException("Not able to initialize Hub connection: ${e.message}")
        }
    }

    public void testHubConnection() {
        logger.info("Attempting connection to the Hub")
        try {
            IntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
            HubServerConfig hubServerConfig = createHubServerConfig(slf4jIntLogger)

            final RestConnection connection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)
            connection.connect()
            logger.info("Connection to the Hub was successful")
        } catch (IllegalStateException e) {
            logger.error("Failed to build the server configuration: ${e.message}", e)
        } catch (IntegrationException e) {
            logger.error("Could not reach the Hub server or the credentials were invalid: ${e.message}", e)
        }
    }

    ProjectRequestService createProjectRequestService() {
        hubServicesFactory.createProjectRequestService()
    }

    ProjectVersionRequestService createProjectVersionRequestService() {
        hubServicesFactory.createProjectVersionRequestService()
    }

    BomImportRequestService createBomImportRequestService() {
        hubServicesFactory.createBomImportRequestService()
    }

    PhoneHomeDataService createPhoneHomeDataService() {
        hubServicesFactory.createPhoneHomeDataService()
    }

    ProjectDataService createProjectDataService() {
        hubServicesFactory.createProjectDataService()
    }

    CodeLocationRequestService createCodeLocationRequestService() {
        hubServicesFactory.createCodeLocationRequestService()
    }

    MetaService createMetaService() {
        hubServicesFactory.createMetaService()
    }

    ScanSummaryRequestService createScanSummaryRequestService() {
        hubServicesFactory.createScanSummaryRequestService()
    }

    ScanStatusDataService createScanStatusDataService() {
        hubServicesFactory.createScanStatusDataService(detectConfiguration.getApiTimeout())
    }

    PolicyStatusDataService createPolicyStatusDataService() {
        hubServicesFactory.createPolicyStatusDataService()
    }

    RiskReportDataService createRiskReportDataService() {
        hubServicesFactory.createRiskReportDataService(detectConfiguration.getApiTimeout())
    }

    CLIDataService createCliDataService() {
        hubServicesFactory.createCLIDataService(120000L)
    }

    private HubServicesFactory createHubServicesFactory(Slf4jIntLogger slf4jIntLogger, HubServerConfig hubServerConfig) {
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        new HubServicesFactory(restConnection)
    }

    private HubServerConfig createHubServerConfig(Slf4jIntLogger slf4jIntLogger) {
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
