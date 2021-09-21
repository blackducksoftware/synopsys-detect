/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.projectinspector.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorOutput {
    @SerializedName("Dir")
    public String directory;

    @SerializedName("Modules")
    public Map<String, ProjectInspectorModule> modules;
}
