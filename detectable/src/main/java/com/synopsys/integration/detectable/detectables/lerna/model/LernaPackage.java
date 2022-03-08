package com.synopsys.integration.detectable.detectables.lerna.model;

import com.google.gson.annotations.SerializedName;

public class LernaPackage {
    @SerializedName("name")
    private final String name;

    @SerializedName("version")
    private final String version;

    @SerializedName("private")
    private final boolean isPrivate;

    @SerializedName("location")
    private final String location;

    public LernaPackage(String name, String version, boolean isPrivate, String location) {
        this.name = name;
        this.version = version;
        this.isPrivate = isPrivate;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getLocation() {
        return location;
    }
}
