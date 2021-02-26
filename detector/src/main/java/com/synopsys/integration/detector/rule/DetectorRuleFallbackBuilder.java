/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.rule;

public class DetectorRuleFallbackBuilder {

    private final DetectorRule failingDetector;
    private DetectorRule fallbackToDetector;

    public DetectorRuleFallbackBuilder(final DetectorRule failingDetector) {
        this.failingDetector = failingDetector;
    }

    public DetectorRuleFallbackBuilder to(final DetectorRule detector) {
        this.fallbackToDetector = detector;
        return this;
    }

    public DetectorRule getFailingDetector() {
        return failingDetector;
    }

    public DetectorRule getFallbackToDetector() {
        return fallbackToDetector;
    }
}
