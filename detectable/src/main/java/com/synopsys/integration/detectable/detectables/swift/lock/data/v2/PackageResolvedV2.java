package com.synopsys.integration.detectable.detectables.swift.lock.data.v2;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedBase;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;

public class PackageResolvedV2 extends PackageResolvedBase {
    @SerializedName("pins")
    private final List<ResolvedPackage> packages;

    public PackageResolvedV2(String fileFormatVersion, List<ResolvedPackage> packages) {
        super(fileFormatVersion);
        this.packages = packages;
    }

    public List<ResolvedPackage> getPackages() {
        return packages;
    }
}
