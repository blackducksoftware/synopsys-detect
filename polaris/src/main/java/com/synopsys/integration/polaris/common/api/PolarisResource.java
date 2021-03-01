/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.auth.PolarisResourceLinks;

public class PolarisResource<A extends PolarisAttributes, R extends PolarisRelationships> extends PolarisComponent {
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("attributes")
    private A attributes = null;
    @SerializedName("relationships")
    private R relationships = null;
    @SerializedName("links")
    private PolarisResourceLinks links;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public A getAttributes() {
        return attributes;
    }

    public void setAttributes(final A attributes) {
        this.attributes = attributes;
    }

    public R getRelationships() {
        return relationships;
    }

    public void setRelationships(final R relationships) {
        this.relationships = relationships;
    }

    public PolarisResourceLinks getLinks() {
        return links;
    }

    public void setLinks(final PolarisResourceLinks links) {
        this.links = links;
    }

}
