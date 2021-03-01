/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class IncludedRelationsParams extends PolarisComponent {
    @SerializedName("params")
    private List<Inclusion> params = null;

    public IncludedRelationsParams addParamsItem(final Inclusion paramsItem) {
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.params.add(paramsItem);
        return this;
    }

    /**
     * Get params
     * @return params
     */
    public List<Inclusion> getParams() {
        return params;
    }

    public void setParams(final List<Inclusion> params) {
        this.params = params;
    }

}

