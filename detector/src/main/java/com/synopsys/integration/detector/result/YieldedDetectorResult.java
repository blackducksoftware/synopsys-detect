/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class YieldedDetectorResult extends FailedDetectorResult {
    public YieldedDetectorResult(final Set<String> yieldedTo) {
        super(String.format("Yielded to detectors: %s", StringUtils.join(yieldedTo, ",")), YieldedDetectorResult.class);
    }
}
