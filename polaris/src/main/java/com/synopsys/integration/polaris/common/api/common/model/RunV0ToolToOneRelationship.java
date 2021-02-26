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

public class RunV0ToolToOneRelationship extends PolarisComponent {
    @SerializedName("links")
    private JsonApiRelationshipLinks links = null;

    @SerializedName("data")
    private ToolV0RelationshipTarget data = null;

    /**
     * Get links
     * @return links
     */
    public JsonApiRelationshipLinks getLinks() {
        return links;
    }

    public void setLinks(final JsonApiRelationshipLinks links) {
        this.links = links;
    }

    /**
     * Get data
     * @return data
     */
    public ToolV0RelationshipTarget getData() {
        return data;
    }

    public void setData(final ToolV0RelationshipTarget data) {
        this.data = data;
    }

}

