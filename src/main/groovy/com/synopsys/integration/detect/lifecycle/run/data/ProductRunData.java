package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

public class ProductRunData {
    private final PolarisRunData polarisRunData;
    private final BlackDuckRunData blackDuckRunData;

    public ProductRunData(final PolarisRunData polarisRunData, final BlackDuckRunData blackDuckRunData) {
        this.polarisRunData = polarisRunData;
        this.blackDuckRunData = blackDuckRunData;
    }

    public PolarisRunData getPolarisRunData() {
        return polarisRunData;
    }

    public BlackDuckRunData getBlackDuckRunData() {
        return blackDuckRunData;
    }

    public boolean shouldUseBlackDuckProduct() {
        return blackDuckRunData != null;
    }

    public boolean shouldUsePolarisProduct() {
        return polarisRunData != null;
    }
}
