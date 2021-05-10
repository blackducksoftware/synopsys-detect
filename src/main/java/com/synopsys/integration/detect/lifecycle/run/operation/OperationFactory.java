/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.CodeLocationBatchOutput;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.scan.RapidScanService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ReportService;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadResult;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BinaryScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectCreationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
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
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectFontLoader;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeGenerateJsonOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeLogReportOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidModeScanOperation;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanDetectResult;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultAggregator;
import com.synopsys.integration.detect.workflow.blackduck.developer.aggregate.RapidScanResultSummary;
import com.synopsys.integration.detect.workflow.blackduck.policy.PolicyChecker;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.OperatingSystemType;

public class OperationFactory { //TODO: OperationRunner
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
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final ConnectionFactory connectionFactory;

    private final PropertyConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;
    private final DetectConfigurationFactory detectConfigurationFactory;
    private final EventSystem eventSystem;
    private final FileFinder fileFinder;
    private final DetectInfo detectInfo;
    private final ProductRunData productRunData;
    private final RapidScanResultAggregator rapidScanResultAggregator;
    private final ProjectEventPublisher projectEventPublisher;

    public OperationFactory(DetectDetectableFactory detectDetectableFactory, DetectFontLoaderFactory detectFontLoaderFactory, BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSingletons eventSingletons) {
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
        bdioCodeLocationCreator = utilitySingletons.getBdioCodeLocationCreator();
        connectionFactory = utilitySingletons.getConnectionFactory();

        //My Managed Dependencies
        this.htmlEscapeDisabledGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.codeLocationConverter = new CodeLocationConverter(utilitySingletons.getExternalIdFactory());
        this.extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
        this.rapidScanResultAggregator = new RapidScanResultAggregator();
    }

    public final DockerOperation createDockerOperation() {
        return new DockerOperation(directoryManager, statusEventPublisher, exitCodePublisher, detectDetectableFactory,
            extractionEnvironmentProvider,
            codeLocationConverter, operationSystem);
    }

    public final BazelOperation createBazelOperation() {
        return new BazelOperation(directoryManager, statusEventPublisher, exitCodePublisher, detectDetectableFactory,
            extractionEnvironmentProvider,
            codeLocationConverter, operationSystem);
    }

    public final DetectorOperation createDetectorOperation() {
        return new DetectorOperation(detectConfiguration, detectConfigurationFactory, directoryManager, eventSystem,
            detectDetectableFactory,
            extractionEnvironmentProvider, codeLocationConverter, statusEventPublisher, exitCodePublisher, detectorEventPublisher,
            fileFinder);
    }

    public final void phoneHome(BlackDuckRunData blackDuckRunData) {
        blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);
    }

    //Rapid
    public final List<DeveloperScanComponentResultView> performRapidScan(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException {
        RapidScanService rapidScanService = blackDuckRunData.getBlackDuckServicesFactory().createRapidScanService();
        return new RapidModeScanOperation(rapidScanService, detectConfigurationFactory.findTimeoutInSeconds(), operationSystem).run(bdioResult);
    }

    public final RapidScanResultSummary logRapidReport(List<DeveloperScanComponentResultView> scanResults) throws DetectUserFriendlyException {
        return new RapidModeLogReportOperation(exitCodePublisher, rapidScanResultAggregator).perform(scanResults);
    }

    public final File generateRapidJsonFile(NameVersion projectNameVersion, List<DeveloperScanComponentResultView> scanResults) throws DetectUserFriendlyException {
        return new RapidModeGenerateJsonOperation(htmlEscapeDisabledGson, directoryManager).generateJsonFile(projectNameVersion, scanResults);
    }

    public final void publishRapidResults(File jsonFile, RapidScanResultSummary summary) throws IOException {
        statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summary));
    }
    //End Rapid

    //Post actions
    //End post actions

    public final AggregateDecisionOperation createAggregateOptionsOperation() {
        return new AggregateDecisionOperation(detectConfigurationFactory.createAggregateOptions(), operationSystem);
    }

    public final BdioFileGenerationOperation createBdioFileGenerationOperation() {
        BdioManager bdioManager = new BdioManager(detectInfo, new SimpleBdioFactory(), new ExternalIdFactory(), new Bdio2Factory(), new IntegrationEscapeUtil(), codeLocationNameManager,
            bdioCodeLocationCreator, directoryManager);

        return new BdioFileGenerationOperation(detectConfigurationFactory.createBdioOptions(), bdioManager, codeLocationEventPublisher,
            operationSystem);
    }

    public final BinaryScanOperation createBinaryScanOperation() {
        BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
        BinaryScanOptions binaryScanOptions = detectConfigurationFactory.createBinaryScanOptions();

        return new BinaryScanOperation(blackDuckRunData, binaryScanOptions, statusEventPublisher, exitCodePublisher, directoryManager,
            codeLocationNameManager,
            operationSystem, fileFinder);
    }

    public final BdioUploadResult uploadBdio(BlackDuckRunData blackDuckRunData, BdioResult bdioResult) throws DetectUserFriendlyException, IntegrationException {
        return new BdioUploadOperation(operationSystem, detectConfigurationFactory.createBdioOptions()).execute(blackDuckRunData, bdioResult);
    }

    public final CodeLocationWaitData calulcateCodeLocationWaitData(List<CodeLocationCreationData<? extends CodeLocationBatchOutput<?>>> codeLocationCreationDatas) {
        return new CodeLocationWaitCalculator().calculateWaitData(codeLocationCreationDatas);
    }

    public final void publishCodeLocationNames(Set<String> codeLocationNames) {
        codeLocationEventPublisher.publishCodeLocationsCompleted(codeLocationNames);
    }

    public final String generateImpactAnalysisCodeLocationName(NameVersion projectNameVersion) throws IOException {
        ImpactAnalysisNamingOperation impactAnalysisNamingOperation = new ImpactAnalysisNamingOperation(codeLocationNameManager);
        return impactAnalysisNamingOperation.createCodeLocationName(directoryManager.getSourceDirectory(), projectNameVersion, detectConfigurationFactory.createImpactAnalysisOptions());
    }

    public final Path generateImpactAnalysisFile(String codeLocationName) throws IOException {
        GenerateImpactAnalysisOperation generateImpactAnalysisOperation = new GenerateImpactAnalysisOperation();
        return generateImpactAnalysisOperation.generateImpactAnalysis(directoryManager.getSourceDirectory(), codeLocationName, directoryManager.getImpactAnalysisOutputDirectory().toPath());
    }

    public final CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysisFile(Path impactAnalysisFile, NameVersion projectNameVersion, String codeLocationName, BlackDuckServicesFactory blackDuckServicesFactory)
        throws IntegrationException {
        ImpactAnalysisUploadOperation impactAnalysisUploadOperation = new ImpactAnalysisUploadOperation(ImpactAnalysisUploadService.create(blackDuckServicesFactory));
        return impactAnalysisUploadOperation.uploadImpactAnalysis(impactAnalysisFile, projectNameVersion, codeLocationName);
    }

    public final void mapImpactAnalysisCodeLocations(Path impactAnalysisFile, CodeLocationCreationData<ImpactAnalysisBatchOutput> impactCodeLocationData, ProjectVersionWrapper projectVersionWrapper,
        BlackDuckServicesFactory blackDuckServicesFactory) throws IntegrationException {
        ImpactAnalysisMapCodeLocationsOperation mapCodeLocationsOperation = new ImpactAnalysisMapCodeLocationsOperation(blackDuckServicesFactory.getBlackDuckApiClient());
        mapCodeLocationsOperation.mapCodeLocations(impactAnalysisFile, impactCodeLocationData, projectVersionWrapper);
    }

    public final ProjectVersionWrapper getOrCreateProject(BlackDuckRunData blackDuckRunData, NameVersion projeNameVersion) throws DetectUserFriendlyException, IntegrationException {// TODO: This is too big to be an operation.
        DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
        DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();

        return new ProjectCreationOperation(detectConfigurationFactory.createShouldUnmapCodeLocations(), options, detectCustomFieldService, operationSystem)
                   .execute(blackDuckRunData.getBlackDuckServicesFactory(), projeNameVersion);
    }

    public final ProjectDecisionOperation createProjectDecisionOperation() {
        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        return new ProjectDecisionOperation(projectNameVersionDecider, operationSystem, detectConfigurationFactory.createPreferredProjectTools());
    }

    public void checkPolicy(BlackDuckRunData blackDuckRunData, ProjectVersionView projectVersionView) throws IntegrationException {
        PolicyChecker policyChecker = new PolicyChecker(exitCodePublisher, blackDuckRunData.getBlackDuckServicesFactory().getBlackDuckApiClient(), blackDuckRunData.getBlackDuckServicesFactory().createProjectBomService());
        policyChecker.checkPolicy(detectConfigurationFactory.createBlackDuckPostOptions().getSeveritiesToFailPolicyCheck(), projectVersionView);
    }

    public void publishReport(final ReportDetectResult report) {
        statusEventPublisher.publishDetectResult(report);
    }

    public File createRiskReportFile(BlackDuckRunData blackDuckRunData, ProjectVersionWrapper projectVersionWrapper) throws DetectUserFriendlyException, IntegrationException {
        DetectFontLoader detectFontLoader = detectFontLoaderFactory.detectFontLoader();
        ReportService reportService = blackDuckRunData.getBlackDuckServicesFactory().createReportService(0); //TODO: Get real timeout?
        File createdPdf = reportService.createReportPdfFile(directoryManager.getReportOutputDirectory(), projectVersionWrapper.getProjectView(), projectVersionWrapper.getProjectVersionView(), detectFontLoader::loadFont,
            detectFontLoader::loadBoldFont);
        return createdPdf;
    }

    public File createNoticesReportFile(final BlackDuckRunData blackDuckRunData, final ProjectVersionWrapper projectVersion) throws IntegrationException, InterruptedException {
        ReportService reportService = blackDuckRunData.getBlackDuckServicesFactory().createReportService(0); //TODO: Get real timeout?
        return reportService.createNoticesReportFile(directoryManager.getReportOutputDirectory(), projectVersion.getProjectView(), projectVersion.getProjectVersionView());
    }

    public void publishProjectNameVersionChosen(final NameVersion nameVersion) {
        projectEventPublisher.publishProjectNameVersionChosen(nameVersion);
    }

    public void publishResult(final DetectResult detectResult) {
        statusEventPublisher.publishDetectResult(detectResult);
    }

    public List<SignatureScanPath> createScanPaths(NameVersion projectNameVersion, DockerTargetData dockerTargetData) throws DetectUserFriendlyException, IOException {
        return new CalculateScanPathsOperation().determinePathsAndExclusions(projectNameVersion, detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getMaxDepth(), dockerTargetData);
    }

    public ScanBatch createScanBatchOnline(final List<SignatureScanPath> scanPaths, File installDirectory, NameVersion projectNameVersion, DockerTargetData dockerTargetData, BlackDuckRunData blackDuckRunData)
        throws DetectUserFriendlyException {
        return new CreateScanBatchOperation().createScanBatchWithBlackDuck(projectNameVersion, installDirectory, scanPaths, blackDuckRunData.getBlackDuckServerConfig(), dockerTargetData);
    }

    public ScanBatch createScanBatchOffline(final List<SignatureScanPath> scanPaths, File installDirectory, NameVersion projectNameVersion, DockerTargetData dockerTargetData)
        throws DetectUserFriendlyException {
        return new CreateScanBatchOperation().createScanBatchWithoutBlackDuck(projectNameVersion, installDirectory, scanPaths, dockerTargetData);
    }

    public File calculateDetectControlledInstallDirectory() {
        return directoryManager.getPermanentDirectory();
    }

    public Optional<File> calculateOfflineLocalScannerInstallPath() throws DetectUserFriendlyException {
        return detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getOfflineLocalScannerInstallPath().map(Path::toFile);
    }

    public Optional<String> calculateUserProvidedScannerUrl() throws DetectUserFriendlyException {
        return detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getUserProvidedScannerInstallUrl();
    }

    public ScanBatchRunner createScanBatchRunnerWithBlackDuck(BlackDuckRunData blackDuckRunData) throws DetectUserFriendlyException {
        ExecutorService executorService = Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        return new CreateScanBatchRunnerWithBlackDuck(intEnvironmentVariables, OperatingSystemType.determineFromSystem(), executorService).createScanBatchRunner(blackDuckRunData.getBlackDuckServerConfig());
    }

    public ScanBatchRunner createScanBatchRunnerFromLocalInstall(File installDirectory) throws DetectUserFriendlyException {
        ExecutorService executorService = Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        ScanPathsUtility scanPathsUtility = new ScanPathsUtility(new Slf4jIntLogger(LoggerFactory.getLogger(ScanPathsUtility.class)), intEnvironmentVariables, OperatingSystemType.determineFromSystem());
        ScanCommandRunner scanCommandRunner = new ScanCommandRunner(new Slf4jIntLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), intEnvironmentVariables, scanPathsUtility, executorService);
        return new CreateScanBatchRunnerWithLocalInstall(intEnvironmentVariables, scanPathsUtility, scanCommandRunner).createScanBatchRunner(installDirectory);
    }

    public ScanBatchRunner createScanBatchRunnerWithCustomUrl(String url) throws DetectUserFriendlyException {
        ExecutorService executorService = Executors.newFixedThreadPool(detectConfigurationFactory.createBlackDuckSignatureScannerOptions().getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        ScanPathsUtility scanPathsUtility = new ScanPathsUtility(new Slf4jIntLogger(LoggerFactory.getLogger(ScanPathsUtility.class)), intEnvironmentVariables, OperatingSystemType.determineFromSystem());
        ScanCommandRunner scanCommandRunner = new ScanCommandRunner(new Slf4jIntLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), intEnvironmentVariables, scanPathsUtility, executorService);
        return new CreateScanBatchRunnerWithCustomUrl(intEnvironmentVariables, new SignatureScannerLogger(LoggerFactory.getLogger(ScanCommandRunner.class)), OperatingSystemType.determineFromSystem(), scanPathsUtility, scanCommandRunner)
                   .createScanBatchRunner(url, connectionFactory, detectInfo);
    }

    public NotificationTaskRange createCodeLocationRange(BlackDuckRunData blackDuckRunData) throws IntegrationException {
        return blackDuckRunData.getBlackDuckServicesFactory().createCodeLocationCreationService().calculateCodeLocationRange();
    }

    public SignatureScanOuputResult signatureScan(ScanBatch scanBatch, ScanBatchRunner scanBatchRunner) throws IntegrationException {
        return new SignatureScanOperation().performScanActions(scanBatch, scanBatchRunner);
    }

    public List<SignatureScannerReport> createSignatureScanReport(List<SignatureScanPath> signatureScanPaths, List<ScanCommandOutput> scanCommandOutputList) {
        return new CreateSignatureScanReports().reportResults(signatureScanPaths, scanCommandOutputList);
    }

    public void publishSignatureScanReport(final List<SignatureScannerReport> report) {
        new PublishSignatureScanReports(exitCodePublisher, statusEventPublisher).publishReports(report);
    }
}
