/*
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

public class CountV0 extends PolarisComponent {
    @SerializedName("type")
    private String type = "count";

    @SerializedName("id")
    private String id;

    @SerializedName("attributes")
    private CountV0Attributes attributes = null;

    @SerializedName("relationships")
    private CountV0Relationships relationships = null;

    /**
     * &#x60;Automatic&#x60;, &#x60;Non-null&#x60;. The literal-string &#x60;count&#x60;.
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    /**
     * &#x60;Automatic&#x60;, &#x60;Non-null&#x60;. Transient, non-durable id.
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get attributes
     * @return attributes
     */
    public CountV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final CountV0Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Get relationships
     * @return relationships
     */
    public CountV0Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final CountV0Relationships relationships) {
        this.relationships = relationships;
    }

}

