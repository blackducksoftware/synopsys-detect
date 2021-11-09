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
    private final InstalledToolFileData installedToolFileData;

    private Map<String, String> installedTools = new HashMap<>();

    public InstalledToolManager(InstalledToolFileData installedToolFileData) {
        this.installedToolFileData = installedToolFileData;
    }

    public Map<String, String> getInstalledTools() {
        return installedTools;
    }

    public void addPreExistingInstallData(Map<String, String> preExistingInstallData) {
        preExistingInstallData.forEach((tool, installPath) -> installedTools.putIfAbsent(tool, installPath));
    }

    public void saveInstalledToolLocation(InstalledTool tool, String location) {
        installedTools.put(installedToolFileData.getToolJsonKey(tool), location);
    }
}
