package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

public class UniqueDetectorNotFoundDecision extends NameVersionDecision {
    @Override
    public void printDescription(final Logger logger) {
        logger.debug("No unique detector was found. Project info could not be found in a detector.");
    }
}
