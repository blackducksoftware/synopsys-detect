package com.synopsys.integration.detectable.detectables.projectinspector.model;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorMavenCoordinate {
    @SerializedName("GroupId")
    public String group;

    @SerializedName("ArtifactId")
    public String artifact;

    @SerializedName("Version")
    public String version;
}
