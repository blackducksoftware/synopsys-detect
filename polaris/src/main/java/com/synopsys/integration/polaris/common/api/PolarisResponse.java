/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public abstract class PolarisResponse extends PolarisComponent {
    @SerializedName("included")
    private List<PolarisResourceSparse> included = null;
    @SerializedName("meta")
    private PolarisResourcesPagination meta = null;

    public List<PolarisResourceSparse> getIncluded() {
        return included;
    }

    public void setIncluded(final List<PolarisResourceSparse> included) {
        this.included = included;
    }

    public PolarisResourcesPagination getMeta() {
        return meta;
    }

    public void setMeta(final PolarisResourcesPagination meta) {
        this.meta = meta;
    }

}
