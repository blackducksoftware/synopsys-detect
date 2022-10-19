package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.tool.signaturescanner.ScanBatchRunnerUserResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanStepRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OperationRunner operationRunner;

    public SignatureScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public SignatureScannerCodeLocationResult runSignatureScannerOnline(
        String detectRunUuid,
        BlackDuckRunData blackDuckRunData,
        NameVersion projectNameVersion,
        DockerTargetData dockerTargetData
    )
        throws DetectUserFriendlyException, OperationException {
        ScanBatchRunner scanBatchRunner = resolveOnlineScanBatchRunner(blackDuckRunData);

        List<SignatureScanPath> scanPaths = operationRunner.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationRunner.createScanBatchOnline(detectRunUuid, scanPaths, projectNameVersion, dockerTargetData, blackDuckRunData);

        NotificationTaskRange notificationTaskRange = operationRunner.createCodeLocationRange(blackDuckRunData);
        List<SignatureScannerReport> reports = executeScan(scanBatch, scanBatchRunner, scanPaths);

        return operationRunner.calculateWaitableSignatureScannerCodeLocations(notificationTaskRange, reports);
    }
    
    public SignatureScanOuputResult runRapidSignatureScannerOnline(String detectRunUuid, BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, DockerTargetData dockerTargetData)
            throws DetectUserFriendlyException, OperationException {
            ScanBatchRunner scanBatchRunner = resolveOnlineScanBatchRunner(blackDuckRunData);

            List<SignatureScanPath> scanPaths = operationRunner.createScanPaths(projectNameVersion, dockerTargetData);
            ScanBatch scanBatch = operationRunner.createScanBatchOnline(detectRunUuid, scanPaths, projectNameVersion, dockerTargetData, blackDuckRunData);

            SignatureScanOuputResult scanResult =  operationRunner.signatureScan(scanBatch, scanBatchRunner);

            // publish report/scan results to status file
            List<SignatureScannerReport> reports = operationRunner.createSignatureScanReport(scanPaths, scanResult.getScanBatchOutput().getOutputs());
            operationRunner.publishSignatureScanReport(reports);

            return scanResult;
        }

    public void runSignatureScannerOffline(String detectRunUuid, NameVersion projectNameVersion, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, OperationException {
        ScanBatchRunner scanBatchRunner = resolveOfflineScanBatchRunner();

        List<SignatureScanPath> scanPaths = operationRunner.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationRunner.createScanBatchOffline(detectRunUuid, scanPaths, projectNameVersion, dockerTargetData);

        executeScan(scanBatch, scanBatchRunner, scanPaths);
    }

    private List<SignatureScannerReport> executeScan(ScanBatch scanBatch, ScanBatchRunner scanBatchRunner, List<SignatureScanPath> scanPaths) throws OperationException {
        SignatureScanOuputResult scanOuputResult = operationRunner.signatureScan(scanBatch, scanBatchRunner);

        List<SignatureScannerReport> reports = operationRunner.createSignatureScanReport(scanPaths, scanOuputResult.getScanBatchOutput().getOutputs());
        operationRunner.publishSignatureScanReport(reports);

        return reports;
    }

    private ScanBatchRunner resolveOfflineScanBatchRunner() throws DetectUserFriendlyException, OperationException {
        return resolveScanBatchRunner(null);
    }

    private ScanBatchRunner resolveOnlineScanBatchRunner(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException, OperationException {
        return resolveScanBatchRunner(blackDuckRunData);
    }

    private ScanBatchRunner resolveScanBatchRunner(@Nullable BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException, OperationException {
        Optional<File> localScannerPath = operationRunner.calculateOnlineLocalScannerInstallPath();
        ScanBatchRunnerUserResult userProvided = findUserProvidedScanBatchRunner(localScannerPath);
        File installDirectory = determineScanInstallDirectory(userProvided);

        ScanBatchRunner scanBatchRunner;
        if (userProvided.getScanBatchRunner().isPresent()) {
            scanBatchRunner = userProvided.getScanBatchRunner().get();
        } else {
            if (blackDuckRunData != null) {
                scanBatchRunner = operationRunner.createScanBatchRunnerWithBlackDuck(blackDuckRunData, installDirectory);
            } else {
                scanBatchRunner = operationRunner.createScanBatchRunnerFromLocalInstall(installDirectory);
            }
        }

        return scanBatchRunner;
    }

    private File determineScanInstallDirectory(ScanBatchRunnerUserResult userProvided) throws OperationException {
        if (userProvided.getInstallDirectory().isPresent()) {
            return userProvided.getInstallDirectory().get();
        } else {
            return operationRunner.calculateDetectControlledInstallDirectory();
        }
    }

    private ScanBatchRunnerUserResult findUserProvidedScanBatchRunner(Optional<File> localScannerPath)
        throws OperationException { //TODO: This should be handled by a decision somewhere.
        if (localScannerPath.isPresent()) {
            logger.debug("Signature scanner given an existing path for the scanner - we won't attempt to manage the install.");
            return ScanBatchRunnerUserResult.fromLocalInstall(operationRunner.createScanBatchRunnerFromLocalInstall(localScannerPath.get()), localScannerPath.get());
        }
        return ScanBatchRunnerUserResult.none();
    }

}
