/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class IntelligentModeStepRunner {
    private OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private StepHelper stepHelper;

    public IntelligentModeStepRunner(OperationFactory operationFactory, StepHelper stepHelper) {
        this.operationFactory = operationFactory;
        this.stepHelper = stepHelper;
    }

    public void runOffline(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException {
        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationFactory);
            signatureScanStepRunner.runSignatureScannerOffline(projectNameVersion, dockerTargetData);
        });
        stepHelper.runToolIfIncluded(DetectTool.IMPACT_ANALYSIS, "Vulnerability Impact Analysis", () -> generateImpactAnalysis(projectNameVersion));
    }

    //TODO: Change black duck post options to a decision and stick it in Run Data somewhere.
    //TODO: Change detect tool filter to a decision and stick it in Run Data somewhere
    public void runOnline(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, NameVersion projectNameVersion, DetectToolFilter detectToolFilter, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException, IOException, InterruptedException {

        ProjectVersionWrapper projectVersion = stepHelper.runAsGroup("Create or Locate Project", () -> getOrCreateProjectOnBlackDuck(blackDuckRunData, projectNameVersion));

        logger.debug("Completed project and version actions.");
        logger.debug("Processing Detect Code Locations.");

        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
        stepHelper.runAsGroup("Upload Bdio", () -> uploadBdio(blackDuckRunData, bdioResult, codeLocationAccumulator));

        logger.debug("Completed Detect Code Location processing.");

        stepHelper.runToolIfIncluded(DetectTool.SIGNATURE_SCAN, "Signature Scanner", () -> {
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationFactory);
            signatureScanStepRunner.runSignatureScannerOnline(blackDuckRunData, projectNameVersion, dockerTargetData);
        });

        stepHelper.runToolIfIncluded(DetectTool.BINARY_SCAN, "Binary Scanner", () -> {
            Optional<CodeLocationCreationData<BinaryScanBatchOutput>> binaryScanResult = operationFactory.createBinaryScanOperation().execute(projectNameVersion, dockerTargetData);
            binaryScanResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
        });

        stepHelper.runToolIfIncluded(DetectTool.IMPACT_ANALYSIS, "Vulnerability Impact Analysis", () -> runImpactAnalysisOnline(projectNameVersion, projectVersion, codeLocationAccumulator, blackDuckRunData.getBlackDuckServicesFactory()));

        stepHelper.runAsGroup("Wait for Code Locations", () -> {
            CodeLocationResults codeLocationResults = calculateCodeLocations(codeLocationAccumulator);
            waitForCodeLocations(codeLocationResults.getCodeLocationWaitData(), projectNameVersion, blackDuckRunData);
        });

        stepHelper.runAsGroup("Black Duck Post Actions", () -> {
            checkPolicy(projectVersion.getProjectVersionView(), blackDuckRunData);
            riskReport(blackDuckRunData, projectVersion);
            noticesReport(blackDuckRunData, projectVersion);
            publishPostResults(bdioResult, projectVersion, detectToolFilter);
        });
    }

    public void uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, CodeLocationAccumulator codeLocationAccumulator) throws DetectUserFriendlyException, IntegrationException {
        BdioOptions bdioOptions = operationFactory.calculateBdioOptions(); //TODO: Move to a decision
        BdioUploadResult uploadResult;
        if (bdioOptions.isLegacyUploadEnabled()) {
            if (bdioOptions.isBdio2Enabled()) {
                uploadResult = operationFactory.uploadBdio2(blackDuckRunData, bdioResult);
            } else {
                uploadResult = operationFactory.uploadBdio1(blackDuckRunData, bdioResult);
            }
        } else {
            uploadResult = operationFactory.uploadBdioIntelligentPersistent(blackDuckRunData, bdioResult);
        }
        uploadResult.getUploadOutput().ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
    }

    public CodeLocationResults calculateCodeLocations(CodeLocationAccumulator codeLocationAccumulator) throws DetectUserFriendlyException, IntegrationException { //this is waiting....
        logger.info(ReportConstants.RUN_SEPARATOR);

        Set<String> allCodeLocationNames = new HashSet<>(codeLocationAccumulator.getNonWaitableCodeLocations());
        CodeLocationWaitData waitData = operationFactory.calulcateCodeLocationWaitData(codeLocationAccumulator.getWaitableCodeLocations());
        allCodeLocationNames.addAll(waitData.getCodeLocationNames());
        operationFactory.publishCodeLocationNames(allCodeLocationNames);
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
                operationFactory.publishResult(detectResult);
            }
        }
    }

    private void checkPolicy(ProjectVersionView projectVersionView, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        logger.info("Detect will check policy for violations.");
        operationFactory.checkPolicy(blackDuckRunData, projectVersionView);
    }

    public void waitForCodeLocations(CodeLocationWaitData codeLocationWaitData, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws DetectUserFriendlyException {
        logger.info("Detect must wait for bom tool calculations to finish.");
        if (codeLocationWaitData.getExpectedNotificationCount() > 0) {
            operationFactory.waitForCodeLocations(blackDuckRunData, codeLocationWaitData, projectNameVersion);
        }
    }

    public void runImpactAnalysisOnline(NameVersion projectNameVersion, ProjectVersionWrapper projectVersionWrapper, CodeLocationAccumulator codeLocationAccumulator,
        BlackDuckServicesFactory blackDuckServicesFactory) throws DetectUserFriendlyException {
        String impactAnalysisName = operationFactory.generateImpactAnalysisCodeLocationName(projectNameVersion);
        Path impactFile = operationFactory.generateImpactAnalysisFile(impactAnalysisName);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadData = operationFactory.uploadImpactAnalysisFile(impactFile, projectNameVersion, impactAnalysisName, blackDuckServicesFactory);
        operationFactory.mapImpactAnalysisCodeLocations(impactFile, uploadData, projectVersionWrapper, blackDuckServicesFactory);
        /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
        codeLocationAccumulator.addNonWaitableCodeLocation(uploadData.getOutput().getSuccessfulCodeLocationNames());
    }

    private Path generateImpactAnalysis(NameVersion projectNameVersion) throws DetectUserFriendlyException {
        String impactAnalysisName = operationFactory.generateImpactAnalysisCodeLocationName(projectNameVersion);
        return operationFactory.generateImpactAnalysisFile(impactAnalysisName);
    }

    public ProjectVersionWrapper getOrCreateProjectOnBlackDuck(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        logger.debug("Getting or creating project.");
        return operationFactory.getOrCreateProject(blackDuckRunData, projectNameVersion); //TODO: This is not an operation.
    }

    public void riskReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws IOException, DetectUserFriendlyException {
        Optional<File> riskReportFile = operationFactory.calculateRiskReportFileLocation();
        if (riskReportFile.isPresent()) {
            logger.info("Creating risk report pdf");
            File reportDirectory = riskReportFile.get();

            if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create risk report pdf directory: %s", reportDirectory));
            }

            File createdPdf = operationFactory.createRiskReportFile(blackDuckRunData, projectVersion, reportDirectory);

            logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            operationFactory.publishReport(new ReportDetectResult("Risk Report", createdPdf.getCanonicalPath()));
        }
    }

    public void noticesReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws DetectUserFriendlyException, IOException {
        Optional<File> noticesReportDirectory = operationFactory.calculateNoticesDirectory();
        if (noticesReportDirectory.isPresent()) {
            logger.info("Creating notices report");
            File noticesDirectory = noticesReportDirectory.get();

            if (!noticesDirectory.exists() && !noticesDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create notices directory at %s", noticesDirectory));
            }

            File noticesFile = operationFactory.createNoticesReportFile(blackDuckRunData, projectVersion);
            logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));

            operationFactory.publishReport(new ReportDetectResult("Notices Report", noticesFile.getCanonicalPath()));

        }
    }
}
