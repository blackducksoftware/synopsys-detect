package com.synopsys.integration.detect.lifecycle.boot.product.version;

import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class BlackDuckVersionSensitiveOptions {
    private final DetectToolFilter detectToolFilter;
    private final BlackduckScanMode blackDuckScanMode;

    public BlackDuckVersionSensitiveOptions(final DetectToolFilter detectToolFilter, final BlackduckScanMode blackDuckScanMode) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckScanMode = blackDuckScanMode;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }

    public BlackduckScanMode getBlackDuckScanMode() {
        return blackDuckScanMode;
    }
}
