package com.synopsys.integration.detectable.detectables.projectinspector.model;

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

    @SerializedName("MavenCoordinates")
    public ProjectInspectorMavenCoordinate mavenCoordinate;
}
