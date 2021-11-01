/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.cache;

public class InstalledToolData {
    private InstalledTool tool;
    private String pathToTool;

    public InstalledToolData(InstalledTool tool, String pathToTool) {
        this.tool = tool;
        this.pathToTool = pathToTool;
    }

    public InstalledTool getTool() {
        return tool;
    }

    public String getPathToTool() {
        return pathToTool;
    }
}
