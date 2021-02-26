/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class PackageLockDependency {
    @SerializedName("version")
    public String version;

    @SerializedName("dev")
    public Boolean dev;

    @SerializedName("requires")
    public Map<String, String> requires;

    @SerializedName("dependencies")
    public Map<String, PackageLockDependency> dependencies;

}
