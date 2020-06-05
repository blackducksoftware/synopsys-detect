/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.run;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ProjectMappingService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.DetectableTool;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanToolResult;
import com.synopsys.integration.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.DetectorTool;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.impl.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.tool.polaris.PolarisTool;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostActions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.DetectBdioUploadService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCodeLocationUnmapService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.WrongOperatingSystemResult;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public RunManager(final DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public RunResult run(final ProductRunData productRunData) throws DetectUserFriendlyException, IntegrationException {
        //TODO: Better way for run manager to get dependencies so he can be tested. (And better ways of creating his objects)
        final Gson gson = detectContext.getBean(Gson.class);
        final PropertyConfiguration detectConfiguration = detectContext.getBean(PropertyConfiguration.class);
        final DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        final DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);
        final EventSystem eventSystem = detectContext.getBean(EventSystem.class);
        final CodeLocationNameManager codeLocationNameManager = detectContext.getBean(CodeLocationNameManager.class);
        final BdioCodeLocationCreator bdioCodeLocationCreator = detectContext.getBean(BdioCodeLocationCreator.class);
        final DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);
        final DetectDetectableFactory detectDetectableFactory = detectContext.getBean(DetectDetectableFactory.class);

        final RunResult runResult = new RunResult();
        final RunOptions runOptions = detectConfigurationFactory.createRunOptions();
        final DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();

        logger.info(ReportConstants.RUN_SEPARATOR);

        if (productRunData.shouldUsePolarisProduct()) {
            runPolarisProduct(productRunData, detectConfiguration, directoryManager, eventSystem, detectToolFilter);
        } else {
            logger.info("Polaris tools will not be run.");
        }

        final UniversalToolsResult universalToolsResult = runUniversalProjectTools(detectConfiguration, detectConfigurationFactory, directoryManager, eventSystem, detectDetectableFactory, runResult, runOptions, detectToolFilter, codeLocationNameManager);

        if (productRunData.shouldUseBlackDuckProduct()) {
            final AggregateOptions aggregateOptions = determineAggregationStrategy(runOptions.getAggregateName().orElse(null), runOptions.getAggregateMode(), universalToolsResult);
            runBlackDuckProduct(productRunData, detectConfigurationFactory, directoryManager, eventSystem, codeLocationNameManager, bdioCodeLocationCreator, detectInfo, runResult, runOptions, detectToolFilter,
                universalToolsResult.getNameVersion(), aggregateOptions);
        } else {
            logger.info("Black Duck tools will not be run.");
        }

        logger.info("All tools have finished.");
        logger.info(ReportConstants.RUN_SEPARATOR);

        return runResult;
    }

    private AggregateOptions determineAggregationStrategy(@Nullable final String aggregateName, final AggregateMode aggregateMode, final UniversalToolsResult universalToolsResult) {
        if (StringUtils.isNotBlank(aggregateName)) {
            if (universalToolsResult.anyFailed()) {
                return AggregateOptions.aggregateButSkipEmpty(aggregateName, aggregateMode);
            } else {
                return AggregateOptions.aggregateAndAlwaysUpload(aggregateName, aggregateMode);
            }
        } else {
            return AggregateOptions.doNotAggregate();
        }
    }

    private UniversalToolsResult runUniversalProjectTools(final PropertyConfiguration detectConfiguration, final DetectConfigurationFactory detectConfigurationFactory,
        final DirectoryManager directoryManager, final EventSystem eventSystem, final DetectDetectableFactory detectDetectableFactory,
        final RunResult runResult, final RunOptions runOptions, final DetectToolFilter detectToolFilter, final CodeLocationNameManager codeLocationNameManager) throws DetectUserFriendlyException {
        final ExtractionEnvironmentProvider extractionEnvironmentProvider = new ExtractionEnvironmentProvider(directoryManager);
        final CodeLocationConverter codeLocationConverter = new CodeLocationConverter(new ExternalIdFactory());

        boolean anythingFailed = false;

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DOCKER)) {
            logger.info("Will include the Docker tool.");
            final DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createDockerDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "DOCKER", DetectTool.DOCKER,
                eventSystem);
            final DetectableToolResult detectableToolResult = detectableTool.execute(directoryManager.getSourceDirectory());
            if (detectableToolResult.getFailedExtractableResult().isPresent()) {
                //TODO: Remove hack when windows docker support added. This workaround allows docker to throw a user friendly exception when not-extractable due to operating system.
                final DetectableResult extractable = detectableToolResult.getFailedExtractableResult().get();
                if (WrongOperatingSystemResult.class.isAssignableFrom(extractable.getClass())) {
                    throw new DetectUserFriendlyException("Docker currently requires a non-Windows OS to run. Attempting to run Docker on Windows is not currently supported.", ExitCodeType.FAILURE_CONFIGURATION);
                }
            }
            runResult.addDetectableToolResult(detectableToolResult);
            eventSystem.publishEvent(Event.CodeLocationNamesCalculated, createCodeLocationNames(detectableToolResult, codeLocationNameManager, directoryManager));
            anythingFailed = anythingFailed || detectableToolResult.isFailure();
            logger.info("Docker actions finished.");
        } else {
            logger.info("Docker tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.BAZEL)) {
            logger.info("Will include the Bazel tool.");
            final DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createBazelDetectable,
                extractionEnvironmentProvider, codeLocationConverter, "BAZEL", DetectTool.BAZEL,
                eventSystem);
            final DetectableToolResult detectableToolResult = detectableTool.execute(directoryManager.getSourceDirectory());
            runResult.addDetectableToolResult(detectableToolResult);
            eventSystem.publishEvent(Event.CodeLocationNamesCalculated, createCodeLocationNames(detectableToolResult, codeLocationNameManager, directoryManager));
            anythingFailed = anythingFailed || detectableToolResult.isFailure();
            logger.info("Bazel actions finished.");
        } else {
            logger.info("Bazel tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DETECTOR)) {
            logger.info("Will include the detector tool.");
            final String projectBomTool = detectConfiguration.getValueOrEmpty(DetectProperties.Companion.getDETECT_PROJECT_DETECTOR()).orElse(null);
            final List<DetectorType> requiredDetectors = detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_REQUIRED_DETECTOR_TYPES());
            final boolean buildless = detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_BUILDLESS());

            final DetectorRuleFactory detectorRuleFactory = new DetectorRuleFactory();
            final DetectorRuleSet detectRuleSet = detectorRuleFactory.createRules(detectDetectableFactory, buildless);

            final Path sourcePath = directoryManager.getSourceDirectory().toPath();
            final DetectorFinderOptions finderOptions = detectConfigurationFactory.createSearchOptions(sourcePath);
            final DetectorEvaluationOptions detectorEvaluationOptions = detectConfigurationFactory.createDetectorEvaluationOptions();

            final DetectorTool detectorTool = new DetectorTool(new DetectorFinder(), extractionEnvironmentProvider, eventSystem, codeLocationConverter);
            final DetectorToolResult detectorToolResult = detectorTool.performDetectors(directoryManager.getSourceDirectory(), detectRuleSet, finderOptions, detectorEvaluationOptions, projectBomTool, requiredDetectors);

            detectorToolResult.getBomToolProjectNameVersion().ifPresent(it -> runResult.addToolNameVersion(DetectTool.DETECTOR, new NameVersion(it.getName(), it.getVersion())));
            runResult.addDetectCodeLocations(detectorToolResult.getBomToolCodeLocations());

            if (!detectorToolResult.getFailedDetectorTypes().isEmpty()) {
                eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "A detector failed."));
                anythingFailed = true;
            }
            logger.info("Detector actions finished.");
        } else {
            logger.info("Detector tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.debug("Completed code location tools.");

        logger.debug("Determining project info.");

        final ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        final ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        final NameVersion projectNameVersion = projectNameVersionDecider.decideProjectNameVersion(runOptions.getPreferredTools(), runResult.getDetectToolProjectInfo());

        logger.info("Project name: " + projectNameVersion.getName());
        logger.info("Project version: " + projectNameVersion.getVersion());

        eventSystem.publishEvent(Event.ProjectNameVersionChosen, projectNameVersion);

        if (anythingFailed) {
            return UniversalToolsResult.failure(projectNameVersion);
        } else {
            return UniversalToolsResult.success(projectNameVersion);
        }
    }

    private void runPolarisProduct(final ProductRunData productRunData, final PropertyConfiguration detectConfiguration, final DirectoryManager directoryManager, final EventSystem eventSystem,
        final DetectToolFilter detectToolFilter) {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.POLARIS)) {
            logger.info("Will include the Polaris tool.");
            final PolarisServerConfig polarisServerConfig = productRunData.getPolarisRunData().getPolarisServerConfig();
            final ExecutableRunner polarisExecutableRunner = DetectExecutableRunner.newInfo(eventSystem);
            final PolarisTool polarisTool = new PolarisTool(eventSystem, directoryManager, polarisExecutableRunner, detectConfiguration, polarisServerConfig);
            polarisTool.runPolaris(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
            logger.info("Polaris actions finished.");
        } else {
            logger.info("Polaris CLI tool will not be run.");
        }
    }

    private void runBlackDuckProduct(final ProductRunData productRunData, final DetectConfigurationFactory detectConfigurationFactory, final DirectoryManager directoryManager, final EventSystem eventSystem,
        final CodeLocationNameManager codeLocationNameManager, final BdioCodeLocationCreator bdioCodeLocationCreator, final DetectInfo detectInfo, final RunResult runResult, final RunOptions runOptions,
        final DetectToolFilter detectToolFilter, final NameVersion projectNameVersion, final AggregateOptions aggregateOptions) throws IntegrationException, DetectUserFriendlyException {

        logger.debug("Black Duck tools will run.");

        final BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();

        blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);

        ProjectVersionWrapper projectVersionWrapper = null;

        final BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory().orElse(null);

        if (blackDuckRunData.isOnline() && blackDuckServicesFactory != null) {
            logger.debug("Getting or creating project.");
            final DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
            final ProjectMappingService detectProjectMappingService = blackDuckServicesFactory.createProjectMappingService();
            final DetectCustomFieldService detectCustomFieldService = new DetectCustomFieldService();
            final DetectProjectService detectProjectService = new DetectProjectService(blackDuckServicesFactory, options, detectProjectMappingService, detectCustomFieldService);
            projectVersionWrapper = detectProjectService.createOrUpdateBlackDuckProject(projectNameVersion);

            if (null != projectVersionWrapper && runOptions.shouldUnmapCodeLocations()) {
                logger.debug("Unmapping code locations.");
                final DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(blackDuckServicesFactory.createBlackDuckService(), blackDuckServicesFactory.createCodeLocationService());
                detectCodeLocationUnmapService.unmapCodeLocations(projectVersionWrapper.getProjectVersionView());
            } else {
                logger.debug("Will not unmap code locations: Project view was not present, or should not unmap code locations.");
            }
        } else {
            logger.debug("Detect is not online, and will not create the project.");
        }

        logger.debug("Completed project and version actions.");

        logger.debug("Processing Detect Code Locations.");
        final BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
        final BdioManager bdioManager = new BdioManager(detectInfo, new SimpleBdioFactory(), new Bdio2Factory(), new IntegrationEscapeUtil(), codeLocationNameManager, bdioCodeLocationCreator, directoryManager, eventSystem);
        final BdioResult bdioResult = bdioManager.createBdioFiles(bdioOptions, aggregateOptions, projectNameVersion, runResult.getDetectCodeLocations(), runOptions.shouldUseBdio2());

        final CodeLocationWaitData codeLocationWaitData = new CodeLocationWaitData();
        if (bdioResult.getUploadTargets().size() > 0) {
            logger.info("Created " + bdioResult.getUploadTargets().size() + " BDIO files.");
            if (null != blackDuckServicesFactory) {
                final DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService();

                logger.debug("Uploading BDIO files.");
                final CodeLocationCreationData<UploadBatchOutput> uploadBatchOutputCodeLocationCreationData;

                final DetectBdioUploadService.BdioUploader bdioUploader;
                if (bdioResult.isBdio2()) {
                    bdioUploader = blackDuckServicesFactory.createBdio2UploadService()::uploadBdio;
                } else {
                    bdioUploader = blackDuckServicesFactory.createBdioUploadService()::uploadBdio;
                }

                final String blackDuckUrl = blackDuckRunData.getBlackDuckServerConfig()
                                                .map(BlackDuckServerConfig::getBlackDuckUrl)
                                                .map(URL::toExternalForm)
                                                .orElse("Unknown Host");
                uploadBatchOutputCodeLocationCreationData = detectBdioUploadService.uploadBdioFiles(blackDuckUrl, bdioResult.getUploadTargets(), bdioUploader);
                codeLocationWaitData.addWaitForCreationData(uploadBatchOutputCodeLocationCreationData, eventSystem);
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }

        logger.debug("Completed Detect Code Location processing.");

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            final BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
            final BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, detectContext);
            final SignatureScannerToolResult signatureScannerToolResult = blackDuckSignatureScannerTool.runScanTool(blackDuckRunData, projectNameVersion, runResult.getDockerTar());
            if (signatureScannerToolResult.getResult() == Result.SUCCESS && signatureScannerToolResult.getCreationData().isPresent()) {
                codeLocationWaitData.addWaitForCreationData(signatureScannerToolResult.getCreationData().get(), eventSystem);
            } else if (signatureScannerToolResult.getResult() != Result.SUCCESS) {
                eventSystem.publishEvent(Event.StatusSummary, new Status("SIGNATURE_SCAN", StatusType.FAILURE));
            }
            logger.info("Signature scanner actions finished.");
        } else {
            logger.info("Signature scan tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.BINARY_SCAN)) {
            logger.info("Will include the binary scanner tool.");
            if (null != blackDuckServicesFactory) {
                final BinaryScanOptions binaryScanOptions = detectConfigurationFactory.createBinaryScanOptions();
                final BlackDuckBinaryScannerTool blackDuckBinaryScanner = new BlackDuckBinaryScannerTool(eventSystem, codeLocationNameManager, directoryManager, new SimpleFileFinder(), binaryScanOptions, blackDuckServicesFactory);
                if (blackDuckBinaryScanner.shouldRun()) {
                    final BinaryScanToolResult result = blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion);
                    if (result.isSuccessful()) {
                        codeLocationWaitData.addWaitForCreationData(result.getCodeLocationCreationData(), eventSystem);
                    }
                }
            }
            logger.info("Binary scanner actions finished.");
        } else {
            logger.info("Binary scan tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (null != blackDuckServicesFactory) {
            logger.info("Will perform Black Duck post actions.");
            final BlackDuckPostOptions blackDuckPostOptions = detectConfigurationFactory.createBlackDuckPostOptions();
            final BlackDuckPostActions blackDuckPostActions = new BlackDuckPostActions(blackDuckServicesFactory, eventSystem);
            blackDuckPostActions.perform(blackDuckPostOptions, codeLocationWaitData, projectVersionWrapper, detectConfigurationFactory.findTimeoutInSeconds());

            if ((bdioResult.getUploadTargets().size() > 0 || detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN))) {
                final Optional<String> componentsLink = Optional.ofNullable(projectVersionWrapper)
                                                            .map(ProjectVersionWrapper::getProjectVersionView)
                                                            .flatMap(projectVersionView -> projectVersionView.getFirstLink(ProjectVersionView.COMPONENTS_LINK));

                if (componentsLink.isPresent()) {
                    final DetectResult detectResult = new BlackDuckBomDetectResult(componentsLink.get());
                    eventSystem.publishEvent(Event.ResultProduced, detectResult);
                }
            }
            logger.info("Black Duck actions have finished.");
        } else {
            logger.debug("Will not perform Black Duck post actions: Detect is not online.");
        }
    }

    private Set<String> createCodeLocationNames(DetectableToolResult detectableToolResult, CodeLocationNameManager codeLocationNameManager, DirectoryManager directoryManager) {
        if (detectableToolResult.getDetectToolProjectInfo().isPresent()) {
            NameVersion projectNameVersion = detectableToolResult.getDetectToolProjectInfo().get().getSuggestedNameVersion();
            return detectableToolResult.getDetectCodeLocations().stream()
                       .map(detectCodeLocation -> codeLocationNameManager.createCodeLocationName(detectCodeLocation, directoryManager.getSourceDirectory(), projectNameVersion.getName(), projectNameVersion.getVersion(), null, null))
                       .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

}
