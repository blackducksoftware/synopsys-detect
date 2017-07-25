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

import com.blackducksoftware.integration.hub.builder.HubScanConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.phonehome.IntegrationInfo
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.scan.HubScanConfig
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.util.ResourceUtil

@Component
class HubSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    @Autowired
    DetectFileManager detectFileManager

    private List<String> registeredPaths = []

    public void registerPathToScan(File file) {
        boolean excluded = false
        for (File excludedPath : detectConfiguration.getHubSignatureScannerExcludedPaths()) {
            if (file.canonicalPath.startsWith(excludedPath.canonicalPath)) {
                excluded = true
                break
            }
        }
        if (excluded) {
            logger.info("Not registering excluded path ${file.canonicalPath} to scan")
        } else if (file.exists() && (file.isFile() || file.isDirectory())) {
            logger.info("Registering path ${file.canonicalPath} to scan")
            registeredPaths.add(file.canonicalPath)
        } else {
            logger.warn("Tried to register a scan for ${file.canonicalPath} but it doesn't appear to exist or it isn't a file or directory.")
        }
    }

    public void scanPaths(HubServerConfig hubServerConfig, HubServicesFactory hubServicesFactory, DetectProject detectProject) {
        Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
        CLIDataService cliDataService = hubServicesFactory.createCLIDataService(slf4jIntLogger, 120000L)

        if (detectProject.projectName && detectProject.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
            detectConfiguration.hubSignatureScannerPaths.each {
                scanPath(cliDataService, hubServerConfig, new File(it).canonicalPath, detectProject.projectName, detectProject.projectVersionName)
            }
        } else {
            registeredPaths.each {
                logger.info("Attempting to scan ${it} for ${detectProject.projectName}/${detectProject.projectVersionName}")
                try {
                    scanPath(cliDataService, hubServerConfig, it, detectProject.projectName, detectProject.projectVersionName)
                } catch (Exception e) {
                    logger.error("Not able to scan ${it}: ${e.message}")
                }
            }
        }
    }

    private void scanPath(CLIDataService cliDataService, HubServerConfig hubServerConfig, String canonicalPath, String project, String version) {
        try {
            ProjectRequestBuilder builder = new ProjectRequestBuilder()
            builder.setProjectName(project)
            builder.setVersionName(version)
            builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments())
            builder.setPhase(detectConfiguration.getProjectVersionPhase())
            builder.setDistribution(detectConfiguration.getProjectVersionDistribution())
            ProjectRequest projectRequest = builder.build()

            File scannerDirectory = detectFileManager.createDirectory('signature_scanner')
            File toolsDirectory = detectFileManager.createDirectory(scannerDirectory, 'tools')

            HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
            hubScanConfigBuilder.scanMemory = detectConfiguration.hubSignatureScannerMemory
            hubScanConfigBuilder.toolsDir = toolsDirectory
            hubScanConfigBuilder.workingDirectory = scannerDirectory
            hubScanConfigBuilder.addScanTargetPath(canonicalPath)
            hubScanConfigBuilder.cleanupLogsOnSuccess = detectConfiguration.getCleanupBomToolFiles()
            if (detectConfiguration.projectCodeLocationName) {
                hubScanConfigBuilder.codeLocationAlias = "${detectConfiguration.projectCodeLocationName} Hub Detect Scan"
            }

            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()

            String hubDetectVersion = ResourceUtil.getResourceAsString('version.txt', StandardCharsets.UTF_8)
            IntegrationInfo integrationInfo = new IntegrationInfo('Hub-Detect', hubDetectVersion, hubDetectVersion)
            ProjectVersionView projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, integrationInfo)
            logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
        } catch (Exception e) {
            logger.error("${project}/${version} was not scanned by the BlackDuck CLI: ${e.message}")
        }
    }
}