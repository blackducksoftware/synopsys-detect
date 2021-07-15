/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorStatus extends Status {
    public DetectorStatus(DetectorType detectorType, StatusType statusType) {
        super(detectorType.toString(), statusType);
    }
}
