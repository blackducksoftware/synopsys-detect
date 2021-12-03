package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class YarnPackageJsonWorkspacesAsList extends PackageJson {

    @SerializedName("workspaces")
    public List<String> workspaceSubdirPatterns = new LinkedList<>();
}
