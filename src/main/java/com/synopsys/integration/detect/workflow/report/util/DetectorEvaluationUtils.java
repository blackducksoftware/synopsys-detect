package com.synopsys.integration.detect.workflow.report.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluationUtils {
    public static List<DetectorEvaluation> applicableChildren(DetectorEvaluationTree tree) {
        return filteredChildren(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> applicableDescendants(DetectorEvaluationTree tree) {
        return filteredDescendents(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> notApplicableChildren(DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isApplicable());
    }

    public static List<DetectorEvaluation> searchableButNotApplicableChildren(DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> detectorEvaluation.isSearchable() && !detectorEvaluation.isApplicable());
    }

    public static List<DetectorEvaluation> notSearchableChildren(DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isSearchable());
    }

    public static List<DetectorEvaluation> extractionSuccessDescendents(DetectorEvaluationTree tree) {
        return filteredDescendents(tree, DetectorEvaluation::wasExtractionSuccessful);
    }

    public static List<DetectorEvaluation> filteredChildren(DetectorEvaluationTree tree, Predicate<DetectorEvaluation> predicate) {
        return tree.getOrderedEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<DetectorEvaluation> filteredDescendents(DetectorEvaluationTree tree, Predicate<DetectorEvaluation> predicate) {
        return tree.allDescendentEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }
}
