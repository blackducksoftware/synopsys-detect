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

public class JobLogResource extends PolarisResource<JobLogAttributes, JobLogRelationships> {
    @SerializedName("data")
    private JobLog data = null;

    /**
     * Get data
     * @return data
     */
    public JobLog getData() {
        return data;
    }

    public void setData(JobLog data) {
        this.data = data;
    }

}
