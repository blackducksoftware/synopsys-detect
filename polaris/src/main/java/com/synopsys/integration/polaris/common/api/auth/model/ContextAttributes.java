/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ContextAttributes extends PolarisAttributes {
    @SerializedName("current")
    private Boolean current;

    @SerializedName("organizationname")
    private String organizationname;

    @SerializedName("username")
    private String username;

    /**
     * Get current
     * @return current
     */
    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(final Boolean current) {
        this.current = current;
    }

    /**
     * Get organizationname
     * @return organizationname
     */
    public String getOrganizationname() {
        return organizationname;
    }

    public void setOrganizationname(final String organizationname) {
        this.organizationname = organizationname;
    }

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

}

