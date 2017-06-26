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

import com.blackducksoftware.integration.hub.builder.HubScanConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.phonehome.IntegrationInfo
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.scan.HubScanConfig
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class HubSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    @Autowired
    DetectFileManager detectFileManager

    def pathToProjectName = [:]
    def pathToProjectVersionName = [:]

    public void registerDirectoryToScan(File directory, String projectName, String projectVersionName) {
        if (directory.exists() && projectName && projectVersionName) {
            pathToProjectName[directory.canonicalPath] = projectName
            pathToProjectVersionName[directory.canonicalPath] = projectVersionName
        } else {
            logger.warn("Tried to register a scan for ${directory.canonicalPath} without enough information: ${projectName}/${projectVersionName}")
        }
    }

    public void scanFiles() {
        try {
            Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
            HubServerConfig hubServerConfig = hubManager.createHubServerConfig(slf4jIntLogger)
            HubServicesFactory hubServicesFactory = hubManager.createHubServicesFactory(slf4jIntLogger, hubServerConfig)
            CLIDataService cliDataService = hubServicesFactory.createCLIDataService(slf4jIntLogger, 0L)

            if (detectConfiguration.projectName && detectConfiguration.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
                detectConfiguration.hubSignatureScannerPaths.each {
                    scanDirectory(cliDataService, hubServerConfig, new File(it), detectConfiguration.projectName, detectConfiguration.projectVersionName)
                }
            } else {
                pathToProjectName.each { path, projectName ->
                    String projectVersionName = pathToProjectVersionName[path]
                    logger.info("Attempting to scan ${path} for ${projectName}/${projectVersionName}")
                    try {
                        scanDirectory(cliDataService, hubServerConfig, new File(path), projectName, projectVersionName)
                    } catch (Exception e) {
                        logger.error("Not able to scan ${path}: ${e.message}")
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Your Hub configuration is not valid: ${e.message}")
        }
    }

    private void scanDirectory(CLIDataService cliDataService, HubServerConfig hubServerConfig, File directory, String project, String version) {
        try {
            String canonicalPath = directory.canonicalPath
            ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder()
            projectRequestBuilder.projectName = project
            projectRequestBuilder.versionName = version
            ProjectRequest projectRequest = projectRequestBuilder.build()

            File scannerDirectory = detectFileManager.createDirectory('signature_scanner')
            File toolsDirectory = detectFileManager.createDirectory(scannerDirectory, 'tools')

            HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
            hubScanConfigBuilder.scanMemory = detectConfiguration.hubSignatureScannerMemory
            hubScanConfigBuilder.toolsDir = toolsDirectory
            hubScanConfigBuilder.workingDirectory = toolsDirectory
            hubScanConfigBuilder.addScanTargetPath(canonicalPath)

            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()

            IntegrationInfo integrationInfo = new IntegrationInfo('Hub-Detect', '0.0.6-SNAPSHOT', '0.0.6-SNAPSHOT')
            //TODO make version.txt file during build
            ProjectVersionView projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, integrationInfo)
            logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
        } catch (Exception e) {
            logger.error("${project}/${version} was not scanned by the BlackDuck CLI: ${e.message}")
        }
    }
}