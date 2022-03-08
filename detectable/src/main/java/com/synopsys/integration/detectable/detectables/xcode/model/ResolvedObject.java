package com.synopsys.integration.detectable.detectables.xcode.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResolvedObject {
    @SerializedName("pins")
    private final List<ResolvedPackage> packages;

    public ResolvedObject(List<ResolvedPackage> packages) {
        this.packages = packages;
    }

    public List<ResolvedPackage> getPackages() {
        return packages;
    }
}
