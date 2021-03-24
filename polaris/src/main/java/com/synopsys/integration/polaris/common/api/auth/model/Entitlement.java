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

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Entitlement extends PolarisComponent {
    @SerializedName("attributes")
    private EntitlementAttributes attributes = null;

    @SerializedName("id")
    private String id;

    @SerializedName("relationships")
    private EntitlementRelationships relationships = null;

    @SerializedName("type")
    private String type;

    /**
     * Get attributes
     * @return attributes
     */
    public EntitlementAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final EntitlementAttributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Get id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get relationships
     * @return relationships
     */
    public EntitlementRelationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final EntitlementRelationships relationships) {
        this.relationships = relationships;
    }

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}

