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

public class MicrosoftLoginAttributes extends PolarisComponent {
    @SerializedName("date-created")
    private OffsetDateTime dateCreated;

    @SerializedName("microsoft-id")
    private String microsoftId;

    /**
     * Get dateCreated
     * @return dateCreated
     */
    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Get microsoftId
     * @return microsoftId
     */
    public String getMicrosoftId() {
        return microsoftId;
    }

    public void setMicrosoftId(final String microsoftId) {
        this.microsoftId = microsoftId;
    }

}

