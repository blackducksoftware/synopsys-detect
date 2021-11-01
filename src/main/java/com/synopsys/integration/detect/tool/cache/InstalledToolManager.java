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

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class InstalledToolManager {
    private Map<InstalledTool, String> installedTools = new HashMap<>();

    public InstalledToolManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.InstalledTool, toolData -> installedTools.put(toolData.getTool(), toolData.getPathToTool()));
    }

    public Map<InstalledTool, String> getInstalledTools() {
        return installedTools;
    }

    public String getPathToTool(InstalledTool tool) {
        return installedTools.get(tool);
    }
}
