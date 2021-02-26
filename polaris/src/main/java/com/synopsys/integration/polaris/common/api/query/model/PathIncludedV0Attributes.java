/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class PathIncludedV0Attributes extends PolarisComponent {
    @SerializedName("path")
    private List<String> path = null;

    @SerializedName("path-type")
    private String pathType;

    @SerializedName("is-leaf")
    private Boolean isLeaf;

    public PathIncludedV0Attributes addPathItem(final String pathItem) {
        if (this.path == null) {
            this.path = new ArrayList<>();
        }
        this.path.add(pathItem);
        return this;
    }

    /**
     * The path as an array of string path elements.
     * @return path
     */
    public List<String> getPath() {
        return path;
    }

    public void setPath(final List<String> path) {
        this.path = path;
    }

    /**
     * The path type (ex. directory, packaged, etc...)
     * @return pathType
     */
    public String getPathType() {
        return pathType;
    }

    public void setPathType(final String pathType) {
        this.pathType = pathType;
    }

    /**
     * Will be set to true if this path is a leaf
     * @return isLeaf
     */
    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(final Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

}

