package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PackageLockPackage {
    @SerializedName("version")
    public String version;

    @SerializedName("dependencies")
    public Map<String, String> dependencies;
    
    // TODO version 3 no longer has a concept of nested dependencies, just prints them weirdly 
    // node_modules/x/node_modules/y
    public Map<String, PackageLockPackage> packages;
    
    // These fields are not in the JSON but are used internally by detect calculations
    public Boolean dev;

    public Boolean peer;
}
