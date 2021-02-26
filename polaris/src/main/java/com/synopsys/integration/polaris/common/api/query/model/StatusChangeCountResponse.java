/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class StatusChangeCountResponse extends PolarisComponent {
    @SerializedName("count")
    private Integer count;

    @SerializedName("transition-date")
    private String transitionDate;

    /**
     * The number of issues that changed to the requested status in this 24-hour period.
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    /**
     * The date-time that begins the 24-hour period that this count is for. In ISO UTC.
     * @return transitionDate
     */
    public String getTransitionDate() {
        return transitionDate;
    }

    public void setTransitionDate(final String transitionDate) {
        this.transitionDate = transitionDate;
    }

}

