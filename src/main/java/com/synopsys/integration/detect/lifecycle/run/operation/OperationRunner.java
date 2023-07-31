package com.synopsys.integration.detect.lifecycle.run.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detect.workflow.componentlocationanalysis.GenerateComponentLocationAnalysisOperation;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detector.base.DetectorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.blackducksoftware.bdio2.Bdio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.view.BomStatusScanView;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.CodeLocationWaitResult;
import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectorToolOptions;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationAuditLog;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationWrapper;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.DetectableTool;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanFindMultipleTargetsOperation;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryUploadOperation;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.tool.detector.DetectorIssuePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.DetectorTool;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.iac.CalculateIacScanTargetsOperation;
import com.synopsys.integration.detect.tool.iac.IacScanOperation;
import com.synopsys.integration.detect.tool.iac.IacScanReport;
import com.synopsys.integration.detect.tool.iac.IacScannerInstaller;
import com.synopsys.integration.detect.tool.iac.PublishIacScanReportOperation;
import com.synopsys.integration.detect.tool.iac.UploadIacScanResultsOperation;
import com.synopsys.integration.detect.tool.impactanalysis.GenerateImpactAnalysisOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisMapCodeLocationsOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisNamingOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisUploadOperation;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerCodeLocationResult;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CalculateScanPathsOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CalculateWaitableSignatureScanCodeLocations;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchRunnerWithBlackDuck;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchRunnerWithLocalInstall;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateSignatureScanReports;
import com.synopsys.integration.detect.tool.signaturescanner.operation.PublishSignatureScanReports;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateCodeLocation;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.bdio.CreateAggregateBdio2FileOperation;
import com.synopsys.integration.detect.workflow.bdio.CreateAggregateCodeLocationOperation;
import com.synopsys.integration.detect.workflow.bdio.aggregation.FullAggregateGraphCreator;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.BomScanWaitOperation;
import com.synopsys.integration.detect.workflow.blackduck.DetectFontLoader;
import com.synopsys.integration.detect.workflow.blackduck.bdio.IntelligentPersistentUploadOperation;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.WaitableCodeLocationData;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeConfigFindOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeGenerateJsonOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeLogReportOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeUploadOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeWaitOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanDetectResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanOptions;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanService;
import com.synopsys.integration.detect.workflow.blackduck.policy.PolicyChecker;
import com.synopsys.integration.detect.workflow.blackduck.project.AddTagsToProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.AddUserGroupsToProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindCloneByLatestOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindCloneByNameOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindLicenseUrlOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindProjectGroupOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.MapToParentOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.SetApplicationIdOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.SyncProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.UnmapCodeLocationsOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.UpdateCustomFieldsOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.FindCloneOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ParentProjectMapOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectVersionLicenseFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectVersionLicenseOptions;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.detect.workflow.status.FormattedCodeLocation;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detector.accuracy.detectable.DetectableEvaluator;
import com.synopsys.integration.detector.accuracy.directory.DirectoryEvaluator;
import com.synopsys.integration.detector.accuracy.entrypoint.DetectorRuleEvaluator;
import com.synopsys.integration.detector.accuracy.search.SearchEvaluator;
import com.synopsys.integration.detector.accuracy.search.SearchOptions;
import com.synopsys.integration.detector.finder.DirectoryFinder;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.FileBodyContent;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.OperatingSystemType;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2ContentExtractor;

import static com.synopsys.integration.componentlocator.ComponentLocator.SUPPORTED_DETECTORS;
import com.synopsys.integration.componentlocator.beans.Component;
import com.synopsys.integration.detect.workflow.componentlocationanalysis.BdioToComponentListTransformer;
import static com.synopsys.integration.detect.workflow.componentlocationanalysis.GenerateComponentLocationAnalysisOperation.SUPPORTED_DETECTORS_LOG_MSG;
import static com.synopsys.integration.detect.workflow.componentlocationanalysis.GenerateComponentLocationAnalysisOperation.OPERATION_NAME;
import com.synopsys.integration.detect.workflow.componentlocationanalysis.ScanResultToComponentListTransformer;

public class OperationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectDetectableFactory detectDetectableFactory;
    private final DetectFontLoaderFactory detectFontLoaderFactory;

    private final Gson htmlEscapeDisabledGson;
    private final CodeLocationConverter codeLocationConverter;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;

    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final DetectorEventPublisher detectorEventPublisher;

    private final CodeLocationNameManager codeLocationNameManager;

    private final DirectoryManager directoryManager;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final EventSystem eventSystem;
    private final FileFinder fileFinder;
    private final DetectInfo detectInfo;
    private final RapidScanResultAggregator rapidScanResultAggregator;
    private final ProjectEventPublisher projectEventPublisher;
    private final DetectExecutableRunner executableRunner;
    private final OperationAuditLog auditLog;
    private static final int[] LIMITED_FIBONACCI_SEQUENCE = {0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55};
    private static final int MIN_POLLING_INTERVAL_THRESHOLD_IN_SECONDS = 5;

    //Internal: Operation -> Action
    //Leave OperationSystem, but it becomes 'user facing groups of actions or steps'
    public OperationRunner(
        DetectDetectableFactory detectDetectableFactory,
        DetectFontLoaderFactory detectFontLoaderFactory,
        BootSingletons bootSingletons,
        UtilitySingletons utilitySingletons,
        EventSingletons eventSingletons
    ) {
        this.detectDetectableFactory = detectDetectableFactory;
        this.detectFontLoaderFactory = detectFontLoaderFactory;

        statusEventPublisher = eventSingletons.getStatusEventPublisher();
        exitCodePublisher = eventSingletons.getExitCodePublisher();
        codeLocationEventPublisher = eventSingletons.getCodeLocationEventPublisher();
        detectorEventPublisher = eventSingletons.getDetectorEventPublisher();
        projectEventPublisher = eventSingletons.getProjectEventPublisher();

        directoryManager = bootSingletons.getDirectoryManager();
        detectConfigurationFactory = bootSingletons.getDetectConfigurationFactory();
        eventSystem = bootSingletons.getEventSystem();
        fileFinder = bootSingletons.getFileFinder();
        detectInfo = bootSingletons.getDetectInfo();
        executableRunner = utilitySingletons.getExecutableRunner();

        OperationSystem operationSystem = utilitySingletons.getOperationSystem();
        codeLocationNameManager = utilitySingletons.getCodeLocationNameManager();

        //My Managed Dependencies
        this.htmlEscapeDisabledGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.codeLocationConverter = new CodeLocationConverter(utilitySingletons.getExternalIdFactory());
        this.extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
        this.rapidScanResultAggregator = new RapidScanResultAggregator();
        this.auditLog = new OperationAuditLog(utilitySingletons.getOperationWrapper(), operationSystem);
    }

    public final Optional<DetectableTool> checkForDocker() throws OperationException {//TODO: refactor bazel+docker out of detectable
        return auditLog.namedInternal("Check For Docker", () -> {
            DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createDockerDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "DOCKER", DetectTool.DOCKER,
                statusEventPublisher, exitCodePublisher
            );

            if (detectableTool.initializeAndCheckForApplicable(directoryManager.getSourceDirectory())) {
                return Optional.of(detectableTool);
            } else {
                return Optional.empty();
            }
        });
    }

    public final Optional<DetectableTool> checkForBazel() throws OperationException {//TODO: refactor bazel+docker out of detectable
        return auditLog.namedInternal("Check For Bazel", () -> {
            DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createBazelDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "BAZEL", DetectTool.BAZEL,
                statusEventPublisher, exitCodePublisher
            );

            if (detectableTool.initializeAndCheckForApplicable(directoryManager.getSourceDirectory())) {
                return Optional.of(detectableTool);
            } else {
                return Optional.empty();
            }
        });
    }

    public DetectableToolResult executeDocker(DetectableTool detectableTool) throws OperationException {//TODO: refactor bazel+docker out of detectable
        return auditLog.namedPublic("Execute Docker", "Docker", detectableTool::extract);
    }

    public DetectableToolResult executeBazel(DetectableTool detectableTool) throws OperationException {//TODO: refactor bazel+docker out of detectable
        return auditLog.namedPublic("Execute Bazel", "Bazel", detectableTool::extract);
    }

    public final DetectorToolResult executeDetectors() throws OperationException {
        return auditLog.namedPublic("Execute Detectors", "Detectors", () -> {
            DetectorToolOptions detectorToolOptions = detectConfigurationFactory.createDetectorToolOptions();
            SearchOptions searchOptions = detectConfigurationFactory.createDetectorSearchOptions();
            DetectorRuleFactory detectorRuleFactory = new DetectorRuleFactory();
            DetectorRuleSet detectRuleSet = detectorRuleFactory.createRules(detectDetectableFactory);
            DetectorRuleEvaluator detectorRuleEvaluator = new DetectorRuleEvaluator(new SearchEvaluator(searchOptions), new DetectableEvaluator());
            DirectoryEvaluator directoryEvaluator = new DirectoryEvaluator(
                detectorRuleEvaluator,
                extractionEnvironmentProvider::createExtractionEnvironment
            );

            DetectorTool detectorTool = new DetectorTool(
                new DirectoryFinder(),
                codeLocationConverter,
                new DetectorIssuePublisher(),
                statusEventPublisher,
                exitCodePublisher,
                detectorEventPublisher,
                directoryEvaluator
            );
            return detectorTool.performDetectors(
                directoryManager.getSourceDirectory(),
                detectRuleSet,
                detectConfigurationFactory.createDetectorFinderOptions(),
                detectorToolOptions.getProjectBomTool(),
                detectorToolOptions.getRequiredDetectors(),
                detectorToolOptions.getRequiredAccuracyTypes(),
                fileFinder
            );
        });
    }

    public final void phoneHome(BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedPublic("Phone Home", () -> blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome));
    }

    //Rapid
    public UUID initiateStatelessBdbaScan(BlackDuckRunData blackDuckRunData) throws OperationException {
        return auditLog.namedInternal("Initial Stateless BDBA Scan", () -> {
            File bdioHeader = new File(directoryManager.getBdioOutputDirectory() + "/bdio-header.pb");

            if (!bdioHeader.exists()) {
                throw new DetectUserFriendlyException(
                        "Unble to locate BDIO header from BDBA scan.",
                        ExitCodeType.FAILURE_SCAN
                    );
            }

            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();

            HttpUrl postUrl = new HttpUrl(blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().toString() + "/api/developer-scans");

            BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
                    .postFile(bdioHeader, ContentType.create("application/vnd.blackducksoftware.scan-evidence-1+protobuf"))
                    .buildBlackDuckResponseRequest(postUrl);

            HttpUrl responseUrl = blackDuckApiClient.executePostRequestAndRetrieveURL(buildBlackDuckResponseRequest);
            String path = responseUrl.uri().getPath();

            return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
        });
    }

    public void uploadBdioEntries(BlackDuckRunData blackDuckRunData, UUID bdScanId) throws IntegrationException, IOException {
        // parse directory and upload all chunks
        File bdioDirectory = directoryManager.getBdioOutputDirectory();

        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
        BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();

        String contentType = "application/vnd.blackducksoftware.scan-evidence-1+protobuf";
        HttpUrl putUrl = new HttpUrl(blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl().toString()
                + "/api/developer-scans/" + bdScanId);

        int sentChunks = 0;

        for (File bdioEntry : bdioDirectory.listFiles()) {
            if (bdioEntry.getName().equals("bdio-header.pb")) {
                continue;
            }

            // Send the chunks using append mode.

            BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
                    .addHeader("X-BD-MODE", "APPEND")
                    .putBodyContent(new FileBodyContent(bdioEntry, ContentType.create(contentType)))
                    .buildBlackDuckResponseRequest(putUrl);

            try (Response response = blackDuckApiClient.execute(buildBlackDuckResponseRequest)) {
                if (response.isStatusCodeSuccess()) {
                    logger.debug("Uploaded BDIO entry file: " + bdioEntry.getName());
                } else {
                    logger.trace("Unable to upload BDIO entry. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                    throw new IntegrationException("Unable to upload BDIO entry. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                }
            }
            sentChunks++;
        }

        if (sentChunks != 0) {
            // We sent something to BD, send a finish message
            BlackDuckResponseRequest buildBlackDuckResponseRequest = new BlackDuckRequestBuilder()
                    .addHeader("X-BD-MODE", "FINISH")
                    .addHeader("X-BD-DOCUMENT-COUNT", String.valueOf(sentChunks))
                    .addHeader("Content-type", contentType)
                    .putString(StringUtils.EMPTY, ContentType.create(contentType, StandardCharsets.UTF_8))
                    .buildBlackDuckResponseRequest(putUrl);

            try (Response response = blackDuckApiClient.execute(buildBlackDuckResponseRequest)) {
                if (response.isStatusCodeSuccess()) {
                    logger.debug("Sent FINISH chunk to Black Duck.");
                } else {
                    logger.trace("Sent FINISH chunk to Black Duck. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                    throw new IntegrationException("Sent FINISH chunk to Black Duck. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                }
            }
        }
    }

    public final List<HttpUrl> performRapidUpload(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, @Nullable File rapidScanConfig) throws OperationException {
        return auditLog.namedInternal("Rapid Upload", () -> {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            RapidScanOptions rapidScanOptions = detectConfigurationFactory.createRapidScanOptions();
            RapidModeUploadOperation operation = new RapidModeUploadOperation(DetectRapidScanService.fromBlackDuckServicesFactory(directoryManager, blackDuckServicesFactory));
            return operation.run(
                bdioResult,
                rapidScanOptions,
                rapidScanConfig
            );
        });
    }

    public List<DeveloperScansScanView> waitForFullRapidResults(BlackDuckRunData blackDuckRunData, List<HttpUrl> rapidScans, BlackduckScanMode mode) throws OperationException {
        return auditLog.namedInternal("Rapid Wait", () -> {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            int fibonacciSequenceIndex = getFibonacciSequenceIndex();
            return new RapidModeWaitOperation(blackDuckServicesFactory.getBlackDuckApiClient()).waitForFullScans(
                rapidScans,
                detectConfigurationFactory.findTimeoutInSeconds(),
                RapidModeWaitOperation.DEFAULT_WAIT_INTERVAL_IN_SECONDS,
                mode,
                calculateMaxWaitInSeconds(fibonacciSequenceIndex)
            );
        });
    }

    public final RapidScanResultSummary logRapidReport(List<DeveloperScansScanView> scanResults, BlackduckScanMode mode) throws OperationException {
        return auditLog.namedInternal("Print Rapid Mode Results", () -> new RapidModeLogReportOperation(exitCodePublisher, rapidScanResultAggregator, mode).perform(scanResults));
    }

    public final File generateRapidJsonFile(NameVersion projectNameVersion, List<DeveloperScansScanView> scanResults) throws OperationException {
        return auditLog.namedPublic(
            "Generate Rapid Json File",
            "RapidScan",
            () -> new RapidModeGenerateJsonOperation(htmlEscapeDisabledGson, directoryManager).generateJsonFile(projectNameVersion, scanResults)
        );
    }

    public final void publishRapidResults(File jsonFile, RapidScanResultSummary summary, BlackduckScanMode mode) throws OperationException {
        auditLog.namedInternal("Publish Rapid Results", () -> statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summary, mode)));
    }
    //End Rapid

    private void failComponentLocationAnalysisOperationTask(String reason) throws OperationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.info(reason);
        logger.info(ReportConstants.RUN_SEPARATOR);
        auditLog.namedPublic(
                OPERATION_NAME,
                () -> new GenerateComponentLocationAnalysisOperation().failComponentLocationAnalysisOperation()
        );
    }
    
    /**
     * Given a BDIO, creates a JSON file called {@value GenerateComponentLocationAnalysisOperation#DETECT_OUTPUT_FILE_NAME} containing
     * every detected component's {@link ExternalId} along with its declaration location when applicable.
     * @param bdio
     * @throws OperationException
     */
    public void generateComponentLocationAnalysisIfEnabled(BdioResult bdio) throws OperationException {
        if (detectConfigurationFactory.isComponentLocationAnalysisEnabled()) {
            if (bdio.getCodeLocationNamesResult().getCodeLocationNames().isEmpty()) {
                failComponentLocationAnalysisOperationTask("Component Location Analysis requires non-empty BDIO results. Skipping location analysis.");
            } else if (!applicableDetectorsIncludeAtLeastOneSupportedDetector(bdio.getApplicableDetectorTypes())) {
                failComponentLocationAnalysisOperationTask(SUPPORTED_DETECTORS_LOG_MSG);
            } else {
                Set<Component> componentsSet = new BdioToComponentListTransformer().transformBdioToComponentSet(bdio);  
                if (componentsSet.isEmpty()) {
                    failComponentLocationAnalysisOperationTask("Component Location Analysis requires at least one dependency in BDIO results. Skipping location analysis.");
                } else {
                    auditLog.namedPublic(
                            OPERATION_NAME,
                            () -> new GenerateComponentLocationAnalysisOperation().locateComponents(componentsSet, directoryManager.getScanOutputDirectory(), directoryManager.getSourceDirectory())
                    );
                }
            }
        }
    }
    
    /**
     * Given a Rapid/Stateless Detector Scan result, creates a JSON file called {@value GenerateComponentLocationAnalysisOperation#DETECT_OUTPUT_FILE_NAME} containing
     * every reported component's {@link ExternalId} along with its declaration location and upgrade guidance information when applicable.
     * @param rapidFullResults
     * @throws OperationException
     */
    public void generateComponentLocationAnalysisIfEnabled(List<DeveloperScansScanView> rapidFullResults, BdioResult bdio) throws OperationException {
        if (detectConfigurationFactory.isComponentLocationAnalysisEnabled()) {
            if (rapidFullResults.isEmpty()) {
                failComponentLocationAnalysisOperationTask("Component Location Analysis requires non-empty Rapid/Stateless Scan results. Skipping location analysis.");
            } else if (!applicableDetectorsIncludeAtLeastOneSupportedDetector(bdio.getApplicableDetectorTypes())) {
                failComponentLocationAnalysisOperationTask(SUPPORTED_DETECTORS_LOG_MSG);
            } else {
                Set<Component> componentsSet = new ScanResultToComponentListTransformer().transformScanResultToComponentList(rapidFullResults);
                if (componentsSet.isEmpty()) {
                    failComponentLocationAnalysisOperationTask("Component Location Analysis requires at least one dependency in Rapid/Stateless Detector Scan results. Skipping location analysis.");
                } else {
                    auditLog.namedPublic(
                            OPERATION_NAME,
                            () -> (new GenerateComponentLocationAnalysisOperation()).locateComponents(componentsSet, directoryManager.getScanOutputDirectory(), directoryManager.getSourceDirectory())
                    );
                }
            }
        }
    }

    private boolean applicableDetectorsIncludeAtLeastOneSupportedDetector(Set<DetectorType> applicableDetectors) {
        if (SUPPORTED_DETECTORS.isEmpty()) {
            // Idler CLL will always give an empty list
            return true;
        }
        Set<String> applicableDetectorsAsStrings = getApplicableDetectorTypesAsStrings(applicableDetectors);
        applicableDetectorsAsStrings.retainAll(SUPPORTED_DETECTORS);
        return !applicableDetectorsAsStrings.isEmpty();
    }

    private Set<String> getApplicableDetectorTypesAsStrings(Set<DetectorType> applicableDetectors) {
        Set<String> applicableDetectorsAsStrings = new HashSet<>();
        for (DetectorType detectorType : applicableDetectors) {
            applicableDetectorsAsStrings.add(detectorType.toString());
        }
        return applicableDetectorsAsStrings;
    }

    /**
     * Since component location analysis is not supported for online Intelligent scans in 8.11, an appropriate console
     * msg is logged and status=FAILURE is recorded in the status.json file
     * @throws OperationException
     */
    public void attemptToGenerateComponentLocationAnalysisIfEnabled() throws OperationException {
        if (detectConfigurationFactory.isComponentLocationAnalysisEnabled()) {
            auditLog.namedPublic(
                    OPERATION_NAME,
                    () -> (new GenerateComponentLocationAnalysisOperation()).locateComponentsForOnlineIntelligentScan()
            );
        }
    }

    //Post actions
    //End post actions

    public final BdioUploadResult uploadBdioIntelligentPersistent(BlackDuckRunData blackDuckRunData, BdioResult bdioResult, Long timeout) throws OperationException {
        return auditLog.namedPublic(
            "Upload Intelligent Persistent Bdio",
            () -> new IntelligentPersistentUploadOperation(
                blackDuckRunData.getBlackDuckServicesFactory().createIntelligentPersistenceService(),
                timeout
            ).uploadBdioFiles(bdioResult)
        );
    }

    public final CodeLocationWaitData calculateCodeLocationWaitData(List<WaitableCodeLocationData> codeLocationCreationDatas) throws OperationException {
        return auditLog.namedInternal("Calculate Code Location Wait Data", () -> new CodeLocationWaitCalculator().calculateWaitData(codeLocationCreationDatas));
    }

    public final void publishCodeLocationData(Set<FormattedCodeLocation> codeLocationData) throws OperationException {
        auditLog.namedInternal(
            "Publish CodeLocationsCompleted Event",
            () -> codeLocationEventPublisher.publishCodeLocationsCompleted(codeLocationData)
        );
    }

    public final String generateImpactAnalysisCodeLocationName(NameVersion projectNameVersion) throws OperationException {
        return auditLog.namedInternal("Calculate Impact Analysis Code Location Name", () -> {
            ImpactAnalysisNamingOperation impactAnalysisNamingOperation = new ImpactAnalysisNamingOperation(codeLocationNameManager);
            return impactAnalysisNamingOperation.createCodeLocationName(
                directoryManager.getSourceDirectory(),
                projectNameVersion
            );
        });
    }

    public final Path generateImpactAnalysisFile(String codeLocationName) throws OperationException {
        return auditLog.namedPublic("Generate Impact Analysis File", "ImpactAnalysis", () -> {
            GenerateImpactAnalysisOperation generateImpactAnalysisOperation = new GenerateImpactAnalysisOperation();
            return generateImpactAnalysisOperation.generateImpactAnalysis(
                directoryManager.getSourceDirectory(),
                codeLocationName,
                directoryManager.getImpactAnalysisOutputDirectory().toPath()
            );
        });
    }

    public final CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysisFile(
        Path impactAnalysisFile,
        NameVersion projectNameVersion,
        String codeLocationName,
        BlackDuckServicesFactory blackDuckServicesFactory
    )
        throws OperationException {
        return auditLog.namedPublic("Upload Impact Analysis File", () -> {
            ImpactAnalysisUploadOperation impactAnalysisUploadOperation = new ImpactAnalysisUploadOperation(ImpactAnalysisUploadService.create(blackDuckServicesFactory));
            return impactAnalysisUploadOperation.uploadImpactAnalysis(impactAnalysisFile, projectNameVersion, codeLocationName);
        });
    }

    public final void mapImpactAnalysisCodeLocations(
        Path impactAnalysisFile, CodeLocationCreationData<ImpactAnalysisBatchOutput> impactCodeLocationData, ProjectVersionWrapper projectVersionWrapper,
        BlackDuckServicesFactory blackDuckServicesFactory
    ) throws OperationException {
        auditLog.namedInternal("Map Impact Analysis Code Locations", () -> {
            ImpactAnalysisMapCodeLocationsOperation mapCodeLocationsOperation = new ImpactAnalysisMapCodeLocationsOperation(blackDuckServicesFactory.getBlackDuckApiClient());
            mapCodeLocationsOperation.mapCodeLocations(impactAnalysisFile, impactCodeLocationData, projectVersionWrapper);
        });
    }

    public final NameVersion createProjectDecisionOperation(List<DetectToolProjectInfo> detectToolProjectInfo) throws OperationException {
        return auditLog.namedInternal("Decide Project Name Version", () -> {
            ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
            ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
            return projectNameVersionDecider.decideProjectNameVersion(detectConfigurationFactory.createPreferredProjectTools(), detectToolProjectInfo);
        });
    }

    public void checkPolicyBySeverity(BlackDuckRunData blackDuckRunData, ProjectVersionView projectVersionView) throws OperationException {
        auditLog.namedPublic("Check for Policy by Severity", "PolicyCheckSeverity", () -> {
            PolicyChecker policyChecker = new PolicyChecker(
                exitCodePublisher,
                blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(),
                blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService()
            );
            BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            List<PolicyRuleSeverityType> severitiesToFailPolicyCheck = blackDuckPostOptions.getSeveritiesToFailPolicyCheck();
            policyChecker.checkPolicyBySeverity(severitiesToFailPolicyCheck, projectVersionView);
        });
    }

    public void checkPolicyByName(BlackDuckRunData blackDuckRunData, ProjectVersionView projectVersionView) throws OperationException {
        auditLog.namedPublic("Check for Policy by Name", "PolicyCheckName", () -> {
            PolicyChecker policyChecker = new PolicyChecker(
                exitCodePublisher,
                blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(),
                blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService()
            );
            BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            List<String> policyNamesToFailPolicyCheck = blackDuckPostOptions.getPolicyNamesToFailPolicyCheck();
            policyChecker.checkPolicyByName(policyNamesToFailPolicyCheck, projectVersionView);
        });
    }

    public void publishReport(ReportDetectResult report) throws OperationException {
        auditLog.namedInternal(
            "Publish DetectResult Event",
            () -> statusEventPublisher.publishDetectResult(report)
        );
    }

    public File createRiskReportFile(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersionWrapper, File reportDirectory) throws OperationException {
        return auditLog.namedPublic("Create Risk Report File", "RiskReport", () -> {
            DetectFontLoader detectFontLoader = detectFontLoaderFactory.detectFontLoader();
            ReportService reportService = creatReportService(blackDuckRunData);
            return reportService.createReportPdfFile(
                reportDirectory,
                projectVersionWrapper.getProjectView(),
                projectVersionWrapper.getProjectVersionView(),
                detectFontLoader::loadFont,
                detectFontLoader::loadBoldFont
            );
        });
    }

    public File createNoticesReportFile(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion, File noticesDirectory) throws OperationException {
        return auditLog.namedPublic("Create Notices Report File", "NoticesReport", () -> {
            ReportService reportService = creatReportService(blackDuckRunData);
            return reportService.createNoticesReportFile(noticesDirectory, projectVersion.getProjectView(), projectVersion.getProjectVersionView());
        });
    }

    private ReportService creatReportService(BlackDuckRunData blackDuckRunData) throws OperationException {
        return auditLog.namedInternal("Create Report Service", () -> {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            Gson gson = blackDuckServicesFactory.getGson();
            HttpUrl blackDuckUrl = blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl();
            BlackDuckApiClient blackDuckApiClient = blackDuckServicesFactory.getBlackDuckApiClient();
            ApiDiscovery apiDiscovery = blackDuckServicesFactory.getApiDiscovery();
            IntLogger reportServiceLogger = blackDuckServicesFactory.getLogger();
            IntegrationEscapeUtil integrationEscapeUtil = blackDuckServicesFactory.createIntegrationEscapeUtil();
            long reportServiceTimeout = detectConfigurationFactory.findTimeoutInSeconds() * 1000;
            return new ReportService(gson, blackDuckUrl, blackDuckApiClient,
                apiDiscovery, reportServiceLogger, integrationEscapeUtil, reportServiceTimeout
            );
        });
    }

    public void publishProjectNameVersionChosen(NameVersion nameVersion) throws OperationException {
        auditLog.namedInternal("Project Name Version Chosen", () -> projectEventPublisher.publishProjectNameVersionChosen(nameVersion));
    }

    public void publishResult(DetectResult detectResult) {
        statusEventPublisher.publishDetectResult(detectResult); //Not in the audit log as it's too broad. Might be good to massage.
    }

    public List<SignatureScanPath> createScanPaths(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws OperationException {
        return auditLog.namedInternal(
            "Calculate Signature Scan Paths",
            () -> {
                List<String> exclusions = detectConfigurationFactory.collectSignatureScannerDirectoryExclusions();
                DetectExcludedDirectoryFilter detectExcludedDirectoryFilter = new DetectExcludedDirectoryFilter(exclusions);
                return new CalculateScanPathsOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, fileFinder,
                    detectExcludedDirectoryFilter::isExcluded
                )
                    .determinePathsAndExclusions(projectNameVersion, detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getMaxDepth(), dockerTargetData);
            }
        );
    }

    public ScanBatch createScanBatchOnline(List<SignatureScanPath> scanPaths, NameVersion projectNameVersion, DockerTargetData dockerTargetData, BlackDuckRunData blackDuckRunData)
        throws OperationException {
        return auditLog.namedPublic("Create Online Signature Scan Batch", "OnlineSigScan",
            () -> new CreateScanBatchOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, codeLocationNameManager)
                .createScanBatchWithBlackDuck(projectNameVersion, scanPaths, blackDuckRunData.getBlackDuckServerConfig(), dockerTargetData)
        );
    }

    public ScanBatch createScanBatchOffline(List<SignatureScanPath> scanPaths, NameVersion projectNameVersion, DockerTargetData dockerTargetData)
        throws OperationException {
        return auditLog.namedPublic("Create Offline Signature Scan Batch", "OfflineSigScan",
            () -> new CreateScanBatchOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, codeLocationNameManager)
                .createScanBatchWithoutBlackDuck(projectNameVersion, scanPaths, dockerTargetData)
        );
    }

    public File calculateDetectControlledInstallDirectory() throws OperationException {
        return auditLog.namedInternal("Calculate Scanner Install Directory", (OperationWrapper.OperationSupplier<File>) directoryManager::getPermanentDirectory);
    }

    public Optional<File> calculateOnlineLocalScannerInstallPath() throws OperationException {
        return auditLog.namedInternal(
            "Calculate Online Local Scanner Path",
            () -> detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getLocalScannerInstallPath().map(Path::toFile)
        );
    }

    public Long calculateDetectTimeout() {
        return detectConfigurationFactory.findTimeoutInSeconds();
    }

    public ScanBatchRunner createScanBatchRunnerWithBlackDuck(BlackDuckRunData blackDuckRunData, File installDirectory) throws OperationException {
        return auditLog.namedInternal("Create Scan Batch Runner with Black Duck", () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
            IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
            return new CreateScanBatchRunnerWithBlackDuck(intEnvironmentVariables, OperatingSystemType.determineFromSystem(), executorService).createScanBatchRunner(
                blackDuckRunData.getBlackDuckServerConfig(),
                installDirectory
            );
        });
    }

    public ScanBatchRunner createScanBatchRunnerFromLocalInstall(File installDirectory) throws OperationException {
        return auditLog.namedInternal("Create Scan Batch Runner From Local Install", () -> {
            IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
            ScanPathsUtility scanPathsUtility = new ScanPathsUtility(
                new Slf4jIntLogger(LoggerFactory.getLogger(ScanPathsUtility.class)),
                intEnvironmentVariables,
                OperatingSystemType.determineFromSystem()
            );
            ScanCommandRunner scanCommandRunner = new ScanCommandRunner(
                new Slf4jIntLogger(LoggerFactory.getLogger(ScanCommandRunner.class)),
                intEnvironmentVariables,
                scanPathsUtility,
                createExecutorServiceForScanner()
            );
            return new CreateScanBatchRunnerWithLocalInstall(intEnvironmentVariables, scanPathsUtility, scanCommandRunner).createScanBatchRunner(installDirectory);
        });
    }

    public NotificationTaskRange createCodeLocationRange(BlackDuckRunData blackDuckRunData) throws OperationException {
        return auditLog.namedInternal(
            "Create Code Location Task Range",
            () -> blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationCreationService().calculateCodeLocationRange()
        );
    }

    public SignatureScanOuputResult signatureScan(ScanBatch scanBatch, ScanBatchRunner scanBatchRunner) throws OperationException {
        return auditLog.namedPublic("Execute Signature Scan CLI", "SigScan", () -> new SignatureScanOperation().performScanActions(scanBatch, scanBatchRunner));
    }

    public List<SignatureScannerReport> createSignatureScanReport(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList)
        throws OperationException {
        return auditLog.namedInternal("Create Signature Scanner Report", () -> new CreateSignatureScanReports().createReports(signatureScanPaths, scanCommandOutputList));
    }

    public void publishSignatureScanReport(List<SignatureScannerReport> report) throws OperationException {
        auditLog.namedInternal("Publish Signature Scan Report", () -> {
            Boolean treatSkippedAsFailure = detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getTreatSkippedScansAsSuccess();
            new PublishSignatureScanReports(exitCodePublisher, statusEventPublisher, treatSkippedAsFailure).publishReports(report);
        });
    }

    public SignatureScannerCodeLocationResult calculateWaitableSignatureScannerCodeLocations(
        NotificationTaskRange notificationTaskRange,
        List<SignatureScannerReport> reports
    ) throws OperationException {
        return auditLog.namedInternal(
            "Calculate Signature Scanner Waitable Code Locations",
            () -> new CalculateWaitableSignatureScanCodeLocations()
                .calculateWaitableCodeLocations(notificationTaskRange, reports)
        );
    }

    public List<File> calculateIacScanScanTargets() throws OperationException {
        return auditLog.namedInternal(
            "Calculate IacScan Scan Targets",
            () -> new CalculateIacScanTargetsOperation(detectConfigurationFactory.createIacScanOptions(), directoryManager)
                .calculateIacScanTargets()
        );
    }

    public Optional<File> calculateUserProvidedIacScanPath() throws OperationException {
        return auditLog.namedInternal(
            "Calculate Local IacScan Path",
            () -> detectConfigurationFactory.createIacScanOptions().getLocalIacScannerPath().map(Path::toFile)
        );
    }

    public File resolveIacScanOnline(BlackDuckRunData blackDuckRunData) throws OperationException {
        return auditLog.namedInternal("Resolve IacScan Online", () -> new IacScannerInstaller(
            blackDuckRunData.getBlackDuckServerConfig().createBlackDuckHttpClient(new Slf4jIntLogger(logger)),
            detectInfo,
            blackDuckRunData.getBlackDuckServerConfig().getBlackDuckUrl(),
            directoryManager
        ).installOrUpdateScanner());
    }

    public String createIacScanCodeLocationName(File scanTarget, NameVersion projectNameVersion) {
        return codeLocationNameManager.createIacScanCodeLocationName(
            scanTarget,
            projectNameVersion.getName(),
            projectNameVersion.getVersion(),
            detectConfigurationFactory.createIacScanOptions().getCodeLocationPrefix().orElse(null),
            detectConfigurationFactory.createIacScanOptions().getCodeLocationSuffix().orElse(null)
        );
    }

    public File performIacScanScan(File scanTarget, File iacScanExe, int count) throws OperationException {
        return auditLog.namedInternal("Perform IacScan Scan", "IacScan",
            () -> new IacScanOperation(directoryManager, executableRunner).performIacScan(
                scanTarget,
                iacScanExe,
                detectConfigurationFactory.createIacScanOptions().getAdditionalArguments().orElse(null),
                count
            )
        );
    }

    public void uploadIacScanResults(BlackDuckRunData blackDuckRunData, File iacScanResultsFile, String scanId) throws OperationException {
        auditLog.namedInternal(
            "Upload IacScan Results",
            () -> new UploadIacScanResultsOperation(blackDuckRunData.getBlackDuckServicesFactory().createIacScanUploadService())
                .uploadResults(iacScanResultsFile, scanId)
        );
    }

    public void publishIacScanReport(List<IacScanReport> iacScanReports) throws OperationException {
        auditLog.namedInternal(
            "Publish IacScan Report",
            () -> new PublishIacScanReportOperation(exitCodePublisher, statusEventPublisher).publishReports(iacScanReports)
        );
    }

    public Optional<File> calculateNoticesDirectory() throws OperationException { //TODO Should be a decision in boot
        return auditLog.namedInternal("Decide Notices Report Path", () -> {
            BlackDuckPostOptions postOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            if (postOptions.shouldGenerateNoticesReport()) {
                return Optional.of(postOptions.getNoticesReportPath().map(Path::toFile)
                    .orElse(directoryManager.getSourceDirectory()));
            }
            return Optional.empty();
        });
    }

    public Optional<File> calculateRiskReportFileLocation() throws OperationException { //TODO Should be a decision in boot
        return auditLog.namedInternal("Decide Risk Report Path", () -> {
            BlackDuckPostOptions postOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            if (postOptions.shouldGenerateRiskReport()) {
                return Optional.of(postOptions.getRiskReportPdfPath().map(Path::toFile)
                    .orElse(directoryManager.getSourceDirectory()));
            }
            return Optional.empty();
        });
    }

    public void waitForCodeLocations(BlackDuckRunData blackDuckRunData, CodeLocationWaitData codeLocationWaitData, NameVersion projectNameVersion)
        throws OperationException {
        auditLog.namedPublic("Wait for Code Locations", () -> {
            //TODO fix this when NotificationTaskRange doesn't include task start time
            // ekerwin - The start time of the task is the earliest time a code location was created.
            // In order to wait the full timeout, we have to not use that start time and instead use now().
            NotificationTaskRange notificationTaskRange = Optional.ofNullable(codeLocationWaitData.getNotificationRange())
                .map(notificationRange -> new NotificationTaskRange(
                    System.currentTimeMillis(),
                    codeLocationWaitData.getNotificationRange().getStartDate(),
                    codeLocationWaitData.getNotificationRange().getEndDate()
                ))
                .orElseThrow(() -> new DetectUserFriendlyException("Date range for notification range wasn't set.", ExitCodeType.FAILURE_UNKNOWN_ERROR));

            CodeLocationCreationService codeLocationCreationService = blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationCreationService();
            CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(
                notificationTaskRange,
                projectNameVersion,
                codeLocationWaitData.getCodeLocationNames(),
                codeLocationWaitData.getExpectedNotificationCount(),
                detectConfigurationFactory.findTimeoutInSeconds()
            );
            if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                throw new DetectUserFriendlyException(
                    result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."),
                    ExitCodeType.FAILURE_TIMEOUT
                );
            }
        });
    }

    public AggregateCodeLocation createAggregateCodeLocation(ProjectDependencyGraph aggregateDependencyGraph, NameVersion projectNameVersion, GitInfo gitInfo)
        throws OperationException {
        return auditLog.namedInternal("Create Aggregate Code Location", () -> new CreateAggregateCodeLocationOperation(codeLocationNameManager)
            .createAggregateCodeLocation(
                directoryManager.getBdioOutputDirectory(),
                aggregateDependencyGraph,
                projectNameVersion,
                gitInfo,
                detectConfigurationFactory.createBdioOptions().getBdioFileName().orElse(null)
            ));
    }

    public ProjectDependencyGraph aggregateSubProject(NameVersion projectNameVersion, List<DetectCodeLocation> detectCodeLocations) throws OperationException {
        return auditLog.namedPublic("SubProject Aggregate", "SubProjectAggregate",
            () -> (new FullAggregateGraphCreator()).aggregateCodeLocations(
                directoryManager.getSourceDirectory(),
                projectNameVersion,
                detectCodeLocations
            )
        );
    }

    public void createAggregateBdio2File(AggregateCodeLocation aggregateCodeLocation, Bdio.ScanType scanType) throws OperationException {
        auditLog.namedInternal(
            "Create Bdio Code Locations",
            () -> new CreateAggregateBdio2FileOperation(new Bdio2Factory(), detectInfo).writeAggregateBdio2File(aggregateCodeLocation, scanType)
        );
    }

    private ExecutorService createExecutorServiceForScanner() {
        return Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
    }

    public BlackDuckPostOptions createBlackDuckPostOptions() {
        return detectConfigurationFactory.createBlackDuckPostOptions();
    }

    public BinaryScanOptions calculateBinaryScanOptions() {
        return detectConfigurationFactory.createBinaryScanOptions();
    }

    public Optional<File> searchForBinaryTargets(Predicate<File> fileFilter, int searchDepth, boolean followSymLinks) throws OperationException {
        return auditLog.namedInternal(
            "Binary Search For Targets",
            () -> new BinaryScanFindMultipleTargetsOperation(fileFinder, directoryManager)
                .searchForMultipleTargets(fileFilter, followSymLinks, searchDepth)
        );
    }

    public void publishBinaryFailure(String message) {
        logger.error("Binary scan failure: {}", message);
        statusEventPublisher.publishStatusSummary(Status.forTool(DetectTool.BINARY_SCAN, StatusType.FAILURE));
        exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, "BINARY_SCAN");
    }

    public void publishImpactFailure(Exception e) {
        logger.error("Impact analysis failure: {}", e.getMessage());
        statusEventPublisher.publishStatusSummary(Status.forTool(DetectTool.IMPACT_ANALYSIS, StatusType.FAILURE));
        exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, "IMPACT_ANALYSIS");
    }

    public void publishImpactSuccess() {
        statusEventPublisher.publishStatusSummary(Status.forTool(DetectTool.IMPACT_ANALYSIS, StatusType.SUCCESS));
    }

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(File binaryUpload, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData)
        throws OperationException {
        return auditLog.namedPublic("Binary Upload", "Binary",
            () -> new BinaryUploadOperation(statusEventPublisher, codeLocationNameManager, calculateBinaryScanOptions())
                .uploadBinaryScanFile(binaryUpload, blackDuckRunData.getBlackDuckServicesFactory().createBinaryScanUploadService(), projectNameVersion)
        );
    }

    public ProjectVersionWrapper syncProjectVersion(
        NameVersion projectNameVersion,
        ProjectGroupFindResult projectGroupFindResult,
        CloneFindResult cloneFindResult,
        ProjectVersionLicenseFindResult projectVersionLicensesFindResult,
        BlackDuckRunData blackDuckRunData
    ) throws OperationException {
        return auditLog.namedInternal(
            "Sync Project",
            () -> new SyncProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectService())
                .sync(
                    projectNameVersion,
                    projectGroupFindResult,
                    cloneFindResult,
                    projectVersionLicensesFindResult,
                    detectConfigurationFactory.createDetectProjectServiceOptions()
                )
        );
    }

    public ParentProjectMapOptions calculateParentProjectMapOptions() {
        return detectConfigurationFactory.createParentProjectMapOptions();
    }

    public void mapToParentProject(
        String parentProjectName,
        String parentProjectVersionName,
        ProjectVersionWrapper projectVersion,
        BlackDuckRunData blackDuckRunData
    ) throws OperationException {
        auditLog.namedInternal("Map to Parent Project", () -> new MapToParentOperation(
            blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(),
            blackDuckRunData.getBlackDuckServicesFactory().createProjectService(),
            blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService()
        ).mapToParentProjectVersion(parentProjectName, parentProjectVersionName, projectVersion));
    }

    public String calculateApplicationId() {
        return detectConfigurationFactory.createApplicationId();
    }

    public void setApplicationId(String applicationId, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedInternal(
            "Sync Project",
            () -> new SetApplicationIdOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectMappingService())
                .setApplicationId(projectVersion.getProjectView(), applicationId)
        );
    }

    public CustomFieldDocument calculateCustomFields() throws DetectUserFriendlyException {
        return detectConfigurationFactory.createCustomFieldDocument();
    }

    public void updateCustomFields(CustomFieldDocument customFieldDocument, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedInternal(
            "Update Custom Fields",
            () -> new UpdateCustomFieldsOperation(blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient())
                .updateCustomFields(projectVersion, customFieldDocument)
        );
    }

    public List<String> calculateUserGroups() {
        return detectConfigurationFactory.createGroups();
    }

    public List<String> calculateTags() {
        return detectConfigurationFactory.createTags();
    }

    public void addUserGroups(List<String> userGroups, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedInternal(
            "Add User Groups",
            () -> new AddUserGroupsToProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectUsersService())
                .addUserGroupsToProject(projectVersion, userGroups)
        );
    }

    public void addTags(List<String> tags, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedInternal(
            "Add Tags",
            () -> new AddTagsToProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createTagService())
                .addTagsToProject(projectVersion, tags)
        );
    }

    public boolean calculateShouldUnmap() {
        return detectConfigurationFactory.createShouldUnmapCodeLocations();
    }

    public void unmapCodeLocations(ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws OperationException {
        auditLog.namedInternal("Unmap Code Locations", () -> new UnmapCodeLocationsOperation(
            blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(),
            blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationService()
        ).unmapCodeLocations(projectVersion.getProjectVersionView()));
    }

    public FindCloneOptions calculateCloneOptions() {
        return detectConfigurationFactory.createCloneFindOptions();
    }

    public ProjectGroupOptions calculateProjectGroupOptions() {
        return detectConfigurationFactory.createProjectGroupOptions();
    }

    public ProjectVersionLicenseOptions calculateProjectVersionLicenses() {
        return detectConfigurationFactory.createProjectVersionLicenseOptions();
    }

    public CloneFindResult findLatestProjectVersionCloneUrl(BlackDuckRunData blackDuckRunData, String projectName) throws OperationException {
        return auditLog.namedInternal("Find Clone Url By Latest", () -> new FindCloneByLatestOperation(
            blackDuckRunData.getBlackDuckServicesFactory().createProjectService(),
            blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient()
        ).findLatestProjectVersionCloneUrl(projectName));
    }

    public CloneFindResult findNamedCloneUrl(BlackDuckRunData blackDuckRunData, String projectName, String cloneVersionName) throws OperationException {
        return auditLog.namedInternal(
            "Find Clone Url By Name",
            () -> new FindCloneByNameOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectService())
                .findNamedCloneUrl(projectName, cloneVersionName)
        );
    }

    public HttpUrl findProjectGroup(BlackDuckRunData blackDuckRunData, String projectGroupName) throws OperationException {
        return auditLog.namedInternal(
            "Find Project Group By Exact Name",
            () -> new FindProjectGroupOperation(
                blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(),
                blackDuckRunData.getBlackDuckServicesFactory().getApiDiscovery()
            ).findProjectGroup(projectGroupName)
        );
    }

    public String findLicenseUrl(BlackDuckRunData blackDuckRunData, String licenseName) throws OperationException {
        return auditLog.namedInternal(
            "Find License Urls By Name",
            "LicenseUrlLookup",
            () -> new FindLicenseUrlOperation(blackDuckRunData.getBlackDuckServicesFactory().createLicenseService())
                .findLicenseUrl(licenseName)
        );
    }

    public void publishDetectorFailure() {
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "A detector failed."));
    }

    public Optional<File> findRapidScanConfig() throws OperationException {
        return auditLog.namedInternal(
            "Find Rapid Scan Config",
            () -> new RapidModeConfigFindOperation(fileFinder)
                .findRapidScanConfig(directoryManager.getSourceDirectory())
        );
    }

    private File findScanCliOutputLogFile() {
        File blackDuckScanOutputDirectory = this.fileFinder.findFile(directoryManager.getScanOutputDirectory(), "BlackDuckScanOutput");
        if (blackDuckScanOutputDirectory != null) {
            File scanIdDirectory = this.fileFinder.findFile(blackDuckScanOutputDirectory, "*");
            if (scanIdDirectory != null) {
                File scanLogDirectory = this.fileFinder.findFile(scanIdDirectory, "log");
                if (scanLogDirectory != null) {
                    return this.fileFinder.findFile(scanLogDirectory, "*.log");
                }
            }
        }
        return null;
    }

    private int countSignatureScannerBdioChunks() {
        File scanCliOutputLogFile = findScanCliOutputLogFile();
        if (scanCliOutputLogFile == null) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(scanCliOutputLogFile))) {
            Pattern pattern = Pattern.compile("scanNodeList\\.size\\(\\)=(\\d+).*scanLeafList\\.size\\(\\)=(\\d+)");
            long scanNodeCount = 0;
            long scanLeafCount = 0;

            String line = reader.readLine();
            while (line != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 2) {
                    scanNodeCount = Integer.parseInt(matcher.group(1));
                    scanLeafCount = Integer.parseInt(matcher.group(2));
                }
                line = reader.readLine();
            }
            long sumOfScanNodesAndLeaves = scanNodeCount + scanLeafCount;
            int bdioChunksCount = (int) Math.ceil(sumOfScanNodesAndLeaves / 30000D);
            return bdioChunksCount;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }

    private int countDetectBdioEntryFiles() {
        try {
            File bdioFile = this.fileFinder.findFile(directoryManager.getBdioOutputDirectory(), "*.bdio");
            if (bdioFile == null) {
                return 0;
            }
            Bdio2ContentExtractor bdio2Extractor = new Bdio2ContentExtractor();
            return bdio2Extractor.extractContent(bdioFile).size() - 1;
        } catch (IntegrationException e) {
            return 0;
        }
    }

    public int calculateMaxWaitInSeconds(int fibonacciSequenceIndex) {
        int fibonacciSequenceLastIndex = LIMITED_FIBONACCI_SEQUENCE.length - 1;
        if (fibonacciSequenceIndex > fibonacciSequenceLastIndex) {
            return LIMITED_FIBONACCI_SEQUENCE[fibonacciSequenceLastIndex];
        } else if (fibonacciSequenceIndex > 4) {
            return LIMITED_FIBONACCI_SEQUENCE[fibonacciSequenceIndex];
        }
        return MIN_POLLING_INTERVAL_THRESHOLD_IN_SECONDS;
    }

    public int getFibonacciSequenceIndex() {
        int bdioChunksCount = countSignatureScannerBdioChunks();
        return bdioChunksCount != 0 ? bdioChunksCount : countDetectBdioEntryFiles();
    }

    public BomStatusScanView waitForBomCompletion(BlackDuckRunData blackDuckRunData, HttpUrl scanUrl) throws OperationException {
        return auditLog.namedInternal("Wait for scan to potentially be included in BOM", () -> {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            int fibonacciSequenceIndex = getFibonacciSequenceIndex();
            return new BomScanWaitOperation(blackDuckServicesFactory.getBlackDuckApiClient()).waitForScan(
                scanUrl,
                detectConfigurationFactory.findTimeoutInSeconds(),
                calculateMaxWaitInSeconds(fibonacciSequenceIndex)
            );
        });
    }
    
    public UUID getScanIdFromScanUrl(HttpUrl blackDuckScanUrl) {
        String url = blackDuckScanUrl.toString();
        UUID scanId = UUID.fromString(url.substring(url.lastIndexOf("/") + 1));
        
        return scanId;
    }
}
