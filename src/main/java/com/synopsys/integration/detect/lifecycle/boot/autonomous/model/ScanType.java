package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScanType {
    private String scanType;
    private Map<String, String> scanProperties = new HashMap<>();
    private Set<String> scanTargets = new HashSet<>();

    public String getScanType() {
        return scanType;
    }

    public void setScanType(final String scanType) {
        this.scanType = scanType;
    }

    public Map<String, String> getScanProperties() {
        return scanProperties;
    }

    public void setScanProperties(final Map<String, String> scanProperties) {
        this.scanProperties = scanProperties;
    }

    public Set<String> getScanTargets() {
        return scanTargets;
    }

    public void setScanTargets(final Set<String> scanTargets) {
        this.scanTargets = scanTargets;
    }
}
