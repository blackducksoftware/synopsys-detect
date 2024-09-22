package com.blackduck.integration.detect.lifecycle.run.data;

import com.blackduck.integration.detect.util.filter.DetectToolFilter;

public class ProductRunData {
    private final BlackDuckRunData blackDuckRunData;
    private final DetectToolFilter detectToolFilter;

    public ProductRunData(BlackDuckRunData blackDuckRunData, DetectToolFilter detectToolFilter) {
        this.blackDuckRunData = blackDuckRunData;
        this.detectToolFilter = detectToolFilter;
    }

    public BlackDuckRunData getBlackDuckRunData() {
        return blackDuckRunData;
    }

    public boolean shouldUseBlackDuckProduct() {
        return blackDuckRunData != null;
    }

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }
}
