package com.synopsys.integration.detect.lifecycle.boot.product.version;

import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckVersionSensitiveOptions {
    private final DetectToolFilter detectToolFilter;
    private final BlackduckScanMode blackDuckScanMode;
    private final boolean integratedMatchingEnabled;

    public BlackDuckVersionSensitiveOptions(final DetectToolFilter detectToolFilter, final BlackduckScanMode blackDuckScanMode, boolean integratedMatchingEnabled) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckScanMode = blackDuckScanMode;
        this.integratedMatchingEnabled = integratedMatchingEnabled;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public BlackduckScanMode getBlackDuckScanMode() {
        return blackDuckScanMode;
    }

    public boolean isIntegratedMatchingEnabled() {
        return integratedMatchingEnabled;
    }
}
