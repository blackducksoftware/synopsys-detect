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
        String matchingExcludedPath = detectConfiguration.hubSignatureScannerPathsToExclude.find {
            file.canonicalPath.startsWith(it)
        }

        if (matchingExcludedPath) {
            logger.info("Not registering excluded path ${file.canonicalPath} to scan")
        } else if (file.exists() && (file.isFile() || file.isDirectory())) {
            logger.info("Registering path ${file.canonicalPath} to scan")
            registeredPaths.add(file.canonicalPath)
        } else {
            logger.warn("Tried to register a scan for ${file.canonicalPath} but it doesn't appear to exist or it isn't a file or directory.")
        }
    }

    public ProjectVersionView scanPaths(HubServerConfig hubServerConfig, CLIDataService cliDataService, DetectProject detectProject) {
        ProjectVersionView projectVersionView = null
        if (detectProject.projectName && detectProject.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
            detectConfiguration.hubSignatureScannerPaths.each {
                projectVersionView = scanPath(cliDataService, hubServerConfig, new File(it).canonicalPath, detectProject)
            }
        } else {
            registeredPaths.each {
                logger.info("Attempting to scan ${it} for ${detectProject.projectName}/${detectProject.projectVersionName}")
                try {
                    ProjectVersionView scanProject = scanPath(cliDataService, hubServerConfig, it, detectProject)
                    if (!projectVersionView) {
                        projectVersionView = scanProject
                    }
                } catch (Exception e) {
                    logger.error("Not able to scan ${it}: ${e.message}")
                }
            }
        }
        return projectVersionView
    }

    private ProjectVersionView scanPath(CLIDataService cliDataService, HubServerConfig hubServerConfig, String canonicalPath, DetectProject detectProject) {
        ProjectVersionView projectVersionView = null
        try {
            ProjectRequestBuilder builder = new ProjectRequestBuilder()
            builder.setProjectName(detectProject.projectName)
            builder.setVersionName(detectProject.projectVersionName)
            builder.setProjectLevelAdjustments(detectConfiguration.projectLevelMatchAdjustments)
            builder.setPhase(detectConfiguration.projectVersionPhase)
            builder.setDistribution(detectConfiguration.projectVersionDistribution)
            ProjectRequest projectRequest = builder.build()

            File scannerDirectory = detectFileManager.createDirectory('signature_scanner')
            File toolsDirectory = detectFileManager.createDirectory(scannerDirectory, 'tools')

            HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
            hubScanConfigBuilder.scanMemory = detectConfiguration.hubSignatureScannerMemory
            hubScanConfigBuilder.toolsDir = toolsDirectory
            hubScanConfigBuilder.workingDirectory = scannerDirectory
            hubScanConfigBuilder.addScanTargetPath(canonicalPath)
            hubScanConfigBuilder.cleanupLogsOnSuccess = detectConfiguration.cleanupBomToolFiles
            hubScanConfigBuilder.dryRun = detectConfiguration.hubSignatureScannerDryRun

            final String codeLocationName = detectProject.getCodeLocationName(detectFileManager, detectConfiguration.sourcePath, canonicalPath, detectConfiguration.getProjectCodeLocationPrefix(), 'Hub Detect Scan')
            hubScanConfigBuilder.codeLocationAlias = codeLocationName

            if (detectConfiguration.hubSignatureScannerExclusionPatterns) {
                hubScanConfigBuilder.setExcludePatterns(detectConfiguration.hubSignatureScannerExclusionPatterns)
            }

            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()

            String hubDetectVersion = ResourceUtil.getResourceAsString('version.txt', StandardCharsets.UTF_8)
            IntegrationInfo integrationInfo = new IntegrationInfo('Hub-Detect', hubDetectVersion, hubDetectVersion)
            projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, integrationInfo)
            logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
        } catch (Exception e) {
            logger.error("${detectProject.projectName}/${detectProject.projectVersionName} was not scanned by the BlackDuck CLI: ${e.message}")
        }
        return projectVersionView
    }
}