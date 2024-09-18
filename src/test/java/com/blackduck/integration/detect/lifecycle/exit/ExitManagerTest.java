package com.blackduck.integration.detect.lifecycle.exit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.blackduck.integration.detect.configuration.enumeration.ExitCodeType;
import com.blackduck.integration.detect.lifecycle.shutdown.ExceptionUtility;
import com.blackduck.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.blackduck.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackduck.integration.detect.workflow.event.EventSystem;
import com.blackduck.integration.detect.workflow.status.DetectStatusManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExitManagerTest {
    @Test
    public void testNormalExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExceptionUtility exitCodeUtility = new ExceptionUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, false, true);
        ExitResult exitResult = exitManager.exit(exitOptions, Optional.empty());

        assertEquals(startTime, exitOptions.getStartTime());
        assertFalse(exitResult.shouldForceSuccess());
        assertTrue(exitResult.shouldPerformExit());
        Assertions.assertEquals(ExitCodeType.SUCCESS, exitResult.getExitCodeType());
    }

    @Test
    public void testForceExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExceptionUtility exitCodeUtility = new ExceptionUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        exitCodeManager.addExitCodeRequest(new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION, "JUnit failure code."));
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, true, true);
        ExitResult exitResult = exitManager.exit(exitOptions, Optional.empty());

        assertEquals(startTime, exitOptions.getStartTime());
        assertTrue(exitResult.shouldForceSuccess());
        assertTrue(exitResult.shouldPerformExit());
        Assertions.assertEquals(ExitCodeType.FAILURE_CONFIGURATION, exitResult.getExitCodeType());
    }

    @Test
    public void testSkipExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExceptionUtility exitCodeUtility = new ExceptionUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        exitCodeManager.addExitCodeRequest(new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION, "JUnit failure code."));
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, false, false);
        ExitResult exitResult = exitManager.exit(exitOptions, Optional.empty());

        assertEquals(startTime, exitOptions.getStartTime());
        assertFalse(exitResult.shouldForceSuccess());
        assertFalse(exitResult.shouldPerformExit());
        Assertions.assertEquals(ExitCodeType.FAILURE_CONFIGURATION, exitResult.getExitCodeType());

    }

}
