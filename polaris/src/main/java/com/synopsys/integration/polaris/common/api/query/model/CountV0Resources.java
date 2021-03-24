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

public class CountV0Resources extends PolarisComponent {
    @SerializedName("data")
    private List<CountV0> data = new ArrayList<>();

    @SerializedName("included")
    private List<JsonApiIncludedResource> included = null;

    @SerializedName("meta")
    private PagedQueryMetaV0 meta = null;

    public CountV0Resources addDataItem(final CountV0 dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * @return data
     */
    public List<CountV0> getData() {
        return data;
    }

    public void setData(final List<CountV0> data) {
        this.data = data;
    }

    public CountV0Resources addIncludedItem(final JsonApiIncludedResource includedItem) {
        if (this.included == null) {
            this.included = new ArrayList<>();
        }
        this.included.add(includedItem);
        return this;
    }

    /**
     * Get included
     * @return included
     */
    public List<JsonApiIncludedResource> getIncluded() {
        return included;
    }

    public void setIncluded(final List<JsonApiIncludedResource> included) {
        this.included = included;
    }

    /**
     * Get meta
     * @return meta
     */
    public PagedQueryMetaV0 getMeta() {
        return meta;
    }

    public void setMeta(final PagedQueryMetaV0 meta) {
        this.meta = meta;
    }

}

