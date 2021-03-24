/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import com.synopsys.integration.detect.workflow.result.DetectResult;

public class RapidScanDetectResult implements DetectResult {
    public static final String RAPID_SCAN_RESULT_HEADING = "Rapid Scan Result";
    public static final String RAPID_SCAN_RESULT_DETAILS_HEADING = "Rapid Scan Result Details";
    private final String jsonFilePath;
    private final List<String> subMessages;

    public RapidScanDetectResult(String jsonFilePath, List<String> subMessages) {
        this.jsonFilePath = jsonFilePath;
        this.subMessages = subMessages;
    }

    @Override
    public String getResultLocation() {
        return jsonFilePath;
    }

    @Override
    public String getResultMessage() {
        return String.format("%s: (for more detail look in the log for %s)", RAPID_SCAN_RESULT_HEADING, RAPID_SCAN_RESULT_DETAILS_HEADING);
    }

    @Override
    public List<String> getResultSubMessages() {
        return subMessages;
    }
}
