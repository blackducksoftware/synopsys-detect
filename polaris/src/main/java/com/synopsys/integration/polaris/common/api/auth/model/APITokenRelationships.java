/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;
import com.synopsys.integration.polaris.common.api.common.model.ToOneRelationship;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class APITokenRelationships extends PolarisComponent {
    @SerializedName("owner")
    private ToOneRelationship owner = null;

    /**
     * Get owner
     * @return owner
     */
    public ToOneRelationship getOwner() {
        return owner;
    }

    public void setOwner(final ToOneRelationship owner) {
        this.owner = owner;
    }

}

