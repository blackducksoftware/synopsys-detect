/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Inclusion extends PolarisComponent {
    @SerializedName("path")
    private String path;

    @SerializedName("pathList")
    private List<String> pathList = null;

    /**
     * Get path
     * @return path
     */
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public Inclusion addPathListItem(final String pathListItem) {
        if (this.pathList == null) {
            this.pathList = new ArrayList<>();
        }
        this.pathList.add(pathListItem);
        return this;
    }

    /**
     * Get pathList
     * @return pathList
     */
    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(final List<String> pathList) {
        this.pathList = pathList;
    }

}

