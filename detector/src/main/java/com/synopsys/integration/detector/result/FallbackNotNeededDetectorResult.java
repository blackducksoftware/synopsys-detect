/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detector.explanation.FallbackNotNeeded;
import com.synopsys.integration.detector.rule.DetectorRule;

public class FallbackNotNeededDetectorResult extends FailedDetectableResult {
    private final DetectorRule<Detectable> passingDetector;

    public FallbackNotNeededDetectorResult(@NotNull final DetectorRule<Detectable> passingDetector) {
        this.passingDetector = passingDetector;
    }

    @Override
    public String toDescription() {
        return String.format("No fallback needed, detector passed: %s", passingDetector.getDescriptiveName());
    }

    @Override
    public List<Explanation> getExplanation() {
        return Collections.singletonList(new FallbackNotNeeded(passingDetector.getDescriptiveName()));
    }
}
