/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisResource;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class JobResource extends PolarisResource<JobAttributes, JobRelationships> {
    @SerializedName("data")
    private Job data = null;

    /**
     * Get data
     * @return data
     */
    public Job getData() {
        return data;
    }

    public void setData(Job data) {
        this.data = data;
    }

}
