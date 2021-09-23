/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

public class AirGapTypeDecider {
    public AirGapType decide(DetectArgumentState detectArgumentState) {
        AirGapType airGapType = AirGapType.FULL;
        if (detectArgumentState.getParsedValue() != null && detectArgumentState.getParsedValue().toLowerCase().equals("no_docker")) {
            airGapType = AirGapType.NO_DOCKER;
        }
        return airGapType;
    }
}
