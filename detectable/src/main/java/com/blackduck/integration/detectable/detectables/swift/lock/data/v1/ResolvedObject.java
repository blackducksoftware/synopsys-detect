package com.blackduck.integration.detectable.detectables.swift.lock.data.v1;

import java.util.List;

import com.blackduck.integration.detectable.detectables.swift.lock.data.ResolvedPackage;
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
