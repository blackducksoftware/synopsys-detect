package com.synopsys.integration.detect.workflow.status;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.LogLevel;

public class DetectStatusLoggerTest {

    @Test
    public void testContent() {
        BufferedIntLogger loggerExpected = new BufferedIntLogger();
        BufferedIntLogger loggerActual = new BufferedIntLogger();

        DetectStatusLogger statusLogger = new DetectStatusLogger();
        List<Status> statusSummaries = Collections.emptyList();
        List<DetectResult> detectResults = Collections.emptyList();
        List<DetectIssue> detectIssues = Collections.emptyList();
        statusLogger.logDetectStatus(loggerExpected, statusSummaries, detectResults, detectIssues, ExitCodeType.SUCCESS);
        statusLogger.logDetectStatus2(loggerActual, statusSummaries, detectResults, detectIssues, ExitCodeType.SUCCESS);
        String expectedOutput = loggerExpected.getOutputString(LogLevel.TRACE);
        String actualOutput = loggerActual.getOutputString(LogLevel.TRACE);
        assertEquals(expectedOutput, actualOutput);
    }
}
