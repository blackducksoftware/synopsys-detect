package com.synopsys.integration.detectable.detectables.projectinspector.model;

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
