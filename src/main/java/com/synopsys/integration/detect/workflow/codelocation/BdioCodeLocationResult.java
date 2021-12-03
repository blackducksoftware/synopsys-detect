package com.synopsys.integration.detect.workflow.codelocation;

import java.util.List;
import java.util.Map;

public class BdioCodeLocationResult {
    private final List<BdioCodeLocation> bdioCodeLocations;
    private final Map<DetectCodeLocation, String> codeLocationNames;

    public BdioCodeLocationResult(List<BdioCodeLocation> bdioCodeLocations, Map<DetectCodeLocation, String> codeLocationNames) {
        this.bdioCodeLocations = bdioCodeLocations;
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }

    public List<BdioCodeLocation> getBdioCodeLocations() {
        return bdioCodeLocations;
    }
}
