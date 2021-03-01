/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.auth.PolarisRelationshipLinks;

public class PolarisRelationship extends PolarisComponent {
    @SerializedName("links")
    private PolarisRelationshipLinks links;

    public PolarisRelationshipLinks getLinks() {
        return links;
    }

    public void setLinks(final PolarisRelationshipLinks links) {
        this.links = links;
    }

}
