package com.blackduck.integration.detectable.detectables.yarn.packagejson;

import java.util.LinkedList;
import java.util.List;

import com.blackduck.integration.detectable.detectables.npm.packagejson.model.YarnPackageJson;
import com.google.gson.annotations.SerializedName;

public class YarnPackageJsonWorkspacesAsList extends YarnPackageJson {

    @SerializedName("workspaces")
    public List<String> workspaceSubdirPatterns = new LinkedList<>();
}
