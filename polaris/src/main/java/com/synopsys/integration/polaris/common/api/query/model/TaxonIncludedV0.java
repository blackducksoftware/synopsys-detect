/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class TaxonIncludedV0 extends JsonApiIncludedResource {
    @SerializedName("attributes")
    private TaxonIncludedV0Attributes attributes = null;

    /**
     * Get attributes
     * @return attributes
     */
    public TaxonIncludedV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final TaxonIncludedV0Attributes attributes) {
        this.attributes = attributes;
    }

}

