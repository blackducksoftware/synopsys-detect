/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisResourcesPagination;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RunPropertyV0Resources extends PolarisComponent {
    @SerializedName("data")
    private List<RunPropertyV0> data = new ArrayList<>();

    @SerializedName("links")
    private LinksPagination links = null;

    @SerializedName("meta")
    private PolarisResourcesPagination meta = null;

    public RunPropertyV0Resources addDataItem(final RunPropertyV0 dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * @return data
     */
    public List<RunPropertyV0> getData() {
        return data;
    }

    public void setData(final List<RunPropertyV0> data) {
        this.data = data;
    }

    /**
     * Get links
     * @return links
     */
    public LinksPagination getLinks() {
        return links;
    }

    public void setLinks(final LinksPagination links) {
        this.links = links;
    }

    /**
     * Get meta
     * @return meta
     */
    public PolarisResourcesPagination getMeta() {
        return meta;
    }

    public void setMeta(final PolarisResourcesPagination meta) {
        this.meta = meta;
    }

}

