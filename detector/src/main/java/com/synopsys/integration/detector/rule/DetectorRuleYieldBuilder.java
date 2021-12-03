package com.synopsys.integration.detector.rule;

public class DetectorRuleYieldBuilder {

    private final DetectorRule yieldingDetector;
    private DetectorRule yieldingToDetector;

    public DetectorRuleYieldBuilder(DetectorRule yieldingDetector) {
        this.yieldingDetector = yieldingDetector;
    }

    public DetectorRuleYieldBuilder to(DetectorRule detector) {
        this.yieldingToDetector = detector;
        return this;
    }

    public DetectorRule getYieldingDetector() {
        return yieldingDetector;
    }

    public DetectorRule getYieldingToDetector() {
        return yieldingToDetector;
    }
}
