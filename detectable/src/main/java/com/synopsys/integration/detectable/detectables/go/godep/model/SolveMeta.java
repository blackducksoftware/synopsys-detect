/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.godep.model;

import com.google.gson.annotations.SerializedName;

public class SolveMeta {
    @SerializedName("inputs-digest")
    public String inputsDigest;

    @SerializedName("analyzer-name")
    public String analyzerName;

    @SerializedName("analyzer-version")
    public Integer analyzerVersion;

    @SerializedName("solver-name")
    public String solverName;

    @SerializedName("solver-version")
    public Integer solverVersion;
}
