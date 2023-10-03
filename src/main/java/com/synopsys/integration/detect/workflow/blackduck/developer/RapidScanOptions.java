package com.synopsys.integration.detect.workflow.blackduck.developer;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;

public class RapidScanOptions {
    private final RapidCompareMode compareMode;
    private final BlackduckScanMode scanMode;
    private final long detectTimeout;

    public RapidScanOptions(RapidCompareMode compareMode, BlackduckScanMode scanMode, long detectTimeout) {
        this.compareMode = compareMode;
        this.scanMode = scanMode;
        this.detectTimeout = detectTimeout;
    }

    public RapidCompareMode getCompareMode() {
        return compareMode;
    }

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }

    public long getDetectTimeout() {
        return detectTimeout;
    }
}
