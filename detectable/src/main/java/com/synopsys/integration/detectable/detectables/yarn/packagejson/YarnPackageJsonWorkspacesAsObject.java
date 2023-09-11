package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.YarnPackageJson;

public class YarnPackageJsonWorkspacesAsObject extends YarnPackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
