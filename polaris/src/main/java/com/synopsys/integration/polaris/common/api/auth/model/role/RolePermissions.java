/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.role;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

public class RolePermissions extends PolarisComponent {
    @SerializedName("ORGANIZATION")
    private List<String> organization;
    @SerializedName("PROJECT")
    private List<String> project;

    public List<String> getOrganization() {
        return organization;
    }

    public void setOrganization(final List<String> organization) {
        this.organization = organization;
    }

    public List<String> getProject() {
        return project;
    }

    public void setProject(final List<String> project) {
        this.project = project;
    }

}
