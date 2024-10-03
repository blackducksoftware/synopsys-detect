package com.blackduck.integration.detect.lifecycle.boot.product.version;

import com.blackduck.integration.detect.util.filter.DetectToolFilter;
import com.blackduck.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckVersionSensitiveOptions {
    private final DetectToolFilter detectToolFilter;
    private final BlackduckScanMode blackDuckScanMode;
    private final boolean correlatedScanningEnabled;

    public BlackDuckVersionSensitiveOptions(final DetectToolFilter detectToolFilter, final BlackduckScanMode blackDuckScanMode, boolean correlatedScanningEnabled) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckScanMode = blackDuckScanMode;
        this.correlatedScanningEnabled = correlatedScanningEnabled;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public BlackduckScanMode getBlackDuckScanMode() {
        return blackDuckScanMode;
    }

    public boolean isCorrelatedScanningEnabled() {
        return correlatedScanningEnabled;
    }
}
