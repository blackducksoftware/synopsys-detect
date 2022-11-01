package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.util.Stringable;

public class ScanCountsPayload extends Stringable {
    private ScanCounts scanCounts;

    @NotNull
    public static ScanCountsPayload createFromCountsByTool(final Map<DetectTool, Integer> countsByTool) {
        int packageManagerScanCount = countsByTool.getOrDefault(DetectTool.DETECTOR, 0)
            + countsByTool.getOrDefault(DetectTool.BAZEL, 0)
            + countsByTool.getOrDefault(DetectTool.DOCKER, 0);
        int signatureScanCount = countsByTool.getOrDefault(DetectTool.SIGNATURE_SCAN, 0);
        int binaryScanCount = countsByTool.getOrDefault(DetectTool.BINARY_SCAN, 0);
        ScanCounts scanCounts = new ScanCounts(packageManagerScanCount, signatureScanCount, binaryScanCount);
        return new ScanCountsPayload(scanCounts);
    }

    private ScanCountsPayload(final ScanCounts scanCounts) {
        this.scanCounts = scanCounts;
    }

    public ScanCounts getScanCounts() {
        return scanCounts;
    }
}
