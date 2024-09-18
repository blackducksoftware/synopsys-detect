package com.blackduck.integration.detect.workflow.blackduck.integratedmatching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import com.blackduck.integration.detect.workflow.blackduck.codelocation.WaitableCodeLocationData;
import com.blackduck.integration.detect.workflow.blackduck.integratedmatching.model.ScanCountsPayload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScanCountsPayloadCreatorTest {

    @Test
    void testMultSigScansPlusBinary() {
        WaitableCodeLocationData signatureScanWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(signatureScanWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.SIGNATURE_SCAN);
        Set<String> successfulSigScanCodeLocationNames = new HashSet<>();
        successfulSigScanCodeLocationNames.add("sigScan1");
        successfulSigScanCodeLocationNames.add("sigScan2");
        Mockito.when(signatureScanWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulSigScanCodeLocationNames);

        WaitableCodeLocationData binaryWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(binaryWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.BINARY_SCAN);
        Set<String> successfulBinaryCodeLocationNames = new HashSet<>();
        successfulBinaryCodeLocationNames.add("binaryScan1");
        successfulBinaryCodeLocationNames.add("binaryScan2");
        successfulBinaryCodeLocationNames.add("binaryScan3");
        Mockito.when(binaryWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulBinaryCodeLocationNames);

        List<WaitableCodeLocationData> waitableCodeLocationDataList = Arrays.asList(
            signatureScanWaitableCodeLocationData,
            binaryWaitableCodeLocationData);

        ScanCountsPayloadCreator creator = new ScanCountsPayloadCreator();
        ScanCountsPayload payload = creator.create(waitableCodeLocationDataList);

        assertEquals(0, payload.getScanCounts().getPackageManager());
        assertEquals(2, payload.getScanCounts().getSignature());
        assertEquals(3, payload.getScanCounts().getBinary());
    }

    @Test
    void testAllPkgMgrTypesPlusIgnored() {
        WaitableCodeLocationData bazelWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(bazelWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.BAZEL);
        Set<String> successfulBazelCodeLocationNames = new HashSet<>();
        successfulBazelCodeLocationNames.add("bazelScan1");
        successfulBazelCodeLocationNames.add("bazelScan2");
        Mockito.when(bazelWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulBazelCodeLocationNames);

        WaitableCodeLocationData dockerWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(dockerWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.DOCKER);
        Set<String> successfulDockerCodeLocationNames = new HashSet<>();
        successfulDockerCodeLocationNames.add("dockerScan1");
        successfulDockerCodeLocationNames.add("dockerScan2");
        Mockito.when(dockerWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulDockerCodeLocationNames);

        WaitableCodeLocationData detectorWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(detectorWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.DETECTOR);
        Set<String> successfulDetectorCodeLocationNames = new HashSet<>();
        successfulDetectorCodeLocationNames.add("detectorScan1");
        successfulDetectorCodeLocationNames.add("detectorScan2");
        Mockito.when(detectorWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulDetectorCodeLocationNames);


        WaitableCodeLocationData iacWaitableCodeLocationData = Mockito.mock(WaitableCodeLocationData.class);
        Mockito.when(iacWaitableCodeLocationData.getDetectTool()).thenReturn(DetectTool.IAC_SCAN);
        Set<String> successfulIacCodeLocationNames = new HashSet<>();
        successfulIacCodeLocationNames.add("iacScan1");
        successfulIacCodeLocationNames.add("iacScan2");
        Mockito.when(iacWaitableCodeLocationData.getSuccessfulCodeLocationNames()).thenReturn(successfulIacCodeLocationNames);

        List<WaitableCodeLocationData> waitableCodeLocationDataList = Arrays.asList(
            bazelWaitableCodeLocationData,
            dockerWaitableCodeLocationData,
            detectorWaitableCodeLocationData,
            iacWaitableCodeLocationData);

        ScanCountsPayloadCreator creator = new ScanCountsPayloadCreator();
        ScanCountsPayload payload = creator.create(waitableCodeLocationDataList);

        assertEquals(6, payload.getScanCounts().getPackageManager());
        assertEquals(0, payload.getScanCounts().getSignature());
        assertEquals(0, payload.getScanCounts().getBinary());
    }
}
