package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.CodeLocationWaitResult;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
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

    public IntelligentModeStepRunner(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public void runOffline(NameVersion projectNameVersion, DetectToolFilter detectToolFilter, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, IntegrationException, IOException {
        runSignatureScannerOffline(detectToolFilter, projectNameVersion, dockerTargetData);
        runImpactAnalysisOffline(detectToolFilter, projectNameVersion);
    }

    //TODO: Change black duck post options to a decision and stick it in Run Data somewhere.
    //TODO: Change detect tool filter to a decision and stick it in Run Data somewhere
    public void runOnline(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, NameVersion projectNameVersion, DetectToolFilter detectToolFilter, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException, IOException, InterruptedException {

        ProjectVersionWrapper projectVersion = getOrCreateProjectOnBlackDuck(blackDuckRunData, projectNameVersion);

        logger.debug("Completed project and version actions.");
        logger.debug("Processing Detect Code Locations.");

        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
        uploadBdio(blackDuckRunData, bdioResult, codeLocationAccumulator);

        logger.debug("Completed Detect Code Location processing.");

        runSignatureScannerOnline(detectToolFilter, blackDuckRunData, codeLocationAccumulator, projectNameVersion, dockerTargetData);
        runBinaryScanner(blackDuckRunData, detectToolFilter, projectNameVersion, codeLocationAccumulator, dockerTargetData);
        runImpactAnalysisOnline(detectToolFilter, projectNameVersion, projectVersion, codeLocationAccumulator, blackDuckRunData.getBlackDuckServicesFactory());

        CodeLocationResults codeLocationResults = calculateCodeLocations(codeLocationAccumulator);
        waitForCodeLocations(codeLocationResults.getCodeLocationWaitData(), 0, projectNameVersion,
            blackDuckRunData);//TODO: Get real timeout. = Long timeoutInSeconds = detectConfigurationFactory.findTimeoutInSeconds(); detectTimeoutInSeconds * 1000

        checkPolicy(projectVersion.getProjectVersionView(), blackDuckRunData);
        riskReport(blackDuckRunData, projectVersion);
        noticesReport(blackDuckRunData, projectVersion);
        publishPostResults(bdioResult, projectVersion, detectToolFilter);
    }

    public void uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, CodeLocationAccumulator codeLocationAccumulator) throws DetectUserFriendlyException, IntegrationException {
        BdioUploadResult uploadResult = operationFactory.uploadBdio(blackDuckRunData, bdioResult);
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

    private void checkPolicy(ProjectVersionView projectVersionView, BlackDuckRunData blackDuckRunData) throws IntegrationException {
        logger.info("Detect will check policy for violations.");
        operationFactory.checkPolicy(blackDuckRunData, projectVersionView);
    }

    public void waitForCodeLocations(CodeLocationWaitData codeLocationWaitData, long timeoutInSeconds, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        logger.info("Detect must wait for bom tool calculations to finish.");
        if (codeLocationWaitData.getExpectedNotificationCount() > 0) {
            //TODO fix this when NotificationTaskRange doesn't include task start time
            //ekerwin - The start time of the task is the earliest time a code location was created.
            // In order to wait the full timeout, we have to not use that start time and instead use now().
            //TODO: Handle the possible null pointer here.
            NotificationTaskRange notificationTaskRange = new NotificationTaskRange(System.currentTimeMillis(), codeLocationWaitData.getNotificationRange().getStartDate(),
                codeLocationWaitData.getNotificationRange().getEndDate());
            CodeLocationCreationService codeLocationCreationService = blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationCreationService(); //TODO: Is this the way? - jp
            CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(
                notificationTaskRange,
                projectNameVersion,
                codeLocationWaitData.getCodeLocationNames(),
                codeLocationWaitData.getExpectedNotificationCount(),
                timeoutInSeconds
            );
            if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                throw new DetectUserFriendlyException(result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."), ExitCodeType.FAILURE_TIMEOUT);
            }
        }
    }

    public void runSignatureScannerOnline(DetectToolFilter detectToolFilter, BlackDuckRunData blackDuckRunData, CodeLocationAccumulator codeLocationAccumulator, NameVersion projectNameVersion, @Nullable DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException, IOException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationFactory);
            SignatureScannerToolResult toolResult = signatureScanStepRunner.runSignatureScannerOnline(blackDuckRunData, projectNameVersion, dockerTargetData);
            toolResult.getCreationData().ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
            logger.info("Signature scanner actions finished.");
        } else {
            logger.info("Signature scan tool will not be run.");
        }
    }

    public void runSignatureScannerOffline(DetectToolFilter detectToolFilter, NameVersion projectNameVersion, @Nullable DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException, IOException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            SignatureScanStepRunner signatureScanStepRunner = new SignatureScanStepRunner(operationFactory);
            signatureScanStepRunner.runSignatureScannerOffline(projectNameVersion, dockerTargetData);
            logger.info("Signature scanner actions finished.");
        } else {
            logger.info("Signature scan tool will not be run.");
        }
    }

    public void runBinaryScanner(BlackDuckRunData blackDuckRunData, DetectToolFilter detectToolFilter, NameVersion projectNameVersion, CodeLocationAccumulator codeLocationAccumulator, @Nullable DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.BINARY_SCAN)) {
            logger.info("Will include the binary scanner tool.");
            if (blackDuckRunData.isOnline()) {
                Optional<CodeLocationCreationData<BinaryScanBatchOutput>> binaryScanResult = operationFactory.createBinaryScanOperation().execute(projectNameVersion, dockerTargetData);
                binaryScanResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
            }
            logger.info("Binary scanner actions finished.");
        } else {
            logger.info("Binary scan tool will not be run.");
        }
    }

    //TODO: Common functionality could be grouped. Balance dependency hell and code duplication.
    public void runImpactAnalysisOnline(DetectToolFilter detectToolFilter, NameVersion projectNameVersion, ProjectVersionWrapper projectVersionWrapper, CodeLocationAccumulator codeLocationAccumulator,
        BlackDuckServicesFactory blackDuckServicesFactory)
        throws DetectUserFriendlyException, IntegrationException, IOException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.IMPACT_ANALYSIS)) {
            logger.info("Will include the Vulnerability Impact Analysis tool.");
            String impactAnalysisName = operationFactory.generateImpactAnalysisCodeLocationName(projectNameVersion);
            Path impactFile = operationFactory.generateImpactAnalysisFile(impactAnalysisName);
            CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadData = operationFactory.uploadImpactAnalysisFile(impactFile, projectNameVersion, impactAnalysisName, blackDuckServicesFactory);
            operationFactory.mapImpactAnalysisCodeLocations(impactFile, uploadData, projectVersionWrapper, blackDuckServicesFactory);
            /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
            codeLocationAccumulator.addNonWaitableCodeLocation(uploadData.getOutput().getSuccessfulCodeLocationNames());
            logger.info("Vulnerability Impact Analysis tool actions finished."); //TODO: not publishing anything
        } else {
            logger.info("Vulnerability Impact Analysis tool will not be run.");
        }
    }

    public void runImpactAnalysisOffline(DetectToolFilter detectToolFilter, NameVersion projectNameVersion)
        throws IOException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.IMPACT_ANALYSIS)) {
            logger.info("Will include the Vulnerability Impact Analysis tool.");
            String impactAnalysisName = operationFactory.generateImpactAnalysisCodeLocationName(projectNameVersion);
            operationFactory.generateImpactAnalysisFile(impactAnalysisName);
            logger.info("Vulnerability Impact Analysis tool actions finished."); //TODO: not publishing anything
        } else {
            logger.info("Vulnerability Impact Analysis tool will not be run.");
        }
    }

    public ProjectVersionWrapper getOrCreateProjectOnBlackDuck(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        logger.debug("Getting or creating project.");
        return operationFactory.getOrCreateProject(blackDuckRunData, projectNameVersion); //TODO: This is not an operation.
    }

    public void riskReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws IOException, DetectUserFriendlyException, IntegrationException {
        Optional<File> riskReportFile = operationFactory.calculateRiskReportFileLocation();
        if (riskReportFile.isPresent()) {
            logger.info("Creating risk report pdf");
            File reportDirectory = riskReportFile.get();

            if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create risk report pdf directory: %s", reportDirectory));
            }

            File createdPdf = operationFactory.createRiskReportFile(blackDuckRunData, projectVersion);

            logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            operationFactory.publishReport(new ReportDetectResult("Risk Report", createdPdf.getCanonicalPath()));
        }
    }

    public void noticesReport(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion) throws IOException, IntegrationException, InterruptedException {
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
