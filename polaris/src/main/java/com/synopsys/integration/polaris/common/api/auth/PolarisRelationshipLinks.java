/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

public class PolarisRelationshipLinks extends PolarisComponent {
    @SerializedName("self")
    private String self;
    @SerializedName("related")
    private String related;

    public String getSelf() {
        return self;
    }

    public void setSelf(final String self) {
        this.self = self;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(final String related) {
        this.related = related;
    }

}
