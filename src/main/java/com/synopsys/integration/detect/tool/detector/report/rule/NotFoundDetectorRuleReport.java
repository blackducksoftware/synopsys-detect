package com.synopsys.integration.detect.tool.detector.report.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.synopsys.integration.detector.accuracy.DetectorSearchEntryPointResult;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;

public class NotFoundDetectorRuleReport { //We don't capture 'Relevant Files' or Explanations but they seem to only exist on Passed so it should be fine.
    private final DetectorRule detectorRule;
    private final List<String> reasons;

    public NotFoundDetectorRuleReport(DetectorRule detectorRule, List<String> reasons) {
        this.detectorRule = detectorRule;
        this.reasons = reasons;
    }

    public static NotFoundDetectorRuleReport notSearchable(DetectorRule rule, DetectorResult detectorResult) {
        return new NotFoundDetectorRuleReport(rule, Collections.singletonList(detectorResult.getDescription()));
    }

    public static NotFoundDetectorRuleReport noApplicableEntryPoint(DetectorRule rule, List<DetectorSearchEntryPointResult> detectorResult) {
        List<String> reasons = new ArrayList<>();
        detectorResult.forEach(result -> {
            reasons.add(result.getEntryPoint().getPrimary().getName() + ": " + result.getApplicableResult().toDescription());
        });
        return new NotFoundDetectorRuleReport(rule, reasons);
    }

    public DetectorRule getDetectorRule() {
        return detectorRule;
    }

    public List<String> getReasons() {
        return reasons;
    }
}

