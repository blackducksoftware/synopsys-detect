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

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.buildtool.BuildToolConstants
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.policychecker.PolicyChecker
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.phonehome.IntegrationInfo
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.util.ResourceUtil

@Component
class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class)

    @Autowired
    DetectConfiguration detectConfiguration

    void uploadBdioFiles(List<File> createdBdioFiles) {
        if (!createdBdioFiles) {
            return
        }

        try {
            Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
            HubServerConfig hubServerConfig = createHubServerConfig(slf4jIntLogger)
            HubServicesFactory hubServicesFactory = createHubServicesFactory(slf4jIntLogger, hubServerConfig)
            BomImportRequestService bomImportRequestService = hubServicesFactory.createBomImportRequestService()
            PhoneHomeDataService phoneHomeDataService = hubServicesFactory.createPhoneHomeDataService(slf4jIntLogger)

            createdBdioFiles.each { file ->
                logger.info("uploading ${file.name} to ${detectConfiguration.getHubUrl()}")
                bomImportRequestService.importBomFile(file, BuildToolConstants.BDIO_FILE_MEDIA_TYPE)

                if (detectConfiguration.getPolicyCheck().equalsIgnoreCase("true")) {
                    logger.info("Checking for policy violations...")
                    ScanStatusDataService scanStatusDataService = hubServicesFactory.createScanStatusDataService(slf4jIntLogger, detectConfiguration.getPolicyTimeout())
                    PolicyStatusDataService policyStatusDataService = hubServicesFactory.createPolicyStatusDataService(slf4jIntLogger)
                    PolicyChecker policyChecker = new PolicyChecker(scanStatusDataService, policyStatusDataService)
                    def policyCheck = policyChecker.checkForPolicyViolations(file)
                    logger.info("Policy check returned result " + policyCheck.toString().replace('_', ' '))
                }

                if (detectConfiguration.getCleanupBdioFiles()) {
                    file.delete()
                }
            }

            String hubDetectVersion = ResourceUtil.getResourceAsString('version.txt', StandardCharsets.UTF_8)
            IntegrationInfo integrationInfo = new IntegrationInfo('Hub-Detect', hubDetectVersion, hubDetectVersion)
            phoneHomeDataService.phoneHome(hubServerConfig, integrationInfo)
        } catch (Exception e) {
            logger.error("Your Hub configuration is not valid: ${e.message}")
        }
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

        hubServerConfigBuilder.setAutoImportHttpsCertificates(detectConfiguration.getHubAutoImportCertificate())
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder.build()
    }
}
