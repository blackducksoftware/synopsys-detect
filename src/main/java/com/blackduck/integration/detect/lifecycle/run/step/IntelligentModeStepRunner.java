package com.blackduck.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.api.generated.view.ProjectVersionView;
import com.blackduck.integration.blackduck.codelocation.CodeLocationCreationData;
import com.blackduck.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.blackduck.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.blackduck.integration.blackduck.codelocation.upload.UploadOutput;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.blackduck.service.model.ProjectVersionWrapper;
import com.blackduck.integration.blackduck.version.BlackDuckVersion;
import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import com.blackduck.integration.detect.lifecycle.OperationException;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.DockerTargetData;
import com.blackduck.integration.detect.lifecycle.run.operation.OperationRunner;
import com.blackduck.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.blackduck.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.blackduck.integration.detect.tool.iac.IacScanCodeLocationData;
import com.blackduck.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.blackduck.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.blackduck.integration.detect.util.filter.DetectToolFilter;
import com.blackduck.integration.detect.workflow.bdio.BdioResult;
import com.blackduck.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.blackduck.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.blackduck.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.blackduck.integration.detect.workflow.blackduck.integratedmatching.ScanCountsPayloadCreator;
import com.blackduck.integration.detect.workflow.blackduck.integratedmatching.model.ScanCountsPayload;
import com.blackduck.integration.detect.workflow.report.util.ReportConstants;
import com.blackduck.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.blackduck.integration.detect.workflow.result.DetectResult;
import com.blackduck.integration.detect.workflow.result.ReportDetectResult;
import com.blackduck.integration.detect.workflow.status.FormattedCodeLocation;
import com.blackduck.integration.detect.workflow.status.OperationType;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.rest.HttpUrl;
import com.blackduck.integration.util.NameVersion;
import com.google.gson.Gson;

public class IntelligentModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;
    private final Gson gson;
    private final ScanCountsPayloadCreator scanCountsPayloadCreator;
    private final String detectRunUuid;
    private static final BlackDuckVersion MIN_MULTIPART_BINARY_VERSION = new BlackDuckVersion(2024, 7, 0);

    public IntelligentModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper, Gson gson, ScanCountsPayloadCreator scanCountsPayloadCreator, String detectRunUuid) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.gson = gson;
        this.scanCountsPayloadCreator = scanCountsPayloadCreator;
        this.detectRunUuid = detectRunUuid;
    }

    public void runOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData, BdioResult bdio) throws OperationException {
        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> { //Internal: Sig scan publishes its own status.
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            signatureScanStepRunner.runSignatureScannerOffline(detectRunUuid, projectNameVersion, dockerTargetData);
        });
        stepHelper.runToolIfIncludedWithCallbacks(
            DetectTool.IMPACT_ANALYSIS,
            "Vulnerability Impact Analysis",
            () -> generateImpactAnalysis(projectNameVersion),
            operationRunner::publishImpactSuccess,
            operationRunner::publishImpactFailure
        );
        stepHelper.runToolIfIncluded(DetectTool.IAC_SCAN, "IaC Scanner", () -> {
            IacScanStepRunner iacScanStepRunner = new IacScanStepRunner(operationRunner);
            iacScanStepRunner.runIacScanOffline();
        });

        operationRunner.generateComponentLocationAnalysisIfEnabled(bdio);
    }

    //TODO: Change black duck post options to a decision and stick it in Run Data somewhere.
    //TODO: Change detect tool filter to a decision and stick it in Run Data somewhere
    public void runOnline(
        BlackDuckRunData blackDuckRunData,
        BdioResult bdioResult,
        NameVersion projectNameVersion,
        DetectToolFilter detectToolFilter,
        DockerTargetData dockerTargetData,
        Set<String> binaryTargets
    ) throws OperationException {

        ProjectVersionWrapper projectVersion = stepHelper.runAsGroup(
            "Create or Locate Project",
            OperationType.INTERNAL,
            () -> new BlackDuckProjectVersionStepRunner(operationRunner).runAll(projectNameVersion, blackDuckRunData)
        );

        logger.debug("Completed project and version actions.");
        logger.debug("Processing Detect Code Locations.");
        
        Set<String> scanIdsToWaitFor = new HashSet<>();
        AtomicBoolean mustWaitAtBomSummaryLevel = new AtomicBoolean(false);

        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();

        if (bdioResult.isNotEmpty()) {
            stepHelper.runAsGroup(
                "Upload Bdio",
                OperationType.INTERNAL,
                () -> uploadBdio(blackDuckRunData, bdioResult, scanIdsToWaitFor, codeLocationAccumulator, operationRunner.calculateDetectTimeout())
            );
        } else {
            logger.debug("No BDIO results to upload. Skipping.");
        }

        logger.debug("Completed Detect Code Location processing.");

        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            SignatureScannerCodeLocationResult signatureScannerCodeLocationResult = signatureScanStepRunner.runSignatureScannerOnline(
                detectRunUuid,
                blackDuckRunData,
                projectNameVersion,
                dockerTargetData,
                scanIdsToWaitFor,
                gson
            );
            codeLocationAccumulator.addWaitableCodeLocations(signatureScannerCodeLocationResult.getWaitableCodeLocationData());
            codeLocationAccumulator.addNonWaitableCodeLocation(signatureScannerCodeLocationResult.getNonWaitableCodeLocationData());
        });

        stepHelper.runToolIfIncluded(DetectTool.BINARY_SCAN, "Binary Scanner", () -> {
            BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationRunner);
            invokeBinaryScanningWorkflow(DetectTool.BINARY_SCAN, binaryScanStepRunner, dockerTargetData, projectNameVersion, blackDuckRunData, binaryTargets, scanIdsToWaitFor, codeLocationAccumulator, mustWaitAtBomSummaryLevel);
        });

        stepHelper.runToolIfIncluded(
            DetectTool.CONTAINER_SCAN,
            "Container Scanner",
            () -> {
                ContainerScanStepRunner containerScanStepRunner = new ContainerScanStepRunner(operationRunner, projectNameVersion, blackDuckRunData, gson);
                logger.debug("Invoking intelligent persistent container scan.");
                Optional<UUID> scanId = containerScanStepRunner.invokeContainerScanningWorkflow();
                scanId.ifPresent(uuid -> scanIdsToWaitFor.add(uuid.toString()));
                Set<String> containerScanCodeLocations = new HashSet<>();
                containerScanCodeLocations.add(containerScanStepRunner.getCodeLocationName());
                codeLocationAccumulator.addNonWaitableCodeLocation(containerScanCodeLocations);
            }
        );

        stepHelper.runToolIfIncludedWithCallbacks(
            DetectTool.IMPACT_ANALYSIS,
            "Vulnerability Impact Analysis",
            () -> {
                runImpactAnalysisOnline(projectNameVersion, projectVersion, codeLocationAccumulator, blackDuckRunData.getBlackDuckServicesFactory());
                mustWaitAtBomSummaryLevel.set(true);
            },
            operationRunner::publishImpactSuccess,
            operationRunner::publishImpactFailure
        );

        stepHelper.runToolIfIncluded(DetectTool.IAC_SCAN, "IaC Scanner", () -> {
            IacScanStepRunner iacScanStepRunner = new IacScanStepRunner(operationRunner);
            IacScanCodeLocationData iacScanCodeLocationData = iacScanStepRunner.runIacScanOnline(detectRunUuid, projectNameVersion, blackDuckRunData);
            codeLocationAccumulator.addNonWaitableCodeLocation(iacScanCodeLocationData.getCodeLocationNames());
            mustWaitAtBomSummaryLevel.set(true);
        });
        
        stepHelper.runToolIfIncluded(DetectTool.THREAT_INTEL, "Threat Intel Scan", () -> {
            ThreatIntelScanStepRunner threatIntelScanStepRunner = new ThreatIntelScanStepRunner(operationRunner, blackDuckRunData, projectNameVersion);
            Optional<UUID> scanId = threatIntelScanStepRunner.invokeThreatIntelWorkflow();
            scanId.ifPresent(uuid -> scanIdsToWaitFor.add(uuid.toString()));
            Set<String> threatIntelScanCodeLocations = new HashSet<>();
            threatIntelScanCodeLocations.add(threatIntelScanStepRunner.getCodeLocationName());
            codeLocationAccumulator.addNonWaitableCodeLocation(threatIntelScanCodeLocations);
        });

        if (operationRunner.createBlackDuckPostOptions().isCorrelatedScanningEnabled()) {
            stepHelper.runAsGroup("Upload Correlated Scan Counts", OperationType.INTERNAL, () -> {
                uploadCorrelatedScanCounts(blackDuckRunData, codeLocationAccumulator, detectRunUuid);
            });
        }
        operationRunner.attemptToGenerateComponentLocationAnalysisIfEnabled();

        stepHelper.runAsGroup("Wait for Results", OperationType.INTERNAL, () -> {
            // Calculate code locations. We do this even if we don't wait as we want to report code location data 
            // in various reports.
            CodeLocationResults codeLocationResults = calculateCodeLocations(codeLocationAccumulator);
            
            if (operationRunner.createBlackDuckPostOptions().shouldWaitForResults()) {                  
                // Waiting at the scan level is more reliable, do that if the BD server is new enough.
                if (blackDuckRunData.shouldWaitAtScanLevel()) {
                    pollForBomScanCompletion(blackDuckRunData, projectVersion, scanIdsToWaitFor);
                } 

                // If the BD server is older, or we can't detect its version, or if we have scans that we are 
                // not yet able to obtain the scanID for, use the original notification based waiting.
                if (!blackDuckRunData.shouldWaitAtScanLevel() || mustWaitAtBomSummaryLevel.get()) {
                    waitForCodeLocations(codeLocationResults.getCodeLocationWaitData(), projectNameVersion, blackDuckRunData);
                }
            }
        });

        stepHelper.runAsGroup("Black Duck Post Actions", OperationType.INTERNAL, () -> {
            checkPolicy(projectVersion.getProjectVersionView(), blackDuckRunData);
            riskReport(blackDuckRunData, projectVersion);
            noticesReport(blackDuckRunData, projectVersion);
            publishPostResults(bdioResult, projectVersion, detectToolFilter);
        });
    }

    private void invokeBinaryScanningWorkflow(
        DetectTool detectTool,
        BinaryScanStepRunner binaryScanStepRunner,
        DockerTargetData dockerTargetData,
        NameVersion projectNameVersion,
        BlackDuckRunData blackDuckRunData,
        Set<String> binaryTargets,
        Set<String> scanIdsToWaitFor,
        CodeLocationAccumulator codeLocationAccumulator,
        AtomicBoolean mustWaitAtBomSummaryLevel
    )
        throws IntegrationException, OperationException {
        Optional<String> scanId = Optional.empty();
        
        if (binaryScanStepRunner.areScassScansPossible(blackDuckRunData.getBlackDuckServerVersion())) {
            scanId = binaryScanStepRunner.runBinaryScan(dockerTargetData, projectNameVersion, blackDuckRunData, binaryTargets);
        } else if (isMultipartUploadPossible(blackDuckRunData)) {
            scanId = binaryScanStepRunner.runLegacyMultipartBinaryScan(dockerTargetData, projectNameVersion,
                    blackDuckRunData, binaryTargets);
        } 
        
        if (scanId.isPresent()) {
            scanIdsToWaitFor.add(scanId.get());
        } else {
            Optional<CodeLocationCreationData<BinaryScanBatchOutput>> codeLocationData =
                binaryScanStepRunner.runLegacyBinaryScan(dockerTargetData, projectNameVersion, blackDuckRunData, binaryTargets);

            if (codeLocationData.isPresent()) {
                codeLocationAccumulator.addWaitableCodeLocations(detectTool, codeLocationData.get());
                mustWaitAtBomSummaryLevel.set(true);
            }
        }
    }

    private void pollForBomScanCompletion(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion,
            Set<String> scanIdsToWaitFor) throws IntegrationException, OperationException {
        HttpUrl bomToSearchFor = projectVersion.getProjectVersionView().getFirstLink(ProjectVersionView.BOM_STATUS_LINK);

        for (String scanId : scanIdsToWaitFor) {
            if (scanId == null) {
                logger.debug("Unexpected null scanID for project version" + projectVersion.getProjectVersionView().getVersionName()
                        + " skipping waiting for this scan.");
                continue;
            }
            
            HttpUrl scanToSearchFor = new HttpUrl(bomToSearchFor.toString() + "/" + scanId);
            operationRunner.waitForBomCompletion(blackDuckRunData, scanToSearchFor);
        }
    }

    public void uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, Set<String> scanIdsToWaitFor, CodeLocationAccumulator codeLocationAccumulator, Long timeout) throws OperationException {
        BdioUploadResult uploadResult = operationRunner.uploadBdioIntelligentPersistent(blackDuckRunData, bdioResult, timeout);
        Optional<CodeLocationCreationData<UploadBatchOutput>> codeLocationCreationData = uploadResult.getUploadOutput();
        codeLocationCreationData.ifPresent(uploadBatchOutputCodeLocationCreationData -> codeLocationAccumulator.addWaitableCodeLocations(
            DetectTool.DETECTOR,
            uploadBatchOutputCodeLocationCreationData
        ));
        if (uploadResult.getUploadOutput().isPresent()) {
            for (UploadOutput result : uploadResult.getUploadOutput().get().getOutput()) {
                result.getScanId().ifPresent((scanId) -> scanIdsToWaitFor.add(scanId));
            }
        }
    }
    
    public void uploadCorrelatedScanCounts(BlackDuckRunData blackDuckRunData, CodeLocationAccumulator codeLocationAccumulator, String detectRunUuid) throws OperationException {
        logger.debug("Uploading correlated scan counts to Black Duck (correlation ID: {})", detectRunUuid);
        ScanCountsPayload scanCountsPayload = scanCountsPayloadCreator.create(codeLocationAccumulator.getWaitableCodeLocations());
        operationRunner.uploadCorrelatedScanCounts(blackDuckRunData, detectRunUuid, scanCountsPayload);        
    }

    public CodeLocationResults calculateCodeLocations(CodeLocationAccumulator codeLocationAccumulator) throws OperationException { //this is waiting....
        logger.info(ReportConstants.RUN_SEPARATOR);

        Set<String> allCodeLocationNames = new HashSet<>(codeLocationAccumulator.getNonWaitableCodeLocations());
        CodeLocationWaitData waitData = operationRunner.calculateCodeLocationWaitData(codeLocationAccumulator.getWaitableCodeLocations());
        allCodeLocationNames.addAll(waitData.getCodeLocationNames());
        
        Set<FormattedCodeLocation> allCodeLocationData = new HashSet<>();
        for (String codeLocationName : allCodeLocationNames) {
            FormattedCodeLocation codeLocation = new FormattedCodeLocation(codeLocationName, null, null);
            allCodeLocationData.add(codeLocation);
        }
        
        operationRunner.publishCodeLocationData(allCodeLocationData);
        return new CodeLocationResults(allCodeLocationNames, waitData);
    }

    private boolean shouldPublishBomLinkForTool(DetectToolFilter detectToolFilter) {
        return detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN) ||
            detectToolFilter.shouldInclude(DetectTool.CONTAINER_SCAN) ||
            detectToolFilter.shouldInclude(DetectTool.BINARY_SCAN) ||
            detectToolFilter.shouldInclude(DetectTool.THREAT_INTEL);
    }

    private void publishPostResults(BdioResult bdioResult, ProjectVersionWrapper projectVersionWrapper, DetectToolFilter detectToolFilter) {
        if ((!bdioResult.getUploadTargets().isEmpty() || shouldPublishBomLinkForTool(detectToolFilter))) {
            Optional<String> componentsLink = Optional.ofNullable(projectVersionWrapper)
                .map(ProjectVersionWrapper::getProjectVersionView)
                .flatMap(projectVersionView -> projectVersionView.getFirstLinkSafely(ProjectVersionView.COMPONENTS_LINK))
                .map(HttpUrl::string);

            if (componentsLink.isPresent()) {
                DetectResult detectResult = new BlackDuckBomDetectResult(componentsLink.get());
                operationRunner.publishResult(detectResult);
            }
        }
    }

    private void checkPolicy(ProjectVersionView projectVersionView, BlackDuckRunData blackDuckRunData) throws OperationException {
        logger.info("Checking to see if Detect should check policy for violations.");
        if (operationRunner.createBlackDuckPostOptions().shouldPerformSeverityPolicyCheck()) {
            operationRunner.checkPolicyBySeverity(blackDuckRunData, projectVersionView);
        }
        if (operationRunner.createBlackDuckPostOptions().shouldPerformNamePolicyCheck()) {
            operationRunner.checkPolicyByName(blackDuckRunData, projectVersionView);
        }
    }
    
    public void waitForCodeLocations(CodeLocationWaitData codeLocationWaitData, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
            throws OperationException {
            logger.info("Checking to see if Detect should wait for bom tool calculations to finish.");
            if (operationRunner.createBlackDuckPostOptions().shouldWaitForResults() && codeLocationWaitData.getExpectedNotificationCount() > 0) {
                operationRunner.waitForCodeLocations(blackDuckRunData, codeLocationWaitData, projectNameVersion);
            }
        }

    public void runImpactAnalysisOnline(
        NameVersion projectNameVersion,
        ProjectVersionWrapper projectVersionWrapper,
        CodeLocationAccumulator codeLocationAccumulator,
        BlackDuckServicesFactory blackDuckServicesFactory
    ) throws OperationException {
        String impactAnalysisName = operationRunner.generateImpactAnalysisCodeLocationName(projectNameVersion);
        Path impactFile = operationRunner.generateImpactAnalysisFile(impactAnalysisName);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadData = operationRunner.uploadImpactAnalysisFile(
            impactFile,
            projectNameVersion,
            impactAnalysisName,
            blackDuckServicesFactory
        );
        operationRunner.mapImpactAnalysisCodeLocations(impactFile, uploadData, projectVersionWrapper, blackDuckServicesFactory);
        /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
        codeLocationAccumulator.addNonWaitableCodeLocation(uploadData.getOutput().getSuccessfulCodeLocationNames());
    }

    private Path generateImpactAnalysis(NameVersion projectNameVersion) throws OperationException {
        String impactAnalysisName = operationRunner.generateImpactAnalysisCodeLocationName(projectNameVersion);
        return operationRunner.generateImpactAnalysisFile(impactAnalysisName);
    }

    public void riskReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws IOException, OperationException {
        Optional<File> riskReportFile = operationRunner.calculateRiskReportFileLocation();
        if (riskReportFile.isPresent()) {
            logger.info("Creating risk report pdf");
            File reportDirectory = riskReportFile.get();

            if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create risk report pdf directory: %s", reportDirectory));
            }

            File createdPdf = operationRunner.createRiskReportFile(blackDuckRunData, projectVersion, reportDirectory);

            logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            operationRunner.publishReport(new ReportDetectResult("Risk Report", createdPdf.getCanonicalPath()));
        }
    }

    public void noticesReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws OperationException, IOException {
        Optional<File> noticesReportDirectory = operationRunner.calculateNoticesDirectory();
        if (noticesReportDirectory.isPresent()) {
            logger.info("Creating notices report");
            File noticesDirectory = noticesReportDirectory.get();

            if (!noticesDirectory.exists() && !noticesDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create notices directory at %s", noticesDirectory));
            }

            File noticesFile = operationRunner.createNoticesReportFile(blackDuckRunData, projectVersion, noticesDirectory);
            logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));

            operationRunner.publishReport(new ReportDetectResult("Notices Report", noticesFile.getCanonicalPath()));

        }
    }
    
    private boolean isMultipartUploadPossible(BlackDuckRunData blackDuckRunData) {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_MULTIPART_BINARY_VERSION);
    }
}
