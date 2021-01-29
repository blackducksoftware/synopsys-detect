/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.bdio2upload.Bdio2UploadService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunContext;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateOptionsOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BinaryScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationResultOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.FullScanPostProcessingOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ImpactAnalysisOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectCreationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.SignatureScanOperation;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostActions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCodeLocationUnmapService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
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
        return new PolarisOperation(runContext.getProductRunData(), runContext.getDetectConfiguration(), runContext.getDirectoryManager(), runOptions.getDetectToolFilter(), runContext.getEventSystem());
    }

    public final DockerOperation createDockerOperation() {
        return new DockerOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runOptions.getDetectToolFilter(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final BazelOperation createBazelOperation() {
        return new BazelOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runOptions.getDetectToolFilter(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final DetectorOperation createDetectorOperation() {
        return new DetectorOperation(runContext.getDetectConfiguration(), runContext.getDetectConfigurationFactory(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(),
            runOptions.getDetectToolFilter(),
            runContext.getExtractionEnvironmentProvider(), runContext.getCodeLocationConverter());
    }

    public final FullScanOperation createFullScanOperation(boolean hasPriorOperationsSucceeded) {
        ImpactAnalysisOptions impactAnalysisOptions = runContext.getDetectConfigurationFactory().createImpactAnalysisOptions();
        return new FullScanOperation(runContext.getDetectContext(), runContext.getDetectInfo(), runContext.getProductRunData(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectConfigurationFactory(),
            runOptions.getDetectToolFilter(),
            runContext.getCodeLocationNameManager(), runContext.getBdioCodeLocationCreator(), runOptions, hasPriorOperationsSucceeded, impactAnalysisOptions);

    }

    public final RapidScanOperation createRapidScanOperation(boolean hasPriorOperationsSucceeded) {
        return new RapidScanOperation(runContext.getDetectContext(), runContext.getDetectInfo(), runContext.getProductRunData(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectConfigurationFactory(),
            runOptions.getDetectToolFilter(),
            runContext.getCodeLocationNameManager(), runContext.getBdioCodeLocationCreator(), runOptions, hasPriorOperationsSucceeded, runContext.getGson());
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
        BlackDuckBinaryScannerTool binaryScannerTool = null;
        if (null != blackDuckRunData && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            binaryScannerTool = new BlackDuckBinaryScannerTool(runContext.getEventSystem(), runContext.getCodeLocationNameManager(), runContext.getDirectoryManager(), new WildcardFileFinder(), binaryScanOptions,
                blackDuckServicesFactory.createBinaryScanUploadService());
        }

        return new BinaryScanOperation(blackDuckRunData, runOptions.getDetectToolFilter(), binaryScannerTool);
    }

    public final CodeLocationOperation createCodeLocationOperation() {
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        BdioUploadService bdioUploadService = null;
        Bdio2UploadService bdio2UploadService = null;
        DetectBdioUploadService detectBdioUploadService = null;
        if (runContext.getProductRunData().shouldUseBlackDuckProduct() && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            bdioUploadService = blackDuckServicesFactory.createBdioUploadService();
            bdio2UploadService = blackDuckServicesFactory.createBdio2UploadService();
            detectBdioUploadService = new DetectBdioUploadService();
        }
        return new CodeLocationOperation(runContext.getProductRunData(), bdioUploadService, bdio2UploadService, detectBdioUploadService);
    }

    public final CodeLocationResultOperation createCodeLocationResultOperation() {
        return new CodeLocationResultOperation(runContext.getProductRunData(), new CodeLocationResultCalculator(), runContext.getEventSystem());
    }

    public final FullScanPostProcessingOperation createFullScanPostProcessingOperation() {
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        DetectConfigurationFactory detectConfigurationFactory = runContext.getDetectConfigurationFactory();
        BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
        Long timeoutInSeconds = detectConfigurationFactory.findTimeoutInSeconds();

        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();

        BlackDuckPostActions blackDuckPostActions = new BlackDuckPostActions(blackDuckServicesFactory.createCodeLocationCreationService(), runContext.getEventSystem(), blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createReportService(timeoutInSeconds));

        return new FullScanPostProcessingOperation(runContext.getProductRunData(), runOptions.getDetectToolFilter(), blackDuckPostOptions, blackDuckPostActions, runContext.getEventSystem(), timeoutInSeconds);
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
        return new ImpactAnalysisOperation(runOptions.getDetectToolFilter(), blackDuckImpactAnalysisTool);
    }

    public final ProjectCreationOperation createProjectCreationOperation() throws DetectUserFriendlyException {
        DetectProjectServiceOptions options = runContext.getDetectConfigurationFactory().createDetectProjectServiceOptions();
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();
        DetectProjectService detectProjectService = null;
        DetectCodeLocationUnmapService detectCodeLocationUnmapService = null;
        if (runContext.getProductRunData().shouldUseBlackDuckProduct() && blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            detectProjectService = new DetectProjectService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createProjectService(),
                blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createProjectUsersService(), blackDuckServicesFactory.createTagService(), options,
                blackDuckServicesFactory.createProjectMappingService(), detectCustomFieldService);
            detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createCodeLocationService());
        }
        return new ProjectCreationOperation(runContext.getProductRunData(), runOptions, options, detectCustomFieldService, detectProjectService, detectCodeLocationUnmapService);
    }

    public final ProjectDecisionOperation createProjectDecisionOperation() {
        ProjectNameVersionOptions projectNameVersionOptions = runContext.getDetectConfigurationFactory().createProjectNameVersionOptions(runContext.getDirectoryManager().getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        return new ProjectDecisionOperation(runOptions, projectNameVersionDecider);
    }

    public final SignatureScanOperation createSignatureScanOperation() throws DetectUserFriendlyException {
        DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();
        BlackDuckRunData blackDuckRunData = runContext.getProductRunData().getBlackDuckRunData();
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = runContext.getDetectConfigurationFactory().createBlackDuckSignatureScannerOptions();
        BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, runContext.getDetectContext());
        BlackDuckServerConfig blackDuckServerConfig = null;
        CodeLocationCreationService codeLocationCreationService = null;
        if (blackDuckRunData.isOnline()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
            codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
            blackDuckServerConfig = blackDuckRunData.getBlackDuckServerConfig();
        }
        return new SignatureScanOperation(detectToolFilter, blackDuckSignatureScannerTool, runContext.getEventSystem(), codeLocationCreationService, blackDuckServerConfig);
    }

}
