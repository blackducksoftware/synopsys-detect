/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class ToolV0 extends ObjectContainer {
    @SerializedName("attributes")
    private ToolV0Attributes attributes = null;

    @SerializedName("relationships")
    private ToolV0Relationships relationships = null;

    @SerializedName("links")
    private JsonApiSelfLinks links = null;

    @SerializedName("meta")
    private MetaWithOrganizationTrash meta = null;

    /**
     * Get attributes
     * @return attributes
     */
    public ToolV0Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final ToolV0Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Get relationships
     * @return relationships
     */
    public ToolV0Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(final ToolV0Relationships relationships) {
        this.relationships = relationships;
    }

    /**
     * Get links
     * @return links
     */
    public JsonApiSelfLinks getLinks() {
        return links;
    }

    public void setLinks(final JsonApiSelfLinks links) {
        this.links = links;
    }

    /**
     * Get meta
     * @return meta
     */
    public MetaWithOrganizationTrash getMeta() {
        return meta;
    }

    public void setMeta(final MetaWithOrganizationTrash meta) {
        this.meta = meta;
    }

}

