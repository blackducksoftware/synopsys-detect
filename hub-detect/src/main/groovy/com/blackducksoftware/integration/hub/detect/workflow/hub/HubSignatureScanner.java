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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

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

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.ProjectRequest;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.cli.summary.ScanServiceOutput;
import com.blackducksoftware.integration.hub.cli.summary.ScanTargetOutput;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubScanConfigBuilder;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.blackducksoftware.integration.hub.detect.workflow.summary.ScanStatusSummary;
import com.blackducksoftware.integration.hub.detect.workflow.summary.StatusSummaryProvider;
import com.blackducksoftware.integration.hub.service.SignatureScannerService;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.summary.Result;

public class HubSignatureScanner implements StatusSummaryProvider<ScanStatusSummary>, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(HubSignatureScanner.class);
    private final Set<String> scanPaths = new HashSet<>();
    private final Map<String, Set<String>> scanPathExclusionPatterns = new HashMap<>();
    private final Map<String, Result> scanSummaryResults = new HashMap<>();
    private String dockerTarFilePath;
    private String dockerTarFilename;

    private final DetectFileManager detectFileManager;
    private final DetectFileFinder detectFileFinder;
    private final OfflineScanner offlineScanner;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;

    public HubSignatureScanner(final DetectFileManager detectFileManager, final DetectFileFinder detectFileFinder, final OfflineScanner offlineScanner, final CodeLocationNameManager codeLocationNameManager,
            final DetectConfiguration detectConfiguration) {
        this.detectFileManager = detectFileManager;
        this.detectFileFinder = detectFileFinder;
        this.offlineScanner = offlineScanner;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
    }

    public ProjectVersionView scanPaths(final HubServerConfig hubServerConfig, final SignatureScannerService signatureScannerService, final DetectProject detectProject)
            throws IntegrationException, InterruptedException {
        determinePathsAndExclusions(detectProject);

        ProjectVersionView projectVersionView = null;
        final ProjectRequest projectRequest = createProjectRequest(detectProject);

        final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, scanPaths, dockerTarFilename);
        final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();

        final ScanServiceOutput scanServiceOutput = signatureScannerService.executeScans(hubServerConfig, hubScanConfig, projectRequest);
        if (null != scanServiceOutput) {
            if (null != scanServiceOutput.getProjectVersionWrapper()) {
                projectVersionView = scanServiceOutput.getProjectVersionWrapper().getProjectVersionView();
            }
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
            final HubScanConfigBuilder hubScanConfigBuilder = createScanConfigBuilder(detectProject, scanPaths, dockerTarFilename);
            hubScanConfigBuilder.setDryRun(true);

            final String offlineLocalPath = detectConfiguration.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH);
            if (StringUtils.isNotBlank(offlineLocalPath)) {
                final File toolsDirectory = new File(offlineLocalPath);
                hubScanConfigBuilder.setToolsDir(toolsDirectory);
            }
            final HubScanConfig hubScanConfig = hubScanConfigBuilder.build();
            final List<ScanTargetOutput> scanTargetOutputs = offlineScanner.offlineScan(detectProject, hubScanConfig, offlineLocalPath);
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
    public List<ScanStatusSummary> getStatusSummaries() {
        final List<ScanStatusSummary> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<String, Result> entry : scanSummaryResults.entrySet()) {
            detectSummaryResults.add(new ScanStatusSummary(entry.getKey(), entry.getValue()));
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

    public void setDockerTarFile(final File dockerTarFile) throws IOException {
        this.dockerTarFilePath = dockerTarFile.getCanonicalPath();
        this.dockerTarFilename = dockerTarFile.getName();
    }

    public String getDockerTarFileName() {
        return dockerTarFilename;
    }

    private void determinePathsAndExclusions(final DetectProject detectProject) throws IntegrationException {
        final String[] signatureScanPaths = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PATHS);
        final boolean userProvidedScanTargets = null != signatureScanPaths && signatureScanPaths.length > 0;
        final String[] providedExclusionPatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS);
        final String[] hubSignatureScannerExclusionNamePatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS);
        if (null != detectProject.getProjectName() && null != detectProject.getProjectVersion() && userProvidedScanTargets) {
            for (final String path : signatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                addScanTarget(path, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
            }
        } else if (StringUtils.isNotBlank(dockerTarFilePath)) {
            addScanTarget(dockerTarFilePath, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
        } else {
            final String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH);
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

    private HubScanConfigBuilder createScanConfigBuilder(final DetectProject detectProject, final Set<String> scanPaths, final String dockerTarFilename) {
        final File scannerDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH));

        final File toolsDirectory = detectFileManager.getPermanentDirectory();

        final HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder();
        hubScanConfigBuilder.setScanMemory(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_MEMORY));
        hubScanConfigBuilder.setToolsDir(toolsDirectory);
        hubScanConfigBuilder.setWorkingDirectory(scannerDirectory);

        hubScanConfigBuilder.setCleanupLogsOnSuccess(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP));
        hubScanConfigBuilder.setDryRun(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN));
        hubScanConfigBuilder.setSnippetModeEnabled(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE));
        hubScanConfigBuilder.setAdditionalScanArguments(detectConfiguration.getProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS));

        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersion();
        final String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH);
        final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX);
        final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX);

        for (final String scanTarget : scanPaths) {
            hubScanConfigBuilder.addScanTargetPath(scanTarget);

            final String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanTarget, dockerTarFilename, projectName, projectVersionName, prefix, suffix);
            hubScanConfigBuilder.addTargetToCodeLocationName(scanTarget, codeLocationName);

            final Set<String> exclusionPatterns = scanPathExclusionPatterns.get(scanTarget);
            if (null != exclusionPatterns && !exclusionPatterns.isEmpty()) {
                hubScanConfigBuilder.addTargetToExclusionPatterns(scanTarget, exclusionPatterns);
            }
        }

        return hubScanConfigBuilder;
    }

}
