package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

class ScanCountsPayloadTest {

    @Test
    void testDetectorSignature() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.DETECTOR, 1);
        countsByTool.put(DetectTool.SIGNATURE_SCAN, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(1, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(1, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(0, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testDetectorSignatureBinary() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.DETECTOR, 1);
        countsByTool.put(DetectTool.SIGNATURE_SCAN, 1);
        countsByTool.put(DetectTool.BINARY_SCAN, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(1, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(1, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(1, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testBazel() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.BAZEL, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(1, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(0, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(0, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testBazelDetector() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.BAZEL, 1);
        countsByTool.put(DetectTool.DETECTOR, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(2, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(0, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(0, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testBazelDetectorOther() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.BAZEL, 1);
        countsByTool.put(DetectTool.DETECTOR, 1);
        countsByTool.put(DetectTool.IAC_SCAN, 1);
        countsByTool.put(DetectTool.IMPACT_ANALYSIS, 1);
        countsByTool.put(DetectTool.SIGNATURE_SCAN, 1);
        countsByTool.put(DetectTool.BINARY_SCAN, 1);
        countsByTool.put(DetectTool.DOCKER, 0);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(2, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(1, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(1, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testBazelDocker() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.BAZEL, 1);
        countsByTool.put(DetectTool.DOCKER, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(2, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(0, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(0, scanCountsPayload.getScanCounts().getBinary());
    }

    @Test
    void testDetectorBazelDocker() {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        countsByTool.put(DetectTool.BAZEL, 1);
        countsByTool.put(DetectTool.DETECTOR, 1);
        countsByTool.put(DetectTool.DOCKER, 1);
        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);

        assertEquals(3, scanCountsPayload.getScanCounts().getPackageManager());
        assertEquals(0, scanCountsPayload.getScanCounts().getSignature());
        assertEquals(0, scanCountsPayload.getScanCounts().getBinary());
    }

}
