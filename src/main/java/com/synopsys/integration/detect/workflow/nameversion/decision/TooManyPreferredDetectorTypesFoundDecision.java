/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.synopsys.integration.detector.base.DetectorType;

public class TooManyPreferredDetectorTypesFoundDecision extends NameVersionDecision {
    private final DetectorType detectorType;

    public TooManyPreferredDetectorTypesFoundDecision(final DetectorType detectorType) {
        this.detectorType = detectorType;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.debug(String.format("More than one preferred detector of type %s was found. Project info could not be found in a detector.", detectorType.name()));
    }
}
