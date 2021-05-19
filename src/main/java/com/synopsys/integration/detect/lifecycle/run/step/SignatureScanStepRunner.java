package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
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
import com.synopsys.integration.detect.tool.signaturescanner.ScanBatchRunnerResult;
import com.synopsys.integration.detect.tool.signaturescanner.ScanBatchRunnerUserResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanStepRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OperationFactory operationFactory;

    public SignatureScanStepRunner(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public SignatureScannerToolResult runSignatureScannerOnline(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, IOException, IntegrationException {
        ScanBatchRunnerResult scanBatchRunnerResult = resolveOnlineScanBatchRunner(blackDuckRunData);

        List<SignatureScanPath> scanPaths = operationFactory.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationFactory.createScanBatchOnline(scanPaths, scanBatchRunnerResult.getInstallDirectory(), projectNameVersion, dockerTargetData, blackDuckRunData);

        NotificationTaskRange notificationTaskRange = operationFactory.createCodeLocationRange(blackDuckRunData);
        SignatureScanOuputResult scanOuputResult = executeScan(scanBatch, scanBatchRunnerResult, scanPaths);

        return SignatureScannerToolResult.createOnlineResult(notificationTaskRange, scanOuputResult.getScanBatchOutput());
    }

    public void runSignatureScannerOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, IOException, IntegrationException {
        ScanBatchRunnerResult scanBatchRunnerResult = resolveOfflineScanBatchRunner();

        List<SignatureScanPath> scanPaths = operationFactory.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationFactory.createScanBatchOffline(scanPaths, scanBatchRunnerResult.getInstallDirectory(), projectNameVersion, dockerTargetData);

        executeScan(scanBatch, scanBatchRunnerResult, scanPaths);
    }

    private SignatureScanOuputResult executeScan(final ScanBatch scanBatch, final ScanBatchRunnerResult scanBatchRunnerResult, final List<SignatureScanPath> scanPaths) throws DetectUserFriendlyException {
        SignatureScanOuputResult scanOuputResult = operationFactory.signatureScan(scanBatch, scanBatchRunnerResult.getScanBatchRunner());

        List<SignatureScannerReport> reports = operationFactory.createSignatureScanReport(scanPaths, scanOuputResult.getScanCommandOutputs());
        operationFactory.publishSignatureScanReport(reports);

        return scanOuputResult;
    }

    private ScanBatchRunnerResult resolveOfflineScanBatchRunner() throws DetectUserFriendlyException {
        ScanBatchRunnerUserResult userProvided = findUserProvidedScanBatchRunner();
        File installDirectory = determineScanInstallDirectory(userProvided);
        ScanBatchRunner scanBatchRunner;
        if (userProvided.getScanBatchRunner().isPresent()) {
            scanBatchRunner = userProvided.getScanBatchRunner().get();
        } else {
            scanBatchRunner = operationFactory.createScanBatchRunnerFromLocalInstall(installDirectory);
        }
        return new ScanBatchRunnerResult(scanBatchRunner, installDirectory);
    }

    private ScanBatchRunnerResult resolveOnlineScanBatchRunner(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        ScanBatchRunnerUserResult userProvided = findUserProvidedScanBatchRunner();
        File installDirectory = determineScanInstallDirectory(userProvided);

        ScanBatchRunner scanBatchRunner;
        if (userProvided.getScanBatchRunner().isPresent()) {
            scanBatchRunner = userProvided.getScanBatchRunner().get();
        } else {
            scanBatchRunner = operationFactory.createScanBatchRunnerWithBlackDuck(blackDuckRunData);
        }

        return new ScanBatchRunnerResult(scanBatchRunner, installDirectory);
    }

    private File determineScanInstallDirectory(ScanBatchRunnerUserResult userProvided) throws DetectUserFriendlyException {
        if (userProvided.getInstallDirectory().isPresent()) {
            return userProvided.getInstallDirectory().get();
        } else {
            return operationFactory.calculateDetectControlledInstallDirectory();
        }
    }

    private ScanBatchRunnerUserResult findUserProvidedScanBatchRunner() throws DetectUserFriendlyException { //TODO: This should be handled by a decision somewhere.
        Optional<File> offlinePath = operationFactory.calculateOfflineLocalScannerInstallPath();
        if (offlinePath.isPresent()) {
            logger.debug("Signature scanner given an existing path for the scanner - we won't attempt to manage the install.");
            return ScanBatchRunnerUserResult.fromLocalInstall(operationFactory.createScanBatchRunnerFromLocalInstall(offlinePath.get()), offlinePath.get());
        } else if (operationFactory.calculateUserProvidedScannerUrl().isPresent()) {
            logger.debug("Signature scanner will use the provided url to download/update the scanner.");
            return ScanBatchRunnerUserResult.fromCustomUrl(operationFactory.createScanBatchRunnerWithCustomUrl(operationFactory.calculateUserProvidedScannerUrl().get()));
        }
        return ScanBatchRunnerUserResult.none();
    }

}
