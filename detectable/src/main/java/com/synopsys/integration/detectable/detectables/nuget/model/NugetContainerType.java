/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget.model;

import com.google.gson.annotations.SerializedName;

public enum NugetContainerType {
    @SerializedName("Solution")
    SOLUTION,
    @SerializedName("Project")
    PROJECT
}
