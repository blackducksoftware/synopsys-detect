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

public class InstalledToolManager {
    public static final String INSTALLED_TOOL_FILE_NAME = "detect-installed-tools.json";

    private final String jsonFileFormatVersion = "0.1.0";

    private Map<String, String> installedTools = new HashMap<>();

    public InstalledToolData getInstalledToolData() {
        InstalledToolData installedToolData = new InstalledToolData();
        installedToolData.toolData = installedTools;
        installedToolData.version = jsonFileFormatVersion;
        return installedToolData;
    }

    public void addPreExistingInstallData(InstalledToolData preExistingInstallData) {
        preExistingInstallData.toolData.forEach((tool, installPath) -> installedTools.putIfAbsent(tool, installPath));
    }

    public void saveInstalledToolLocation(String toolKey, String location) {
        installedTools.put(toolKey, location);
    }
}
