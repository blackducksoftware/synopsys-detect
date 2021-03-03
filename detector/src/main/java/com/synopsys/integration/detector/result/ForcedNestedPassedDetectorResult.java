/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

import java.util.Collections;

public class ForcedNestedPassedDetectorResult extends PassedDetectorResult {
    public ForcedNestedPassedDetectorResult() {
        super("Forced to pass because nested forced by user.", ForcedNestedPassedDetectorResult.class, Collections.emptyList(), Collections.emptyList());
    }
}
