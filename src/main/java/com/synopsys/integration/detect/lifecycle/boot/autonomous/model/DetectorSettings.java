package com.synopsys.integration.detect.lifecycle.boot.autonomous.model;

import java.util.Map;
import java.util.Set;

public class DetectorSettings {

    private Map<String, String> detectorSharedProperties;
    private Set<Detector> detectors;

    public Map<String, String> getDetectorSharedProperties() {
        return detectorSharedProperties;
    }

    public void setDetectorSharedProperties(final Map<String, String> detectorSharedProperties) {
        this.detectorSharedProperties = detectorSharedProperties;
    }

    public Set<Detector> getDetectors() {
        return detectors;
    }

    public void setDetectors(final Set<Detector> detectors) {
        this.detectors = detectors;
    }
}
