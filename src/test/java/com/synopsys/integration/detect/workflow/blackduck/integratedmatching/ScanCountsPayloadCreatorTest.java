package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.WaitableCodeLocationData;
import com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model.ScanCountsPayload;

class ScanCountsPayloadCreatorTest {

    @Test
    void testMultSigScans() {
        WaitableCodeLocationData signatureScanWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(signatureScanWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.SIGNATURE_SCAN);
        Set<String> successfulCodeLocationNames = new HashSet<>();
        successfulCodeLocationNames.add("sigScan1");
        successfulCodeLocationNames.add("sigScan2");
        Mockito.when(signatureScanWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulCodeLocationNames);
        List<WaitableCodeLocationData> waitableCodeLocationDataList = Arrays.asList(signatureScanWaitableCodeLocationData);

        ScanCountsPayloadCreator creator = new ScanCountsPayloadCreator();
        ScanCountsPayload payload = creator.create(waitableCodeLocationDataList);

        assertEquals(0, payload.getScanCounts().getPackageManager());
        assertEquals(2, payload.getScanCounts().getSignature());
        assertEquals(0, payload.getScanCounts().getBinary());
    }

    // TODO lots more tests
}
