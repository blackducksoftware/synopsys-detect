/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conda.model;

import com.google.gson.annotations.SerializedName;

public class CondaListElement {
    @SerializedName("name")
    public String name;

    @SerializedName("version")
    public String version;

    @SerializedName("build_string")
    public String buildString;
}
