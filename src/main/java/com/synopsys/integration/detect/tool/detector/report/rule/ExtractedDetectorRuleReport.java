package com.synopsys.integration.detect.tool.detector.report.rule;

import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.detectable.AttemptedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detector.rule.DetectorRule;

public class ExtractedDetectorRuleReport extends EvaluatedDetectorRuleReport {
    private final ExtractedDetectableReport extractedDetectable;

    public ExtractedDetectorRuleReport(
        DetectorRule rule,
        int depth,
        List<AttemptedDetectableReport> skippedEntryPoints,
        List<AttemptedDetectableReport> attemptedDetectables,
        ExtractedDetectableReport extractedDetectable
    ) {
        super(rule, depth, skippedEntryPoints, attemptedDetectables);
        this.extractedDetectable = extractedDetectable;
    }

    public ExtractedDetectableReport getExtractedDetectable() {
        return extractedDetectable;
    }
}
