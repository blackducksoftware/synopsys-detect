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

// TODO give this class a more descriptive name (describe how workspace is represented, not version)
//// also: this is Yarn specific, so include that in the name
public class PackageJsonCurrent extends PackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
