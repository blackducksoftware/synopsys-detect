package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DetectTool {
    private String type;
    private Map<String, String> toolProperties = new HashMap<>();
    private Set<String> scanTargets = new HashSet<>();

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Map<String, String> getToolProperties() {
        return toolProperties;
    }

    public void setToolProperties(final Map<String, String> toolProperties) {
        this.toolProperties = toolProperties;
    }

    public Set<String> getScanTargets() {
        return scanTargets;
    }

    public void setScanTargets(final Set<String> scanTargets) {
        this.scanTargets = scanTargets;
    }
}
