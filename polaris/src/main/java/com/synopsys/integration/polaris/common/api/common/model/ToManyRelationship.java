/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ToManyRelationship extends PolarisComponent {
    @SerializedName("data")
    private List<ObjectContainer> data = new ArrayList<>();

    public ToManyRelationship addDataItem(final ObjectContainer dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * @return data
     */
    public List<ObjectContainer> getData() {
        return data;
    }

    public void setData(final List<ObjectContainer> data) {
        this.data = data;
    }

}

