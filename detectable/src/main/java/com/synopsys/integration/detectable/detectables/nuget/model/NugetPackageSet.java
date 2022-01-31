package com.synopsys.integration.detectable.detectables.nuget.model;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.annotations.SerializedName;

public class NugetPackageSet {
    @SerializedName("PackageId")
    public NugetPackageId packageId;

    @SerializedName("Dependencies")
    public List<NugetPackageId> dependencies;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
