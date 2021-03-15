/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class EntityTag extends PolarisComponent {
    @SerializedName("value")
    private String value;

    @SerializedName("weak")
    private Boolean weak;

    /**
     * Get value
     * @return value
     */
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Get weak
     * @return weak
     */
    public Boolean getWeak() {
        return weak;
    }

    public void setWeak(final Boolean weak) {
        this.weak = weak;
    }

}

