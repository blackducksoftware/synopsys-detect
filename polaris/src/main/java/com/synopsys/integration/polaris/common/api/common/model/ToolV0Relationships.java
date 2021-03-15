/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ToolV0Relationships extends PolarisComponent {
    @SerializedName("runs")
    private JsonApiLazyRelationship runs = null;

    /**
     * Get runs
     * @return runs
     */
    public JsonApiLazyRelationship getRuns() {
        return runs;
    }

    public void setRuns(final JsonApiLazyRelationship runs) {
        this.runs = runs;
    }

}

