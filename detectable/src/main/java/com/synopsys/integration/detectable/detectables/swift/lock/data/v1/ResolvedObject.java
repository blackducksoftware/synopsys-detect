package com.synopsys.integration.detectable.detectables.swift.lock.data.v1;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;

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
