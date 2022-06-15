package com.synopsys.integration.detect.tool.detector.report.rule;

import java.util.List;

import com.synopsys.integration.detector.rule.DetectorRule;

public class NotFoundDetectorRuleReport { //We don't capture 'Relevant Files' or Explanations but they seem to only exist on Passed so it should be fine.
    private final DetectorRule detectorRule;
    private final List<String> reasons;

    public NotFoundDetectorRuleReport(DetectorRule detectorRule, List<String> reasons) {
        this.detectorRule = detectorRule;
        this.reasons = reasons;
    }

    public DetectorRule getDetectorRule() {
        return detectorRule;
    }

    public List<String> getReasons() {
        return reasons;
    }
}

