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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class BlackDuckSignatureScanner implements StatusSummaryProvider<ScanStatusSummary>, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);
    private final Set<String> scanPaths = new HashSet<>();
    private final Map<String, Set<String>> scanPathExclusionPatterns = new HashMap<>();
    private final Map<String, Result> scanSummaryResults = new HashMap<>();
    private boolean anyExitCodeIs64 = false;
    private String dockerTarFilePath;
    private String dockerTarFilename;

    private final DetectFileManager detectFileManager;
    private final DetectFileFinder detectFileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;

    public BlackDuckSignatureScanner(final DetectFileManager detectFileManager, final DetectFileFinder detectFileFinder, final CodeLocationNameManager codeLocationNameManager,
        final DetectConfiguration detectConfiguration) {
        this.detectFileManager = detectFileManager;
        this.detectFileFinder = detectFileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
    }

    public void scanPaths(final HubServerConfig hubServerConfig, ScanJobManager scanJobManager, final DetectProject detectProject) throws IntegrationException, InterruptedException {
        determinePathsAndExclusions(detectProject);

        final ScanJobBuilder scanJobBuilder = createScanJobBuilder(detectProject, scanPaths, dockerTarFilename);
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
    }

    private void handleScanCommandOutput(final ScanCommandOutput scanCommandOutput) {
        final Result result = scanCommandOutput.getResult();
        scanSummaryResults.put(scanCommandOutput.getScanTarget(), result);
        if (Result.FAILURE == result) {
            logger.error(String.format("Scanning target %s failed: %s", scanCommandOutput.getScanTarget(), scanCommandOutput.getErrorMessage()));
            if (scanCommandOutput.getScanExitCode().isPresent()) {
                anyExitCodeIs64 = anyExitCodeIs64 || scanCommandOutput.getScanExitCode().get() == 64;
            }
            if (scanCommandOutput.getErrorMessage().isPresent() && scanCommandOutput.getException().isPresent()) {
                logger.debug(scanCommandOutput.getErrorMessage().get(), scanCommandOutput.getException().get());
            } else if (scanCommandOutput.getException().isPresent()) {
                logger.debug("Scanner returned an exception but no message: " + scanCommandOutput.getException().isPresent());
            }
        } else {
            logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", scanCommandOutput.getScanTarget()));
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
        if (anyExitCodeIs64) {
            logger.error("");
            logger.error("Signature scanner returned 64. The most likely cause is you are using an unsupported version of Black Duck (<5.0.0).");
            logger.error("You should update your Black Duck or downgrade your version of detect.");
            logger.error("If you are using the detect scripts, you can use DETECT_LATEST_RELEASE_VERSION.");
            logger.error("");
            return ExitCodeType.FAILURE_BLACKDUCK_VERSION_NOT_SUPPORTED;
        }
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
            scanPaths.add(targetPath);
            // Add the path as a FAILURE until it completes successfully
            scanSummaryResults.put(targetPath, Result.FAILURE);
            final ExclusionPatternDetector exclusionPatternDetector = new ExclusionPatternDetector(detectFileFinder, target);
            DetectProperty maxDepthProperty = DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH;
            Integer maxDepth = detectConfiguration.getIntegerProperty(maxDepthProperty);
            final String maxDepthHitMsg = String.format("Maximum depth %d hit while traversing source tree to generate signature scanner exclusion patterns. To search deeper, adjust the value of property %s",
                    maxDepth, maxDepthProperty.getPropertyName());
            final Set<String> scanExclusionPatterns = exclusionPatternDetector.determineExclusionPatterns(maxDepthHitMsg, maxDepth, hubSignatureScannerExclusionNamePatterns);
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
        File installDirectory = detectFileManager.getPermanentDirectory();
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
