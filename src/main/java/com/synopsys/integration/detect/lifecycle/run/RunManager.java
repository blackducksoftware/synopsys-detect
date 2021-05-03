///*
// * synopsys-detect
// *
// * Copyright (c) 2021 Synopsys, Inc.
// *
// * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
// */
//package com.synopsys.integration.detect.lifecycle.run;
//
//import java.util.Optional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
//import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScanBatchOutput;
//import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
//import com.synopsys.integration.blackduck.codelocation.upload.UploadBatchOutput;
//import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
//import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
//import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
//import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
//import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
//import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
//import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
//import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ImpactAnalysisOperation;
//import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
//import com.synopsys.integration.detect.lifecycle.run.operation.input.FullScanPostProcessingInput;
//import com.synopsys.integration.detect.lifecycle.run.operation.input.ImpactAnalysisInput;
//import com.synopsys.integration.detect.lifecycle.run.operation.input.SignatureScanInput;
//import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
//import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
//import com.synopsys.integration.detect.lifecycle.run.singleton.SingletonFactory;
//import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
//import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
//import com.synopsys.integration.detect.tool.DetectableToolResult;
//import com.synopsys.integration.detect.tool.UniversalToolsResult;
//import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
//import com.synopsys.integration.detect.tool.detector.factory.DetectorFactory;
//import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisToolResult;
//import com.synopsys.integration.detect.util.filter.DetectToolFilter;
//import com.synopsys.integration.detect.workflow.bdio.AggregateDecision;
//import com.synopsys.integration.detect.workflow.bdio.BdioResult;
//import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
//import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
//import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanDetectResult;
//import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
//import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
//import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
//import com.synopsys.integration.detect.workflow.status.OperationSystem;
//import com.synopsys.integration.exception.IntegrationException;
//import com.synopsys.integration.util.NameVersion;
//
//public class RunManager {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final ExitCodeManager exitCodeManager;
//
//    public RunManager(ExitCodeManager exitCodeManager) {
//        this.exitCodeManager = exitCodeManager;
//    }
//
//    public void run(BootSingletons bootSingletons) {
//        OperationSystem operationSystem = null;
//        try {
//            RunResult runResult = new RunResult();
//            ProductRunData productRunData = bootSingletons.getProductRunData();
//
//            SingletonFactory singletonFactory = new SingletonFactory(bootSingletons);
//            EventSingletons eventSingletons = singletonFactory.createEventSingletons();
//            UtilitySingletons utilitySingletons = singletonFactory.createUtilitySingletons(eventSingletons);
//            operationSystem = utilitySingletons.getOperationSystem();
//
//            DetectorFactory detectorFactory = new DetectorFactory(bootSingletons, utilitySingletons);
//            DetectFontLoaderFactory detectFontLoaderFactory = new DetectFontLoaderFactory(bootSingletons, utilitySingletons);
//            OperationFactory operationFactory = new OperationFactory(detectorFactory.detectDetectableFactory(), detectFontLoaderFactory, bootSingletons, utilitySingletons, eventSingletons);
//
//            // know singleton -
//            ProjectEventPublisher projectEventPublisher = eventSingletons.getProjectEventPublisher();
//            DetectToolFilter detectToolFilter = productRunData.getDetectToolFilter();
//
//            UniversalToolsResult universalToolsResult = runUniversalProjectTools(operationFactory, detectToolFilter, projectEventPublisher, runResult);
//
//            if (productRunData.shouldUseBlackDuckProduct()) {
//                AggregateDecision aggregateDecision = operationFactory.createAggregateOptionsOperation().execute(universalToolsResult.anyFailed());
//                runBlackDuckProduct(productRunData.getBlackDuckRunData(), operationFactory, detectToolFilter, runResult,
//                    universalToolsResult.getNameVersion(), aggregateDecision);
//            } else {
//                logger.info("Black Duck tools will not be run.");
//            }
//
//            logger.info("All tools have finished.");
//            logger.info(ReportConstants.RUN_SEPARATOR);
//        } catch (Exception e) {
//            if (e.getMessage() != null) {
//                logger.error("Detect run failed: {}", e.getMessage());
//            } else {
//                logger.error("Detect run failed: {}", e.getClass().getSimpleName());
//            }
//            logger.debug("An exception was thrown during the detect run.", e);
//            exitCodeManager.requestExitCode(e);
//        } finally {
//            if (operationSystem != null) {
//                operationSystem.publishOperations();
//            }
//        }
//    }
//
//    private UniversalToolsResult runUniversalProjectTools(
//        OperationFactory operationFactory,
//        DetectToolFilter detectToolFilter,
//        ProjectEventPublisher projectEventPublisher,
//        RunResult runResult
//    ) throws DetectUserFriendlyException, IntegrationException {
//        boolean anythingFailed = false;
//
//        logger.info(ReportConstants.RUN_SEPARATOR);
//        if (detectToolFilter.shouldInclude(DetectTool.DOCKER)) {
//            logger.info("Will include the Docker tool.");
//            DetectableToolResult detectableToolResult = operationFactory.createDockerOperation().execute();
//            runResult.addDetectableToolResult(detectableToolResult);
//            anythingFailed = anythingFailed || detectableToolResult.isFailure();
//            logger.info("Docker actions finished.");
//        } else {
//            logger.info("Docker tool will not be run.");
//        }
//
//        logger.info(ReportConstants.RUN_SEPARATOR);
//        if (detectToolFilter.shouldInclude(DetectTool.BAZEL)) {
//            logger.info("Will include the Bazel tool.");
//            DetectableToolResult detectableToolResult = operationFactory.createBazelOperation().execute();
//            runResult.addDetectableToolResult(detectableToolResult);
//            anythingFailed = anythingFailed || detectableToolResult.isFailure();
//            logger.info("Bazel actions finished.");
//        } else {
//            logger.info("Bazel tool will not be run.");
//        }
//        logger.info(ReportConstants.RUN_SEPARATOR);
//        if (detectToolFilter.shouldInclude(DetectTool.DETECTOR)) {
//            logger.info("Will include the detector tool.");
//            DetectorToolResult detectorToolResult = operationFactory.createDetectorOperation().execute();
//            detectorToolResult.getBomToolProjectNameVersion().ifPresent(it -> runResult.addToolNameVersion(DetectTool.DETECTOR, new NameVersion(it.getName(), it.getVersion())));
//            runResult.addDetectCodeLocations(detectorToolResult.getBomToolCodeLocations());
//            anythingFailed = anythingFailed || detectorToolResult.anyDetectorsFailed();
//            logger.info("Detector actions finished.");
//        } else {
//            logger.info("Detector tool will not be run.");
//        }
//
//        logger.info(ReportConstants.RUN_SEPARATOR);
//        logger.debug("Completed code location tools.");
//
//        logger.debug("Determining project info.");
//
//        NameVersion projectNameVersion = operationFactory.createProjectDecisionOperation().execute(runResult.getDetectToolProjectInfo());
//
//        logger.info(String.format("Project name: %s", projectNameVersion.getName()));
//        logger.info(String.format("Project version: %s", projectNameVersion.getVersion()));
//
//        projectEventPublisher.publishProjectNameVersionChosen(projectNameVersion);
//
//        if (anythingFailed) {
//            return UniversalToolsResult.failure(projectNameVersion);
//        } else {
//            return UniversalToolsResult.success(projectNameVersion);
//        }
//    }
//
//    private void runRapidScan(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, BdioResult bdioResult) {
//        operationFactory.phoneHome();
//        File results = operationFactory.performRapidScan();
//        File jsonFile = operationFactory.generateRapidJsonFile(results);
//        File summary = operationFactory.logRapidReport(results);
//        statusEventPublisher.publishDetectResult(new RapidScanDetectResult(jsonFile.getCanonicalPath(), summary));
//    }
//
//    private void runBlackDuckProduct(BlackDuckRunData blackDuckRunData, OperationFactory operationFactory, DetectToolFilter detectToolFilter, RunResult runResult, NameVersion projectNameVersion,
//        AggregateDecision aggregateDecision)
//        throws IntegrationException, DetectUserFriendlyException {
//
//        logger.debug("Black Duck tools will run.");
//
//        ProjectVersionWrapper projectVersionWrapper = null;
//
//        BdioInput bdioInput = new BdioInput(aggregateDecision, projectNameVersion, runResult.getDetectCodeLocations());
//        BdioResult bdioResult = operationFactory.createBdioFileGenerationOperation().execute(bdioInput);
//        if (blackDuckRunData.isRapid() && blackDuckRunData.isOnline()) {
//            blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);
//            logger.info(ReportConstants.RUN_SEPARATOR);
//            runRapidScan();
//        } else {
//            if (blackDuckRunData.isOnline()) {
//                blackDuckRunData.getPhoneHomeManager().ifPresent(PhoneHomeManager::startPhoneHome);
//                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
//                logger.debug("Getting or creating project.");
//                projectVersionWrapper = operationFactory.createProjectCreationOperation().execute(blackDuckServicesFactory, projectNameVersion);
//            } else {
//                logger.debug("Detect is not online, and will not create the project.");
//            }
//
//            logger.debug("Completed project and version actions.");
//            logger.debug("Processing Detect Code Locations.");
//
//            CodeLocationAccumulator codeLocationAccumulator = new CodeLocationAccumulator<>();
//            Optional<CodeLocationCreationData<UploadBatchOutput>> uploadResult = operationFactory.createBdioUploadOperation().execute(blackDuckRunData.getScanMode(), blackDuckRunData, bdioResult);
//            uploadResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
//
//            logger.debug("Completed Detect Code Location processing.");
//
//            logger.info(ReportConstants.RUN_SEPARATOR);
//            if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
//                logger.info("Will include the signature scanner tool.");
//                SignatureScanInput signatureScanInput = new SignatureScanInput(projectNameVersion, runResult.getDockerTargetData().orElse(null));
//                Optional<CodeLocationCreationData<ScanBatchOutput>> signatureScanResult = operationFactory.createSignatureScanOperation().execute(signatureScanInput);
//                signatureScanResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
//                logger.info("Signature scanner actions finished.");
//            } else {
//                logger.info("Signature scan tool will not be run.");
//            }
//
//            logger.info(ReportConstants.RUN_SEPARATOR);
//            if (detectToolFilter.shouldInclude(DetectTool.BINARY_SCAN)) {
//                logger.info("Will include the binary scanner tool.");
//                if (blackDuckRunData.isOnline()) {
//                    Optional<CodeLocationCreationData<BinaryScanBatchOutput>> binaryScanResult = operationFactory.createBinaryScanOperation().execute(projectNameVersion, runResult.getDockerTargetData().orElse(null));
//                    binaryScanResult.ifPresent(codeLocationAccumulator::addWaitableCodeLocation);
//                }
//                logger.info("Binary scanner actions finished.");
//            } else {
//                logger.info("Binary scan tool will not be run.");
//            }
//            ImpactAnalysisOperation impactAnalysisOperation = operationFactory.createImpactAnalysisOperation();
//            logger.info(ReportConstants.RUN_SEPARATOR);
//            if (detectToolFilter.shouldInclude(DetectTool.IMPACT_ANALYSIS)) {
//                logger.info("Will include the Vulnerability Impact Analysis tool.");
//                ImpactAnalysisInput impactAnalysisInput = new ImpactAnalysisInput(projectNameVersion, projectVersionWrapper);
//                ImpactAnalysisToolResult impactAnalysisToolResult = impactAnalysisOperation.execute(impactAnalysisInput);
//                /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
//                codeLocationAccumulator.addNonWaitableCodeLocation(impactAnalysisToolResult.getCodeLocationNames());
//                logger.info("Vulnerability Impact Analysis tool actions finished.");
//            } else {
//                logger.info("Vulnerability Impact Analysis tool will not be run.");
//            }
//
//            logger.info(ReportConstants.RUN_SEPARATOR);
//            //We have finished code locations.
//            CodeLocationResults codeLocationResults = operationFactory.createCodeLocationResultCalculationOperation().execute(codeLocationAccumulator);
//
//            if (blackDuckRunData.isOnline()) {
//                logger.info("Will perform Black Duck post actions.");
//                BlackDuckServicesFactory blackDuckServicesFactory = blackDuckRunData.getBlackDuckServicesFactory();
//                FullScanPostProcessingInput fullScanPostProcessingInput = new FullScanPostProcessingInput(projectNameVersion, bdioResult, codeLocationResults, projectVersionWrapper);
//                operationFactory.createFullScanPostProcessingOperation(detectToolFilter).execute(blackDuckServicesFactory, fullScanPostProcessingInput);
//                logger.info("Black Duck actions have finished.");
//            } else {
//                logger.debug("Will not perform Black Duck post actions: Detect is not online.");
//            }
//        }
//    }
//}
