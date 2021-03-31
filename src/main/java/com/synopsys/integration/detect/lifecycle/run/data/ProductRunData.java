/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import com.synopsys.integration.detect.util.filter.DetectToolFilter;

public class ProductRunData {
    private final PolarisRunData polarisRunData;
    private final BlackDuckRunData blackDuckRunData;
    private final DetectToolFilter detectToolFilter;

    public ProductRunData(final PolarisRunData polarisRunData, final BlackDuckRunData blackDuckRunData, final DetectToolFilter detectToolFilter) {
        this.polarisRunData = polarisRunData;
        this.blackDuckRunData = blackDuckRunData;
        this.detectToolFilter = detectToolFilter;
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

    public DetectToolFilter getDetectToolFilter() {
        return detectToolFilter;
    }
}
