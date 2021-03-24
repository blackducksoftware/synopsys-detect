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

public class JsonPath extends PolarisComponent {
    @SerializedName("elementName")
    private String elementName;

    @SerializedName("ids")
    private PathIds ids = null;

    @SerializedName("parentResource")
    private JsonPath parentResource = null;

    @SerializedName("childResource")
    private JsonPath childResource = null;

    @SerializedName("collection")
    private Boolean collection;

    @SerializedName("resourceName")
    private String resourceName;

    /**
     * Get elementName
     * @return elementName
     */
    public String getElementName() {
        return elementName;
    }

    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    /**
     * Get ids
     * @return ids
     */
    public PathIds getIds() {
        return ids;
    }

    public void setIds(final PathIds ids) {
        this.ids = ids;
    }

    /**
     * Get parentResource
     * @return parentResource
     */
    public JsonPath getParentResource() {
        return parentResource;
    }

    public void setParentResource(final JsonPath parentResource) {
        this.parentResource = parentResource;
    }

    /**
     * Get childResource
     * @return childResource
     */
    public JsonPath getChildResource() {
        return childResource;
    }

    public void setChildResource(final JsonPath childResource) {
        this.childResource = childResource;
    }

    /**
     * Get collection
     * @return collection
     */
    public Boolean getCollection() {
        return collection;
    }

    public void setCollection(final Boolean collection) {
        this.collection = collection;
    }

    /**
     * Get resourceName
     * @return resourceName
     */
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

}

