package com.synopsys.integration.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
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

            // combine: processProjectInformation() -> ProjectResult (nameversion, bdio)
            NameVersion nameVersion = stepRunner.determineProjectInformation(universalToolsResult);
            operationRunner.publishProjectNameVersionChosen(nameVersion);
            BdioResult bdio;
            if (!universalToolsResult.getDetectCodeLocations().isEmpty()) {
                bdio = stepRunner.generateBdio(universalToolsResult, nameVersion);
            } else {
                bdio = BdioResult.none();
            }
            if (productRunData.shouldUseBlackDuckProduct()) {
                BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
                if (blackDuckRunData.isRapid() && blackDuckRunData.isOnline()) {
                    RapidModeStepRunner rapidModeSteps = new RapidModeStepRunner(operationRunner, stepHelper, bootSingletons.getGson());
                    rapidModeSteps.runOnline(blackDuckRunData, nameVersion, bdio, universalToolsResult.getDockerTargetData());
                } else if (blackDuckRunData.isRapid()) {
                    logger.info("Rapid Scan is offline, nothing to do.");
                } else if (blackDuckRunData.isOnline()) {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationRunner, stepHelper);
                    intelligentModeSteps.runOnline(blackDuckRunData, bdio, nameVersion, productRunData.getDetectToolFilter(), universalToolsResult.getDockerTargetData());
                } else {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationRunner, stepHelper);
                    intelligentModeSteps.runOffline(nameVersion, universalToolsResult.getDockerTargetData());
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

    private void checkForInterruptedException(Exception e) {
        if (e instanceof InterruptedException) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
}
