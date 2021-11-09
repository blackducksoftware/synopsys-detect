/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.cache;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class InstalledToolData {
    @SerializedName("version")
    public String version;
    @SerializedName("tools")
    public Map<String, String> toolData;

}
