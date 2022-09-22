package com.synopsys.integration.detect.util.filter;

import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.RunDecision;

/**
 * Using this class as a temporary way to handle 'tool' decisions but instead of being an immutable calculation it re-calculates.
 * The idea is in the future, this is pre-calculated at detect-boot and is passed around as a decision so instead of shouldInclude we just ask willBlackDuckRun
 * -jp 3/29/20
 */
public class DetectToolFilter {
    private final ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter;
    private final boolean impactEnabled;
    private final boolean iacEnabled;
    private final boolean rapidSignatureScanEnabled;
    private final RunDecision runDecision;
    private final BlackDuckDecision blackDuckDecision;
    
    // If a rapid scan is specified, default to running only package manager scans
    private final List<DetectTool> rapidTools = Arrays.asList(DetectTool.DETECTOR);

    public DetectToolFilter(
        ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter,
        boolean impactEnabled,
        boolean iacEnabled,
        boolean rapidSignatureScanEnabled, 
        RunDecision runDecision,
        BlackDuckDecision blackDuckDecision
    ) {

        this.excludedIncludedFilter = excludedIncludedFilter;
        this.impactEnabled = impactEnabled;
        this.rapidSignatureScanEnabled = rapidSignatureScanEnabled;
        this.iacEnabled = iacEnabled;
        this.runDecision = runDecision;
        this.blackDuckDecision = blackDuckDecision;
    }

    public boolean shouldInclude(DetectTool detectTool) { //Only turn tools OFF, turning a tool ON prevents the user from being able to turn an undesired tool OFF.
        if (detectTool == DetectTool.IMPACT_ANALYSIS) {
            return impactEnabled;
        }
        if (detectTool == DetectTool.IAC_SCAN) {
            return iacEnabled;
        }

        if (detectTool == DetectTool.DETECTOR && runDecision.isDockerMode()) {
            return false;
        }
        if (blackDuckDecision.scanMode() == BlackduckScanMode.RAPID) {
            // Only run rapid signature scans if the user specifically asked for them
            if (detectTool == DetectTool.SIGNATURE_SCAN) {
                return rapidSignatureScanEnabled;
            } else if (!rapidTools.contains(detectTool)) {
                return false;
            }
        }
        return excludedIncludedFilter.shouldInclude(detectTool);
    }
}
