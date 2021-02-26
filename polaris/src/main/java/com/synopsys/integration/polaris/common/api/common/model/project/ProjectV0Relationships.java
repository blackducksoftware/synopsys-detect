/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model.project;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class ProjectV0Relationships extends PolarisRelationships {
    @SerializedName("branches")
    private PolarisRelationship branches = null;

    @SerializedName("runs")
    private PolarisRelationship runs = null;

    /**
     * Get branches
     * @return branches
     */
    public PolarisRelationship getBranches() {
        return branches;
    }

    public void setBranches(final PolarisRelationship branches) {
        this.branches = branches;
    }

    /**
     * Get runs
     * @return runs
     */
    public PolarisRelationship getRuns() {
        return runs;
    }

    public void setRuns(final PolarisRelationship runs) {
        this.runs = runs;
    }

}

