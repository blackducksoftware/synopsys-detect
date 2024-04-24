package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScanSettings {
    Map<String, String> globalDetectProperties = new HashMap<>();
    Map<String, String> detectorSharedProperties = new HashMap<>();
    Set<ScanType> scanTypes = new HashSet<>();

    public Map<String, String> getGlobalDetectProperties() {
        return globalDetectProperties;
    }

    public void setGlobalDetectProperties(final Map<String, String> globalDetectProperties) {
        this.globalDetectProperties = globalDetectProperties;
    }

    public Set<ScanType> getScanTypes() {
        return scanTypes;
    }

    public void setScanTypes(final Set<ScanType> scanTypes) {
        this.scanTypes = scanTypes;
    }
}
