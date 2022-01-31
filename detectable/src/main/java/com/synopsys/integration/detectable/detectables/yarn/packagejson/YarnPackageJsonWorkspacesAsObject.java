package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class YarnPackageJsonWorkspacesAsObject extends PackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
