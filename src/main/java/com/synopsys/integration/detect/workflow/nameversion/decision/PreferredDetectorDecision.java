package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

import com.synopsys.integration.detect.workflow.nameversion.DetectorProjectInfo;

public class PreferredDetectorDecision extends NameVersionDecision {
    private final DetectorProjectInfo detectorProjectInfo;

    public PreferredDetectorDecision(DetectorProjectInfo detectorProjectInfo) {
        super(detectorProjectInfo.getNameVersion());
        this.detectorProjectInfo = detectorProjectInfo;
    }

    public DetectorProjectInfo getDetectorType() {
        return detectorProjectInfo;
    }

    @Override
    public void printDescription(Logger logger) {
        logger.debug(String.format(
            "Using preferred bom tool project info from %s found at depth %d as project info.",
            detectorProjectInfo.getDetectorType().name(),
            detectorProjectInfo.getDepth()
        ));
    }
}
