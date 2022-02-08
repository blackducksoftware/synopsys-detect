package com.synopsys.integration.detectable.detectables.nuget.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.annotations.SerializedName;

public class NugetInspection {
    @SerializedName("Name")
    public String name;

    @SerializedName("Version")
    public String version;

    @SerializedName("Containers")
    public List<NugetContainer> containers = new ArrayList<>();

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
