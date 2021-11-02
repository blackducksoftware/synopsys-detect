/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.SingletonFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.run.step.IntelligentModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.RapidModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.UniversalStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.detector.factory.DetectorFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.util.NameVersion;

public class DetectRun {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExitCodeManager exitCodeManager;
    private final EventSystem eventSystem;

    public DetectRun(ExitCodeManager exitCodeManager, EventSystem eventSystem) {
        this.exitCodeManager = exitCodeManager;
        this.eventSystem = eventSystem;
    }

    private OperationFactory createOperationFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSingletons eventSingletons) throws DetectUserFriendlyException {
        DetectorFactory detectorFactory = new DetectorFactory(bootSingletons, utilitySingletons, eventSystem);
        DetectFontLoaderFactory detectFontLoaderFactory = new DetectFontLoaderFactory(bootSingletons, utilitySingletons);
        return new OperationFactory(detectorFactory.detectDetectableFactory(), detectFontLoaderFactory, bootSingletons, utilitySingletons, eventSingletons, exitCodeManager);
    }

    public void run(BootSingletons bootSingletons) {
        Optional<OperationSystem> operationSystem = Optional.empty();
        try {
            SingletonFactory singletonFactory = new SingletonFactory(bootSingletons);
            EventSingletons eventSingletons = singletonFactory.createEventSingletons();
            UtilitySingletons utilitySingletons = singletonFactory.createUtilitySingletons(eventSingletons, exitCodeManager);
            operationSystem = Optional.of(utilitySingletons.getOperationSystem());

            ProductRunData productRunData = bootSingletons.getProductRunData(); //TODO: Remove run data from boot singletons
            OperationFactory operationFactory = createOperationFactory(bootSingletons, utilitySingletons, eventSingletons);
            StepHelper stepHelper = new StepHelper(utilitySingletons.getOperationSystem(), utilitySingletons.getOperationWrapper(), productRunData.getDetectToolFilter());

            UniversalStepRunner stepRunner = new UniversalStepRunner(operationFactory, stepHelper); //Product independent tools
            UniversalToolsResult universalToolsResult = stepRunner.runUniversalTools();

            // combine: processProjectInformation() -> ProjectResult (nameversion, bdio)
            NameVersion nameVersion = stepRunner.determineProjectInformation(universalToolsResult);
            operationFactory.publishProjectNameVersionChosen(nameVersion);
            BdioResult bdio = stepRunner.generateBdio(universalToolsResult, nameVersion);

            if (productRunData.shouldUseBlackDuckProduct()) {
                BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
                if (blackDuckRunData.isRapid() && blackDuckRunData.isOnline()) {
                    RapidModeStepRunner rapidModeSteps = new RapidModeStepRunner(operationFactory);
                    rapidModeSteps.runOnline(blackDuckRunData, nameVersion, bdio);
                } else if (blackDuckRunData.isRapid()) {
                    logger.info("Rapid Scan is offline, nothing to do.");
                } else if (blackDuckRunData.isOnline()) {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationFactory, stepHelper, eventSystem);
                    intelligentModeSteps.runOnline(blackDuckRunData, bdio, nameVersion, productRunData.getDetectToolFilter(), universalToolsResult.getDockerTargetData());
                } else {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationFactory, stepHelper, eventSystem);
                    intelligentModeSteps.runOffline(nameVersion, universalToolsResult.getDockerTargetData());
                }
            }
        } catch (Exception e) {
            logger.error("Detect run failed: {}", getExceptionMessage(e));
            logger.debug("An exception was thrown during the detect run.", e);
            exitCodeManager.requestExitCode(e);
            checkForInterruptedException(e);
        } finally {
            operationSystem.ifPresent(OperationSystem::publishOperations);
        }
    }

    private String getExceptionMessage(Exception e) {
        if (e.getMessage() != null) {
            return e.getMessage();
        } else {
            return e.getClass().getSimpleName();
        }
    }

    private void checkForInterruptedException(Exception e) {
        if (e instanceof InterruptedException) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
}
