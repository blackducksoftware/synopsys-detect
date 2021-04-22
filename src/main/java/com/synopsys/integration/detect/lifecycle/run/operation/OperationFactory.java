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
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2Factory;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.run.DetectFontLoaderFactory;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BinaryScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationResultCalculationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.FullScanPostProcessingOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ImpactAnalysisOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectCreationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.SignatureScanOperation;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.tool.detector.factory.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NoThreadExecutorService;

public class OperationFactory {
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

    public OperationFactory(DetectDetectableFactory detectDetectableFactory, DetectFontLoaderFactory detectFontLoaderFactory, BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSingletons eventSingletons) {
        this.detectDetectableFactory = detectDetectableFactory;
        this.detectFontLoaderFactory = detectFontLoaderFactory;

        statusEventPublisher = eventSingletons.getStatusEventPublisher();
        exitCodePublisher = eventSingletons.getExitCodePublisher();
        codeLocationEventPublisher = eventSingletons.getCodeLocationEventPublisher();
        detectorEventPublisher = eventSingletons.getDetectorEventPublisher();

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

        this.htmlEscapeDisabledGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.codeLocationConverter = new CodeLocationConverter(utilitySingletons.getExternalIdFactory());
        this.extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
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

    public final RapidScanOperation createRapidScanOperation() {
        return new RapidScanOperation(htmlEscapeDisabledGson, statusEventPublisher, exitCodePublisher, directoryManager,
            operationSystem,
            detectConfigurationFactory.findTimeoutInSeconds());
    }

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

    public final BdioUploadOperation createBdioUploadOperation() {
        return new BdioUploadOperation(operationSystem, detectConfigurationFactory.createBdioOptions());
    }

    public final CodeLocationResultCalculationOperation createCodeLocationResultCalculationOperation() {
        return new CodeLocationResultCalculationOperation(new CodeLocationResultCalculator(), codeLocationEventPublisher, operationSystem);
    }

    public final FullScanPostProcessingOperation createFullScanPostProcessingOperation(DetectToolFilter detectToolFilter) throws DetectUserFriendlyException {
        BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
        Long timeoutInSeconds = detectConfigurationFactory.findTimeoutInSeconds();

        return new FullScanPostProcessingOperation(detectToolFilter, blackDuckPostOptions, statusEventPublisher, exitCodePublisher, operationSystem, timeoutInSeconds, detectFontLoaderFactory.detectFontLoader());
    }

    public final ImpactAnalysisOperation createImpactAnalysisOperation() {
        BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
        ImpactAnalysisOptions impactAnalysisOptions = detectConfigurationFactory.createImpactAnalysisOptions();
        BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool;
        if (productRunData.shouldUseBlackDuckProduct() && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            ImpactAnalysisBatchRunner impactAnalysisBatchRunner = new ImpactAnalysisBatchRunner(blackDuckServicesFactory.getLogger(), blackDuckServicesFactory.getBlackDuckApiClient(), new NoThreadExecutorService(),
                blackDuckServicesFactory.getGson());
            ImpactAnalysisUploadService impactAnalysisUploadService = new ImpactAnalysisUploadService(impactAnalysisBatchRunner, blackDuckServicesFactory.createCodeLocationCreationService());
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool
                                              .ONLINE(directoryManager, codeLocationNameManager, impactAnalysisOptions, blackDuckServicesFactory.getBlackDuckApiClient(), impactAnalysisUploadService,
                                                  blackDuckServicesFactory.createCodeLocationService(), statusEventPublisher, exitCodePublisher, operationSystem);
        } else {
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool
                                              .OFFLINE(directoryManager, codeLocationNameManager, impactAnalysisOptions, statusEventPublisher,
                                                  exitCodePublisher,
                                                  operationSystem);
        }
        return new ImpactAnalysisOperation(blackDuckImpactAnalysisTool);
    }

    public final ProjectCreationOperation createProjectCreationOperation() throws DetectUserFriendlyException {
        DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
        DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();

        return new ProjectCreationOperation(detectConfigurationFactory.createShouldUnmapCodeLocations(), options, detectCustomFieldService, operationSystem);
    }

    public final ProjectDecisionOperation createProjectDecisionOperation() {
        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        return new ProjectDecisionOperation(projectNameVersionDecider, operationSystem, detectConfigurationFactory.createPreferredProjectTools());
    }

    public final SignatureScanOperation createSignatureScanOperation() throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        Path sourcePath = directoryManager.getSourceDirectory().toPath();
        DetectExcludedDirectoryFilter fileFilter = detectConfigurationFactory.createDetectDirectoryFileFilter(sourcePath);
        Predicate<File> collectExcludedDirectoriesPredicate = file -> fileFilter.isExcluded(file);
        BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, collectExcludedDirectoriesPredicate, connectionFactory, directoryManager,
            codeLocationNameManager, detectInfo, fileFinder, operationSystem, exitCodePublisher, statusEventPublisher);

        return new SignatureScanOperation(blackDuckRunData, blackDuckSignatureScannerTool, statusEventPublisher, exitCodePublisher);
    }

}
