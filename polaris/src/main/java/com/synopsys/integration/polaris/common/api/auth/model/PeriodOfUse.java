/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class PeriodOfUse extends PolarisComponent {
    @SerializedName("start")
    private OffsetDateTime start;

    @SerializedName("end")
    private OffsetDateTime end;

    /**
     * Get start
     * @return start
     */
    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(final OffsetDateTime start) {
        this.start = start;
    }

    /**
     * Get end
     * @return end
     */
    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(final OffsetDateTime end) {
        this.end = end;
    }

}

