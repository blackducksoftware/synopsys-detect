package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PackageLockPackage {
    @SerializedName("version")
    public String version;

    @SerializedName("dependencies")
    public Map<String, String> dependencies;
    
    // These fields are not in the JSON but are used internally by detect calculations
    public Boolean dev;

    public Boolean peer;

    public Map<String, PackageLockPackage> detectDependencies;
}
