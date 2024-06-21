package com.synopsys.integration.detect.lifecycle.run.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.blackduck.upload.rest.model.response.UploadFinishResponse;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;

public class BinaryScanStepRunnerTest {

    @Test
    public void testExtractBinaryScanId() {
        OperationRunner operationRunner = mock(OperationRunner.class);
        BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationRunner);
        
        String expectedScanId = "93420e34-348c-440a-9911-198a65ed6f00";
        String location = "https://localhost/api/intelligent-persistence-scans/93420e34-348c-440a-9911-198a65ed6f00";

        UploadFinishResponse mockResponse = mock(UploadFinishResponse.class);
        when(mockResponse.getLocation()).thenReturn(location);

        Optional<String> result = binaryScanStepRunner.extractBinaryScanId(mockResponse);

        assertTrue(result.isPresent());
        assertEquals(expectedScanId, result.get());
    }
    
}
