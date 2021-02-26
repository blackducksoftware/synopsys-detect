/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.role;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class RoleAttributes extends PolarisAttributes {
    public static final String ROLE_ADMINISTRATOR = "Administrator";
    public static final String ROLE_CONTRIBUTOR = "Contributor";

    @SerializedName("permissions")
    private RolePermissions permissions;
    @SerializedName("rolename")
    private String rolename;

    /**
     * Get permissions
     * @return permissions
     */
    public RolePermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(final RolePermissions permissions) {
        this.permissions = permissions;
    }

    /**
     * Get rolename
     * @return rolename
     */
    public String getRolename() {
        return rolename;
    }

    public void setRolename(final String rolename) {
        this.rolename = rolename;
    }

}

