package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class WorkspacePackageJsons {

    public static Map<String, PackageJson> toPackageJsons(Map<String, WorkspacePackageJson> workspacePackageJsons) {
        Map<String, PackageJson> packageJsons = new HashMap<>(workspacePackageJsons.size());
        for (Map.Entry<String, WorkspacePackageJson> entry : workspacePackageJsons.entrySet()) {
            packageJsons.put(entry.getKey(), entry.getValue().getPackageJson());
        }
        return packageJsons;
    }
}
