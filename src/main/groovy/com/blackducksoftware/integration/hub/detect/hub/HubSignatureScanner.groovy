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
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.scan.HubScanConfig

@Component
@groovy.transform.TypeChecked
class HubSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    OfflineScanner offlineScanner

    private Set<String> registeredPaths = []
    private Set<String> registeredPathsToExclude = []

    public void registerPathToScan(File file, String... fileNamesToExclude) {
        String matchingExcludedPath = detectConfiguration.hubSignatureScannerPathsToExclude.find {
            file.canonicalPath.startsWith(it)
        }

        if (matchingExcludedPath) {
            logger.info("Not registering excluded path ${file.canonicalPath} to scan")
        } else if (file.exists() && (file.isFile() || file.isDirectory())) {
            logger.info("Registering path ${file.canonicalPath} to scan")
            registeredPaths.add(file.canonicalPath)
            if (fileNamesToExclude) {
                for (String fileNameToExclude : fileNamesToExclude) {
                    File fileToExclude = detectFileManager.findFile(file, fileNameToExclude)
                    if (fileToExclude) {
                        String pattern = fileToExclude.getCanonicalPath().replace(file.canonicalPath, '')
                        if (pattern.contains('\\\\')) {
                            pattern = pattern.replace('\\\\', '/')
                        }
                        if (pattern.contains('\\')) {
                            pattern = pattern.replace('\\', '/')
                        }
                        pattern = pattern + '/'
                        registeredPathsToExclude.add(pattern)
                    }
                }
            }
        } else {
            logger.warn("Tried to register a scan for ${file.canonicalPath} but it doesn't appear to exist or it isn't a file or directory.")
        }
    }

    public ProjectVersionView scanPaths(HubServerConfig hubServerConfig, CLIDataService cliDataService, DetectProject detectProject) {
        ProjectVersionView projectVersionView = null
        if (detectProject.projectName && detectProject.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
            detectConfiguration.hubSignatureScannerPaths.each { String path ->
                projectVersionView = scanPath(cliDataService, hubServerConfig, new File(path).canonicalPath, detectProject)
            }
        } else {
            registeredPaths.each {
                logger.info("Attempting to scan ${it} for ${detectProject.projectName}/${detectProject.projectVersionName}")
                ProjectVersionView scanProject = scanPath(cliDataService, hubServerConfig, it, detectProject)
                if (!projectVersionView) {
                    projectVersionView = scanProject
                }
            }
        }
        return projectVersionView
    }

    public void scanPathsOffline(DetectProject detectProject) {
        if (detectProject.projectName && detectProject.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
            detectConfiguration.hubSignatureScannerPaths.each { String path ->
                scanPathOffline(new File(path).canonicalPath, detectProject)
            }
        } else {
            registeredPaths.each {
                logger.info("Attempting to scan ${it} for ${detectProject.projectName}/${detectProject.projectVersionName}")
                scanPathOffline(it, detectProject)
            }
        }
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

            HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath)
            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()

            String hubDetectVersion = detectConfiguration.getBuildInfo().getDetectVersion()
            projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, 'Hub-Detect', hubDetectVersion, hubDetectVersion)
            logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
        } catch (Exception e) {
            logger.error("${detectProject.projectName}/${detectProject.projectVersionName} - ${canonicalPath} was not scanned by the BlackDuck CLI: ${e.message}")
        }
        return projectVersionView
    }

    private void scanPathOffline(String canonicalPath, DetectProject detectProject) {
        try {
            HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath)
            hubScanConfigBuilder.setDryRun(true)

            if (!detectConfiguration.hubSignatureScannerOfflineLocalPath) {
                File scannerDirectory = detectFileManager.createDirectory('signature_scanner')
                File toolsDirectory = detectFileManager.createDirectory(scannerDirectory, 'tools')
                hubScanConfigBuilder.toolsDir = toolsDirectory
            }

            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()
            offlineScanner.offlineScan(hubScanConfig, detectConfiguration.hubSignatureScannerOfflineLocalPath)
        } catch (Exception e) {
            logger.error("${detectProject.projectName}/${detectProject.projectVersionName} - ${canonicalPath} was not scanned by the BlackDuck CLI: ${e.message}")
        }
    }

    private HubScanConfigBuilder createScanConfigBuilder(DetectProject detectProject, String canonicalPath) {
        File scannerDirectory = detectFileManager.createDirectory('signature_scanner')
        File toolsDirectory = detectFileManager.createDirectory(scannerDirectory, 'tools')

        HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
        hubScanConfigBuilder.scanMemory = detectConfiguration.hubSignatureScannerMemory
        hubScanConfigBuilder.toolsDir = toolsDirectory
        hubScanConfigBuilder.workingDirectory = scannerDirectory
        hubScanConfigBuilder.addScanTargetPath(canonicalPath)
        hubScanConfigBuilder.cleanupLogsOnSuccess = detectConfiguration.cleanupBomToolFiles
        hubScanConfigBuilder.dryRun = detectConfiguration.hubSignatureScannerDryRun

        final String codeLocationName = detectProject.getCodeLocationName(detectConfiguration.sourcePath, canonicalPath, detectFileManager.extractFinalPieceFromPath(detectConfiguration.sourcePath), detectConfiguration.getProjectCodeLocationPrefix(), 'Hub Detect Scan')
        hubScanConfigBuilder.codeLocationAlias = codeLocationName

        if (detectConfiguration.hubSignatureScannerExclusionPatterns) {
            hubScanConfigBuilder.setExcludePatterns(detectConfiguration.hubSignatureScannerExclusionPatterns)
        } else if (registeredPathsToExclude) {
            hubScanConfigBuilder.setExcludePatterns(registeredPathsToExclude as String[])
        }

        hubScanConfigBuilder
    }
}
