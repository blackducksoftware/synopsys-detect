/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.xcode.model;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class PackageState {
    @SerializedName("branch")
    @Nullable
    private final String branch;

    @SerializedName("revision")
    private final String revision;

    @SerializedName("version")
    private final String version;

    public PackageState(@Nullable String branch, String revision, String version) {
        this.branch = branch;
        this.revision = revision;
        this.version = version;
    }

    @Nullable
    public String getBranch() {
        return branch;
    }

    public String getRevision() {
        return revision;
    }

    public String getVersion() {
        return version;
    }
}
