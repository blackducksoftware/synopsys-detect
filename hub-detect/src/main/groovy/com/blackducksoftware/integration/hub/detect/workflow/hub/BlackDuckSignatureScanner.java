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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.exit.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.blackducksoftware.integration.hub.detect.workflow.status.SignatureScanStatus;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.exception.IntegrationException;

public class BlackDuckSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);
    private final Set<BlackDuckSignatureScannerEvaluation> scans = new HashSet<>();
    private final Map<String, Set<String>> scanPathExclusionPatterns = new HashMap<>();
    private String dockerTarFilePath;
    private String dockerTarFilename;

    private final DirectoryManager directoryManager;
    private final DetectFileFinder detectFileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final EventSystem eventSystem;

    public BlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder, final CodeLocationNameManager codeLocationNameManager,
        final DetectConfiguration detectConfiguration, EventSystem eventSystem) {
        this.directoryManager = directoryManager;
        this.detectFileFinder = detectFileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.eventSystem = eventSystem;
    }

    public void scanPaths(final HubServerConfig hubServerConfig, ScanJobManager scanJobManager, final DetectProject detectProject) throws IntegrationException, InterruptedException {
        determinePathsAndExclusions(detectProject);

        final ScanJobBuilder scanJobBuilder = createScanJobBuilder(detectProject, scans.stream().map(it -> it.scanPath).collect(Collectors.toSet()), dockerTarFilename);
        scanJobBuilder.fromHubServerConfig(hubServerConfig);

        final ScanJob scanJob = scanJobBuilder.build();

        try {
            final ScanJobOutput scanJobOutput = scanJobManager.executeScans(scanJob);
            if (scanJobOutput.getScanCommandOutputs() != null) {
                for (ScanCommandOutput scanCommandOutput : scanJobOutput.getScanCommandOutputs()) {
                    handleScanCommandOutput(scanCommandOutput);
                }
            }
        } catch (IOException e) {
            throw new IntegrationException("Could not execute the scans: " + e.getMessage());
        }

        reportResults();

    }

    private void reportResults() {
        boolean anyFailed = false;
        for (final BlackDuckSignatureScannerEvaluation scan : scans) {
            StatusType scanStatus;
            if (!scan.scanFinished) {
                scanStatus = StatusType.FAILURE;
                logger.info(String.format("Scanning target %s was never scanned by the BlackDuck CLI.", scan.scanPath));
            } else if (scan.scanResult == Result.FAILURE) {
                scanStatus = StatusType.FAILURE;
                logger.error(String.format("Scanning target %s failed: %s", scan.scanPath, scan.scanMessage));
                if (null != scan.scanException) {
                    logger.debug(scan.scanMessage, scan.scanException);
                }
            } else {
                scanStatus = StatusType.SUCCESS;
                logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", scan.scanPath));
            }
            anyFailed = anyFailed || scanStatus == StatusType.FAILURE;
            eventSystem.publishEvent(Event.StatusSummary, new SignatureScanStatus(scan.scanPath, scanStatus));
        }
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_SCAN));
    }

    private void handleScanCommandOutput(final ScanCommandOutput scanCommandOutput) {
        final Result result = scanCommandOutput.getResult();
        final String path = scanCommandOutput.getScanTarget();
        scans.stream()
            .filter(it -> it.scanPath.equals(it))
            .findFirst()
            .ifPresent(it -> {
                it.scanResult = scanCommandOutput.getResult();
                it.scanException = scanCommandOutput.getException();
                it.scanMessage = scanCommandOutput.getErrorMessage();
                it.scanFinished = true;
            });
    }

    public void setDockerTarFile(final File dockerTarFile) throws IOException {
        this.dockerTarFilePath = dockerTarFile.getCanonicalPath();
        this.dockerTarFilename = dockerTarFile.getName();
    }

    private void determinePathsAndExclusions(final DetectProject detectProject) throws IntegrationException {
        final String[] signatureScanPaths = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS);
        final boolean userProvidedScanTargets = null != signatureScanPaths && signatureScanPaths.length > 0;
        final String[] providedExclusionPatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS);
        final String[] hubSignatureScannerExclusionNamePatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS);
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
            BlackDuckSignatureScannerEvaluation scannerEvaluation = new BlackDuckSignatureScannerEvaluation();
            scannerEvaluation.scanPath = targetPath;
            scans.add(scannerEvaluation);
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

    private ScanJobBuilder createScanJobBuilder(final DetectProject detectProject, final Set<String> scanPaths, final String dockerTarFilename) {
        final File scannerDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH));

        final String locallScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH);
        File installDirectory = directoryManager.getPermanentDirectory();
        if (StringUtils.isNotBlank(locallScannerInstallPath)) {
            installDirectory = new File(locallScannerInstallPath);
        }

        final ScanJobBuilder scanJobBuilder = new ScanJobBuilder();
        scanJobBuilder.scanMemoryInMegabytes(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY));
        scanJobBuilder.installDirectory(installDirectory);
        scanJobBuilder.outputDirectory(scannerDirectory);

        scanJobBuilder.cleanupOutput(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP));
        scanJobBuilder.dryRun(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN));
        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE)) {
            scanJobBuilder.snippetMatching(SnippetMatching.SNIPPET_MATCHING);
        }
        scanJobBuilder.additionalScanArguments(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS));

        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersion();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName);

        final String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH);
        final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX);
        final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX);

        for (final String scanTarget : scanPaths) {
            final String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanTarget, dockerTarFilename, projectName, projectVersionName, prefix, suffix);
            final Set<String> exclusionPatterns = scanPathExclusionPatterns.get(scanTarget);
            if (null != exclusionPatterns && !exclusionPatterns.isEmpty()) {
                scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanTarget, exclusionPatterns, codeLocationName));
            } else {
                scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanTarget, Collections.emptySet(), codeLocationName));
            }
        }

        return scanJobBuilder;
    }

}
