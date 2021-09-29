/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.projectinspector.model;

import com.google.gson.annotations.SerializedName;

public class ProjectInspectorMavenCoordinate {
    @SerializedName("GroupId")
    public String group;

    @SerializedName("ArtifactId")
    public String artifact;

    @SerializedName("Version")
    public String version;
}
