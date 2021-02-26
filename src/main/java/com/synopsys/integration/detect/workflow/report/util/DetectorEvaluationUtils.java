/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluationUtils {
    public static List<DetectorEvaluation> applicableChildren(final DetectorEvaluationTree tree) {
        return filteredChildren(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> applicableDescendants(final DetectorEvaluationTree tree) {
        return filteredDescendents(tree, DetectorEvaluation::isApplicable);
    }

    public static List<DetectorEvaluation> notApplicableChildren(final DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isApplicable());
    }

    public static List<DetectorEvaluation> searchableButNotApplicableChildren(final DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> detectorEvaluation.isSearchable() && !detectorEvaluation.isApplicable());
    }

    public static List<DetectorEvaluation> notSearchableChildren(final DetectorEvaluationTree tree) {
        return filteredChildren(tree, detectorEvaluation -> !detectorEvaluation.isSearchable());
    }

    public static List<DetectorEvaluation> extractionSuccessDescendents(final DetectorEvaluationTree tree) {
        return filteredDescendents(tree, DetectorEvaluation::wasExtractionSuccessful);
    }

    public static List<DetectorEvaluation> filteredChildren(final DetectorEvaluationTree tree, final Predicate<DetectorEvaluation> predicate) {
        return tree.getOrderedEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<DetectorEvaluation> filteredDescendents(final DetectorEvaluationTree tree, final Predicate<DetectorEvaluation> predicate) {
        return tree.allDescendentEvaluations().stream().filter(predicate).collect(Collectors.toList());
    }
}
