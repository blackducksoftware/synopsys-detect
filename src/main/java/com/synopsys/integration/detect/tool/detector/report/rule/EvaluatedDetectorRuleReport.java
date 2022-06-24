package com.synopsys.integration.detect.tool.detector.report.rule;

import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.detectable.AttemptedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detector.rule.DetectorRule;

public class EvaluatedDetectorRuleReport {
    private final DetectorRule rule;
    private final int depth;
    private final List<AttemptedDetectableReport> skippedEntryPoints;
    private final List<AttemptedDetectableReport> attemptedDetectables;

    public EvaluatedDetectorRuleReport(
        DetectorRule rule,
        int depth,
        List<AttemptedDetectableReport> skippedEntryPoints,
        List<AttemptedDetectableReport> attemptedDetectables
    ) {
        this.rule = rule;
        this.depth = depth;
        this.skippedEntryPoints = skippedEntryPoints;
        this.attemptedDetectables = attemptedDetectables;
    }

    public static ExtractedDetectorRuleReport extracted(
        DetectorRule rule,
        int depth,
        List<AttemptedDetectableReport> skippedEntryPoints,
        List<AttemptedDetectableReport> attemptedDetectables,
        ExtractedDetectableReport extractedDetectableReport
    ) {
        return new ExtractedDetectorRuleReport(rule, depth, skippedEntryPoints, attemptedDetectables, extractedDetectableReport);
    }

    public static EvaluatedDetectorRuleReport notExtracted(
        DetectorRule rule,
        int depth,
        List<AttemptedDetectableReport> skippedEntryPoints,
        List<AttemptedDetectableReport> attemptedDetectables
    ) {
        return new EvaluatedDetectorRuleReport(rule, depth, skippedEntryPoints, attemptedDetectables);
    }

    public DetectorRule getRule() {
        return rule;
    }

    public int getDepth() {
        return depth;
    }

    public List<AttemptedDetectableReport> getSkippedEntryPoints() {
        return skippedEntryPoints;
    }

    public List<AttemptedDetectableReport> getAttemptedDetectables() {
        return attemptedDetectables;
    }
}
