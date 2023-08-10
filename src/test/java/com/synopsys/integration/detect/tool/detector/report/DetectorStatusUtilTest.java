package com.synopsys.integration.detect.tool.detector.report;

import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.extraction.Extraction;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetectorStatusUtilTest {

    @Test
    void testGetFailedStatusReason_withNonZeroExitStatusCode() {
        Extraction extraction = Mockito.mock(Extraction.class);
        ExecutableFailedException exception = Mockito.mock(ExecutableFailedException.class);

        Mockito.when(extraction.getError()).thenReturn(exception);

        Mockito.when(exception.hasReturnCode()).thenReturn(true);
        Mockito.when(exception.getReturnCode()).thenReturn(137);
        Mockito.when(exception.getExecutableDescription()).thenReturn("some-exec-desc");

        assertEquals(
            "Failed to execute command, returned non-zero (137): some-exec-desc",
            DetectorStatusUtil.getFailedStatusReason(extraction)
        );
    }
}
