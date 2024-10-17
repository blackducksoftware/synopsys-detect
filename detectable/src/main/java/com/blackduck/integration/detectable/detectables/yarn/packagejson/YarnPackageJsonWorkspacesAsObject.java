package com.blackduck.integration.detectable.detectables.yarn.packagejson;

import com.google.gson.annotations.SerializedName;
import com.blackduck.integration.detectable.detectables.npm.packagejson.model.YarnPackageJson;

public class YarnPackageJsonWorkspacesAsObject extends YarnPackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
