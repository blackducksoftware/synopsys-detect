/**
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
package com.blackducksoftware.integration.hub.detect.hub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.cli.summary.ScanServiceOutput;
import com.blackducksoftware.integration.hub.cli.summary.ScanTargetOutput;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubScanConfigBuilder;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.manager.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.ScanSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.summary.Result;

@Component
public class HubSignatureScanner implements SummaryResultReporter, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class);
    private final Set<String> scanPaths = new HashSet<>();
    private final Map<String, Set<String>> scanPathExclusionPatterns = new HashMap<>();
    private final Map<String, Result> scanSummaryResults = new HashMap<>();
    private String dockerTarFilePath;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private OfflineScanner offlineScanner;

    @Autowired
    public CodeLocationNameManager codeLocationNameManager;

    public ProjectVersionView scanPaths(final HubServerConfig hubServerConfig, final SignatureScannerService signatureScannerService, final DetectProject detectProject)
            throws IntegrationException, InterruptedException {
        determinePathsAndExclusions(detectProject);

        ProjectVersionView projectVersionView = null;
        final ProjectRequest projectRequest = createProjectRequest(detectProject);

        final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, scanPaths);
        final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();

        final ScanServiceOutput scanServiceOutput = signatureScannerService.executeScans(hubServerConfig, hubScanConfig, projectRequest);
        if (null != scanServiceOutput) {
            projectVersionView = scanServiceOutput.getProjectVersionWrapper().getProjectVersionView();

            if (null != scanServiceOutput.getScanTargetOutputs() && !scanServiceOutput.getScanTargetOutputs().isEmpty()) {
                for (final ScanTargetOutput scanTargetOutput : scanServiceOutput.getScanTargetOutputs()) {
                    handleScanTargetOutput(scanTargetOutput);
                }
            }
        }
        return projectVersionView;
    }

    public void scanPathsOffline(final DetectProject detectProject) throws IntegrationException {
        determinePathsAndExclusions(detectProject);
        try {
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, scanPaths);
            hubScanConfigBuilder.setDryRun(true);

            if (StringUtils.isNotBlank(detectConfiguration.getHubSignatureScannerOfflineLocalPath())) {
                final File toolsDirectory = new File(detectConfiguration.getHubSignatureScannerOfflineLocalPath());
                hubScanConfigBuilder.setToolsDir(toolsDirectory);
            }
            final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();
            final List<ScanTargetOutput> scanTargetOutputs = offlineScanner.offlineScan(detectProject, hubScanConfig, detectConfiguration.getHubSignatureScannerOfflineLocalPath());
            if (null != scanTargetOutputs && !scanTargetOutputs.isEmpty()) {
                for (final ScanTargetOutput scanTargetOutput : scanTargetOutputs) {
                    handleScanTargetOutput(scanTargetOutput);
                }
            }
        } catch (final Exception e) {
            logger.error(String.format("Scanning failed: %s", detectProject.getProjectName(), detectProject.getProjectVersion(), e.getMessage()));
        }
    }

    private void handleScanTargetOutput(final ScanTargetOutput scanTargetOutput) {
        final Result result = scanTargetOutput.getResult();
        scanSummaryResults.put(scanTargetOutput.getScanTarget(), result);
        logger.info(String.format("%s was scanned by the BlackDuck CLI. Result: %s", scanTargetOutput.getScanTarget(), result.name()));
        if (Result.FAILURE == result) {
            logger.error(String.format("Scanning target %s failed: %s", scanTargetOutput.getScanTarget(), scanTargetOutput.getErrorMessage()));
            if (null != scanTargetOutput.getException()) {
                logger.debug(scanTargetOutput.getErrorMessage(), scanTargetOutput.getException());
            }
        } else {
            logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", scanTargetOutput.getScanTarget()));
        }
    }

    @Override
    public List<ScanSummaryResult> getDetectSummaryResults() {
        final List<ScanSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            detectSummaryResults.add(new ScanSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_SCAN;
            }
        }
        return ExitCodeType.SUCCESS;
    }

    public String getDockerTarFilePath() {
        return dockerTarFilePath;
    }

    public void setDockerTarFilePath(final String dockerTarFilePath) {
        this.dockerTarFilePath = dockerTarFilePath;
    }

    private void determinePathsAndExclusions(final DetectProject detectProject) throws IntegrationException {
        final boolean userProvidedScanTargets = null != detectConfiguration.getHubSignatureScannerPaths() && detectConfiguration.getHubSignatureScannerPaths().length > 0;
        final String[] providedExclusionPatterns = detectConfiguration.getHubSignatureScannerExclusionPatterns();
        final String[] hubSignatureScannerExclusionNamePatterns = detectConfiguration.getHubSignatureScannerExclusionNamePatterns();
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersion() && userProvidedScanTargets) {
            for (final String path : detectConfiguration.getHubSignatureScannerPaths()) {
                logger.info(String.format("Registering explicit scan path %s", path));
                addScanTarget(path, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
            }
        } else if (StringUtils.isNotBlank(dockerTarFilePath)) {
            addScanTarget(dockerTarFilePath, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
        } else {
            final String sourcePath = detectConfiguration.getSourcePath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            addScanTarget(sourcePath, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
        }
    }

    private void addScanTarget(final String path, final String[] hubSignatureScannerExclusionNamePatterns, final String[] providedExclusionPatterns) throws IntegrationException {
        try {
            final File target = new File(path);
            final String targetPath = target.getCanonicalPath();
            scanPaths.add(targetPath);
            // Add the path as a FAILURE until it completes successfully
            scanSummaryResults.put(targetPath, Result.FAILURE);
            final ExclusionPatternDetector exclusionPatternDetector = new ExclusionPatternDetector(detectFileFinder, target);
            final Set<String> scanExclusionPatterns = exclusionPatternDetector.determineExclusionPatterns(hubSignatureScannerExclusionNamePatterns);
            if (null != providedExclusionPatterns) {
                for (final String providedExclusionPattern : providedExclusionPatterns) {
                    scanExclusionPatterns.add(providedExclusionPattern);
                }
            }
            scanPathExclusionPatterns.put(targetPath, scanExclusionPatterns);
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    private ProjectRequest createProjectRequest(final DetectProject detectProject) {
        final ProjectRequestBuilder builder = new DetectProjectRequestBuilder(detectConfiguration, detectProject);
        return builder.build();
    }

    private HubScanConfigBuilder createScanConfigBuilder(final DetectProject detectProject, final Set<String> scanPaths) {
        final File scannerDirectory = new File(detectConfiguration.getScanOutputDirectoryPath());
        final File toolsDirectory = detectFileManager.getPermanentDirectory();

        final HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder();
        hubScanConfigBuilder.setScanMemory(detectConfiguration.getHubSignatureScannerMemory());
        hubScanConfigBuilder.setToolsDir(toolsDirectory);
        hubScanConfigBuilder.setWorkingDirectory(scannerDirectory);

        hubScanConfigBuilder.setCleanupLogsOnSuccess(detectConfiguration.getCleanupDetectFiles());
        hubScanConfigBuilder.setDryRun(detectConfiguration.getHubSignatureScannerDryRun());
        hubScanConfigBuilder.setSnippetModeEnabled(detectConfiguration.getHubSignatureScannerSnippetMode());
        hubScanConfigBuilder.setAdditionalScanArguments(detectConfiguration.getHubSignatureScannerArguments());

        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersion();
        final String sourcePath = detectConfiguration.getSourcePath();
        final String prefix = detectConfiguration.getProjectCodeLocationPrefix();
        final String suffix = detectConfiguration.getProjectCodeLocationSuffix();

        for (final String scanTarget : scanPaths) {
            hubScanConfigBuilder.addScanTargetPath(scanTarget);

            final String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanTarget, projectName, projectVersionName, prefix, suffix);
            hubScanConfigBuilder.addTargetToCodeLocationName(scanTarget, codeLocationName);

            final Set<String> exclusionPatterns = scanPathExclusionPatterns.get(scanTarget);
            if (null != exclusionPatterns && !exclusionPatterns.isEmpty()) {
                hubScanConfigBuilder.addTargetToExclusionPatterns(scanTarget, exclusionPatterns);
            }
        }

        return hubScanConfigBuilder;
    }

}
