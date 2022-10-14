package com.synopsys.integration.detect.workflow.blackduck.developer;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;

public class RapidScanOptions {
    private final RapidCompareMode compareMode;
    private final BlackduckScanMode scanMode;

    public RapidScanOptions(RapidCompareMode compareMode, BlackduckScanMode scanMode) {
        this.compareMode = compareMode;
        this.scanMode = scanMode;
    }

    public RapidCompareMode getCompareMode() {
        return compareMode;
    }

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }
}
