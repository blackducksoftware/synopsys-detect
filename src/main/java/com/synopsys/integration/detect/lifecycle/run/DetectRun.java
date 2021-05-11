/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.EventSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.SingletonFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.lifecycle.run.step.IntelligentModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.RapidModeStepRunner;
import com.synopsys.integration.detect.lifecycle.run.step.UniversalStepRunner;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.detector.factory.DetectorFactory;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.util.NameVersion;

public class DetectRun {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExitCodeManager exitCodeManager;

    public DetectRun(ExitCodeManager exitCodeManager) {
        this.exitCodeManager = exitCodeManager;
    }

    private OperationFactory createOperationFactory(BootSingletons bootSingletons) throws DetectUserFriendlyException {
        SingletonFactory singletonFactory = new SingletonFactory(bootSingletons);
        EventSingletons eventSingletons = singletonFactory.createEventSingletons();
        UtilitySingletons utilitySingletons = singletonFactory.createUtilitySingletons(eventSingletons);

        DetectorFactory detectorFactory = new DetectorFactory(bootSingletons, utilitySingletons);
        DetectFontLoaderFactory detectFontLoaderFactory = new DetectFontLoaderFactory(bootSingletons, utilitySingletons);

        return new OperationFactory(detectorFactory.detectDetectableFactory(), detectFontLoaderFactory, bootSingletons, utilitySingletons, eventSingletons);
    }

    public void run(BootSingletons bootSingletons) {
        OperationSystem operationSystem = null;//where is this supposed to come from?
        try {
            ProductRunData productRunData = bootSingletons.getProductRunData(); //TODO: Remove run data from boot singletons
            OperationFactory operationFactory = createOperationFactory(bootSingletons);

            UniversalStepRunner stepRunner = new UniversalStepRunner(operationFactory, productRunData.getDetectToolFilter()); //Product independent tools
            UniversalToolsResult universalToolsResult = stepRunner.runUniversalTools();

            // combine: processProjectInformation() -> ProjectResult (nameversion, bdio)
            NameVersion nameVersion = stepRunner.determineProjectInformation(universalToolsResult);
            operationFactory.publishProjectNameVersionChosen(nameVersion);
            BdioResult bdio = stepRunner.generateBdio(universalToolsResult, nameVersion);

            if (productRunData.shouldUseBlackDuckProduct()) {
                if (productRunData.getBlackDuckRunData().isRapid()) {
                    RapidModeStepRunner rapidModeSteps = new RapidModeStepRunner();
                    rapidModeSteps.runAll(productRunData.getBlackDuckRunData(), nameVersion, bdio);
                } else if (productRunData.getBlackDuckRunData().isOnline()) {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationFactory);
                    intelligentModeSteps.runOnline(productRunData.getBlackDuckRunData(), bdio, nameVersion, productRunData.getDetectToolFilter(), universalToolsResult.getDockerTargetData()); //todo get post options
                } else {
                    IntelligentModeStepRunner intelligentModeSteps = new IntelligentModeStepRunner(operationFactory);
                    intelligentModeSteps.runOffline(nameVersion, productRunData.getDetectToolFilter(), universalToolsResult.getDockerTargetData());
                }
            } else {
                logger.info("Black Duck tools will not be run.");
            }

        } catch (Exception e) {
            if (e.getMessage() != null) {
                logger.error("Detect run failed: {}", e.getMessage());
            } else {
                logger.error("Detect run failed: {}", e.getClass().getSimpleName());
            }
            logger.debug("An exception was thrown during the detect run.", e);
            exitCodeManager.requestExitCode(e);
        } finally {
            if (operationSystem != null) {
                operationSystem.publishOperations();
            }
        }
    }
}
