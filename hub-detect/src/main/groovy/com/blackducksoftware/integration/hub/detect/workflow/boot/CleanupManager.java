package com.blackducksoftware.integration.hub.detect.workflow.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanupManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //TODO replicate cleanup and summary
    public void cleanup(DetectRunDependencies detectRunDependencies) {
        try {
            detectRunDependencies.phoneHomeManager.endPhoneHome();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
        }

        //detectRunDependencies.directoryManager.cleanup();
    }
}
