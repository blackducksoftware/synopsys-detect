/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.util.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter;
    private final Optional<Boolean> deprecatedSigScanDisabled;
    private final Optional<Boolean> deprecatedPolarisEnabled;
    private final Optional<Boolean> impactEnabled;
    private RunDecision runDecision;
    private BlackDuckDecision blackDuckDecision;
    private List<DetectTool> rapidTools = Arrays.asList(DetectTool.DETECTOR, DetectTool.SIGNATURE_SCAN);

    public DetectToolFilter(ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter, final Optional<Boolean> deprecatedSigScanDisabled, final Optional<Boolean> deprecatedPolarisEnabled, Optional<Boolean> impactEnabled,
        final RunDecision runDecision, final BlackDuckDecision blackDuckDecision) {
        this.excludedIncludedFilter = excludedIncludedFilter;
        this.deprecatedSigScanDisabled = deprecatedSigScanDisabled;
        this.deprecatedPolarisEnabled = deprecatedPolarisEnabled;
        this.impactEnabled = impactEnabled;
        this.runDecision = runDecision;
        this.blackDuckDecision = blackDuckDecision;
    }

    public boolean shouldInclude(final DetectTool detectTool) { //Only turn tools OFF, turning a tool ON prevents the user from being able to turn an undesired tool OFF.
        if (detectTool == DetectTool.SIGNATURE_SCAN && deprecatedSigScanDisabled.isPresent()) {
            return !deprecatedSigScanDisabled.get();
        } else if (detectTool == DetectTool.POLARIS && deprecatedPolarisEnabled.isPresent()) {
            return deprecatedPolarisEnabled.get();
        } else if (detectTool == DetectTool.IMPACT_ANALYSIS && impactEnabled.isPresent()) {
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
