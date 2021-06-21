/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.util.filter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.RunDecision;

/**
 * Using this class as a temporary way to handle 'tool' decisions but instead of being an immutable calculation it re-calculates.
 * The idea is in the future, this is pre-calculated at detect-boot and is passed around as a decision so instead of shouldInclude we just ask willPolarisRun
 * -jp 3/29/20
 */
public class DetectToolFilter {
    private final ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter;
    private final Optional<Boolean> impactEnabled;
    private final RunDecision runDecision;
    private final BlackDuckDecision blackDuckDecision;
    private final List<DetectTool> rapidTools = Collections.singletonList(DetectTool.DETECTOR);

    public DetectToolFilter(ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter, Optional<Boolean> impactEnabled,
        RunDecision runDecision, BlackDuckDecision blackDuckDecision) {
        this.excludedIncludedFilter = excludedIncludedFilter;
        this.impactEnabled = impactEnabled;
        this.runDecision = runDecision;
        this.blackDuckDecision = blackDuckDecision;
    }

    public boolean shouldInclude(DetectTool detectTool) { //Only turn tools OFF, turning a tool ON prevents the user from being able to turn an undesired tool OFF.
        if (detectTool == DetectTool.IMPACT_ANALYSIS && impactEnabled.isPresent()) {
            return impactEnabled.get();
        }

        if (detectTool == DetectTool.DETECTOR) {
            if (runDecision.isDockerMode()) {
                return false;
            }
        }
        if (blackDuckDecision.scanMode() == BlackduckScanMode.RAPID) {
            if (!rapidTools.contains(detectTool)) {
                return false;
            }
        }
        return excludedIncludedFilter.shouldInclude(detectTool);
    }
}
