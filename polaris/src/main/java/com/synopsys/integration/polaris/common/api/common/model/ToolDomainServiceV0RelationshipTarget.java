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

public class ToolDomainServiceV0RelationshipTarget extends PolarisComponent {
    @SerializedName("type")
    private String type = "tool-domain-service";

    @SerializedName("id")
    private String id;

    /**
     * &#x60;Automatic&#x60; &#x60;Non-null&#x60; The literal string &#x60;tool-domain-service&#x60;.
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    /**
     * &#x60;Non-null&#x60; &#x60;Required&#x60; The ID of this tool domain service.
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

}

