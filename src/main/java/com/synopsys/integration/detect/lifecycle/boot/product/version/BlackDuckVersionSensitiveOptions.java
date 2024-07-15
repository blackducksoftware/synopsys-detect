package com.synopsys.integration.detect.lifecycle.boot.product.version;

import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckVersionSensitiveOptions {
    private final DetectToolFilter detectToolFilter;
    private final BlackduckScanMode blackDuckScanMode;
    private final boolean isIntegratedMatchingEnabled;

    public BlackDuckVersionSensitiveOptions(final DetectToolFilter detectToolFilter, final BlackduckScanMode blackDuckScanMode, boolean isIntegratedMatchingEnabled) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckScanMode = blackDuckScanMode;
        this.isIntegratedMatchingEnabled = isIntegratedMatchingEnabled;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public BlackduckScanMode getBlackDuckScanMode() {
        return blackDuckScanMode;
    }

    public boolean isIsIntegratedMatchingEnabled() {
        return isIntegratedMatchingEnabled;
    }
}
