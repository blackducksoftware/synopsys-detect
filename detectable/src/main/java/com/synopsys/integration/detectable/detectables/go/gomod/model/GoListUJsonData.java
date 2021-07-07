/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.model;

import com.google.gson.annotations.SerializedName;

public class GoListUJsonData {
    @SerializedName("Path")
    private String path;

    @SerializedName("Version")
    private String version;

    @SerializedName("Replace")
    private ReplaceData replace;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ReplaceData getReplace() {
        return replace;
    }

    public void setReplace(ReplaceData replace) {
        this.replace = replace;
    }
}
