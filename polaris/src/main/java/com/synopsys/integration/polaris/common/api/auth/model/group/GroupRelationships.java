/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.group;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipMultiple;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class GroupRelationships extends PolarisRelationships {
    @SerializedName("ancestors")
    private PolarisRelationshipMultiple ancestors = null;

    @SerializedName("children")
    private PolarisRelationship children = null;

    @SerializedName("organization")
    private PolarisRelationshipSingle organization = null;

    @SerializedName("parent")
    private PolarisRelationshipSingle parent = null;

    /**
     * Get ancestors
     * @return ancestors
     */
    public PolarisRelationshipMultiple getAncestors() {
        return ancestors;
    }

    public void setAncestors(final PolarisRelationshipMultiple ancestors) {
        this.ancestors = ancestors;
    }

    /**
     * Get children
     * @return children
     */
    public PolarisRelationship getChildren() {
        return children;
    }

    public void setChildren(final PolarisRelationship children) {
        this.children = children;
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
     * Get parent
     * @return parent
     */
    public PolarisRelationshipSingle getParent() {
        return parent;
    }

    public void setParent(final PolarisRelationshipSingle parent) {
        this.parent = parent;
    }

}

