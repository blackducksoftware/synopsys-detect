package com.blackduck.integration.detect.workflow.project;

import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import com.blackduck.integration.util.NameVersion;
import com.blackduck.integration.util.Stringable;

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
