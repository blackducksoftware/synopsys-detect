/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
