/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;

public class FailedDetectorResult extends DetectorResult {
    public FailedDetectorResult(@NotNull final String description, final Class resultClass) {
        super(false, description, resultClass, Collections.emptyList(), Collections.emptyList());
    }
}
