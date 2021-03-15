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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class PackageJsonReader {
    public static final String WORKSPACES_OBJECT_KEY = "workspaces";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    public PackageJsonReader(Gson gson) {
        this.gson = gson;
    }

    public PackageJson read(String packageJsonText) {
        return gson.fromJson(packageJsonText, PackageJson.class);
    }

    public List<String> extractWorkspaceDirPatterns(String packageJsonText) {
        Map<String, Object> packageJsonMap = gson.fromJson(packageJsonText, Map.class);
        // TODO: alternative: pass it a TypeAdapter
        Object workspacesObject = packageJsonMap.get(WORKSPACES_OBJECT_KEY);
        List<String> workspaceSubdirPatterns = new LinkedList<>();
        if (workspacesObject != null) {
            logger.trace("workspacesObject type: {}", workspacesObject.getClass().getName());
            if (workspacesObject instanceof Map) {
                logger.trace("workspacesObject is a Map");
                PackageJsonCurrent rootPackageJsonCurrent = gson.fromJson(packageJsonText, PackageJsonCurrent.class);
                workspaceSubdirPatterns.addAll(rootPackageJsonCurrent.workspaces.workspaceSubdirPatterns);
            } else if (workspacesObject instanceof List) {
                logger.trace("workspacesObject is a List");
                PackageJsonPreV1_5_0 rootPackageJsonPreV1_5_0 = gson.fromJson(packageJsonText, PackageJsonPreV1_5_0.class);
                workspaceSubdirPatterns.addAll(rootPackageJsonPreV1_5_0.workspaceSubdirPatterns);
            } else {
                logger.warn("package.json 'workspaces' object is an unrecognized format; workspace declarations will be ignored");
            }
        }
        return workspaceSubdirPatterns;
    }
}
