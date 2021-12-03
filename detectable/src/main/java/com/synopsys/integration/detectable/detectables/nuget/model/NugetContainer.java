package com.synopsys.integration.detectable.detectables.nuget.model;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.annotations.SerializedName;

public class NugetContainer {
    @SerializedName("Name")
    public String name;

    @SerializedName("Version")
    public String version;

    @SerializedName("Type")
    public NugetContainerType type;

    @SerializedName("SourcePath")
    public String sourcePath;

    @SerializedName("OutputPaths")
    public List<String> outputPaths;

    @SerializedName("Packages")
    public List<NugetPackageSet> packages;

    @SerializedName("Dependencies")
    public List<NugetPackageId> dependencies;

    @SerializedName("Children")
    public List<NugetContainer> children;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
