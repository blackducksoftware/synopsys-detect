package com.synopsys.integration.detect.tool.detector.report.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public class DetectorReportUtil {
    public static List<Explanation> combineExplanations(DetectableResult... results) {
        List<Explanation> explanations = new ArrayList<>();
        for (DetectableResult result : results) {
            explanations.addAll(result.getExplanation());
        }
        return explanations;
    }

    public static List<File> combineRelevantFiles(DetectableResult... results) {
        List<File> explanations = new ArrayList<>();
        for (DetectableResult result : results) {
            explanations.addAll(result.getRelevantFiles());
        }
        return explanations;
    }
}
