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
