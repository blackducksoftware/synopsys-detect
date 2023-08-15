package com.synopsys.integration.detect.tool.detector.report;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorStatusCode;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

public class DetectorStatusUtilTest {

    @Test
    void testGetFailedStatusReason_withNonZeroExitStatusCode() {
        Extraction extraction = Mockito.mock(Extraction.class);
        ExecutableFailedException exception = Mockito.mock(ExecutableFailedException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        Mockito.when(exception.hasReturnCode()).thenReturn(true);
        Mockito.when(exception.getReturnCode()).thenReturn(1);
        Mockito.when(exception.getExecutableDescription()).thenReturn("some-exec-desc");

        assertEquals(
            "Failed to execute command, returned non-zero (1): some-exec-desc",
            DetectorStatusUtil.getFailedStatusReason(extraction)
        );
    }

    @Test
    void testGetFailedStatusReason_with137ExitStatusCode() {
        Extraction extraction = Mockito.mock(Extraction.class);
        ExecutableFailedException exception = Mockito.mock(ExecutableFailedException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        Mockito.when(exception.hasReturnCode()).thenReturn(true);
        Mockito.when(exception.getReturnCode()).thenReturn(137);
        Mockito.when(exception.getExecutableDescription()).thenReturn("some-exec-desc");

        assertEquals(
            DetectorStatusCode.EXECUTABLE_TERMINATED_LIKELY_OUT_OF_MEMORY.getDescription(),
            DetectorStatusUtil.getFailedStatusReason(extraction)
        );
    }

    @Test
    void testGetFailedStatusCode_executableExceptionExitCode137() {
        Extraction extraction = Mockito.mock(Extraction.class);
        ExecutableFailedException exception = Mockito.mock(ExecutableFailedException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        Mockito.when(exception.hasReturnCode()).thenReturn(true);
        Mockito.when(exception.getReturnCode()).thenReturn(137);

        assertEquals(
            DetectorStatusCode.EXECUTABLE_TERMINATED_LIKELY_OUT_OF_MEMORY,
            DetectorStatusUtil.getFailedStatusCode(extraction)
        );
    }

    @Test
    void testGetFailedStatusCode_executableExceptionExitCode1() {
        Extraction extraction = Mockito.mock(Extraction.class);
        ExecutableFailedException exception = Mockito.mock(ExecutableFailedException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        Mockito.when(exception.hasReturnCode()).thenReturn(true);
        Mockito.when(exception.getReturnCode()).thenReturn(1);

        assertEquals(
            DetectorStatusCode.EXECUTABLE_FAILED,
            DetectorStatusUtil.getFailedStatusCode(extraction)
        );
    }

    @Test
    void testGetFailedStatusCode_nonExecutableException() {
        Extraction extraction = Mockito.mock(Extraction.class);
        IOException exception = Mockito.mock(IOException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        assertEquals(
            DetectorStatusCode.EXTRACTION_FAILED,
            DetectorStatusUtil.getFailedStatusCode(extraction)
        );
    }
}
