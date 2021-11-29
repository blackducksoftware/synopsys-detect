/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.xcode.model;

import com.google.gson.annotations.SerializedName;

public class PackageResolved {
    @SerializedName("object")
    private final ResolvedObject resolvedObject;

    @SerializedName("version")
    private final String packageResolvedVersion;

    public PackageResolved(ResolvedObject resolvedObject, String packageResolvedVersion) {
        this.resolvedObject = resolvedObject;
        this.packageResolvedVersion = packageResolvedVersion;
    }

    public ResolvedObject getResolvedObject() {
        return resolvedObject;
    }

    public String getPackageResolvedVersion() {
        return packageResolvedVersion;
    }
}
