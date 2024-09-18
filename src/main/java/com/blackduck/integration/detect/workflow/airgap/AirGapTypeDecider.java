package com.blackduck.integration.detect.workflow.airgap;

import com.blackduck.integration.detect.configuration.help.DetectArgumentState;

public class AirGapTypeDecider {
    public AirGapType decide(DetectArgumentState detectArgumentState) {
        AirGapType airGapType = AirGapType.FULL;
        if (detectArgumentState.getParsedValue() != null && detectArgumentState.getParsedValue().equalsIgnoreCase("no_docker")) {
            airGapType = AirGapType.NO_DOCKER;
        }
        return airGapType;
    }
}
