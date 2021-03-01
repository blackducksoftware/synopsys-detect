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

import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class LicenseList extends PolarisComponent {
    @SerializedName("meta")
    private PagedMeta meta = null;

    @SerializedName("licenses")
    private final List<License> licenses = null;

    /**
     * Get meta
     * @return meta
     */
    public PagedMeta getMeta() {
        return meta;
    }

    public void setMeta(final PagedMeta meta) {
        this.meta = meta;
    }

    /**
     * Get licenses
     * @return licenses
     */
    public List<License> getLicenses() {
        return licenses;
    }

}

