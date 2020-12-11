package com.synopsys.integration.detect.lifecycle.exit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeUtility;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.DetectStatusManager;

public class ExitManagerTest {
    @Test
    public void testNormalExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, true, false, true);
        ExitResult exitResult = exitManager.exit(exitOptions);

        assertEquals(startTime, exitOptions.getStartTime());
        assertTrue(exitOptions.isLogResults());
        assertEquals(exitOptions.isForceSuccessExit(), exitResult.isForceSuccess());
        assertEquals(exitOptions.isShouldExit(), exitResult.shouldPerformExit());
        assertNotNull(exitResult.getDuration());
        assertEquals(ExitCodeType.SUCCESS, exitResult.getExitCodeType());
        assertEquals(ExitCodeType.SUCCESS.getExitCode(), exitResult.getExitCode());
    }

    @Test
    public void testForceExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        exitCodeManager.addExitCodeRequest(new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION, "JUnit failure code."));
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, true, true, true);
        ExitResult exitResult = exitManager.exit(exitOptions);

        assertEquals(startTime, exitOptions.getStartTime());
        assertTrue(exitOptions.isLogResults());
        assertEquals(exitOptions.isForceSuccessExit(), exitResult.isForceSuccess());
        assertEquals(exitOptions.isShouldExit(), exitResult.shouldPerformExit());
        assertNotNull(exitResult.getDuration());
        assertEquals(ExitCodeType.FAILURE_CONFIGURATION, exitResult.getExitCodeType());
        assertEquals(ExitCodeType.FAILURE_CONFIGURATION.getExitCode(), exitResult.getExitCode());
    }

    @Test
    public void testSkipExit() {
        long startTime = System.currentTimeMillis();
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);
        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);
        exitCodeManager.addExitCodeRequest(new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION, "JUnit failure code."));
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

        ExitOptions exitOptions = new ExitOptions(startTime, true, false, false);
        ExitResult exitResult = exitManager.exit(exitOptions);

        assertEquals(startTime, exitOptions.getStartTime());
        assertTrue(exitOptions.isLogResults());
        assertEquals(exitOptions.isForceSuccessExit(), exitResult.isForceSuccess());
        assertEquals(exitOptions.isShouldExit(), exitResult.shouldPerformExit());
        assertNotNull(exitResult.getDuration());
        assertEquals(ExitCodeType.FAILURE_CONFIGURATION, exitResult.getExitCodeType());
        assertEquals(ExitCodeType.FAILURE_CONFIGURATION.getExitCode(), exitResult.getExitCode());

    }

}
