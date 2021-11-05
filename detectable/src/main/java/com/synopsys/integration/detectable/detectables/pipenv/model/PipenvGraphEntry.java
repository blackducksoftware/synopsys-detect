/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pipenv.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PipenvGraphEntry {
    @SerializedName("package_name")
    private final String name;
    @SerializedName("installed_version")
    private final String version;
    @SerializedName("dependencies")
    private final List<PipenvGraphDependency> children;

    public PipenvGraphEntry(String name, String version, List<PipenvGraphDependency> children) {
        this.name = name;
        this.version = version;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<PipenvGraphDependency> getChildren() {
        return children;
    }
}
