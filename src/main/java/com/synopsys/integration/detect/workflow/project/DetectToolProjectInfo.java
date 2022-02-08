package com.synopsys.integration.detect.workflow.project;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.Stringable;

public class DetectToolProjectInfo extends Stringable {
    private final DetectTool detectTool;
    private final NameVersion suggestedNameVersion;

    public DetectToolProjectInfo(DetectTool detectTool, NameVersion suggestedNameVersion) {
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
