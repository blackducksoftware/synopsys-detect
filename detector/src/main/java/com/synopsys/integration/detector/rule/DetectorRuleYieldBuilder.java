/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.rule;

public class DetectorRuleYieldBuilder {

    private final DetectorRule yieldingDetector;
    private DetectorRule yieldingToDetector;

    public DetectorRuleYieldBuilder(final DetectorRule yieldingDetector) {
        this.yieldingDetector = yieldingDetector;
    }

    public DetectorRuleYieldBuilder to(final DetectorRule detector) {
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
