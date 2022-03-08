package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.util.HashMap;
import java.util.Map;

public class WorkspacePackageJsons {

    private WorkspacePackageJsons() {}

    public static Map<String, NullSafePackageJson> toPackageJsons(Map<String, WorkspacePackageJson> workspacePackageJsons) {
        Map<String, NullSafePackageJson> packageJsons = new HashMap<>(workspacePackageJsons.size());
        for (Map.Entry<String, WorkspacePackageJson> entry : workspacePackageJsons.entrySet()) {
            packageJsons.put(entry.getKey(), entry.getValue().getPackageJson());
        }
        return packageJsons;
    }
}
