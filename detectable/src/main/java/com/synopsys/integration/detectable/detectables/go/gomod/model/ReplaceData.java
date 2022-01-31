package com.synopsys.integration.detectable.detectables.go.gomod.model;

import com.google.gson.annotations.SerializedName;

public class ReplaceData {
    @SerializedName("Path")
    private String path;

    @SerializedName("Version")
    private String version;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
