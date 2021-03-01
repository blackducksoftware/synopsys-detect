/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model.branch;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class BranchV0Attributes extends PolarisAttributes {
    @SerializedName("name")
    private String name;

    @SerializedName("main-for-project")
    private Boolean mainForProject;

    /**
     * &#x60;Mutable&#x60;
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * &#x60;Mutable&#x60;
     * @return mainForProject
     */
    public Boolean getMainForProject() {
        return mainForProject;
    }

    public void setMainForProject(final Boolean mainForProject) {
        this.mainForProject = mainForProject;
    }

}

