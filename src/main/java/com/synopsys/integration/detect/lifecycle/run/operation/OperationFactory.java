/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
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
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.blackduck.scan.RapidScanService;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectorToolOptions;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationAuditLog;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationWrapper;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
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
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.impactanalysis.GenerateImpactAnalysisOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisMapCodeLocationsOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisNamingOperation;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisUploadOperation;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScanPath;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerLogger;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CalculateScanPathsOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchRunnerWithBlackDuck;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchRunnerWithCustomUrl;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateScanBatchRunnerWithLocalInstall;
import com.synopsys.integration.detect.tool.signaturescanner.operation.CreateSignatureScanReports;
import com.synopsys.integration.detect.tool.signaturescanner.operation.PublishSignatureScanReports;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOperation;
import com.synopsys.integration.detect.tool.signaturescanner.operation.SignatureScanOuputResult;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateCodeLocation;
import com.synopsys.integration.detect.workflow.bdio.AggregateModeDirectOperation;
import com.synopsys.integration.detect.workflow.bdio.AggregateModeTransitiveOperation;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.bdio.CreateAggregateBdio1FileOperation;
import com.synopsys.integration.detect.workflow.bdio.CreateAggregateBdio2FileOperation;
import com.synopsys.integration.detect.workflow.bdio.CreateAggregateCodeLocationOperation;
import com.synopsys.integration.detect.workflow.bdio.CreateBdio1FilesOperation;
import com.synopsys.integration.detect.workflow.bdio.CreateBdio2FilesOperation;
import com.synopsys.integration.detect.workflow.bdio.DetectBdioWriter;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.DetectFontLoader;
import com.synopsys.integration.detect.workflow.blackduck.bdio.IntelligentPersistentUploadOperation;
import com.synopsys.integration.detect.workflow.blackduck.bdio.LegacyBdio1UploadOperation;
import com.synopsys.integration.detect.workflow.blackduck.bdio.LegacyBdio2UploadOperation;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.AccumulatedCodeLocationData;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeGenerateJsonOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeLogReportOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeScanOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanDetectResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.blackduck.policy.PolicyChecker;
import com.synopsys.integration.detect.workflow.blackduck.project.AddTagsToProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.AddUserGroupsToProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindCloneByLatestOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.FindCloneByNameOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.MapToParentOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.SetApplicationIdOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.SyncProjectOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.UnmapCodeLocationsOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.UpdateCustomFieldsOperation;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.FindCloneOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ParentProjectMapOptions;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.CreateBdioCodeLocationsFromDetectCodeLocationsOperation;
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
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.OperatingSystemType;

public class OperationFactory { //TODO: OperationRunner
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectDetectableFactory detectDetectableFactory;
    private final DetectFontLoaderFactory detectFontLoaderFactory; //TODO: Eh? Only need it if you want to do risk reports.

    private final Gson htmlEscapeDisabledGson;
    private final CodeLocationConverter codeLocationConverter;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;

    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final DetectorEventPublisher detectorEventPublisher;

    private final OperationSystem operationSystem;
    private final CodeLocationNameManager codeLocationNameManager;
    private final ConnectionDetails connectionDetails;

    private final PropertyConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final EventSystem eventSystem;
    private final FileFinder fileFinder;
    private final DetectInfo detectInfo;
    private final ProductRunData productRunData;
    private final RapidScanResultAggregator rapidScanResultAggregator;
    private final ProjectEventPublisher projectEventPublisher;

    private final OperationAuditLog auditLog;

    //Internal: Operation -> Action
    //Leave OperationSystem but it becomes 'user facing groups of actions or steps'
    public OperationFactory(DetectDetectableFactory detectDetectableFactory, DetectFontLoaderFactory detectFontLoaderFactory, BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSingletons eventSingletons,
        ExitCodeManager exitCodeManager) {
        this.detectDetectableFactory = detectDetectableFactory;
        this.detectFontLoaderFactory = detectFontLoaderFactory;

        statusEventPublisher = eventSingletons.getStatusEventPublisher();
        exitCodePublisher = eventSingletons.getExitCodePublisher();
        codeLocationEventPublisher = eventSingletons.getCodeLocationEventPublisher();
        detectorEventPublisher = eventSingletons.getDetectorEventPublisher();
        projectEventPublisher = eventSingletons.getProjectEventPublisher();

        directoryManager = bootSingletons.getDirectoryManager();
        detectConfiguration = bootSingletons.getDetectConfiguration();
        detectConfigurationFactory = bootSingletons.getDetectConfigurationFactory();
        eventSystem = bootSingletons.getEventSystem();
        fileFinder = bootSingletons.getFileFinder();
        detectInfo = bootSingletons.getDetectInfo();
        productRunData = bootSingletons.getProductRunData();

        operationSystem = utilitySingletons.getOperationSystem();
        codeLocationNameManager = utilitySingletons.getCodeLocationNameManager();
        connectionDetails = utilitySingletons.getConnectionDetails();

        //My Managed Dependencies
        this.htmlEscapeDisabledGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.codeLocationConverter = new CodeLocationConverter(utilitySingletons.getExternalIdFactory());
        this.extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
        this.rapidScanResultAggregator = new RapidScanResultAggregator();
        this.auditLog = new OperationAuditLog(utilitySingletons.getOperationWrapper(), operationSystem);
    }

    public final DetectableToolResult executeDocker() throws DetectUserFriendlyException {
        return auditLog.namedPublic("Execute Docker", () -> {
            DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createDockerDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "DOCKER", DetectTool.DOCKER,
                statusEventPublisher, exitCodePublisher, operationSystem);

            return detectableTool.execute(directoryManager.getSourceDirectory());
        });
    }

    public final DetectableToolResult executeBazel() throws DetectUserFriendlyException {
        return auditLog.namedPublic("Execute Bazel", () -> {
            DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createBazelDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "BAZEL", DetectTool.BAZEL,
                statusEventPublisher, exitCodePublisher, operationSystem);
            return detectableTool.execute(directoryManager.getSourceDirectory());
        });
    }

    public final DetectorToolResult executeDetectors() throws DetectUserFriendlyException {
        return auditLog.namedPublic("Execute Detectors", () -> {
            DetectorToolOptions detectorToolOptions = detectConfigurationFactory.createDetectorToolOptions();
            DetectorRuleFactory detectorRuleFactory = new DetectorRuleFactory();
            DetectorRuleSet detectRuleSet = detectorRuleFactory.createRules(detectDetectableFactory, detectorToolOptions.isBuildless());
            File sourcePath = directoryManager.getSourceDirectory();

            DetectorTool detectorTool = new DetectorTool(new DetectorFinder(), extractionEnvironmentProvider, eventSystem, codeLocationConverter, new DetectorIssuePublisher(), statusEventPublisher, exitCodePublisher,
                detectorEventPublisher);
            return detectorTool.performDetectors(directoryManager.getSourceDirectory(), detectRuleSet, detectConfigurationFactory.createDetectorFinderOptions(sourcePath.toPath()),
                detectConfigurationFactory.createDetectorEvaluationOptions(), detectorToolOptions.getProjectBomTool(), detectorToolOptions.getRequiredDetectors(), fileFinder);
        });
    }

    public final void phoneHome(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedPublic("Phone Home", () -> blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome));
    }

    //Rapid
    public final List<DeveloperScanComponentResultView> performRapidScan(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Project Name Version Chosen", () -> {
            RapidScanService rapidScanService = blackDuckRunData.getBlackDuckServicesFactory().createRapidScanService();
            return new RapidModeScanOperation(rapidScanService, detectConfigurationFactory.findTimeoutInSeconds()).run(bdioResult);
        });
    }

    public final RapidScanResultSummary logRapidReport(List<DeveloperScanComponentResultView> scanResults) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Print Rapid Mode Results", () -> new RapidModeLogReportOperation(exitCodePublisher, rapidScanResultAggregator).perform(scanResults));
    }

    public final File generateRapidJsonFile(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> scanResults) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Generate Rapid Json File", () -> new RapidModeGenerateJsonOperation(htmlEscapeDisabledGson, directoryManager).generateJsonFile(projectNameVersion, scanResults));
    }

    public final void publishRapidResults(File jsonFile, RapidScanResultSummary summary) throws DetectUserFriendlyException {
        auditLog.namedInternal("Publish Rapid Results", () -> statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summary)));
    }
    //End Rapid

    //Post actions
    //End post actions

    public final AggregateDecisionOperation createAggregateOptionsOperation() throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Aggregate Options", () -> new AggregateDecisionOperation(detectConfigurationFactory.createAggregateOptions()));
    }

    public final BdioUploadResult uploadBdio1(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Upload Legacy Bdio 1", () -> new LegacyBdio1UploadOperation(blackDuckRunData.getBlackDuckServicesFactory().createBdioUploadService()).uploadBdioFiles(bdioResult));
    }

    public final BdioUploadResult uploadBdio2(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Upload Legacy Bdio 2", () -> new LegacyBdio2UploadOperation(blackDuckRunData.getBlackDuckServicesFactory().createBdio2UploadService()).uploadBdioFiles(bdioResult));
    }

    public final BdioUploadResult uploadBdioIntelligentPersistent(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Upload Intelligent Persistent Bdio", () -> new IntelligentPersistentUploadOperation(blackDuckRunData.getBlackDuckServicesFactory().createIntelligentPersistenceService()).uploadBdioFiles(bdioResult));
    }

    public final CodeLocationWaitData calulcateCodeLocationWaitData(List<AccumulatedCodeLocationData> codeLocationCreationDatas) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Code Location Wait Data", () -> new CodeLocationWaitCalculator().calculateWaitData(codeLocationCreationDatas));
    }

    public final void publishCodeLocationNames(Set<String> codeLocationNames) {
        codeLocationEventPublisher.publishCodeLocationsCompleted(codeLocationNames);//TODO: Currently too broad? Add to audit log.
    }

    public final String generateImpactAnalysisCodeLocationName(NameVersion projectNameVersion) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Impact Analysis Code Location Name", () -> {
            ImpactAnalysisNamingOperation impactAnalysisNamingOperation = new ImpactAnalysisNamingOperation(codeLocationNameManager);
            return impactAnalysisNamingOperation.createCodeLocationName(directoryManager.getSourceDirectory(), projectNameVersion, detectConfigurationFactory.createImpactAnalysisOptions());
        });
    }

    public final Path generateImpactAnalysisFile(String codeLocationName) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Generate Impact Analysis File", () -> {
            GenerateImpactAnalysisOperation generateImpactAnalysisOperation = new GenerateImpactAnalysisOperation();
            return generateImpactAnalysisOperation.generateImpactAnalysis(directoryManager.getSourceDirectory(), codeLocationName, directoryManager.getImpactAnalysisOutputDirectory().toPath());
        });
    }

    public final CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysisFile(Path impactAnalysisFile, NameVersion projectNameVersion, String codeLocationName, BlackDuckServicesFactory blackDuckServicesFactory)
        throws DetectUserFriendlyException {
        return auditLog.namedPublic("Upload Impact Analysis File", () -> {
            ImpactAnalysisUploadOperation impactAnalysisUploadOperation = new ImpactAnalysisUploadOperation(ImpactAnalysisUploadService.create(blackDuckServicesFactory));
            return impactAnalysisUploadOperation.uploadImpactAnalysis(impactAnalysisFile, projectNameVersion, codeLocationName);
        });
    }

    public final void mapImpactAnalysisCodeLocations(Path impactAnalysisFile, CodeLocationCreationData<ImpactAnalysisBatchOutput> impactCodeLocationData, ProjectVersionWrapper projectVersionWrapper,
        BlackDuckServicesFactory blackDuckServicesFactory) throws DetectUserFriendlyException {
        auditLog.namedInternal("Map Impact Analysis Code Locations", () -> {
            ImpactAnalysisMapCodeLocationsOperation mapCodeLocationsOperation = new ImpactAnalysisMapCodeLocationsOperation(blackDuckServicesFactory.getBlackDuckApiClient());
            mapCodeLocationsOperation.mapCodeLocations(impactAnalysisFile, impactCodeLocationData, projectVersionWrapper);
        });
    }

    public final NameVersion createProjectDecisionOperation(List<DetectToolProjectInfo> detectToolProjectInfo) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Decide Project Name Version", () -> {
            ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
            ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
            return projectNameVersionDecider.decideProjectNameVersion(detectConfigurationFactory.createPreferredProjectTools(), detectToolProjectInfo);
        });
    }

    public void checkPolicy(BlackDuckRunData blackDuckRunData, ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        auditLog.namedPublic("Check for Policy", () -> {
            PolicyChecker policyChecker = new PolicyChecker(exitCodePublisher, blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(), blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService());
            policyChecker.checkPolicy(detectConfigurationFactory.createBlackDuckPostOptions().getSeveritiesToFailPolicyCheck(), projectVersionView);
        });
    }

    public void publishReport(ReportDetectResult report) {
        statusEventPublisher.publishDetectResult(report); //TODO Currently too broad.
    }

    public File createRiskReportFile(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersionWrapper, File reportDirectory) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Risk Report File", () -> {
            DetectFontLoader detectFontLoader = detectFontLoaderFactory.detectFontLoader();
            ReportService reportService = creatReportService(blackDuckRunData);
            File createdPdf = reportService.createReportPdfFile(reportDirectory, projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView(), detectFontLoader::loadFont,
                detectFontLoader::loadBoldFont);
            return createdPdf;
        });
    }

    public File createNoticesReportFile(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersion, File noticesDirectory) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Notices Report File", () -> {
            ReportService reportService = creatReportService(blackDuckRunData);
            return reportService.createNoticesReportFile(noticesDirectory, projectVersion.getProjectView(), projectVersion.getProjectVersionView());
        });
    }

    private ReportService creatReportService(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
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
                apiDiscovery, reportServiceLogger, integrationEscapeUtil, reportServiceTimeout);
        });
    }

    public void publishProjectNameVersionChosen(NameVersion nameVersion) throws DetectUserFriendlyException {
        auditLog.namedInternal("Project Name Version Chosen", () -> projectEventPublisher.publishProjectNameVersionChosen(nameVersion));
    }

    public void publishResult(DetectResult detectResult) {
        statusEventPublisher.publishDetectResult(detectResult); //Not in the audit log as it's too broad. Might be good to massage.
    }

    public List<SignatureScanPath> createScanPaths(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Signature Scan Paths",
            () -> {
                DetectExcludedDirectoryFilter detectExcludedDirectoryFilter = detectConfigurationFactory.createDetectDirectoryFileFilter(directoryManager.getSourceDirectory().toPath());
                return new CalculateScanPathsOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, fileFinder,
                    detectExcludedDirectoryFilter::isExcluded)
                           .determinePathsAndExclusions(projectNameVersion, detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getMaxDepth(), dockerTargetData);
            });
    }

    public ScanBatch createScanBatchOnline(List<SignatureScanPath> scanPaths, NameVersion projectNameVersion, DockerTargetData dockerTargetData, BlackDuckRunData blackDuckRunData)
        throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Online Signature Scan Batch",
            () -> new CreateScanBatchOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, codeLocationNameManager)
                      .createScanBatchWithBlackDuck(projectNameVersion, scanPaths, blackDuckRunData.getBlackDuckServerConfig(), dockerTargetData));
    }

    public ScanBatch createScanBatchOffline(List<SignatureScanPath> scanPaths, NameVersion projectNameVersion, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Offline Signature Scan Batch",
            () -> new CreateScanBatchOperation(detectConfigurationFactory.createBlackDuckSignatureScannerOptions(), directoryManager, codeLocationNameManager)
                      .createScanBatchWithoutBlackDuck(projectNameVersion, scanPaths, dockerTargetData));
    }

    public File calculateDetectControlledInstallDirectory() throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Scanner Install Directory", (OperationWrapper.OperationSupplier<File>) directoryManager::getPermanentDirectory);
    }

    public Optional<File> calculateOfflineLocalScannerInstallPath() throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Offline Local Scanner Path", () -> detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getOfflineLocalScannerInstallPath().map(Path::toFile));
    }

    public Optional<File> calculateOnlineLocalScannerInstallPath() throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate Online Local Scanner Path", () -> detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getOnlineLocalScannerInstallPath().map(Path::toFile));
    }

    public Optional<String> calculateUserProvidedScannerUrl() throws DetectUserFriendlyException {
        return auditLog.namedInternal("Calculate User Provided Scanner Url", () -> detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getUserProvidedScannerInstallUrl());
    }

    public ScanBatchRunner createScanBatchRunnerWithBlackDuck(BlackDuckRunData blackDuckRunData, File installDirectory) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Scan Batch Runner with Black Duck", () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
            IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
            return new CreateScanBatchRunnerWithBlackDuck(intEnvironmentVariables, OperatingSystemType.determineFromSystem(), executorService).createScanBatchRunner(blackDuckRunData.getBlackDuckServerConfig(), installDirectory);
        });
    }

    public ScanBatchRunner createScanBatchRunnerFromLocalInstall(File installDirectory) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Scan Batch Runner From Local Install", () -> {
            IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
            ScanPathsUtility scanPathsUtility = new ScanPathsUtility(new Slf4jIntLogger(LoggerFactory.getLogger(ScanPathsUtility.class)), intEnvironmentVariables, OperatingSystemType.determineFromSystem());
            ScanCommandRunner scanCommandRunner = new ScanCommandRunner(new Slf4jIntLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), intEnvironmentVariables, scanPathsUtility, createExecutorServiceForScanner());
            return new CreateScanBatchRunnerWithLocalInstall(intEnvironmentVariables, scanPathsUtility, scanCommandRunner).createScanBatchRunner(installDirectory);
        });
    }

    public ScanBatchRunner createScanBatchRunnerWithCustomUrl(String url, File installDirectory) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Scan Batch Runner with Custom URL", () -> {
            IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
            ScanPathsUtility scanPathsUtility = new ScanPathsUtility(new Slf4jIntLogger(LoggerFactory.getLogger(ScanPathsUtility.class)), intEnvironmentVariables, OperatingSystemType.determineFromSystem());
            ScanCommandRunner scanCommandRunner = new ScanCommandRunner(new Slf4jIntLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), intEnvironmentVariables, scanPathsUtility, createExecutorServiceForScanner());
            return new CreateScanBatchRunnerWithCustomUrl(intEnvironmentVariables, new SignatureScannerLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), OperatingSystemType.determineFromSystem(), scanPathsUtility, scanCommandRunner)
                       .createScanBatchRunner(url, connectionDetails, detectInfo, installDirectory);
        });
    }

    public NotificationTaskRange createCodeLocationRange(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Code Location Task Range", () -> blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationCreationService().calculateCodeLocationRange());
    }

    public SignatureScanOuputResult signatureScan(ScanBatch scanBatch, ScanBatchRunner scanBatchRunner) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Execute Signature Scan CLI", () -> new SignatureScanOperation().performScanActions(scanBatch, scanBatchRunner));
    }

    public List<SignatureScannerReport> createSignatureScanReport(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Signature Scanner Report", () -> new CreateSignatureScanReports().reportResults(signatureScanPaths, scanCommandOutputList));
    }

    public void publishSignatureScanReport(List<SignatureScannerReport> report) throws DetectUserFriendlyException {
        auditLog.namedInternal("Publish Signature Scan Report", () -> {
            new PublishSignatureScanReports(exitCodePublisher, statusEventPublisher).publishReports(report);
        });
    }

    public Optional<File> calculateNoticesDirectory() throws DetectUserFriendlyException { //TODO Should be a decision in boot
        return auditLog.namedInternal("Decide Notices Report Path", () -> {
            BlackDuckPostOptions postOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            if (postOptions.shouldGenerateNoticesReport()) {
                return Optional.of(postOptions.getNoticesReportPath().map(Path::toFile)
                                       .orElse(directoryManager.getSourceDirectory()));
            }
            return Optional.empty();
        });
    }

    public Optional<File> calculateRiskReportFileLocation() throws DetectUserFriendlyException { //TODO Should be a decision in boot
        return auditLog.namedInternal("Decide Risk Report Path", () -> {
            BlackDuckPostOptions postOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            if (postOptions.shouldGenerateRiskReport()) {
                return Optional.of(postOptions.getRiskReportPdfPath().map(Path::toFile)
                                       .orElse(directoryManager.getSourceDirectory()));
            }
            return Optional.empty();
        });
    }

    public void waitForCodeLocations(BlackDuckRunData blackDuckRunData, CodeLocationWaitData codeLocationWaitData, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        auditLog.namedPublic("Wait for Code Locations", () -> {
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
                detectConfigurationFactory.findTimeoutInSeconds()
            );
            if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                throw new DetectUserFriendlyException(result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."), ExitCodeType.FAILURE_TIMEOUT);
            }
        });
    }

    public BdioOptions calculateBdioOptions() {
        return detectConfigurationFactory.createBdioOptions();
    }

    public BdioCodeLocationResult createBdioCodeLocationsFromDetectCodeLocations(List<DetectCodeLocation> detectCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Bdio Code Locations", () -> {
            BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
            return new CreateBdioCodeLocationsFromDetectCodeLocationsOperation(codeLocationNameManager, directoryManager)
                       .transformDetectCodeLocations(detectCodeLocations, bdioOptions.getProjectCodeLocationPrefix(), bdioOptions.getProjectCodeLocationSuffix(), projectNameVersion);
        });
    }

    public List<UploadTarget> createBdio1Files(BdioCodeLocationResult bdioCodeLocationResult, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Bdio 1 Files", () -> {
            DetectBdioWriter detectBdioWriter = new DetectBdioWriter(new SimpleBdioFactory(), detectInfo);
            return new CreateBdio1FilesOperation(detectBdioWriter, new SimpleBdioFactory()).createBdioFiles(bdioCodeLocationResult, directoryManager.getBdioOutputDirectory(), projectNameVersion);
        });
    }

    public List<UploadTarget> createBdio2Files(BdioCodeLocationResult bdioCodeLocationResult, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Create Bdio 2 Files", () -> {
            return new CreateBdio2FilesOperation(new Bdio2Factory(), detectInfo).createBdioFiles(bdioCodeLocationResult, directoryManager.getBdioOutputDirectory(), projectNameVersion);
        });
    }

    public AggregateCodeLocation createAggregateCodeLocation(DependencyGraph aggregateDependencyGraph, NameVersion projectNameVersion, String aggregateName, String extension) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Create Aggregate Code Location", () -> new CreateAggregateCodeLocationOperation(new ExternalIdFactory(), codeLocationNameManager)
                                                                                  .createAggregateCodeLocation(directoryManager.getBdioOutputDirectory(), aggregateDependencyGraph, projectNameVersion, aggregateName, extension));
    }

    public DependencyGraph aggregateDirect(List<DetectCodeLocation> detectCodeLocations) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Direct Aggregate", () -> new AggregateModeDirectOperation(new SimpleBdioFactory()).aggregateCodeLocations(directoryManager.getSourceDirectory(), detectCodeLocations));
    }

    public DependencyGraph aggregateTransitive(List<DetectCodeLocation> detectCodeLocations) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Transitive Aggregate", () -> new AggregateModeTransitiveOperation(new SimpleBdioFactory()).aggregateCodeLocations(directoryManager.getSourceDirectory(), detectCodeLocations));
    }

    public void createAggregateBdio1File(AggregateCodeLocation aggregateCodeLocation) throws DetectUserFriendlyException {
        auditLog.namedPublic("Create Aggregate Bdio 1 File", () -> {
            DetectBdioWriter detectBdioWriter = new DetectBdioWriter(new SimpleBdioFactory(), detectInfo);
            new CreateAggregateBdio1FileOperation(new SimpleBdioFactory(), detectBdioWriter).writeAggregateBdio1File(aggregateCodeLocation);
        });
    }

    public void createAggregateBdio2File(AggregateCodeLocation aggregateCodeLocation) throws DetectUserFriendlyException {
        auditLog.namedInternal("Create Bdio Code Locations", () -> {
            DetectBdioWriter detectBdioWriter = new DetectBdioWriter(new SimpleBdioFactory(), detectInfo);
            new CreateAggregateBdio2FileOperation(new Bdio2Factory()).writeAggregateBdio2File(aggregateCodeLocation);
        });
    }

    private ExecutorService createExecutorServiceForScanner() throws DetectUserFriendlyException {
        return Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
    }

    public BlackDuckPostOptions createBlackDuckPostOptions() {
        return detectConfigurationFactory.createBlackDuckPostOptions();
    }

    public BinaryScanOptions calculateBinaryScanOptions() {
        return detectConfigurationFactory.createBinaryScanOptions();
    }

    public Optional<File> searchForBinaryTargets(final List<String> multipleTargetFileNamePatterns, final int searchDepth) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Binary Search For Targets", () -> {
            return new BinaryScanFindMultipleTargetsOperation(fileFinder, directoryManager).searchForMultipleTargets(multipleTargetFileNamePatterns, searchDepth);
        });
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

    public CodeLocationCreationData<BinaryScanBatchOutput> uploadBinaryScanFile(final File binaryUpload, NameVersion projectNameVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        return auditLog.namedPublic("Binary Upload", () -> {
            return new BinaryUploadOperation(statusEventPublisher, codeLocationNameManager, calculateBinaryScanOptions())
                       .uploadBinaryScanFile(binaryUpload, blackDuckRunData.getBlackDuckServicesFactory().createBinaryScanUploadService(), projectNameVersion);
        });
    }

    public ProjectVersionWrapper syncProjectVersion(NameVersion projectNameVersion, CloneFindResult cloneFindResult, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Sync Project", () -> {
            return new SyncProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectService()).sync(projectNameVersion, cloneFindResult, detectConfigurationFactory.createDetectProjectServiceOptions());
        });
    }

    public ParentProjectMapOptions calculateParentProjectMapOptions() {
        return detectConfigurationFactory.createParentProjectMapOptions();
    }

    public void mapToParentProject(String parentProjectName, String parentProjectVersionName, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Map to Parent Project", () -> {
            new MapToParentOperation(blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(), blackDuckRunData.getBlackDuckServicesFactory().createProjectService(),
                blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService())
                .mapToParentProjectVersion(parentProjectName, parentProjectVersionName, projectVersion);
        });
    }

    public String calculateApplicationId() {
        return detectConfigurationFactory.createApplicationId();
    }

    public void setApplicationId(String applicationId, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Sync Project", () -> {
            new SetApplicationIdOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectMappingService()).setApplicationId(projectVersion.getProjectView(), applicationId);
        });
    }

    public CustomFieldDocument calculateCustomFields() throws DetectUserFriendlyException {
        return detectConfigurationFactory.createCustomFieldDocument();
    }

    public void updateCustomFields(CustomFieldDocument customFieldDocument, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Update Custom Fields", () -> {
            new UpdateCustomFieldsOperation(blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient()).updateCustomFields(projectVersion, customFieldDocument);
        });
    }

    public List<String> calculateUserGroups() {
        return detectConfigurationFactory.createGroups();
    }

    public List<String> calculateTags() {
        return detectConfigurationFactory.createTags();
    }

    public void addUserGroups(List<String> userGroups, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Add User Groups", () -> {
            new AddUserGroupsToProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectUsersService())
                .addUserGroupsToProject(projectVersion, userGroups);
        });
    }

    public void addTags(List<String> tags, ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Add Tags", () -> {
            new AddTagsToProjectOperation(blackDuckRunData.getBlackDuckServicesFactory().createTagService())
                .addTagsToProject(projectVersion, tags);
        });
    }

    public boolean calculateShouldUnmap() {
        return detectConfigurationFactory.createShouldUnmapCodeLocations();
    }

    public void unmapCodeLocations(ProjectVersionWrapper projectVersion, BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        auditLog.namedInternal("Unmap Code Locations", () -> {
            new UnmapCodeLocationsOperation(blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(), blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationService())
                .unmapCodeLocations(projectVersion.getProjectVersionView());
        });
    }

    public FindCloneOptions calculateCloneOptions() {
        return detectConfigurationFactory.createCloneFindOptions();
    }

    public CloneFindResult findLatestProjectVersionCloneUrl(BlackDuckRunData blackDuckRunData, String projectName) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Find Clone Url By Latest", () -> {
            return new FindCloneByLatestOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectService(), blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient())
                       .findLatestProjectVersionCloneUrl(projectName);
        });
    }

    public CloneFindResult findNamedCloneUrl(BlackDuckRunData blackDuckRunData, String projectName, String cloneVersionName) throws DetectUserFriendlyException {
        return auditLog.namedInternal("Find Clone Url By Name", () -> {
            return new FindCloneByNameOperation(blackDuckRunData.getBlackDuckServicesFactory().createProjectService())
                       .findNamedCloneUrl(projectName, cloneVersionName);
        });
    }

    public void publishDetectorFailure() {
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "A detector failed."));
    }
}
