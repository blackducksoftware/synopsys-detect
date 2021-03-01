/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;

public class PreferredDetectorDecision extends NameVersionDecision {
    private final DetectorProjectInfo detectorProjectInfo;

    public PreferredDetectorDecision(final DetectorProjectInfo detectorProjectInfo) {
        super(detectorProjectInfo.getNameVersion());
        this.detectorProjectInfo = detectorProjectInfo;
    }

    public DetectorProjectInfo getDetectorType() {
        return detectorProjectInfo;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.debug(String.format("Using preferred bom tool project info from %s found at depth %d as project info.", detectorProjectInfo.getDetectorType().name(), detectorProjectInfo.getDepth()));
    }
}
