/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.role.assignments;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class RoleAssignmentAttributes extends PolarisAttributes {
    @SerializedName("expires-by")
    private String expiresBy;

    @SerializedName("object")
    private String object;

    /**
     * Get expiresBy
     * @return expiresBy
     */
    public String getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(final String expiresBy) {
        this.expiresBy = expiresBy;
    }

    /**
     * Get object
     * @return object
     */
    public String getObject() {
        return object;
    }

    public void setObject(final String object) {
        this.object = object;
    }

}

