package com.blackducksoftware.integration.hub.detect.detector.go;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class GoVendorJsonData extends Stringable {
    private final String comment;
    private final String ignore;
    @SerializedName("package") private final List<GoVendorJsonPackageData> packages;
    private final String rootPath;

    public GoVendorJsonData(final String comment, final String ignore, final List<GoVendorJsonPackageData> packages, final String rootPath) {
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

    public List<GoVendorJsonPackageData> getPackages() {
        return packages;
    }

    public String getRootPath() {
        return rootPath;
    }
}
