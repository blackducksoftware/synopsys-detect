package com.synopsys.integration.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTargetType;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.SingletonFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.run.step.IntelligentModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.RapidModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.UniversalStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.lifecycle.shutdown.ExceptionUtility;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.detector.factory.DetectorFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.util.NameVersion;

public class DetectRun {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExitCodeManager exitCodeManager;
    private final ExceptionUtility exceptionUtility;

    public DetectRun(ExitCodeManager exitCodeManager, ExceptionUtility exceptionUtility) {
        this.exitCodeManager = exitCodeManager;
        this.exceptionUtility = exceptionUtility;
    }

    private OperationRunner createOperationFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSingletons eventSingletons)
        throws DetectUserFriendlyException {
        DetectorFactory detectorFactory = new DetectorFactory(bootSingletons, utilitySingletons);
        DetectFontLoaderFactory detectFontLoaderFactory = new DetectFontLoaderFactory(bootSingletons, utilitySingletons);
        return new OperationRunner(detectorFactory.detectDetectableFactory(), detectFontLoaderFactory, bootSingletons, utilitySingletons, eventSingletons);
    }

    public void run(BootSingletons bootSingletons) {
        Optional<OperationSystem> operationSystem = Optional.empty();
        try {
            SingletonFactory singletonFactory = new SingletonFactory(bootSingletons);
            EventSingletons eventSingletons = singletonFactory.createEventSingletons();
            UtilitySingletons utilitySingletons = singletonFactory.createUtilitySingletons(eventSingletons);
            operationSystem = Optional.of(utilitySingletons.getOperationSystem());

            ProductRunData productRunData = bootSingletons.getProductRunData(); //TODO: Remove run data from boot singletons
            OperationRunner operationRunner = createOperationFactory(bootSingletons, utilitySingletons, eventSingletons);
            StepHelper stepHelper = new StepHelper(utilitySingletons.getOperationSystem(), utilitySingletons.getOperationWrapper(), productRunData.getDetectToolFilter());

            UniversalStepRunner stepRunner = new UniversalStepRunner(operationRunner, stepHelper); //Product independent tools
            UniversalToolsResult universalToolsResult = stepRunner.runUniversalTools();

            // test to ensure image scan results are available (i.e. image scan success) throws an
            // exception if results components are empty and/or not found.  This will never throw an
            // exception for any other scan type
            imageScanCanScanFurther(universalToolsResult, bootSingletons);

            // combine: processProjectInformation() -> ProjectResult (nameversion, bdio)
            NameVersion nameVersion = stepRunner.determineProjectInformation(universalToolsResult);
            operationRunner.publishProjectNameVersionChosen(nameVersion);
            BdioResult bdio;
            Boolean forceBdio = bootSingletons.getDetectConfigurationFactory().forceBdio();
            if (!universalToolsResult.getDetectCodeLocations().isEmpty()
                    || (productRunData.shouldUseBlackDuckProduct() && !productRunData.getBlackDuckRunData().isOnline() && forceBdio && !universalToolsResult.didAnyFail() && exitCodeManager.getWinningExitCode().isSuccess())) {
                bdio = stepRunner.generateBdio(universalToolsResult, nameVersion);
            } else {
                bdio = BdioResult.none();
            }
            if (productRunData.shouldUseBlackDuckProduct()) {
                BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
                if (blackDuckRunData.isNonPersistent() && blackDuckRunData.isOnline()) {
                    RapidModeStepRunner rapidModeSteps = new RapidModeStepRunner(operationRunner, stepHelper, bootSingletons.getGson(), bootSingletons.getDirectoryManager());

                    Optional<String> scaaasFilePath = bootSingletons.getDetectConfigurationFactory().getScaaasFilePath();

                    rapidModeSteps.runOnline(blackDuckRunData, nameVersion, bdio, universalToolsResult.getDockerTargetData(), scaaasFilePath);
                } else if (blackDuckRunData.isNonPersistent()) {
                    logger.info("Rapid Scan is offline, nothing to do.");
                } else if (blackDuckRunData.isOnline()) {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationRunner, stepHelper, bootSingletons.getGson());
                    intelligentModeSteps.runOnline(blackDuckRunData, bdio, nameVersion, productRunData.getDetectToolFilter(), universalToolsResult.getDockerTargetData());
                } else {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationRunner, stepHelper, bootSingletons.getGson());
                    intelligentModeSteps.runOffline(nameVersion, universalToolsResult.getDockerTargetData(), bdio);
                }
            }
        } catch (Exception e) {
            logger.error(ReportConstants.RUN_SEPARATOR);
            logger.error("Detect run failed.");
            exceptionUtility.logException(e);
            logger.debug("An exception was thrown during the detect run.", e);
            logger.error(ReportConstants.RUN_SEPARATOR);
            exitCodeManager.requestExitCode(e);
            checkForInterruptedException(e);
        } finally {
            operationSystem.ifPresent(OperationSystem::publishOperations);
        }
    }

    /**
     * Image scan check to see if any docker information is there. If image isn't found for image scan, lists will
     * be empty and dockerTargetData will be null...  If not an image scan just return true
     * @param result
     * @param bootSingletons
     * @return
     */
    private void imageScanCanScanFurther(UniversalToolsResult result, BootSingletons bootSingletons) throws Exception {
        boolean canScanFurther = true;
        boolean isImageScan = bootSingletons.getDetectConfigurationFactory().createDetectTarget().equals(DetectTargetType.IMAGE);
        boolean hasDockerTargetData = result.getDockerTargetData() != null;
        
        if (isImageScan) {
            canScanFurther = (!result.didAnyFail() && 
                                    !result.getDetectToolProjectInfo().isEmpty() &&
                                    !result.getDetectCodeLocations().isEmpty() &&
                                    hasDockerTargetData
                );
        }        

        if (!canScanFurther) {
            throw new DetectUserFriendlyException("Cannot scan Docker image." , ExitCodeType.FAILURE_IMAGE_NOT_AVAILABLE);
        }

    }

    private void checkForInterruptedException(Exception e) {
        if (e instanceof InterruptedException) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
}
