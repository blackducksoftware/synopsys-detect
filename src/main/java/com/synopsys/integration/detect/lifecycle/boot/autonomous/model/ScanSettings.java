package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScanSettings {
    Map<String, String> globalProperties = new HashMap<>();
    Set<ScanType> scanTypes = new HashSet<>();

    public Map<String, String> getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(final Map<String, String> globalProperties) {
        this.globalProperties = globalProperties;
    }

    public Set<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(final Set<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}
