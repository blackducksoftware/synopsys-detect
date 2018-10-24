package com.blackducksoftware.integration.hub.detect.lifecycle.shutdown;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final PhoneHomeManager phoneHomeManager;
    private final DirectoryManager directoryManager;
    private final DetectConfiguration detectConfiguration;

    public ShutdownManager(DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager,
        final PhoneHomeManager phoneHomeManager, final DirectoryManager directoryManager, final DetectConfiguration detectConfiguration) {
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.phoneHomeManager = phoneHomeManager;
        this.directoryManager = directoryManager;
        this.detectConfiguration = detectConfiguration;
    }

    public ExitCodeType shutdown() {
        try {
            logger.debug("Ending phone home.");
            phoneHomeManager.endPhoneHome();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
        }

        try {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None)) {
                logger.info("Cleaning up directory: " + directoryManager.getRunHomeDirectory().getAbsolutePath());
                FileUtils.deleteDirectory(directoryManager.getRunHomeDirectory());
            } else {
                logger.info("Skipping cleanup, it is disabled.");
            }
        } catch (final Exception e) {
            logger.debug(String.format("Error trying cleanup the run directory: %s", e.getMessage()));
        }

        boolean printOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT, PropertyAuthority.None);

        ExitCodeType detectExitCode = exitCodeManager.getWinningExitCode();
        if (!printOutput) {
            detectStatusManager.logDetectResults(new Slf4jIntLogger(logger), detectExitCode);
        }

        return detectExitCode;
    }
}
