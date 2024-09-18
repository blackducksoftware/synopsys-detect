package com.blackduck.integration.detector.rule;

import java.util.List;

public class DetectorRuleSet {
    private final List<DetectorRule> detectorRules;

    public DetectorRuleSet(List<DetectorRule> detectorRules) {this.detectorRules = detectorRules;}

    public List<DetectorRule> getDetectorRules() {
        return detectorRules;
    }
}
