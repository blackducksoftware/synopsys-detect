/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.blackduck.ExclusionPatternCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.SignatureScanStatus;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);

    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final EventSystem eventSystem;
    private final ScanBatchRunner scanJobManager;

    //When OFFLINE, this should be NULL. No other changes required for offline (in this class).
    private final BlackDuckServerConfig blackDuckServerConfig;

    public BlackDuckSignatureScanner(final DirectoryManager directoryManager, final FileFinder fileFinder, final CodeLocationNameManager codeLocationNameManager,
        final BlackDuckSignatureScannerOptions signatureScannerOptions, final EventSystem eventSystem, final ScanBatchRunner scanJobManager, final BlackDuckServerConfig blackDuckServerConfig) {
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.signatureScannerOptions = signatureScannerOptions;
        this.eventSystem = eventSystem;
        this.scanJobManager = scanJobManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public ScanBatchOutput performScanActions(final NameVersion projectNameVersion, final File installDirectory, final File dockerTarFile) throws IntegrationException, IOException, DetectUserFriendlyException {
        final List<SignatureScanPath> signatureScanPaths = determinePathsAndExclusions(projectNameVersion, signatureScannerOptions.getMaxDepth(), dockerTarFile);

        final ScanBatchBuilder scanJobBuilder = createDefaultScanBatchBuilder(projectNameVersion, installDirectory, signatureScanPaths, dockerTarFile);
        scanJobBuilder.fromBlackDuckServerConfig(blackDuckServerConfig);//when offline, we must still call this with 'null' as a workaround for library issues, so offline scanner must be created with this set to null.
        final ScanBatch scanJob;
        try {
            scanJob = scanJobBuilder.build();
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        final List<ScanCommandOutput> scanCommandOutputs = new ArrayList<>();
        final ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        if (scanJobOutput.getOutputs() != null) {
            for (final ScanCommandOutput scanCommandOutput : scanJobOutput.getOutputs()) {
                scanCommandOutputs.add(scanCommandOutput);
            }
        }

        reportResults(signatureScanPaths, scanCommandOutputs);

        return scanJobOutput;
    }

    //TODO: Possibly promote this to the Tool. Ideally it would return some object describing these results and the Tool translates that into detect nonsense -jp.
    private void reportResults(final List<SignatureScanPath> signatureScanPaths, final List<ScanCommandOutput> scanCommandOutputList) {
        boolean anyFailed = false;
        boolean anyExitCodeIs64 = false;
        for (final SignatureScanPath target : signatureScanPaths) {
            final Optional<ScanCommandOutput> targetOutput = scanCommandOutputList.stream()
                                                                 .filter(output -> output.getScanTarget().equals(target.getTargetCanonicalPath()))
                                                                 .findFirst();

            final StatusType scanStatus;
            if (!targetOutput.isPresent()) {
                scanStatus = StatusType.FAILURE;
                logger.info(String.format("Scanning target %s was never scanned by the BlackDuck CLI.", target.getTargetCanonicalPath()));
            } else {
                final ScanCommandOutput output = targetOutput.get();
                if (output.getResult() == Result.FAILURE) {
                    scanStatus = StatusType.FAILURE;

                    if (output.getException().isPresent() && output.getErrorMessage().isPresent()) {
                        logger.error(String.format("Scanning target %s failed: %s", target.getTargetCanonicalPath(), output.getErrorMessage().get()));
                        logger.debug(output.getErrorMessage().get(), output.getException().get());
                    } else if (output.getErrorMessage().isPresent()) {
                        logger.error(String.format("Scanning target %s failed: %s", target.getTargetCanonicalPath(), output.getErrorMessage().get()));
                    } else {
                        logger.error(String.format("Scanning target %s failed for an unknown reason.", target.getTargetCanonicalPath()));
                    }

                    if (output.getScanExitCode().isPresent()) {
                        anyExitCodeIs64 = anyExitCodeIs64 || output.getScanExitCode().get() == 64;
                    }

                } else {
                    scanStatus = StatusType.SUCCESS;
                    logger.info(String.format("%s was successfully scanned by the BlackDuck CLI.", target.getTargetCanonicalPath()));
                }
            }

            anyFailed = anyFailed || scanStatus == StatusType.FAILURE;
            eventSystem.publishEvent(Event.StatusSummary, new SignatureScanStatus(target.getTargetCanonicalPath(), scanStatus));
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

    private List<SignatureScanPath> determinePathsAndExclusions(final NameVersion projectNameVersion, final Integer maxDepth, final File dockerTarFile) throws IOException {
        final List<Path> providedSignatureScanPaths = signatureScannerOptions.getSignatureScannerPaths();
        final boolean userProvidedScanTargets = null != providedSignatureScanPaths && providedSignatureScanPaths.size() > 0;
        final List<String> providedExclusionPatterns = signatureScannerOptions.getExclusionPatterns();
        final List<String> signatureScannerExclusionNamePatterns = signatureScannerOptions.getExclusionNamePatterns();

        final List<SignatureScanPath> signatureScanPaths = new ArrayList<>();
        if (null != projectNameVersion.getName() && null != projectNameVersion.getVersion() && userProvidedScanTargets) {
            for (final Path path : providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                final SignatureScanPath scanPath = createScanPath(path, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
                signatureScanPaths.add(scanPath);
            }
        } else if (dockerTarFile != null) {
            final SignatureScanPath scanPath = createScanPath(dockerTarFile.getCanonicalFile().toPath(), maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        } else {
            final Path sourcePath = directoryManager.getSourceDirectory().getAbsoluteFile().toPath();
            if (userProvidedScanTargets) {
                logger.warn(String.format("No Project name or version found. Skipping User provided scan targets - registering the source path %s to scan", sourcePath));
            } else {
                logger.info(String.format("No scan targets provided - registering the source path %s to scan", sourcePath));
            }
            final SignatureScanPath scanPath = createScanPath(sourcePath, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        }
        return signatureScanPaths;
    }

    private SignatureScanPath createScanPath(final Path path, final Integer maxDepth, final List<String> signatureScannerExclusionNamePatterns, final List<String> providedExclusionPatterns) {
        final File target = path.toFile();
        final ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(fileFinder, target);

        final Set<String> scanExclusionPatterns = exclusionPatternCreator.determineExclusionPatterns(maxDepth, signatureScannerExclusionNamePatterns);
        if (null != providedExclusionPatterns) {
            scanExclusionPatterns.addAll(providedExclusionPatterns);
        }
        final SignatureScanPath signatureScanPath = new SignatureScanPath();
        signatureScanPath.setTargetPath(target);
        signatureScanPath.getExclusions().addAll(scanExclusionPatterns);
        return signatureScanPath;
    }

    protected ScanBatchBuilder createDefaultScanBatchBuilder(final NameVersion projectNameVersion, final File installDirectory, final List<SignatureScanPath> signatureScanPaths, final File dockerTarFile) throws DetectUserFriendlyException {
        final ScanBatchBuilder scanJobBuilder = new ScanBatchBuilder();
        scanJobBuilder.scanMemoryInMegabytes(signatureScannerOptions.getScanMemory());
        scanJobBuilder.installDirectory(installDirectory);
        scanJobBuilder.outputDirectory(directoryManager.getScanOutputDirectory());

        scanJobBuilder.dryRun(signatureScannerOptions.getDryRun());
        scanJobBuilder.cleanupOutput(false);

        signatureScannerOptions.getSnippetMatching().ifPresent(scanJobBuilder::snippetMatching);
        scanJobBuilder.uploadSource(signatureScannerOptions.getUploadSource());
        scanJobBuilder.licenseSearch(signatureScannerOptions.getLicenseSearch());
        scanJobBuilder.copyrightSearch(signatureScannerOptions.getCopyrightSearch());

        signatureScannerOptions.getAdditionalArguments().ifPresent(scanJobBuilder::additionalScanArguments);

        final String projectName = projectNameVersion.getName();
        final String projectVersionName = projectNameVersion.getVersion();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName);

        signatureScannerOptions.getIndividualFileMatching()
            .ifPresent(scanJobBuilder::individualFileMatching);

        final File sourcePath = directoryManager.getSourceDirectory();
        final String prefix = signatureScannerOptions.getCodeLocationPrefix().orElse(null);
        final String suffix = signatureScannerOptions.getCodeLocationSuffix().orElse(null);

        for (final SignatureScanPath scanPath : signatureScanPaths) {
            final String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanPath.getTargetPath(), dockerTarFile, projectName, projectVersionName, prefix, suffix);
            scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanPath.getTargetCanonicalPath(), scanPath.getExclusions(), codeLocationName));
        }

        return scanJobBuilder;
    }
}