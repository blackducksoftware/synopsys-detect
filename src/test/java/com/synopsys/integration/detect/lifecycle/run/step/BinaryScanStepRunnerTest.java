package com.synopsys.integration.detect.lifecycle.run.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.blackduck.upload.rest.model.response.BinaryFinishResponseContent;
import com.synopsys.blackduck.upload.rest.status.BinaryUploadStatus;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;

public class BinaryScanStepRunnerTest {

    @Test
    public void testExtractBinaryScanId() {
        OperationRunner operationRunner = mock(OperationRunner.class);
        BinaryScanStepRunner binaryScanStepRunner = new BinaryScanStepRunner(operationRunner);
        
        String expectedScanId = "93420e34-348c-440a-9911-198a65ed6f00";
        String location = "https://localhost/api/intelligent-persistence-scans/93420e34-348c-440a-9911-198a65ed6f00";

        BinaryFinishResponseContent mockResponse = mock(BinaryFinishResponseContent.class);
        when(mockResponse.getLocation()).thenReturn(location);
        
        BinaryUploadStatus mockStatus = mock(BinaryUploadStatus.class);
        when(mockStatus.getResponseContent()).thenReturn(Optional.of(mockResponse));

        Optional<String> result = binaryScanStepRunner.extractBinaryScanId(mockStatus);

        assertTrue(result.isPresent());
        assertEquals(expectedScanId, result.get());
    }
    
}
