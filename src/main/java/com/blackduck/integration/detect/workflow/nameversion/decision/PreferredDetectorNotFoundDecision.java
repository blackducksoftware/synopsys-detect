package com.blackduck.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.blackduck.integration.detector.base.DetectorType;

public class PreferredDetectorNotFoundDecision extends NameVersionDecision {
    private final DetectorType detectorType;

    public PreferredDetectorNotFoundDecision(DetectorType detectorType) {
        this.detectorType = detectorType;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    @Override
    public void printDescription(Logger logger) {
        logger.debug(String.format("A detector of type %s was not found. Project info could not be found in a detector.", detectorType.name()));
    }
}
