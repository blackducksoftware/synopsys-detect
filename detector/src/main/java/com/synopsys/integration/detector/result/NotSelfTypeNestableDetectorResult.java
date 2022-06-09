package com.synopsys.integration.detector.result;

import com.synopsys.integration.detector.base.DetectorType;

public class NotSelfTypeNestableDetectorResult extends FailedDetectorResult {
    public NotSelfTypeNestableDetectorResult(DetectorType detectorType) {
        super(String.format("Nestable but this detector type (%s) already applied in a parent directory.", detectorType));
    }
}
