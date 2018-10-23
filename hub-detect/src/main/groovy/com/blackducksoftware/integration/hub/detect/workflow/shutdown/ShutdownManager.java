package com.blackducksoftware.integration.hub.detect.workflow.shutdown;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.boot.CleanupManager;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ShutdownManager {

    public void shutdown() {
        if (status.failedBomToolGroupTypes.size() > 0) {
            return ExitCodeType.FAILURE_BOM_TOOL;
        }
        if (status.missingBomToolGroupTypes.size() > 0) {
            return ExitCodeType.FAILURE_BOM_TOOL_REQUIRED;
        }

        try {
            if (bootResult != null) {
                CleanupManager cleanupManager = new CleanupManager();
                cleanupManager.cleanup(bootResult.detectRunDependencies);
            }
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        boolean printOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT, PropertyAuthority.None);
        if (!printOutput) {
            detectStatusManager.logDetectResults(new Slf4jIntLogger(logger), exitCodeManager.getWinningExitCode());
            //detectSummaryManager.logDetectResults(, currentExitCodeType);
        }
    }
}
