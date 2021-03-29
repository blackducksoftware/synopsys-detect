/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.blackduck.ExclusionPatternCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.SignatureScanStatus;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScanner {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScanner.class);
    public static final String OPERATION_NAME = "Black Duck Signature Scanner";

    private final DirectoryManager directoryManager;
    private final FileFinder fileFinder;
    private final CodeLocationNameManager codeLocationNameManager;
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;
    private final ScanBatchRunner scanJobManager;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final OperationSystem operationSystem;

    //When OFFLINE, this should be NULL. No other changes required for offline (in this class).
    private final BlackDuckServerConfig blackDuckServerConfig;

    public BlackDuckSignatureScanner(DirectoryManager directoryManager, FileFinder fileFinder, CodeLocationNameManager codeLocationNameManager,
        BlackDuckSignatureScannerOptions signatureScannerOptions, ScanBatchRunner scanJobManager, BlackDuckServerConfig blackDuckServerConfig, StatusEventPublisher statusEventPublisher,
        ExitCodePublisher exitCodePublisher, OperationSystem operationSystem) {
        this.directoryManager = directoryManager;
        this.fileFinder = fileFinder;
        this.codeLocationNameManager = codeLocationNameManager;
        this.signatureScannerOptions = signatureScannerOptions;
        this.scanJobManager = scanJobManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.operationSystem = operationSystem;

    }

    public ScanBatchOutput performScanActions(NameVersion projectNameVersion, File installDirectory, @Nullable DockerTargetData dockerTargetData) throws IntegrationException, IOException, DetectUserFriendlyException {
        operationSystem.beginOperation(OPERATION_NAME);
        List<SignatureScanPath> signatureScanPaths = determinePathsAndExclusions(projectNameVersion, signatureScannerOptions.getMaxDepth(), dockerTargetData);

        ScanBatchBuilder scanJobBuilder = createDefaultScanBatchBuilder(projectNameVersion, installDirectory, signatureScanPaths, dockerTargetData);
        scanJobBuilder.fromBlackDuckServerConfig(blackDuckServerConfig);//when offline, we must still call this with 'null' as a workaround for library issues, so offline scanner must be created with this set to null.
        ScanBatch scanJob;
        try {
            scanJob = scanJobBuilder.build();
        } catch (IllegalArgumentException e) {
            operationSystem.completeWithFailure(OPERATION_NAME);
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        List<ScanCommandOutput> scanCommandOutputs = new ArrayList<>();
        ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        if (scanJobOutput.getOutputs() != null) {
            scanCommandOutputs.addAll(scanJobOutput.getOutputs());
        }

        reportResults(signatureScanPaths, scanCommandOutputs);

        return scanJobOutput;
    }

    //TODO: Possibly promote this to the Tool. Ideally it would return some object describing these results and the Tool translates that into detect nonsense -jp.
    private void reportResults(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList) {
        List<SignatureScannerReport> signatureScannerReports = new ArrayList<>();
        for (SignatureScanPath signatureScanPath : signatureScanPaths) {
            Optional<ScanCommandOutput> scanCommandOutput = scanCommandOutputList.stream()
                                                                .filter(output -> output.getScanTarget().equals(signatureScanPath.getTargetCanonicalPath()))
                                                                .findFirst();
            SignatureScannerReport signatureScannerReport = SignatureScannerReport.create(signatureScanPath, scanCommandOutput.orElse(null));
            signatureScannerReports.add(signatureScannerReport);
        }

        signatureScannerReports.forEach(this::publishResults);

        signatureScannerReports.stream()
            .filter(SignatureScannerReport::isFailure)
            .findAny()
            .ifPresent(report -> {
                logger.error(String.format("The Signature Scanner encountered an error%s. Please refer to Black Duck documentation or contact support.", report.getExitCode().map(code -> " (" + code + ")").orElse(".")));
                exitCodePublisher.publishExitCode(new ExitCodeRequest(ExitCodeType.FAILURE_SCAN));
            });
    }

    private void publishResults(SignatureScannerReport signatureScannerReport) {
        if (signatureScannerReport.isSuccessful()) {
            statusEventPublisher.publishStatusSummary(new SignatureScanStatus(signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath(), StatusType.SUCCESS));
            operationSystem.completeWithSuccess(OPERATION_NAME);
            return;
        }

        String scanTargetPath = signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath();
        if (!signatureScannerReport.hasOutput()) {
            String errorMessage = String.format("Scanning target %s was never scanned by the BlackDuck CLI.", scanTargetPath);
            logger.info(errorMessage);
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, OPERATION_NAME, Arrays.asList(errorMessage)));
        } else {
            String errorMessage = signatureScannerReport.getErrorMessage()
                                      .map(message -> String.format("Scanning target %s failed: %s", scanTargetPath, message))
                                      .orElse(String.format("Scanning target %s failed for an unknown reason.", scanTargetPath));
            logger.error(errorMessage);
            signatureScannerReport.getException().ifPresent(exception -> logger.debug(errorMessage, exception));

            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, OPERATION_NAME, Arrays.asList(errorMessage)));
        }

        statusEventPublisher.publishStatusSummary(new SignatureScanStatus(signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath(), StatusType.FAILURE));
        operationSystem.completeWithFailure(OPERATION_NAME);
    }

    private List<SignatureScanPath> determinePathsAndExclusions(NameVersion projectNameVersion, Integer maxDepth, @Nullable DockerTargetData dockerTargetData) throws IOException {
        List<Path> providedSignatureScanPaths = signatureScannerOptions.getSignatureScannerPaths();
        boolean userProvidedScanTargets = null != providedSignatureScanPaths && !providedSignatureScanPaths.isEmpty();
        List<String> providedExclusionPatterns = signatureScannerOptions.getExclusionPatterns();
        List<String> signatureScannerExclusionNamePatterns = signatureScannerOptions.getExclusionNamePatterns();

        List<SignatureScanPath> signatureScanPaths = new ArrayList<>();
        if (null != projectNameVersion.getName() && null != projectNameVersion.getVersion() && userProvidedScanTargets) {
            for (Path path : providedSignatureScanPaths) {
                logger.info(String.format("Registering explicit scan path %s", path));
                SignatureScanPath scanPath = createScanPath(path, maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
                signatureScanPaths.add(scanPath);
            }
        } else if (dockerTargetData != null && dockerTargetData.getSquashedImage().isPresent()) {
            SignatureScanPath scanPath = createScanPath(dockerTargetData.getSquashedImage().get().getCanonicalFile().toPath(), maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        } else if (dockerTargetData != null && dockerTargetData.getProvidedImageTar().isPresent()) {
            SignatureScanPath scanPath = createScanPath(dockerTargetData.getProvidedImageTar().get().getCanonicalFile().toPath(), maxDepth, signatureScannerExclusionNamePatterns, providedExclusionPatterns);
            signatureScanPaths.add(scanPath);
        } else {
            Path sourcePath = directoryManager.getSourceDirectory().getAbsoluteFile().toPath();
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

    private SignatureScanPath createScanPath(Path path, Integer maxDepth, List<String> signatureScannerExclusionNamePatterns, List<String> providedExclusionPatterns) {
        File target = path.toFile();
        ExclusionPatternCreator exclusionPatternCreator = new ExclusionPatternCreator(fileFinder, target);

        Set<String> scanExclusionPatterns = exclusionPatternCreator.determineExclusionPatterns(maxDepth, signatureScannerExclusionNamePatterns);
        if (null != providedExclusionPatterns) {
            scanExclusionPatterns.addAll(providedExclusionPatterns);
        }
        SignatureScanPath signatureScanPath = new SignatureScanPath();
        signatureScanPath.setTargetPath(target);
        signatureScanPath.getExclusions().addAll(scanExclusionPatterns);
        return signatureScanPath;
    }

    protected ScanBatchBuilder createDefaultScanBatchBuilder(NameVersion projectNameVersion, File installDirectory, List<SignatureScanPath> signatureScanPaths, @Nullable DockerTargetData dockerTargetData) {
        ScanBatchBuilder scanJobBuilder = new ScanBatchBuilder();
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

        String projectName = projectNameVersion.getName();
        String projectVersionName = projectNameVersion.getVersion();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName);

        signatureScannerOptions.getIndividualFileMatching()
            .ifPresent(scanJobBuilder::individualFileMatching);

        File sourcePath = directoryManager.getSourceDirectory();
        String prefix = signatureScannerOptions.getCodeLocationPrefix().orElse(null);
        String suffix = signatureScannerOptions.getCodeLocationSuffix().orElse(null);

        for (SignatureScanPath scanPath : signatureScanPaths) {
            File dockerTarget = null;
            if (dockerTargetData != null) {
                dockerTarget = dockerTargetData.getSquashedImage().orElse(dockerTargetData.getProvidedImageTar().orElse(null));
            }
            String codeLocationName = codeLocationNameManager.createScanCodeLocationName(sourcePath, scanPath.getTargetPath(), dockerTarget, projectName, projectVersionName, prefix, suffix);
            scanJobBuilder.addTarget(ScanTarget.createBasicTarget(scanPath.getTargetCanonicalPath(), scanPath.getExclusions(), codeLocationName));
        }

        return scanJobBuilder;
    }
}
