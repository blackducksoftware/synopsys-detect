package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
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
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.operation.input.ImpactAnalysisInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.SignatureScanInput;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class IntelligentModeStepRunner {
    private OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public IntelligentModeStepRunner(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    //public void runAllOffline(BdioResult bdioResult, NameVersion projectNameVersion, DetectToolFilter detectToolFilter, DockerTargetData dockerTargetData) {

    //}

    //TODO: Change black duck post options to a decision and stick it in Run Data somewhere.
    //TODO: Change detect tool filter to a decision and stick it in Run Data somewhere
    public void runAll(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, NameVersion projectNameVersion, BlackDuckPostOptions blackDuckPostOptions, DetectToolFilter detectToolFilter, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException, IOException, InterruptedException {
        Optional<ProjectVersionWrapper> projectVersion = Optional.empty();
        if (blackDuckRunData.isOnline()) {
            operationFactory.phoneHome(blackDuckRunData);
            projectVersion = Optional.ofNullable(getOrCreateProjectOnBlackDuck(blackDuckRunData, projectNameVersion));
        } else {
            logger.debug("Detect is not online, and will not create the project.");
        }

        logger.debug("Completed project and version actions.");
        logger.debug("Processing Detect Code Locations.");

        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
        uploadBdio(blackDuckRunData, bdioResult, codeLocationAccumulator);

        logger.debug("Completed Detect Code Location processing.");

        runSignatureScanner(detectToolFilter, projectNameVersion, codeLocationAccumulator, dockerTargetData); //TODO: There is NO WAY this doesn't need the black duck run data!
        runBinaryScanner(blackDuckRunData, detectToolFilter, projectNameVersion, codeLocationAccumulator, dockerTargetData);

        runImpactAnalysis(detectToolFilter, projectNameVersion, projectVersion, codeLocationAccumulator);

        CodeLocationResults codeLocationResults = calculateCodeLocations(codeLocationAccumulator);
        waitForCodeLocations(codeLocationResults.getCodeLocationWaitData(), 0, projectNameVersion, blackDuckRunData);//TODO: Get real timeout.

        /* This makes me want to throw detect in the trash and start over from scratch in kotlin
        projectVersionViewOptional.ifPresent(projectVersionView -> {
            checkPolicy(projectVersionView);
        });
        */

        if (projectVersion.isPresent()) {
            checkPolicy(projectVersion.get().getProjectVersionView(), blackDuckRunData);
            riskReport(blackDuckRunData, blackDuckPostOptions, projectVersion.get());
            noticesReport(blackDuckRunData, blackDuckPostOptions, projectVersion.get());
        }
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

    public void runSignatureScanner(DetectToolFilter detectToolFilter, NameVersion projectNameVersion, CodeLocationAccumulator codeLocationAccumulator, @Nullable DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            SignatureScanInput signatureScanInput = new SignatureScanInput(projectNameVersion, dockerTargetData);
            Optional<CodeLocationCreationData<ScanBatchOutput>> signatureScanResult = operationFactory.createSignatureScanOperation().execute(signatureScanInput);
            signatureScanResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
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

    public void runImpactAnalysis(DetectToolFilter detectToolFilter, NameVersion projectNameVersion, Optional<ProjectVersionWrapper> projectVersionWrapper, CodeLocationAccumulator codeLocationAccumulator)
        throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.IMPACT_ANALYSIS) && projectVersionWrapper.isPresent()) {
            logger.info("Will include the Vulnerability Impact Analysis tool.");
            ImpactAnalysisInput impactAnalysisInput = new ImpactAnalysisInput(projectNameVersion, projectVersionWrapper.get());
            ImpactAnalysisToolResult impactAnalysisToolResult = operationFactory.createImpactAnalysisOperation().execute(impactAnalysisInput);
            /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
            codeLocationAccumulator.addNonWaitableCodeLocation(impactAnalysisToolResult.getCodeLocationNames());
            logger.info("Vulnerability Impact Analysis tool actions finished.");
        } else {
            logger.info("Vulnerability Impact Analysis tool will not be run.");
        }
    }

    public ProjectVersionWrapper getOrCreateProjectOnBlackDuck(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        logger.debug("Getting or creating project.");
        return operationFactory.getOrCreateProject(blackDuckRunData, projectNameVersion); //TODO: This is not an operation.
    }

    public void riskReport(BlackDuckRunData blackDuckRunData, BlackDuckPostOptions blackDuckPostOptions, ProjectVersionWrapper projectVersion) throws IOException, DetectUserFriendlyException, IntegrationException {
        if (blackDuckPostOptions.shouldGenerateRiskReport()) { //TODO: Should be a decision somewhere.
            logger.info("Creating risk report pdf");
            File reportDirectory = blackDuckPostOptions.getRiskReportPdfPath().toFile();

            if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create risk report pdf directory: %s", blackDuckPostOptions.getRiskReportPdfPath().toString()));
            }

            File createdPdf = operationFactory.createRiskReportFile(blackDuckRunData, projectVersion);

            logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            operationFactory.publishReport(new ReportDetectResult("Risk Report", createdPdf.getCanonicalPath()));
        }
    }

    public void noticesReport(BlackDuckRunData blackDuckRunData, BlackDuckPostOptions blackDuckPostOptions, ProjectVersionWrapper projectVersion) throws IOException, IntegrationException, InterruptedException {
        if (blackDuckPostOptions.shouldGenerateNoticesReport()) { //TODO: Should be a decision somewhere.
            logger.info("Creating notices report");
            File noticesDirectory = blackDuckPostOptions.getNoticesReportPath().toFile();

            if (!noticesDirectory.exists() && !noticesDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create notices directory at %s", blackDuckPostOptions.getNoticesReportPath().toString()));
            }

            File noticesFile = operationFactory.createNoticesReportFile(blackDuckRunData, projectVersion);
            logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));

            operationFactory.publishReport(new ReportDetectResult("Notices Report", noticesFile.getCanonicalPath()));

        }
    }
}
