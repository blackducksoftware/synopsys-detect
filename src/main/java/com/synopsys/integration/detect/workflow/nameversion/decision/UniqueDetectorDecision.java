package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;

public class UniqueDetectorDecision extends NameVersionDecision {
    private final DetectorProjectInfo detectorProjectInfo;

    public UniqueDetectorDecision(DetectorProjectInfo detectorProjectInfo) {
        super(detectorProjectInfo.getNameVersion());
        this.detectorProjectInfo = detectorProjectInfo;
    }

    public DetectorProjectInfo getDetectorType() {
        return detectorProjectInfo;
    }

    @Override
    public void printDescription(Logger logger) {
        logger.debug(String.format(
            "Exactly one unique detector was found. Using %s found at depth %d as project info.",
            detectorProjectInfo.getDetectorType().name(),
            detectorProjectInfo.getDepth()
        ));
    }
}
