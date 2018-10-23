package com.blackducksoftware.integration.hub.detect.workflow.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class CleanupManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private PhoneHomeManager phoneHomeManager;
    private DirectoryManager directoryManager;

    //TODO replicate cleanup
    public void cleanup() {
        try {
            phoneHomeManager.endPhoneHome();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
        }

        try {
            directoryManager.getRunHomeDirectory().delete();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying cleanup the run directory: %s", e.getMessage()));
        }

    }
}
