/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class PackageJsonReader {
    private final Gson gson;
    private final GsonBuilder gsonBuilder;

    public PackageJsonReader(GsonBuilder gsonBuilder, Gson gson) {
        this.gsonBuilder = gsonBuilder;
        this.gson = gson;
    }

    public PackageJson read(String packageJsonText) {
        return gson.fromJson(packageJsonText, PackageJson.class);
    }

    public List<String> extractWorkspaceDirPatterns(String packageJsonText) {
        Map<String, Object> packageJsonMap = gsonBuilder.create().fromJson(packageJsonText, Map.class);
        Object workspacesObject = packageJsonMap.get("workspaces");
        List<String> workspaceSubdirPatterns = new LinkedList<>();
        if (workspacesObject != null) {
            System.out.printf("workspacesObject type: %s\n", workspacesObject.getClass().getName());
            if (workspacesObject instanceof Map) {
                System.out.printf("workspacesObject is a Map\n");
                PackageJsonCurrent rootPackageJsonCurrent = gson.fromJson(packageJsonText, PackageJsonCurrent.class);
                // TODO pull workspaces out to a neutral format, like List<String>
                workspaceSubdirPatterns.addAll(rootPackageJsonCurrent.workspaces.workspaceSubdirPatterns);
            } else if (workspacesObject instanceof List) {
                System.out.printf("workspacesObject is a List\n");
                PackageJsonPreV1_5_0 rootPackageJsonPreV1_5_0 = gson.fromJson(packageJsonText, PackageJsonPreV1_5_0.class);
                // TODO pull workspaces out to a neutral format
                workspaceSubdirPatterns.addAll(rootPackageJsonPreV1_5_0.workspaceSubdirPatterns);
            } else {
                System.out.printf("workspacesObject is something I don't understand\n");
            }
        }
        return workspaceSubdirPatterns;
    }
}
