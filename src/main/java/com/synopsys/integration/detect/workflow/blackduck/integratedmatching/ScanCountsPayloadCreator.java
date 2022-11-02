package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.WaitableCodeLocationData;
import com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model.ScanCounts;
import com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model.ScanCountsPayload;

public class ScanCountsPayloadCreator {

    public ScanCountsPayload create(List<WaitableCodeLocationData> createdCodelocations) {
        Map<DetectTool, Integer> countsByTool = new HashMap<>();
        for (WaitableCodeLocationData waitableCodeLocationData : createdCodelocations) {
            int oldCount = countsByTool.getOrDefault(waitableCodeLocationData.getDetectTool(), 0);
            int newCount = oldCount + waitableCodeLocationData.getSuccessfulCodeLocationNames().size();
            countsByTool.put(waitableCodeLocationData.getDetectTool(), newCount);
        }
        int packageManagerScanCount = countsByTool.getOrDefault(DetectTool.DETECTOR, 0)
            + countsByTool.getOrDefault(DetectTool.BAZEL, 0)
            + countsByTool.getOrDefault(DetectTool.DOCKER, 0);
        int signatureScanCount = countsByTool.getOrDefault(DetectTool.SIGNATURE_SCAN, 0);
        int binaryScanCount = countsByTool.getOrDefault(DetectTool.BINARY_SCAN, 0);
        ScanCounts scanCounts = new ScanCounts(packageManagerScanCount, signatureScanCount, binaryScanCount);
        return new ScanCountsPayload(scanCounts);
    }
}
