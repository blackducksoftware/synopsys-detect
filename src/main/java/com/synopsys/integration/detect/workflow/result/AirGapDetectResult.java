/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.result;

import java.util.Collections;
import java.util.List;

public class AirGapDetectResult implements DetectResult {
    private final String airGapFolder;

    public AirGapDetectResult(String airGapFolder) {
        this.airGapFolder = airGapFolder;
    }

    @Override
    public String getResultLocation() {
        return airGapFolder;
    }

    @Override
    public String getResultMessage() {
        return String.format("Detect Air Gap Zip: %s", airGapFolder);
    }

    @Override
    public List<String> getResultSubMessages() {
        return Collections.emptyList();
    }
}
