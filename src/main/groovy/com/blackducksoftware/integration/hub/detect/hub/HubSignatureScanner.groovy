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
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationName
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.summary.Result
import com.blackducksoftware.integration.hub.detect.summary.ScanSummaryResult
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.model.request.ProjectRequest
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder
import com.blackducksoftware.integration.hub.scan.HubScanConfig
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HubSignatureScanner implements SummaryResultReporter {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    HubManager hubManager

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    OfflineScanner offlineScanner

    @Autowired
    CodeLocationNameService codeLocationNameService

    private Set<String> registeredPaths = []
    private Set<String> registeredPathsToExclude = []
    private Map<String, Result> scanSummaryResults = new HashMap<>();

    public void registerPathToScan(File file, String... fileNamesToExclude) {
        boolean scannerEnabled = !detectConfiguration.hubSignatureScannerDisabled;
        boolean customPathOverride = detectConfiguration.hubSignatureScannerPaths.size() > 0;

        if (scannerEnabled && !customPathOverride) {
            String matchingExcludedPath = detectConfiguration.hubSignatureScannerPathsToExclude.find {
                file.canonicalPath.startsWith(it)
            }

            if (matchingExcludedPath) {
                logger.info("Not registering excluded path ${file.canonicalPath} to scan")
            } else if (file.exists() && (file.isFile() || file.isDirectory())) {
                logger.info("Registering path ${file.canonicalPath} to scan")
                scanSummaryResults.put(file.getCanonicalPath(), Result.FAILURE);
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
        } else if (!scannerEnabled) {
            logger.info("Not registering path ${file.canonicalPath}, scan is disabled");
        } else if (customPathOverride){
            logger.info("Not scanning path ${file.canonicalPath}, scan paths provided");
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
        return projectVersionView;
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

    @Override
    public List<ScanSummaryResult> getDetectSummaryResults() {
        List<ScanSummaryResult> detectSummaryResults = new ArrayList<>();
        for (Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            detectSummaryResults.add(new ScanSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
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

            String hubDetectVersion = detectConfiguration.getBuildInfo().detectVersion
            projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, hubScanConfig, projectRequest, false, ThirdPartyName.DETECT, hubDetectVersion, hubDetectVersion)
            scanSummaryResults.put(canonicalPath, Result.SUCCESS);
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
                File scannerDirectory = new File(detectConfiguration.scanOutputDirectoryPath)
                File toolsDirectory = detectFileManager.createDirectory('tools', false)
                hubScanConfigBuilder.toolsDir = toolsDirectory
            }

            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()
            offlineScanner.offlineScan(hubScanConfig, detectConfiguration.hubSignatureScannerOfflineLocalPath)
            scanSummaryResults.put(canonicalPath, Result.SUCCESS);
            logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
        } catch (Exception e) {
            logger.error("${detectProject.projectName}/${detectProject.projectVersionName} - ${canonicalPath} was not scanned by the BlackDuck CLI: ${e.message}")
        }
    }

    private HubScanConfigBuilder createScanConfigBuilder(DetectProject detectProject, String canonicalPath) {
        File scannerDirectory = new File(detectConfiguration.scanOutputDirectoryPath)
        File toolsDirectory = detectFileManager.createDirectory('tools', false)

        HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
        hubScanConfigBuilder.scanMemory = detectConfiguration.hubSignatureScannerMemory
        hubScanConfigBuilder.toolsDir = toolsDirectory
        hubScanConfigBuilder.workingDirectory = scannerDirectory
        hubScanConfigBuilder.addScanTargetPath(canonicalPath)
        hubScanConfigBuilder.cleanupLogsOnSuccess = detectConfiguration.cleanupBomToolFiles
        hubScanConfigBuilder.dryRun = detectConfiguration.hubSignatureScannerDryRun

        String projectName = detectProject.projectName
        String projectVersionName = detectProject.projectVersionName
        String sourcePath = detectConfiguration.sourcePath
        String prefix = detectConfiguration.projectCodeLocationPrefix
        String suffix = detectConfiguration.projectCodeLocationSuffix
        CodeLocationName codeLocationName = codeLocationNameService.createScanName(sourcePath, canonicalPath, projectName, projectVersionName, prefix, suffix)
        String codeLocationNameString = codeLocationNameService.generateScanCurrent(codeLocationName)
        hubScanConfigBuilder.codeLocationAlias = codeLocationNameString

        if (detectConfiguration.hubSignatureScannerExclusionPatterns) {
            hubScanConfigBuilder.setExcludePatterns(detectConfiguration.hubSignatureScannerExclusionPatterns)
        } else if (registeredPathsToExclude) {
            hubScanConfigBuilder.setExcludePatterns(registeredPathsToExclude as String[])
        }

        hubScanConfigBuilder
    }
}
