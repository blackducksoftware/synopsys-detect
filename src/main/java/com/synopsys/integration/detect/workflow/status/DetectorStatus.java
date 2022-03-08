package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorStatus extends Status {
    public DetectorStatus(DetectorType detectorType, StatusType statusType) {
        super(detectorType.toString(), statusType);
    }
}
