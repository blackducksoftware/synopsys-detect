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

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView
import com.blackducksoftware.integration.hub.configuration.HubScanConfig
import com.blackducksoftware.integration.hub.configuration.HubScanConfigBuilder
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationName
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.summary.Result
import com.blackducksoftware.integration.hub.detect.summary.ScanSummaryResult
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.service.SignatureScannerService
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HubSignatureScanner implements SummaryResultReporter, ExitCodeReporter {
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
        registerPathToScan(file, fileNamesToExclude, false);
    }

    /**
     * When the scanner is NOT enabled, no paths will be registered to scan.
     *
     * If the scanner is enabled and there is at least one explicit path set, ONLY the explicit path(s) will be scanned.
     *
     * If the scanner is enabled, no explicit paths are set, and snippet mode is enabled, ONLY the source path will be scanned.
     *
     * In all other cases, the bom tools will, when they can, identify good candidates for registering scan targets.
     */
    public void registerPathToScan(File file, String... fileNamesToExclude, boolean forceRegistering) {
        boolean scannerEnabled = !detectConfiguration.hubSignatureScannerDisabled;
        boolean customPathOverride = detectConfiguration.hubSignatureScannerPaths.size() > 0;
        boolean snippetModeEnabled = detectConfiguration.hubSignatureScannerSnippetMode;

        if (scannerEnabled && (!customPathOverride && !snippetModeEnabled || forceRegistering)) {
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
        } else if (customPathOverride) {
            logger.info("Not scanning path ${file.canonicalPath}, scan paths provided");
        }
    }

    public ProjectVersionView scanPaths(HubServerConfig hubServerConfig, SignatureScannerService signatureScannerService, DetectProject detectProject) {
        ProjectVersionView projectVersionView = null

        ProjectRequest projectRequest = createProjectRequest(detectProject)
        Set<String> canonicalPathsToScan = registeredPaths
        if (detectProject.projectName && detectProject.projectVersionName && detectConfiguration.hubSignatureScannerPaths) {
            canonicalPathsToScan = new HashSet<>()
            detectConfiguration.hubSignatureScannerPaths.each { String path ->
                canonicalPathsToScan.add(new File(path).canonicalPath)
            }
        }

        List<ScanPathCallable> scanPathCallables = new ArrayList<>()
        canonicalPathsToScan.each { String canonicalPath ->
            HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, canonicalPath)
            HubScanConfig hubScanConfig = hubScanConfigBuilder.build()
            ScanPathCallable scanPathCallable = new ScanPathCallable(signatureScannerService, hubServerConfig, hubScanConfig, projectRequest, canonicalPath, scanSummaryResults);
            scanPathCallables.add(scanPathCallable)
        }

        ExecutorService pool = Executors.newFixedThreadPool(detectConfiguration.hubSignatureScannerParallelProcessors)
        try {
            scanPathCallables.collect { pool.submit(it) }.each {
                ProjectVersionView projectVersionViewFromScan = it.get()
                if (projectVersionViewFromScan != null) {
                    projectVersionView = projectVersionViewFromScan
                }
            }
        } finally {
            // get() was called on every java.util.concurrent.Future, no need to wait any longer
            pool.shutdownNow()
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

    @Override
    public ExitCodeType getExitCodeType() {
        for (Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_SCAN;
            }
        }
        return ExitCodeType.SUCCESS;
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
            boolean pathWasScanned = offlineScanner.offlineScan(detectProject, hubScanConfig, detectConfiguration.hubSignatureScannerOfflineLocalPath)
            if(pathWasScanned) {
                scanSummaryResults.put(canonicalPath, Result.SUCCESS);
                logger.info("${canonicalPath} was successfully scanned by the BlackDuck CLI.")
            }
        } catch (Exception e) {
            logger.error("${detectProject.projectName}/${detectProject.projectVersionName} - ${canonicalPath} was not scanned by the BlackDuck CLI: ${e.message}")
        }
    }

    private ProjectRequest createProjectRequest(DetectProject detectProject) {
        ProjectRequestBuilder builder = new ProjectRequestBuilder()
        builder.setProjectName(detectProject.projectName)
        builder.setVersionName(detectProject.projectVersionName)
        builder.setProjectLevelAdjustments(detectConfiguration.projectLevelMatchAdjustments)
        builder.setPhase(detectConfiguration.projectVersionPhase)
        builder.setDistribution(detectConfiguration.projectVersionDistribution)
        return builder.build()
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
        hubScanConfigBuilder.snippetModeEnabled = detectConfiguration.hubSignatureScannerSnippetMode

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
