/*
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

public class JsonApiRelationshipLinks extends PolarisComponent {
    @SerializedName("self")
    private String self;

    @SerializedName("related")
    private String related;

    /**
     * Get self
     * @return self
     */
    public String getSelf() {
        return self;
    }

    public void setSelf(final String self) {
        this.self = self;
    }

    /**
     * Get related
     * @return related
     */
    public String getRelated() {
        return related;
    }

    public void setRelated(final String related) {
        this.related = related;
    }

}

