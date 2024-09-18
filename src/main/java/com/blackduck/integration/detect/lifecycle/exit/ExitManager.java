package com.blackduck.integration.detect.lifecycle.exit;

import java.util.Optional;

import com.blackduck.integration.detect.workflow.event.Event;
import com.blackduck.integration.detect.workflow.event.EventSystem;
import com.blackduck.integration.detect.workflow.status.DetectStatusManager;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.detect.lifecycle.autonomous.AutonomousManager;
import com.blackduck.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.blackduck.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.log.Slf4jIntLogger;

public class ExitManager {
    private final Logger logger = LoggerFactory.getLogger(ExitManager.class);
    private final EventSystem eventSystem;
    private final ExitCodeManager exitCodeManager;
    private final DetectStatusManager statusManager;

    public ExitManager(EventSystem eventSystem, ExitCodeManager exitCodeManager, DetectStatusManager statusManager) {
        this.eventSystem = eventSystem;
        this.exitCodeManager = exitCodeManager;
        this.statusManager = statusManager;
    }

    public ExitResult exit(ExitOptions exitOptions, Optional<AutonomousManager> autonomousManagerOptional) {
        long startTime = exitOptions.getStartTime();
        boolean forceSuccessExit = exitOptions.shouldForceSuccessExit();
        boolean shouldExit = exitOptions.shouldExit();

        //Generally, when requesting a failure status, an exit code is also requested, but if it is not, we default to an unknown error.
        if (statusManager.hasAnyFailure()) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_UNKNOWN_ERROR, "A failure status was requested by one or more of Detect's tools."));
        }

        //Find the final (as requested) exit code
        ExitCodeType finalExitCode = exitCodeManager.getWinningExitCode();

        //Print detect's status
        statusManager.logDetectResults(new Slf4jIntLogger(logger), finalExitCode, autonomousManagerOptional);

        //Print duration of run
        long endTime = System.currentTimeMillis();
        String duration = DurationFormatUtils.formatPeriod(startTime, endTime, "HH'h' mm'm' ss's' SSS'ms'");
        logger.info("Detect duration: {}", duration);

        //Exit with formal exit code
        if (finalExitCode != ExitCodeType.SUCCESS && forceSuccessExit) {
            logger.warn("Forcing success: Exiting with exit code 0. Ignored exit code was {}.", finalExitCode.getExitCode());
        } else if (finalExitCode != ExitCodeType.SUCCESS) {
            logger.error("Exiting with code {} - {}", finalExitCode.getExitCode(), finalExitCode);
        }

        if (!shouldExit) {
            logger.info("Would normally exit({}) but it is overridden.", finalExitCode.getExitCode());
        }

        return new ExitResult(finalExitCode, forceSuccessExit, shouldExit);
    }
}
