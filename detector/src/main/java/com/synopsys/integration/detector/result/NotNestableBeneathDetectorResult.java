package com.synopsys.integration.detector.result;

import com.synopsys.integration.detector.base.DetectorType;

public class NotNestableBeneathDetectorResult extends FailedDetectorResult {
    public NotNestableBeneathDetectorResult(DetectorType theType) {
        super("Not nestable below a detector of type: " + theType.toString());
    }
}
