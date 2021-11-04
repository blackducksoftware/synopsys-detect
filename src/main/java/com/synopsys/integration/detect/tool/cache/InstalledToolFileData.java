/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.cache;

import java.util.HashMap;
import java.util.Map;

public class InstalledToolFileData {
    public static final String INSTALLED_TOOL_FILE_NAME = "detect-installed-tools.json";
    private Map<InstalledTool, String> installedToolJsonKeyMap;

    public InstalledToolFileData() {
        Map<InstalledTool, String> map = new HashMap<>();

        map.put(InstalledTool.DOCKER_INSPECTOR, "docker-inspector");
        map.put(InstalledTool.NUGET_INSPECTOR, "nuget-inspector");
        map.put(InstalledTool.PROJECT_INSPECTOR, "project-inspector");

        this.installedToolJsonKeyMap = map;
    }

    public String getToolJsonKey(InstalledTool tool) {
        return installedToolJsonKeyMap.get(tool);
    }
}
