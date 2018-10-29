package com.blackducksoftware.integration.hub.detect.workflow.project;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.synopsys.integration.util.NameVersion;

public class DetectToolProjectInfo {
    private final DetectTool detectTool;
    private final NameVersion suggestedNameVersion;

    public DetectToolProjectInfo(final DetectTool detectTool, final NameVersion suggestedNameVersion) {
        this.detectTool = detectTool;
        this.suggestedNameVersion = suggestedNameVersion;
    }

    public DetectTool getDetectTool() {
        return detectTool;
    }

    public NameVersion getSuggestedNameVersion() {
        return suggestedNameVersion;
    }
}
