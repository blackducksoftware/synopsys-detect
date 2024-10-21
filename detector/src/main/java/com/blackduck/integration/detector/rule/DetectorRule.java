package com.blackduck.integration.detector.rule;

import java.util.List;

import com.blackduck.integration.detector.base.DetectorType;

public class DetectorRule {
    private final DetectorType detectorType;
    private final List<EntryPoint> entryPoints;

    public DetectorRule(DetectorType detectorType, List<EntryPoint> entryPoints) {
        this.detectorType = detectorType;
        this.entryPoints = entryPoints;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }
}
