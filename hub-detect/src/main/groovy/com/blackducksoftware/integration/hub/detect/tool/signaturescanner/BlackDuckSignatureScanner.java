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
package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.ExclusionPatternCreator;
import com.blackducksoftware.integration.hub.detect.workflow.status.SignatureScanStatus;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public abstract class BlackDuckSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);

    private final DirectoryManager directoryManager;
    private final DetectFileFinder detectFileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final EventSystem eventSystem;
    private final ScanJobManager scanJobManager;

    public BlackDuckSignatureScanner(final DirectoryManager directoryManager, final DetectFileFinder detectFileFinder, final CodeLocationNameManager codeLocationNameManager,
        final BlackDuckSignatureScannerOptions signatureScannerOptions, EventSystem eventSystem, final ScanJobManager scanJobManager) {
        this.directoryManager = directoryManager;
        this.detectFileFinder = detectFileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.signatureScannerOptions = signatureScannerOptions;
        this.eventSystem = eventSystem;
        this.scanJobManager = scanJobManager;
    }

    protected abstract ScanJob createScanJob(NameVersion projectNameVersion, List<SignatureScanPath> signatureScanPaths, File dockerTarFile);

    public void performScanActions(NameVersion projectNameVersion, File dockerTarFile) throws InterruptedException, IntegrationException, DetectUserFriendlyException, IOException {
        scanPaths(projectNameVersion, dockerTarFile);
    }

    private void scanPaths(final NameVersion projectNameVersion, File dockerTarFile) throws IntegrationException, InterruptedException, IOException {
        List<SignatureScanPath> signatureScanPaths = determinePathsAndExclusions(projectNameVersion, signatureScannerOptions.getMaxDepth(), dockerTarFile);
        final ScanJob scanJob = createScanJob(projectNameVersion, signatureScanPaths, dockerTarFile);

        List<ScanCommandOutput> scanCommandOutputs = new ArrayList<>();
        try {
            final ScanJobOutput scanJobOutput = scanJobManager.executeScans(scanJob);
            if (scanJobOutput.getScanCommandOutputs() != null) {
                for (ScanCommandOutput scanCommandOutput : scanJobOutput.getScanCommandOutputs()) {
                    scanCommandOutputs.add(scanCommandOutput);
                }
            }
        } catch (IOException e) {
            throw new IntegrationException("Could not execute the scans: " + e.getMessage());
        }

        reportResults(signatureScanPaths, scanCommandOutputs);
    }

    private void reportResults(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList) {
        boolean anyFailed = false;
        boolean anyExitCodeIs64 = false;
        for (final SignatureScanPath target : signatureScanPaths) {
            Optional<ScanCommandOutput> targetOutput = scanCommandOutputList.stream()
                                                           .filter(output -> output.getScanTarget().equals(target.targetPath))
                                                           .findFirst();

            StatusType scanStatus;
            if (!targetOutput.isPresent()) {
                scanStatus = StatusType.FAILURE;
                logger.info(String.format("Scanning target %s was never scanned by the BlackDuck CLI.", target.targetPath));
            } else {
                ScanCommandOutput output = targetOutput.get();
                if (output.getResult() == Result.FAILURE) {
                    scanStatus = StatusType.FAILURE;

                    if (output.getException().isPresent() && output.getErrorMessage().isPresent()) {
                        logger.error(String.format("Scanning target %s failed: %s", target.targetPath, output.getErrorMessage().get()));
                        logger.debug(output.getErrorMessage().get(), output.getException().get());
                    } else if (output.getErrorMessage().isPresent()) {
                        logger.error(String.format("Scanning target %s failed: %s", target.targetPath, output.getErrorMessage().get()));
                    } else {
                        logger.error(String.format("Scanning target %s failed for an unknown reason.", target.targetPath));
                    }

                    if (output.getScanExitCode().isPresent()) {
                        anyExitCodeIs64 = anyExitCodeIs64 || output.getScanExitCode().get() == 64;
                    }

                } else {
                    scanStatus = StatusType.SUCCESS;
                    logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", target.targetPath));
                }
            }

            anyFailed = anyFailed || scanStatus == StatusType.FAILURE;
            eventSystem.publishEvent(Event.StatusSummary, new SignatureScanStatus(target.targetPath, scanStatus));
        }

        if (anyFailed) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_SCAN));
        }

        if (anyExitCodeIs64) {
            logger.error("");
            logger.error("Signature scanner returned 64. The most likely cause is you are using an unsupported version of Black Duck (<5.0.0).");
            logger.error("You should update your Black Duck or downgrade your version of detect.");
            logger.error("If you are using the detect scripts, you can use DETECT_LATEST_RELEASE_VERSION.");
            logger.error("");
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_VERSION_NOT_SUPPORTED));
        }
    }

    private List<SignatureScanPath> determinePathsAndExclusions(final NameVersion projectNameVersion, Integer maxDepth, File dockerTarFile) throws IntegrationException, IOException {
        final String[] providedSignatureScanPaths = signatureScannerOptions.getSignatureScannerPaths();
        final boolean userProvidedScanTargets = null != providedSignatureScanPaths && providedSignatureScanPaths.length > 0;
        final String[] providedExclusionPatterns = signatureScannerOptions.getExclusionPatterns();
        final String[] hubSignatureScannerExclusionNamePatterns = signatureScannerOptions.getExclusionNamePatterns();

        List<SignatureScanPath> signatureScanPaths = new ArrayList<>();
        if (null != projectNameVersion.getName() && null != projectNameVersion.getVersion() && userProvidedScanTargets) {
            for (final String path : providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                SignatureScanPath scanPath = createScanPath(path, maxDepth, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
                signatureScanPaths.add(scanPath);
            }
        } else if (dockerTarFile != null) {
            SignatureScanPath scanPath = createScanPath(dockerTarFile.getCanonicalPath(), maxDepth, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        } else {
            final String sourcePath = directoryManager.getSourceDirectory().getAbsolutePath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            SignatureScanPath scanPath = createScanPath(sourcePath, maxDepth, hubSignatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        }
        return signatureScanPaths;
    }

    private SignatureScanPath createScanPath(final String path, Integer maxDepth, final String[] hubSignatureScannerExclusionNamePatterns, final String[] providedExclusionPatterns) throws IntegrationException {
        try {
            final File target = new File(path);
            final String targetPath = target.getCanonicalPath();
            final ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(detectFileFinder, target);

            final String maxDepthHitMsg = String.format("Maximum depth %d hit while traversing source tree to generate signature scanner exclusion patterns. To search deeper, adjust the value of property %s",
                maxDepth, DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH.getPropertyName());

            final Set<String> scanExclusionPatterns = exclusionPatternCreator.determineExclusionPatterns(maxDepthHitMsg, maxDepth, hubSignatureScannerExclusionNamePatterns);
            if (null != providedExclusionPatterns) {
                for (final String providedExclusionPattern : providedExclusionPatterns) {
                    scanExclusionPatterns.add(providedExclusionPattern);
                }
            }
            SignatureScanPath signatureScanPath = new SignatureScanPath();
            signatureScanPath.targetPath = targetPath;
            signatureScanPath.exclusions.addAll(scanExclusionPatterns);
            return signatureScanPath;
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    protected ScanJobBuilder createDefaultScanJobBuilder(final NameVersion projectNameVersion, final List<SignatureScanPath> signatureScanPaths, File dockerTarFile) {
        final ScanJobBuilder scanJobBuilder = new ScanJobBuilder();
        scanJobBuilder.scanMemoryInMegabytes(signatureScannerOptions.getScanMemory());
        scanJobBuilder.installDirectory(directoryManager.getPermanentDirectory());
        scanJobBuilder.outputDirectory(directoryManager.getScanOutputDirectory());

        scanJobBuilder.cleanupOutput(signatureScannerOptions.getCleanupOutput());
        scanJobBuilder.dryRun(signatureScannerOptions.getDryRun());
        if (signatureScannerOptions.getSnippetMatching()) {
            scanJobBuilder.snippetMatching(SnippetMatching.SNIPPET_MATCHING);
        }
        scanJobBuilder.additionalScanArguments(signatureScannerOptions.getAdditionalArguments());

        final String projectName = projectNameVersion.getName();
        final String projectVersionName = projectNameVersion.getVersion();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName);

        final String sourcePath = directoryManager.getSourceDirectory().getAbsolutePath();
        final String prefix = signatureScannerOptions.getCodeLocationPrefix();
        final String suffix = signatureScannerOptions.getCodeLocationSuffix();

        String dockerTarFilename = null;
        if (dockerTarFile != null) {
            dockerTarFilename = dockerTarFile.getName();
        }
        for (final SignatureScanPath scanPath : signatureScanPaths) {
            final String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanPath.targetPath, dockerTarFilename, projectName, projectVersionName, prefix, suffix);
            scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanPath.targetPath, scanPath.exclusions, codeLocationName));
        }

        return scanJobBuilder;
    }

}
