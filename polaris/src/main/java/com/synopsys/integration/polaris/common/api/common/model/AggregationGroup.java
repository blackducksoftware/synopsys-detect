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

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class AggregationGroup extends PolarisComponent {
    @SerializedName("group")
    private Object group = null;

    @SerializedName("result")
    private Object result = null;

    /**
     * Get group
     * @return group
     */
    public Object getGroup() {
        return group;
    }

    public void setGroup(final Object group) {
        this.group = group;
    }

    /**
     * Get result
     * @return result
     */
    public Object getResult() {
        return result;
    }

    public void setResult(final Object result) {
        this.result = result;
    }

}

