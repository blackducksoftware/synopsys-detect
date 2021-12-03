package com.synopsys.integration.detectable.detectables.xcode.model;

import com.google.gson.annotations.SerializedName;

public class ResolvedPackage {
    @SerializedName("package")
    private final String packageName;

    @SerializedName("repositoryURL")
    private final String repositoryURL;

    @SerializedName("state")
    private final PackageState packageState;

    public ResolvedPackage(String packageName, String repositoryURL, PackageState packageState) {
        this.packageName = packageName;
        this.repositoryURL = repositoryURL;
        this.packageState = packageState;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public PackageState getPackageState() {
        return packageState;
    }
}
