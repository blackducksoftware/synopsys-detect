package com.synopsys.integration.detectable.detectables.go.vendor.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class VendorJson extends Stringable {
    private final String comment;
    private final String ignore;
    @SerializedName("package")
    private final List<PackageData> packages;
    private final String rootPath;

    public VendorJson(String comment, String ignore, List<PackageData> packages, String rootPath) {
        this.comment = comment;
        this.ignore = ignore;
        this.packages = packages;
        this.rootPath = rootPath;
    }

    public String getComment() {
        return comment;
    }

    public String getIgnore() {
        return ignore;
    }

    public List<PackageData> getPackages() {
        return packages;
    }

    public String getRootPath() {
        return rootPath;
    }
}
