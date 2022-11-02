package com.synopsys.integration.detect.workflow.blackduck.integratedmatching.model;

import com.synopsys.integration.util.Stringable;

public class ScanCountsPayload extends Stringable {
    private final ScanCounts scanCounts;

    public ScanCountsPayload(ScanCounts scanCounts) {
        this.scanCounts = scanCounts;
    }

    public ScanCounts getScanCounts() {
        return scanCounts;
    }
}
