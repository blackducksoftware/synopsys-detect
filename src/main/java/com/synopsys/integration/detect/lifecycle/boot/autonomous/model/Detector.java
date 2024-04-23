package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Detector {
    private String type;
    private Map<String, String> detectorProperties = new HashMap<>();
    private Set<String> scanTargets = new HashSet<>();

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Map<String, String> getDetectorProperties() {
        return detectorProperties;
    }

    public void setDetectorProperties(final Map<String, String> detectorProperties) {
        this.detectorProperties = detectorProperties;
    }

    public Set<String> getScanTargets() {
        return scanTargets;
    }

    public void setScanTargets(final Set<String> scanTargets) {
        this.scanTargets = scanTargets;
    }
}
