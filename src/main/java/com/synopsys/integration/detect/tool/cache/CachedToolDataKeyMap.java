package com.synopsys.integration.detect.tool.cache;

import java.util.HashMap;
import java.util.Map;

public class CachedToolDataKeyMap {
    private Map<InstalledTool, String> map;

    public CachedToolDataKeyMap() {
        Map<InstalledTool, String> map = new HashMap<>();

        map.put(InstalledTool.DOCKER_INSPECTOR, "docker-inspector");
        map.put(InstalledTool.NUGET_INSPECTOR, "nuget-inspector");
        map.put(InstalledTool.PROJECT_INSPECTOR, "project-inspector");
        map.put(InstalledTool.SIGNATURE_SCANNER, "signature-scanner");

        this.map = map;
    }

    public String getKey(InstalledTool tool) {
        return map.get(tool);
    }
}
