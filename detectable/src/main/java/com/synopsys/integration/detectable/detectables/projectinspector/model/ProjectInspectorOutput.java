package com.synopsys.integration.detectable.detectables.projectinspector.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorOutput {
    @SerializedName("Dir")
    public String directory;

    @SerializedName("Modules")
    public Map<String, ProjectInspectorModule> modules;
}
