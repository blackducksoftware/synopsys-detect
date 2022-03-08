package com.synopsys.integration.detector.result;

import java.util.Collections;

public class ForcedNestedPassedDetectorResult extends PassedDetectorResult {
    public ForcedNestedPassedDetectorResult() {
        super("Forced to pass because nested forced by user.", ForcedNestedPassedDetectorResult.class, Collections.emptyList(), Collections.emptyList());
    }
}
