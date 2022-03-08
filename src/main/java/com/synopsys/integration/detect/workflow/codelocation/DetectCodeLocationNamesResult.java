package com.synopsys.integration.detect.workflow.codelocation;

import java.util.Map;

public class DetectCodeLocationNamesResult {
    private final Map<DetectCodeLocation, String> codeLocationNames;

    public DetectCodeLocationNamesResult(Map<DetectCodeLocation, String> codeLocationNames) {
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
