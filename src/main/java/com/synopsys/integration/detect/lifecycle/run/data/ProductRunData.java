package com.synopsys.integration.detect.lifecycle.run.data;

import com.synopsys.integration.detect.util.filter.DetectToolFilter;

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
