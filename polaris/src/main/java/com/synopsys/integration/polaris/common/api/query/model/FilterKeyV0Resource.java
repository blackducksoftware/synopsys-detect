/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class FilterKeyV0Resource extends PolarisComponent {
    @SerializedName("data")
    private List<FilterKeyV0> data = null;

    @SerializedName("meta")
    private PagedMetaV0 meta = null;

    public FilterKeyV0Resource addDataItem(final FilterKeyV0 dataItem) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * @return data
     */
    public List<FilterKeyV0> getData() {
        return data;
    }

    public void setData(final List<FilterKeyV0> data) {
        this.data = data;
    }

    /**
     * Get meta
     * @return meta
     */
    public PagedMetaV0 getMeta() {
        return meta;
    }

    public void setMeta(final PagedMetaV0 meta) {
        this.meta = meta;
    }

}

