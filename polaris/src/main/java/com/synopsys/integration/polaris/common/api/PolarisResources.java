/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PolarisResources<R extends PolarisResource> extends PolarisResponse {
    @SerializedName("data")
    private List<R> data = null;

    /**
     * Get data
     * @return data
     */
    public List<R> getData() {
        return data;
    }

    public void setData(final List<R> data) {
        this.data = data;
    }

}
