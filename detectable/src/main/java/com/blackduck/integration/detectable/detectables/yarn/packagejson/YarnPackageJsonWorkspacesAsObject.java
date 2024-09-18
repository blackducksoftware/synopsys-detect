package com.blackduck.integration.detectable.detectables.yarn.packagejson;

import com.blackduck.integration.detectable.detectables.npm.packagejson.model.YarnPackageJson;
import com.google.gson.annotations.SerializedName;

public class YarnPackageJsonWorkspacesAsObject extends YarnPackageJson {

    @SerializedName("workspaces")
    public Workspaces workspaces;
}
