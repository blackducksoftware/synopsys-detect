/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public VendorJson(final String comment, final String ignore, final List<PackageData> packages, final String rootPath) {
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
