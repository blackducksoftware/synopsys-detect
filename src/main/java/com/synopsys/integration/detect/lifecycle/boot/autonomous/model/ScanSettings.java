package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScanSettings {
    Map<String, String> globalProperties = new HashMap<>();
    DetectorSettings detectorSettings;
    Set<Detector> detectors = new HashSet<>();

    public Map<String, String> getGlobalProperties() {
        return globalProperties;
    }

    public void setGlobalProperties(final Map<String, String> globalProperties) {
        this.globalProperties = globalProperties;
    }

    public DetectorSettings getDetectorSettings() {
        return detectorSettings;
    }

    public void setDetectorSettings(final DetectorSettings detectorSettings) {
        this.detectorSettings = detectorSettings;
    }

    public Set<Detector> getDetectors() {
        return detectors;
    }

    public void setDetectors(final Set<Detector> detectors) {
        this.detectors = detectors;
    }

    public Set<DetectTool> getDetectTools() {
        return detectTools;
    }

    public void setDetectTools(final Set<DetectTool> detectTools) {
        this.detectTools = detectTools;
    }

    Set<DetectTool> detectTools = new HashSet<>();
}
