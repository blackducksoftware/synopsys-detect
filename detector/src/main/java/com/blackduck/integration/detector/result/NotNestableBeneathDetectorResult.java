package com.blackduck.integration.detector.result;

import com.blackduck.integration.detector.base.DetectorType;

public class NotNestableBeneathDetectorResult extends FailedDetectorResult {
    public NotNestableBeneathDetectorResult(DetectorType theType) {
        super("Not nestable below a detector of type: " + theType.toString());
    }
}
