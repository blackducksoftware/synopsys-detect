/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion.decision;

import org.slf4j.Logger;

public class UniqueDetectorNotFoundDecision extends NameVersionDecision {
    @Override
    public void printDescription(final Logger logger) {
        logger.debug("No unique detector was found. Project info could not be found in a detector.");
    }
}
