/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class PackageJsonCurrent extends PackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
