package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.blackduck.integration.detect.configuration.DetectUserFriendlyException;
import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.DockerTargetData;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.tool.signaturescanner.ScanBatchRunnerUserResult;
import com.blackduck.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.blackduck.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.blackduck.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.blackduck.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.blackduck.integration.detect.tool.signaturescanner.operation.SignatureScanResult;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanStepRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OperationRunner operationRunner;

    public SignatureScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public SignatureScannerCodeLocationResult runSignatureScannerOnline(String detectRunUuid, BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, DockerTargetData dockerTargetData, Set<String> scanIdsToWaitFor, Gson gson)
        throws DetectUserFriendlyException, OperationException, IOException {
        ScanBatchRunner scanBatchRunner = resolveOnlineScanBatchRunner(blackDuckRunData);

        List<SignatureScanPath> scanPaths = operationRunner.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationRunner.createScanBatchOnline(detectRunUuid, scanPaths, projectNameVersion, dockerTargetData, blackDuckRunData);

        NotificationTaskRange notificationTaskRange = operationRunner.createCodeLocationRange(blackDuckRunData);
        List<SignatureScannerReport> reports = executeScan(scanBatch, scanBatchRunner, scanPaths, scanIdsToWaitFor, gson, blackDuckRunData.shouldWaitAtScanLevel());

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

    public void runSignatureScannerOffline(String detectRunUuid, NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, OperationException, IOException {
        ScanBatchRunner scanBatchRunner = resolveOfflineScanBatchRunner();

        List<SignatureScanPath> scanPaths = operationRunner.createScanPaths(projectNameVersion, dockerTargetData);
        ScanBatch scanBatch = operationRunner.createScanBatchOffline(detectRunUuid, scanPaths, projectNameVersion, dockerTargetData);

        executeScan(scanBatch, scanBatchRunner, scanPaths, null, null, false);
    }

    private List<SignatureScannerReport> executeScan(ScanBatch scanBatch, ScanBatchRunner scanBatchRunner, List<SignatureScanPath> scanPaths, Set<String> scanIdsToWaitFor, Gson gson, boolean shouldWaitAtScanLevel) throws OperationException, IOException {
        SignatureScanOuputResult scanOuputResult = operationRunner.signatureScan(scanBatch, scanBatchRunner);
        
        // Do not attempt to gather additional information, and parse files that are potentially not
        // there, if we should not wait at the scan level
        if (shouldWaitAtScanLevel && scanIdsToWaitFor != null) {
            addScansToWaitFor(scanIdsToWaitFor, scanOuputResult, gson);
        }

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
    
    private void addScansToWaitFor(Set<String> scanIdsToWaitFor, SignatureScanOuputResult signatureScanOutputResult, Gson gson) throws IOException {
        List<ScanCommandOutput> outputs = signatureScanOutputResult.getScanBatchOutput().getOutputs();

        for (ScanCommandOutput output : outputs) {
            File specificRunOutputDirectory = output.getSpecificRunOutputDirectory();
            String scanOutputLocation = specificRunOutputDirectory.toString() + SignatureScanResult.OUTPUT_FILE_PATH;

            try {
                Reader reader = Files.newBufferedReader(Paths.get(scanOutputLocation));

                SignatureScanResult result = gson.fromJson(reader, SignatureScanResult.class);
                
                scanIdsToWaitFor.addAll(result.parseScanIds());
            } catch (NoSuchFileException e) {
                logger.warn("Unable to find scanOutput.json file at location: " + scanOutputLocation
                        + ". Will skip waiting for this signature scan.");
            }
        }
    }

}
