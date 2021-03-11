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

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunContext;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateOptionsOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioUploadOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BinaryScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationResultCalculationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.FullScanPostProcessingOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ImpactAnalysisOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectCreationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.SignatureScanOperation;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NoThreadExecutorService;

public class OperationFactory {
    private final RunContext runContext;
    private final RunOptions runOptions;

    public OperationFactory(RunContext runContext) {
        this.runContext = runContext;
        this.runOptions = runContext.createRunOptions();
    }

    public final PolarisOperation createPolarisOperation() {
        return new PolarisOperation(runContext.getProductRunData(), runContext.getDetectConfiguration(), runContext.getDirectoryManager(), runContext.getEventSystem());
    }

    public final DockerOperation createDockerOperation() {
        return new DockerOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final BazelOperation createBazelOperation() {
        return new BazelOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runOptions.getDetectToolFilter(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final DetectorOperation createDetectorOperation() {
        return new DetectorOperation(runContext.getDetectConfiguration(), runContext.getDetectConfigurationFactory(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(),
            runContext.getExtractionEnvironmentProvider(), runContext.getCodeLocationConverter(), runContext.getFileFinder());
    }

    public final RapidScanOperation createRapidScanOperation() {
        return new RapidScanOperation(runContext.getHtmlEscapeDisabledGson(), runContext.getEventSystem(), runContext.getDirectoryManager(), runContext.getDetectConfigurationFactory().findTimeoutInSeconds());
    }

    public final AggregateOptionsOperation createAggregateOptionsOperation() {
        return new AggregateOptionsOperation(runOptions);
    }

    public final BdioFileGenerationOperation createBdioFileGenerationOperation() {
        BdioManager bdioManager = new BdioManager(runContext.getDetectInfo(), new SimpleBdioFactory(), new ExternalIdFactory(), new Bdio2Factory(), new IntegrationEscapeUtil(), runContext.getCodeLocationNameManager(),
            runContext.getBdioCodeLocationCreator(), runContext.getDirectoryManager());
        return new BdioFileGenerationOperation(runOptions, runContext.getDetectConfigurationFactory().createBdioOptions(), bdioManager, runContext.getEventSystem());
    }

    public final BinaryScanOperation createBinaryScanOperation() {
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        BinaryScanOptions binaryScanOptions = runContext.getDetectConfigurationFactory().createBinaryScanOptions();

        return new BinaryScanOperation(blackDuckRunData, binaryScanOptions, runContext.getEventSystem(), runContext.getDirectoryManager(), runContext.getCodeLocationNameManager(), runContext.getFileFinder());
    }

    public final BdioUploadOperation createBdioUploadOperation() {
        return new BdioUploadOperation();
    }

    public final CodeLocationResultCalculationOperation createCodeLocationResultCalculationOperation() {
        return new CodeLocationResultCalculationOperation(new CodeLocationResultCalculator(), runContext.getEventSystem());
    }

    public final FullScanPostProcessingOperation createFullScanPostProcessingOperation() {
        DetectConfigurationFactory detectConfigurationFactory = runContext.getDetectConfigurationFactory();
        BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
        Long timeoutInSeconds = detectConfigurationFactory.findTimeoutInSeconds();

        return new FullScanPostProcessingOperation(runOptions.getDetectToolFilter(), blackDuckPostOptions, runContext.getEventSystem(), timeoutInSeconds);
    }

    public final ImpactAnalysisOperation createImpactAnalysisOperation() {
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        ImpactAnalysisOptions impactAnalysisOptions = runContext.getDetectConfigurationFactory().createImpactAnalysisOptions();
        BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool;
        if (runContext.getProductRunData().shouldUseBlackDuckProduct() && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            ImpactAnalysisBatchRunner impactAnalysisBatchRunner = new ImpactAnalysisBatchRunner(blackDuckServicesFactory.getLogger(), blackDuckServicesFactory.getBlackDuckApiClient(), new NoThreadExecutorService(),
                blackDuckServicesFactory.getGson());
            ImpactAnalysisUploadService impactAnalysisUploadService = new ImpactAnalysisUploadService(impactAnalysisBatchRunner, blackDuckServicesFactory.createCodeLocationCreationService());
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool
                                              .ONLINE(runContext.getDirectoryManager(), runContext.getCodeLocationNameManager(), impactAnalysisOptions, blackDuckServicesFactory.getBlackDuckApiClient(), impactAnalysisUploadService,
                                                  blackDuckServicesFactory.createCodeLocationService(), runContext.getEventSystem());
        } else {
            blackDuckImpactAnalysisTool = BlackDuckImpactAnalysisTool.OFFLINE(runContext.getDirectoryManager(), runContext.getCodeLocationNameManager(), impactAnalysisOptions, runContext.getEventSystem());
        }
        return new ImpactAnalysisOperation(blackDuckImpactAnalysisTool);
    }

    public final ProjectCreationOperation createProjectCreationOperation() throws DetectUserFriendlyException {
        DetectProjectServiceOptions options = runContext.getDetectConfigurationFactory().createDetectProjectServiceOptions();
        DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();

        return new ProjectCreationOperation(runOptions, options, detectCustomFieldService);
    }

    public final ProjectDecisionOperation createProjectDecisionOperation() {
        ProjectNameVersionOptions projectNameVersionOptions = runContext.getDetectConfigurationFactory().createProjectNameVersionOptions(runContext.getDirectoryManager().getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        return new ProjectDecisionOperation(runOptions, projectNameVersionDecider);
    }

    public final SignatureScanOperation createSignatureScanOperation() throws DetectUserFriendlyException {
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = runContext.getDetectConfigurationFactory().createBlackDuckSignatureScannerOptions();
        Path sourcePath = runContext.getDirectoryManager().getSourceDirectory().toPath();
        DetectExcludedDirectoryFilter fileFilter = runContext.getDetectConfigurationFactory().createDetectDirectoryFileFilter(sourcePath, false);
        Predicate<File> collectExcludedDirectoriesPredicate = file -> fileFilter.isExcluded(file);
        BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, runContext.getDetectContext(), collectExcludedDirectoriesPredicate);

        return new SignatureScanOperation(blackDuckRunData, blackDuckSignatureScannerTool, runContext.getEventSystem());
    }

}
