package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.iac.IacScanCodeLocationData;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model.ScanCountsPayload;
import com.synopsys.integration.detect.workflow.blackduck.integratedmatching.ScanCountsPayloadCreator;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.detect.workflow.status.OperationType;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class IntelligentModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;
    private final ScanCountsPayloadCreator scanCountsPayloadCreator;
    private final String detectRunUuid;

    public IntelligentModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper, ScanCountsPayloadCreator scanCountsPayloadCreator, String detectRunUuid) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.scanCountsPayloadCreator = scanCountsPayloadCreator;
        this.detectRunUuid = detectRunUuid;
    }

    public void runOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws OperationException {
        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> { //Internal: Sig scan publishes it's own status.
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
    }

    //TODO: Change black duck post options to a decision and stick it in Run Data somewhere.
    //TODO: Change detect tool filter to a decision and stick it in Run Data somewhere
    public void runOnline(
        BlackDuckRunData blackDuckRunData,
        BdioResult bdioResult,
        NameVersion projectNameVersion,
        DetectToolFilter detectToolFilter,
        DockerTargetData dockerTargetData
    ) throws OperationException {

        ProjectVersionWrapper projectVersion = stepHelper.runAsGroup(
            "Create or Locate Project",
            OperationType.INTERNAL,
            () -> new BlackDuckProjectVersionStepRunner(operationRunner).runAll(projectNameVersion, blackDuckRunData)
        );

        logger.debug("Completed project and version actions.");
        logger.debug("Processing Detect Code Locations.");

        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();

        if (bdioResult.isNotEmpty()) {
            stepHelper.runAsGroup(
                "Upload Bdio",
                OperationType.INTERNAL,
                () -> uploadBdio(blackDuckRunData, bdioResult, codeLocationAccumulator, operationRunner.calculateDetectTimeout())
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
                dockerTargetData
            );
            codeLocationAccumulator.addWaitableCodeLocations(signatureScannerCodeLocationResult.getWaitableCodeLocationData());
            codeLocationAccumulator.addNonWaitableCodeLocation(signatureScannerCodeLocationResult.getNonWaitableCodeLocationData());
        });

        stepHelper.runToolIfIncluded(DetectTool.BINARY_SCAN, "Binary Scanner", () -> {
            BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationRunner);
            Optional<CodeLocationCreationData<BinaryScanBatchOutput>> codeLocationCreationData = binaryScanStepRunner.runBinaryScan(dockerTargetData, projectNameVersion, blackDuckRunData);
            codeLocationCreationData.ifPresent(binaryScanBatchOutputCodeLocationCreationData -> codeLocationAccumulator.addWaitableCodeLocations(
                DetectTool.BINARY_SCAN,
                binaryScanBatchOutputCodeLocationCreationData
            ));
        });

        stepHelper.runToolIfIncludedWithCallbacks(
            DetectTool.IMPACT_ANALYSIS,
            "Vulnerability Impact Analysis",
            () -> runImpactAnalysisOnline(projectNameVersion, projectVersion, codeLocationAccumulator, blackDuckRunData.getBlackDuckServicesFactory()),
            operationRunner::publishImpactSuccess,
            operationRunner::publishImpactFailure
        );

        stepHelper.runToolIfIncluded(DetectTool.IAC_SCAN, "IaC Scanner", () -> {
            IacScanStepRunner iacScanStepRunner = new IacScanStepRunner(operationRunner);
            IacScanCodeLocationData iacScanCodeLocationData = iacScanStepRunner.runIacScanOnline(detectRunUuid, projectNameVersion, blackDuckRunData);
            codeLocationAccumulator.addNonWaitableCodeLocation(iacScanCodeLocationData.getCodeLocationNames());
        });

        if (operationRunner.createBlackDuckPostOptions().isIntegratedMatchingEnabled()) {
            stepHelper.runAsGroup("Upload Correlated Scan Counts", OperationType.INTERNAL, () -> {
                uploadCorrelatedScanCounts(blackDuckRunData, codeLocationAccumulator, detectRunUuid);
            });
        }

        stepHelper.runAsGroup("Wait for Results", OperationType.INTERNAL, () -> {
            CodeLocationResults codeLocationResults = calculateCodeLocations(codeLocationAccumulator);
            waitForCodeLocations(codeLocationResults.getCodeLocationWaitData(), projectNameVersion, blackDuckRunData);
        });

        stepHelper.runAsGroup("Black Duck Post Actions", OperationType.INTERNAL, () -> {
            checkPolicy(projectVersion.getProjectVersionView(), blackDuckRunData);
            riskReport(blackDuckRunData, projectVersion);
            noticesReport(blackDuckRunData, projectVersion);
            publishPostResults(bdioResult, projectVersion, detectToolFilter);
        });
    }

    public void uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, CodeLocationAccumulator codeLocationAccumulator, Long timeout) throws OperationException {
        BdioUploadResult uploadResult = operationRunner.uploadBdioIntelligentPersistent(blackDuckRunData, bdioResult, timeout);
        Optional<CodeLocationCreationData<UploadBatchOutput>> codeLocationCreationData = uploadResult.getUploadOutput();
        codeLocationCreationData.ifPresent(uploadBatchOutputCodeLocationCreationData -> codeLocationAccumulator.addWaitableCodeLocations(
            DetectTool.DETECTOR,
            uploadBatchOutputCodeLocationCreationData
        ));
    }

    public void uploadCorrelatedScanCounts(BlackDuckRunData blackDuckRunData, CodeLocationAccumulator codeLocationAccumulator, String detectRunUuid) throws OperationException {
        logger.info("Uploading correlated scan counts to Black Duck (correlation ID: {})", detectRunUuid);
        ScanCountsPayload scanCountsPayload = scanCountsPayloadCreator.create(codeLocationAccumulator.getWaitableCodeLocations());
        operationRunner.uploadCorrelatedScanCounts(blackDuckRunData, detectRunUuid, scanCountsPayload);
    }

    public CodeLocationResults calculateCodeLocations(CodeLocationAccumulator codeLocationAccumulator) throws OperationException { //this is waiting....
        logger.info(ReportConstants.RUN_SEPARATOR);

        Set<String> allCodeLocationNames = new HashSet<>(codeLocationAccumulator.getNonWaitableCodeLocations());
        CodeLocationWaitData waitData = operationRunner.calculateCodeLocationWaitData(codeLocationAccumulator.getWaitableCodeLocations());
        allCodeLocationNames.addAll(waitData.getCodeLocationNames());
        operationRunner.publishCodeLocationNames(allCodeLocationNames);
        return new CodeLocationResults(allCodeLocationNames, waitData);
    }

    private void publishPostResults(BdioResult bdioResult, ProjectVersionWrapper projectVersionWrapper, DetectToolFilter detectToolFilter) {
        if ((!bdioResult.getUploadTargets().isEmpty() || detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN))) {
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
}
