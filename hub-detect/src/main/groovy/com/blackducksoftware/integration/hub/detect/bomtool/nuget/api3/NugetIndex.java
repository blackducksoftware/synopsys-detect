package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NugetIndex {
    @SerializedName("version")
    private String version;

    @SerializedName("resource")
    private List<NugetResource> resources;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public List<NugetResource> getResources() {
        return resources;
    }

    public void setResources(final List<NugetResource> resources) {
        this.resources = resources;
    }
}
