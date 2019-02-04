package com.blackducksoftware.integration.hub.detect.detectables;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public class DetectableDetectorResult extends DetectorResult {
    private final DetectableResult detectableResult;

    public DetectableDetectorResult(DetectableResult detectableResult){
        this.detectableResult = detectableResult;
    }

    @Override
    public boolean getPassed() {
        return detectableResult.getPassed();
    }

    @Override
    public String toDescription() {
        return detectableResult.toDescription();
    }
}

