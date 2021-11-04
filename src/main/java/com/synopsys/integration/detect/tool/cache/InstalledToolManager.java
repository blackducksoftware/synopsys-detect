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
    private Map<InstalledTool, String> installedTools = new HashMap<>();

    public Map<InstalledTool, String> getInstalledTools() {
        return installedTools;
    }

    public void saveInstalledToolLocation(InstalledTool tool, String location) {
        installedTools.put(tool, location);
    }
}
