/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.signaturescanner.ScanBatchRunnerUserResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanStepRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OperationFactory operationFactory;

    public SignatureScanStepRunner(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public SignatureScannerToolResult runSignatureScannerOnline(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException {
        ScanBatchRunner scanBatchRunner = resolveOnlineScanBatchRunner(blackDuckRunData);

        List<SignatureScanPath> scanPaths = operationFactory.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationFactory.createScanBatchOnline(scanPaths, projectNameVersion, dockerTargetData, blackDuckRunData);

        NotificationTaskRange notificationTaskRange = operationFactory.createCodeLocationRange(blackDuckRunData);
        SignatureScanOuputResult scanOuputResult = executeScan(scanBatch, scanBatchRunner, scanPaths);

        return SignatureScannerToolResult.createOnlineResult(notificationTaskRange, scanOuputResult.getScanBatchOutput());
    }

    public void runSignatureScannerOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException {
        ScanBatchRunner scanBatchRunner = resolveOfflineScanBatchRunner();

        List<SignatureScanPath> scanPaths = operationFactory.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationFactory.createScanBatchOffline(scanPaths, projectNameVersion, dockerTargetData);

        executeScan(scanBatch, scanBatchRunner, scanPaths);
    }

    private SignatureScanOuputResult executeScan(final ScanBatch scanBatch, final ScanBatchRunner scanBatchRunner, final List<SignatureScanPath> scanPaths) throws DetectUserFriendlyException {
        SignatureScanOuputResult scanOuputResult = operationFactory.signatureScan(scanBatch, scanBatchRunner);

        List<SignatureScannerReport> reports = operationFactory.createSignatureScanReport(scanPaths, scanOuputResult.getScanCommandOutputs());
        operationFactory.publishSignatureScanReport(reports);

        return scanOuputResult;
    }

    private ScanBatchRunner resolveOfflineScanBatchRunner() throws DetectUserFriendlyException {
        Optional<File> localScannerPath = operationFactory.calculateOfflineLocalScannerInstallPath();
        ScanBatchRunnerUserResult userProvided = findUserProvidedScanBatchRunner(localScannerPath);
        File installDirectory = determineScanInstallDirectory(userProvided);
        ScanBatchRunner scanBatchRunner;
        if (userProvided.getScanBatchRunner().isPresent()) {
            scanBatchRunner = userProvided.getScanBatchRunner().get();
        } else {
            scanBatchRunner = operationFactory.createScanBatchRunnerFromLocalInstall(installDirectory);
        }
        return scanBatchRunner;
    }

    private ScanBatchRunner resolveOnlineScanBatchRunner(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        Optional<File> localScannerPath = operationFactory.calculateOnlineLocalScannerInstallPath();
        ScanBatchRunnerUserResult userProvided = findUserProvidedScanBatchRunner(localScannerPath);
        File installDirectory = determineScanInstallDirectory(userProvided);

        ScanBatchRunner scanBatchRunner;
        if (userProvided.getScanBatchRunner().isPresent()) {
            scanBatchRunner = userProvided.getScanBatchRunner().get();
        } else {
            scanBatchRunner = operationFactory.createScanBatchRunnerWithBlackDuck(blackDuckRunData, installDirectory);
        }

        return scanBatchRunner;
    }

    private File determineScanInstallDirectory(ScanBatchRunnerUserResult userProvided) throws DetectUserFriendlyException {
        if (userProvided.getInstallDirectory().isPresent()) {
            return userProvided.getInstallDirectory().get();
        } else {
            return operationFactory.calculateDetectControlledInstallDirectory();
        }
    }

    private ScanBatchRunnerUserResult findUserProvidedScanBatchRunner(Optional<File> localScannerPath) throws DetectUserFriendlyException { //TODO: This should be handled by a decision somewhere.
        if (localScannerPath.isPresent()) {
            logger.debug("Signature scanner given an existing path for the scanner - we won't attempt to manage the install.");
            return ScanBatchRunnerUserResult.fromLocalInstall(operationFactory.createScanBatchRunnerFromLocalInstall(localScannerPath.get()), localScannerPath.get());
        } else if (operationFactory.calculateUserProvidedScannerUrl().isPresent()) {
            logger.debug("Signature scanner will use the provided url to download/update the scanner.");
            //TODO ejk - not confident this is right
            File installDirectory = operationFactory.calculateDetectControlledInstallDirectory();
            return ScanBatchRunnerUserResult.fromCustomUrl(operationFactory.createScanBatchRunnerWithCustomUrl(operationFactory.calculateUserProvidedScannerUrl().get(), installDirectory));
        }
        return ScanBatchRunnerUserResult.none();
    }

}
