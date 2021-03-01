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

public class SamlIdentityProviderResources extends PolarisComponent {
    @SerializedName("data")
    private List<SamlIdentityProvider> data = null;

    public SamlIdentityProviderResources addDataItem(final SamlIdentityProvider dataItem) {
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
    public List<SamlIdentityProvider> getData() {
        return data;
    }

    public void setData(final List<SamlIdentityProvider> data) {
        this.data = data;
    }

}

