/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RevisionV0Included extends JsonApiIncludedResource {
    @SerializedName("attributes")
    private RevisionV0Attributes attributes = null;

    /**
     * Get attributes
     * @return attributes
     */
    public RevisionV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final RevisionV0Attributes attributes) {
        this.attributes = attributes;
    }

}

