package com.synopsys.integration.detector.base;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;

public class DetectorEvaluationUtil {
    public static List<DetectorRuleEvaluation> allDescendentFound(DetectorEvaluation evaluation) {
        List<DetectorRuleEvaluation> types = new ArrayList<>(evaluation.getFoundDetectorRuleEvaluations());
        for (DetectorEvaluation child : evaluation.getChildren()) {
            types.addAll(allDescendentFound(child));
        }
        return types;
    }

    public static List<DetectorEvaluation> asFlatList(DetectorEvaluation node) {
        List<DetectorEvaluation> allChildren = new ArrayList<>();
        allChildren.add(node);
        for (DetectorEvaluation child : node.getChildren()) {
            allChildren.addAll(DetectorEvaluationUtil.asFlatList(child));
        }
        return allChildren;
    }
}
