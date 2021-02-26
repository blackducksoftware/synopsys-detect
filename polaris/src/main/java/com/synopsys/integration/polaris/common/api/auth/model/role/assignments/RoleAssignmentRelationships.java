/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.role.assignments;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class RoleAssignmentRelationships extends PolarisRelationships {
    @SerializedName("group")
    private PolarisRelationshipSingle group = null;

    @SerializedName("organization")
    private PolarisRelationshipSingle organization = null;

    @SerializedName("role")
    private PolarisRelationshipSingle role = null;

    @SerializedName("user")
    private PolarisRelationshipSingle user = null;

    /**
     * Get group
     * @return group
     */
    public PolarisRelationshipSingle getGroup() {
        return group;
    }

    public void setGroup(final PolarisRelationshipSingle group) {
        this.group = group;
    }

    /**
     * Get organization
     * @return organization
     */
    public PolarisRelationshipSingle getOrganization() {
        return organization;
    }

    public void setOrganization(final PolarisRelationshipSingle organization) {
        this.organization = organization;
    }

    /**
     * Get role
     * @return role
     */
    public PolarisRelationshipSingle getRole() {
        return role;
    }

    public void setRole(final PolarisRelationshipSingle role) {
        this.role = role;
    }

    /**
     * Get user
     * @return user
     */
    public PolarisRelationshipSingle getUser() {
        return user;
    }

    public void setUser(final PolarisRelationshipSingle user) {
        this.user = user;
    }

}

