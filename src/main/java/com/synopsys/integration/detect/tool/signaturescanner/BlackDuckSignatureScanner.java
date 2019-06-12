/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.blackduck.ExclusionPatternCreator;
import com.synopsys.integration.detect.workflow.status.SignatureScanStatus;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public abstract class BlackDuckSignatureScanner {
    public static final String SIGNATURE_SCAN_UPLOAD_SOURCE_OPTION = "--upload-source";
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);

    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final EventSystem eventSystem;
    private final ScanBatchRunner scanJobManager;

    public BlackDuckSignatureScanner(final DirectoryManager directoryManager, final FileFinder fileFinder, final CodeLocationNameManager codeLocationNameManager,
                                     final BlackDuckSignatureScannerOptions signatureScannerOptions, EventSystem eventSystem, final ScanBatchRunner scanJobManager) {
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.signatureScannerOptions = signatureScannerOptions;
        this.eventSystem = eventSystem;
        this.scanJobManager = scanJobManager;
    }

    protected abstract ScanBatch createScanBatch(NameVersion projectNameVersion, File installDirectory, List<SignatureScanPath> signatureScanPaths, File dockerTarFile);

    public ScanBatchOutput performScanActions(NameVersion projectNameVersion, File installDirectory, File dockerTarFile) throws InterruptedException, IntegrationException, DetectUserFriendlyException, IOException {
        return scanPaths(projectNameVersion, installDirectory, dockerTarFile);
    }

    private ScanBatchOutput scanPaths(final NameVersion projectNameVersion, File installDirectory, File dockerTarFile) throws IntegrationException, InterruptedException, IOException {
        List<SignatureScanPath> signatureScanPaths = determinePathsAndExclusions(projectNameVersion, signatureScannerOptions.getMaxDepth(), dockerTarFile);
        final ScanBatch scanJob = createScanBatch(projectNameVersion, installDirectory, signatureScanPaths, dockerTarFile);

        List<ScanCommandOutput> scanCommandOutputs = new ArrayList<>();
        final ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        if (scanJobOutput.getOutputs() != null) {
            for (ScanCommandOutput scanCommandOutput : scanJobOutput.getOutputs()) {
                scanCommandOutputs.add(scanCommandOutput);
            }
        }

        reportResults(signatureScanPaths, scanCommandOutputs);

        return scanJobOutput;
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
        final String[] signatureScannerExclusionNamePatterns = signatureScannerOptions.getExclusionNamePatterns();

        List<SignatureScanPath> signatureScanPaths = new ArrayList<>();
        if (null != projectNameVersion.getName() && null != projectNameVersion.getVersion() && userProvidedScanTargets) {
            for (final String path : providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                SignatureScanPath scanPath = createScanPath(path, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
                signatureScanPaths.add(scanPath);
            }
        } else if (dockerTarFile != null) {
            SignatureScanPath scanPath = createScanPath(dockerTarFile.getCanonicalPath(), maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        } else {
            final String sourcePath = directoryManager.getSourceDirectory().getAbsolutePath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            SignatureScanPath scanPath = createScanPath(sourcePath, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        }
        return signatureScanPaths;
    }

    private SignatureScanPath createScanPath(final String path, Integer maxDepth, final String[] signatureScannerExclusionNamePatterns, final String[] providedExclusionPatterns) throws IntegrationException {
        try {
            final File target = new File(path);
            final String targetPath = target.getCanonicalPath();
            final ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(fileFinder, target);

            final String maxDepthHitMsg = String.format("Maximum depth %d hit while traversing source tree to generate signature scanner exclusion patterns. To search deeper, adjust the value of property %s",
                    maxDepth, DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH.getPropertyName());

            final Set<String> scanExclusionPatterns = exclusionPatternCreator.determineExclusionPatterns(maxDepthHitMsg, maxDepth, signatureScannerExclusionNamePatterns);
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

    protected ScanBatchBuilder createDefaultScanBatchBuilder(final NameVersion projectNameVersion, File installDirectory, final List<SignatureScanPath> signatureScanPaths, File dockerTarFile) {
        final ScanBatchBuilder scanJobBuilder = new ScanBatchBuilder();
        scanJobBuilder.scanMemoryInMegabytes(signatureScannerOptions.getScanMemory());
        scanJobBuilder.installDirectory(installDirectory);
        scanJobBuilder.outputDirectory(directoryManager.getScanOutputDirectory());

        scanJobBuilder.dryRun(signatureScannerOptions.getDryRun());
        scanJobBuilder.cleanupOutput(signatureScannerOptions.getCleanupOutput());

        Optional<SnippetMatching> optionalSnippetMatching = signatureScannerOptions.getSnippetMatchingEnum();
        boolean uploadSource = BooleanUtils.toBoolean(signatureScannerOptions.getUploadSource());
        if (optionalSnippetMatching.isPresent()) {
            scanJobBuilder.uploadSource(optionalSnippetMatching.get(), uploadSource);
        }

        String additionalArguments = signatureScannerOptions.getAdditionalArguments();
        scanJobBuilder.additionalScanArguments(additionalArguments);

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