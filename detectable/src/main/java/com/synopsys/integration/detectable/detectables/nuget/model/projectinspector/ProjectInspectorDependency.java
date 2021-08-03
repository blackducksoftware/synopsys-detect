/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget.model.projectinspector;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorDependency {
    @SerializedName("Id")
    public String id;

    @SerializedName("IncludedBy")
    public List<String> includedBy;

    @SerializedName("DependencyType")
    public String dependencyType;

    @SerializedName("DependencySource")
    public String dependencySource;

    @SerializedName("Name")
    public String name;

    @SerializedName("Version")
    public String version;

    @SerializedName("Artifacts")
    public List<String> artifacts;
}
