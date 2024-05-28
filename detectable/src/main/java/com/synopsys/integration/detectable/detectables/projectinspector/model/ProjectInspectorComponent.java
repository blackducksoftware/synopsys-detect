package com.synopsys.integration.detectable.detectables.projectinspector.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorComponent {

    public static class IncludedBy {
        @SerializedName("Id")
        public String id;

        @SerializedName("Description")
        public String description;
    }

    public static class ShadedBy {
        @SerializedName("Id")
        public String id;

        @SerializedName("Description")
        public String description;
    }

    @SerializedName("ShadedBy")
    public ShadedBy shadedBy;

    @SerializedName("IncludedBy")
    public List<IncludedBy> includedBy;

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

    @SerializedName("InclusionType")
    public String inclusionType;

    @SerializedName("ModuleFile")
    public String moduleFile;

    @SerializedName("Scope")
    public String scope;

    @SerializedName("Present")
    public String present;
}
