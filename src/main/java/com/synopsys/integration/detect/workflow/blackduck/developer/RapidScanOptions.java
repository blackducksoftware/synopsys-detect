package com.synopsys.integration.detect.workflow.blackduck.developer;

import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;

public class RapidScanOptions {
    private final RapidCompareMode compareMode;

    public RapidScanOptions(RapidCompareMode compareMode) {
        this.compareMode = compareMode;
    }

    public RapidCompareMode getCompareMode() {
        return compareMode;
    }
}
