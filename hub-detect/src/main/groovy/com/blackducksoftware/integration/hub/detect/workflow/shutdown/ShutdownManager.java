package com.blackducksoftware.integration.hub.detect.workflow.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.boot.CleanupManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final CleanupManager cleanupManager;
    private final DetectConfiguration detectConfiguration;

    public ShutdownManager(DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager, final CleanupManager cleanupManager,
        final DetectConfiguration detectConfiguration) {
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.cleanupManager = cleanupManager;
        this.detectConfiguration = detectConfiguration;
    }

    public ExitCodeType shutdown() {
        try {
            cleanupManager.cleanup();
        } catch (final Exception e) {
            exitCodeManager.requestExitCode(e);
        }

        boolean printOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT, PropertyAuthority.None);

        ExitCodeType detectExitCode = exitCodeManager.getWinningExitCode();
        if (!printOutput) {
            detectStatusManager.logDetectResults(new Slf4jIntLogger(logger), detectExitCode);
        }

        return detectExitCode;
    }
}
