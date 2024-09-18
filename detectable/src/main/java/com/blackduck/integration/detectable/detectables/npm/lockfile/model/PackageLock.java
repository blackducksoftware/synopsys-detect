package com.blackduck.integration.detectable.detectables.npm.lockfile.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PackageLock {
    @SerializedName("name")
    public String name;

    @SerializedName("version")
    public String version;
    
    @SerializedName("packages")
    public Map<String, PackageLockPackage> packages;
}
