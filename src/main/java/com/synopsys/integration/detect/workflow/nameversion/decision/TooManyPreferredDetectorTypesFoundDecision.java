package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.synopsys.integration.detector.base.DetectorType;

public class TooManyPreferredDetectorTypesFoundDecision extends NameVersionDecision {
    private final DetectorType detectorType;

    public TooManyPreferredDetectorTypesFoundDecision(DetectorType detectorType) {
        this.detectorType = detectorType;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    @Override
    public void printDescription(Logger logger) {
        logger.debug(String.format("More than one preferred detector of type %s was found. Project info could not be found in a detector.", detectorType.name()));
    }
}
