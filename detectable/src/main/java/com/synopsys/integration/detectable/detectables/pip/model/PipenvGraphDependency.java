/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PipenvGraphDependency {
    @SerializedName("package_name")
    private final String name;
    @SerializedName("installed_version")
    private final String installedVersion;
    @SerializedName("dependencies")
    private final List<PipenvGraphDependency> children;

    public PipenvGraphDependency(final String name, final String installedVersion, final List<PipenvGraphDependency> children) {
        this.name = name;
        this.installedVersion = installedVersion;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public List<PipenvGraphDependency> getChildren() {
        return children;
    }
}
