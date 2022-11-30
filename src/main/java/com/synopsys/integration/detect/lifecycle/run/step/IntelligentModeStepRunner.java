package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.upload.UploadOutput;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersion;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanRapidResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.detect.workflow.status.OperationType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class IntelligentModeStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;
    private final Gson gson;

    public IntelligentModeStepRunner(OperationRunner operationRunner, StepHelper stepHelper, Gson gson) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.gson = gson;
    }

    public void runOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws OperationException {
        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> { //Internal: Sig scan publishes it's own status.
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            signatureScanStepRunner.runSignatureScannerOffline(projectNameVersion, dockerTargetData);
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

        Set<String> scanIdsToWaitFor = new HashSet<>();
        AtomicBoolean mustWaitAtBomSummaryLevel = new AtomicBoolean(false);
        
        if (bdioResult.isNotEmpty()) {
            stepHelper.runAsGroup(
                "Upload Bdio",
                OperationType.INTERNAL,
                () -> uploadBdio(blackDuckRunData, bdioResult, scanIdsToWaitFor, operationRunner.calculateDetectTimeout())
            );
        } else {
            logger.debug("No BDIO results to upload. Skipping.");
        }

        logger.debug("Completed Detect Code Location processing.");

        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationRunner);
            SignatureScanOuputResult signatureScanResult = signatureScanStepRunner.runSignatureScannerOnline(
                blackDuckRunData,
                projectNameVersion,
                dockerTargetData
            );
            
            addSignatureScansToWaitFor(scanIdsToWaitFor, signatureScanResult);
        });

        stepHelper.runToolIfIncluded(DetectTool.BINARY_SCAN, "Binary Scanner", () -> {
            BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationRunner);
            binaryScanStepRunner.runBinaryScan(dockerTargetData, projectNameVersion, blackDuckRunData);
            mustWaitAtBomSummaryLevel.set(true);
        });

        stepHelper.runToolIfIncludedWithCallbacks(
            DetectTool.IMPACT_ANALYSIS,
            "Vulnerability Impact Analysis",
            () -> {
                runImpactAnalysisOnline(projectNameVersion, projectVersion, blackDuckRunData.getBlackDuckServicesFactory());
                mustWaitAtBomSummaryLevel.set(true);
            },
            operationRunner::publishImpactSuccess,
            operationRunner::publishImpactFailure
        );

        stepHelper.runToolIfIncluded(DetectTool.IAC_SCAN, "IaC Scanner", () -> {
            IacScanStepRunner iacScanStepRunner = new IacScanStepRunner(operationRunner);
            iacScanStepRunner.runIacScanOnline(projectNameVersion, blackDuckRunData);
            mustWaitAtBomSummaryLevel.set(true);
        });

        stepHelper.runAsGroup("Wait for Results", OperationType.INTERNAL, () -> {
            if (operationRunner.createBlackDuckPostOptions().shouldWaitForResults()) {                                
                BlackDuckVersion blackDuckServerVersion = blackDuckRunData.getBlackDuckServerVersion().get();
                BlackDuckVersion minVersion = new BlackDuckVersion(2022, 10, 0);
                
                // Waiting at the scan level is more reliable, do that if the BD server is new enough.
                if (blackDuckServerVersion.isAtLeast(minVersion)) {
                    pollForBomScanCompletion(blackDuckRunData, projectVersion, scanIdsToWaitFor);
                } 
                
                // If the BD server is older or if we have scans that we are not yet able to obtain the
                // scanID for, wait at the BOM version level.
                if (!blackDuckServerVersion.isAtLeast(minVersion) || mustWaitAtBomSummaryLevel.get()) {
                    Thread.sleep(5000);
                    pollForBomCompletion(blackDuckRunData, projectVersion);
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

    private void addSignatureScansToWaitFor(Set<String> scanIdsToWaitFor, SignatureScanOuputResult signatureScanOutputResult) throws IOException {
        List<ScanCommandOutput> outputs = signatureScanOutputResult.getScanBatchOutput().getOutputs();
        
        for (ScanCommandOutput output : outputs) {
                File specificRunOutputDirectory = output.getSpecificRunOutputDirectory();
                String scanOutputLocation = specificRunOutputDirectory.toString() + "/output/scanOutput.json";
                Reader reader = Files.newBufferedReader(Paths.get(scanOutputLocation));

                SignatureScanRapidResult result = gson.fromJson(reader, SignatureScanRapidResult.class);

                scanIdsToWaitFor.add(result.scanId);
        }
    }

    private void pollForBomScanCompletion(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion,
            Set<String> scanIdsToWaitFor) throws IntegrationException, OperationException {
        HttpUrl bomToSearchFor = projectVersion.getProjectVersionView().getFirstLink(ProjectVersionView.BOM_STATUS_LINK);
        
        for (String scanId : scanIdsToWaitFor) {
            HttpUrl scanToSearchFor = new HttpUrl(bomToSearchFor.toString() + "/" + scanId);
            
            operationRunner.waitForBomScanCompletion(blackDuckRunData, scanToSearchFor);
        }
        
    }

    /**
     * Polls for the BOM associated with a given version to see if it is completed. Returns nothing
     * as later code will print out the location.
     * 
     * @param blackDuckRunData
     * @param projectVersion
     * @throws OperationException
     * @throws IntegrationException
     */
    private void pollForBomCompletion(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws OperationException, IntegrationException {
        HttpUrl bomToSearchFor = projectVersion.getProjectVersionView().getFirstLink(ProjectVersionView.BOM_STATUS_LINK);
        
        // Poll to see if Bom is ready
        operationRunner.waitForBomCompletion(blackDuckRunData, bomToSearchFor);    
    }

    public void uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, Set<String> scanIdsToWaitFor, Long timeout) throws OperationException {
        BdioUploadResult results = operationRunner.uploadBdioIntelligentPersistent(blackDuckRunData, bdioResult, timeout);
        
        if (results.getUploadOutput().isPresent()) {
            for (UploadOutput result : results.getUploadOutput().get().getOutput()) {
                scanIdsToWaitFor.add(result.getScanId());
            }
        }
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

    public void runImpactAnalysisOnline(
        NameVersion projectNameVersion,
        ProjectVersionWrapper projectVersionWrapper,
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
