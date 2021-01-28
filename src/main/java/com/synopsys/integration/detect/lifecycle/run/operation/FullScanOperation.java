package com.synopsys.integration.detect.lifecycle.run.operation;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisToolResult;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostActions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCodeLocationUnmapService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.NoThreadExecutorService;

public class FullScanOperation extends BlackDuckOperation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ImpactAnalysisOptions impactAnalysisOptions;

    public FullScanOperation(DetectContext detectContext, DetectInfo detectInfo,
        ProductRunData productRunData, DirectoryManager directoryManager, EventSystem eventSystem,
        DetectConfigurationFactory detectConfigurationFactory, DetectToolFilter detectToolFilter,
        CodeLocationNameManager codeLocationNameManager, BdioCodeLocationCreator bdioCodeLocationCreator,
        RunOptions runOptions, boolean priorStepsSucceeded, ImpactAnalysisOptions impactAnalysisOptions) {
        super(detectContext, detectInfo, productRunData, directoryManager, eventSystem, detectConfigurationFactory, detectToolFilter, codeLocationNameManager, bdioCodeLocationCreator, runOptions, priorStepsSucceeded);
        this.impactAnalysisOptions = impactAnalysisOptions;
    }

    @Override
    public String getOperationName() {
        return "Black Duck (Full Scan)";
    }

    @Override
    protected OperationResult<RunResult> executeOperation(RunResult input) throws DetectUserFriendlyException, IntegrationException {
        NameVersion projectNameVersion = getProjectInformation(input);
        AggregateOptions aggregateOptions = determineAggregationStrategy(getRunOptions(), !havePriorStepsSucceeded());
        BlackDuckRunData blackDuckRunData = getProductRunData().getBlackDuckRunData();

        blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);

        Optional<ProjectVersionWrapper> projectVersionWrapper = createProject(projectNameVersion);
        BdioResult bdioResult = createBdioFiles(input, aggregateOptions, projectNameVersion);
        CodeLocationAccumulator codeLocationAccumulator = processCodeLocations(bdioResult);
        checkAndExecuteSignatureScan(input, codeLocationAccumulator, projectNameVersion);
        checkAndExecuteBinaryScanner(codeLocationAccumulator, projectNameVersion);
        performImpactAnalysis(codeLocationAccumulator, projectNameVersion, projectVersionWrapper.orElse(null));
        CodeLocationResults codeLocationResults = getCodeLocationResults(codeLocationAccumulator);
        performPostProcessing(bdioResult, codeLocationResults, projectVersionWrapper.orElse(null), projectNameVersion);

        //TODO have input tell me there a prior step failed or the break up of all the
        return OperationResult.success(input);
    }

    private Optional<ProjectVersionWrapper> createProject(NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        ProjectVersionWrapper projectVersionWrapper = null;
        if (blackDuckRunData.isOnline()) {

            logger.debug("Getting or creating project.");
            DetectProjectServiceOptions options = getDetectConfigurationFactory().createDetectProjectServiceOptions();
            DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            DetectProjectService detectProjectService = new DetectProjectService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createProjectService(),
                blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createProjectUsersService(), blackDuckServicesFactory.createTagService(), options,
                blackDuckServicesFactory.createProjectMappingService(), detectCustomFieldService);
            projectVersionWrapper = detectProjectService.createOrUpdateBlackDuckProject(projectNameVersion);

            if (null != projectVersionWrapper && getRunOptions().shouldUnmapCodeLocations()) {
                logger.debug("Unmapping code locations.");
                DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createCodeLocationService());
                detectCodeLocationUnmapService.unmapCodeLocations(projectVersionWrapper.getProjectVersionView());
            } else {
                logger.debug("Will not unmap code locations: Project view was not present, or should not unmap code locations.");
            }
        } else {
            logger.debug("Detect is not online, and will not create the project.");
        }
        return Optional.ofNullable(projectVersionWrapper);
    }

    private CodeLocationAccumulator processCodeLocations(BdioResult bdioResult) throws DetectUserFriendlyException, IntegrationException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator();
        if (!bdioResult.getUploadTargets().isEmpty()) {
            logger.info(String.format("Created %d BDIO files.", bdioResult.getUploadTargets().size()));
            if (blackDuckRunData.isOnline()) {
                logger.debug("Uploading BDIO files.");
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService();
                CodeLocationCreationData<UploadBatchOutput> uploadBatchOutputCodeLocationCreationData = detectBdioUploadService.uploadBdioFiles(bdioResult, blackDuckServicesFactory.createBdioUploadService(),
                    blackDuckServicesFactory.createBdio2UploadService());
                codeLocationAccumulator.addWaitableCodeLocation(uploadBatchOutputCodeLocationCreationData);
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }

        logger.debug("Completed Detect Code Location processing.");
        return codeLocationAccumulator;
    }

    private void checkAndExecuteSignatureScan(RunResult runResult, CodeLocationAccumulator codeLocationAccumulator, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (getDetectToolFilter().shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = getDetectConfigurationFactory().createBlackDuckSignatureScannerOptions();
            BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, getDetectContext());
            BlackDuckServerConfig blackDuckServerConfig = null;
            CodeLocationCreationService codeLocationCreationService = null;
            if (blackDuckRunData.isOnline()) {
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
                blackDuckServerConfig = blackDuckRunData.getBlackDuckServerConfig();
            }
            SignatureScannerToolResult signatureScannerToolResult = blackDuckSignatureScannerTool.runScanTool(codeLocationCreationService, blackDuckServerConfig, projectNameVersion, runResult.getDockerTar());
            if (signatureScannerToolResult.getResult() == Result.SUCCESS && signatureScannerToolResult.getCreationData().isPresent()) {
                codeLocationAccumulator.addWaitableCodeLocation(signatureScannerToolResult.getCreationData().get());
            } else if (signatureScannerToolResult.getResult() != Result.SUCCESS) {
                getEventSystem().publishEvent(Event.StatusSummary, new Status("SIGNATURE_SCAN", StatusType.FAILURE));
                getEventSystem().publishEvent(Event.Issue, new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, Arrays.asList(signatureScannerToolResult.getResult().toString())));
            }
            logger.info("Signature scanner actions finished.");
        } else {
            logger.info("Signature scan tool will not be run.");
        }
    }

    private void checkAndExecuteBinaryScanner(CodeLocationAccumulator codeLocationAccumulator, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (getDetectToolFilter().shouldInclude(DetectTool.BINARY_SCAN)) {
            logger.info("Will include the binary scanner tool.");
            if (blackDuckRunData.isOnline()) {
                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
                BinaryScanOptions binaryScanOptions = getDetectConfigurationFactory().createBinaryScanOptions();
                BlackDuckBinaryScannerTool blackDuckBinaryScanner = new BlackDuckBinaryScannerTool(getEventSystem(), getCodeLocationNameManager(), getDirectoryManager(), new WildcardFileFinder(), binaryScanOptions,
                    blackDuckServicesFactory.createBinaryScanUploadService());
                if (blackDuckBinaryScanner.shouldRun()) {
                    BinaryScanToolResult result = blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion);
                    if (result.isSuccessful()) {
                        codeLocationAccumulator.addWaitableCodeLocation(result.getCodeLocationCreationData());
                    }
                }
            }
            logger.info("Binary scanner actions finished.");
        } else {
            logger.info("Binary scan tool will not be run.");
        }
    }

    private void performImpactAnalysis(CodeLocationAccumulator codeLocationAccumulator, NameVersion projectNameVersion, ProjectVersionWrapper projectVersionWrapper) throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        logger.info(ReportConstants.RUN_SEPARATOR);
        BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool;
        if (blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            ImpactAnalysisBatchRunner impactAnalysisBatchRunner = new ImpactAnalysisBatchRunner(blackDuckServicesFactory.getLogger(), blackDuckServicesFactory.getBlackDuckApiClient(), new NoThreadExecutorService(),
                blackDuckServicesFactory.getGson());
            ImpactAnalysisUploadService impactAnalysisUploadService = new ImpactAnalysisUploadService(impactAnalysisBatchRunner, blackDuckServicesFactory.createCodeLocationCreationService());
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool.ONLINE(getDirectoryManager(), getCodeLocationNameManager(), impactAnalysisOptions, blackDuckServicesFactory.getBlackDuckApiClient(), impactAnalysisUploadService,
                blackDuckServicesFactory.createCodeLocationService(), getEventSystem());
        } else {
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool.OFFLINE(getDirectoryManager(), getCodeLocationNameManager(), impactAnalysisOptions, getEventSystem());
        }
        if (getDetectToolFilter().shouldInclude(DetectTool.IMPACT_ANALYSIS) && blackDuckImpactAnalysisTool.shouldRun()) {
            logger.info("Will include the Vulnerability Impact Analysis tool.");
            ImpactAnalysisToolResult impactAnalysisToolResult = blackDuckImpactAnalysisTool.performImpactAnalysisActions(projectNameVersion, projectVersionWrapper);

            /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
            codeLocationAccumulator.addNonWaitableCodeLocation(impactAnalysisToolResult.getCodeLocationNames());

            if (impactAnalysisToolResult.isSuccessful()) {
                logger.info("Vulnerability Impact Analysis successful.");
            } else {
                logger.warn("Something went wrong with the Vulnerability Impact Analysis tool.");
            }

            logger.info("Vulnerability Impact Analysis tool actions finished.");
        } else if (blackDuckImpactAnalysisTool.shouldRun()) {
            logger.info("Vulnerability Impact Analysis tool is enabled but will not run due to tool configuration.");
        } else {
            logger.info("Vulnerability Impact Analysis tool will not be run.");
        }
    }

    private CodeLocationResults getCodeLocationResults(CodeLocationAccumulator codeLocationAccumulator) {
        logger.info(ReportConstants.RUN_SEPARATOR);
        //We have finished code locations.
        CodeLocationResultCalculator waitCalculator = new CodeLocationResultCalculator();
        CodeLocationResults codeLocationResults = waitCalculator.calculateCodeLocationResults(codeLocationAccumulator);
        getEventSystem().publishEvent(Event.CodeLocationsCompleted, codeLocationResults.getAllCodeLocationNames());
        return codeLocationResults;
    }

    private void performPostProcessing(BdioResult bdioResult, CodeLocationResults codeLocationResults, ProjectVersionWrapper projectVersionWrapper, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = getBlackDuckRunData();
        if (blackDuckRunData.isOnline()) {
            logger.info("Will perform Black Duck post actions.");
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            BlackDuckPostOptions blackDuckPostOptions = getDetectConfigurationFactory().createBlackDuckPostOptions();
            BlackDuckPostActions blackDuckPostActions = new BlackDuckPostActions(blackDuckServicesFactory.createCodeLocationCreationService(), getEventSystem(), blackDuckServicesFactory.getBlackDuckApiClient(),
                blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createReportService(getDetectConfigurationFactory().findTimeoutInSeconds() * 1000));
            blackDuckPostActions.perform(blackDuckPostOptions, codeLocationResults.getCodeLocationWaitData(), projectVersionWrapper, projectNameVersion, getDetectConfigurationFactory().findTimeoutInSeconds());

            if ((!bdioResult.getUploadTargets().isEmpty() || getDetectToolFilter().shouldInclude(DetectTool.SIGNATURE_SCAN))) {
                Optional<String> componentsLink = Optional.ofNullable(projectVersionWrapper)
                                                      .map(ProjectVersionWrapper::getProjectVersionView)
                                                      .flatMap(projectVersionView -> projectVersionView.getFirstLinkSafely(ProjectVersionView.COMPONENTS_LINK))
                                                      .map(HttpUrl::string);

                if (componentsLink.isPresent()) {
                    DetectResult detectResult = new BlackDuckBomDetectResult(componentsLink.get());
                    getEventSystem().publishEvent(Event.ResultProduced, detectResult);
                }
            }
            logger.info("Black Duck actions have finished.");
        } else {
            logger.debug("Will not perform Black Duck post actions: Detect is not online.");
        }
    }
}
