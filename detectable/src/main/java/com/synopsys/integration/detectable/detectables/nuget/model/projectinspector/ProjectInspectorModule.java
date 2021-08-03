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

public class ProjectInspectorModule {
    @SerializedName("ModuleFile")
    public String moduleFile;

    @SerializedName("ModuleDir")
    public String moduleDirectory;

    @SerializedName("Strategy")
    public String strategy;

    @SerializedName("Dependencies")
    public List<ProjectInspectorDependency> dependencies;
}
