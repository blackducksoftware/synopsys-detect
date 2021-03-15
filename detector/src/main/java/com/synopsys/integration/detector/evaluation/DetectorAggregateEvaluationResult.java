/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorAggregateEvaluationResult {
    private DetectorEvaluationTree evaluationTree;

    public DetectorAggregateEvaluationResult(DetectorEvaluationTree evaluationTree) {
        this.evaluationTree = evaluationTree;
    }

    public DetectorEvaluationTree getEvaluationTree() {
        return evaluationTree;
    }

    public List<DetectorEvaluation> getDetectorEvaluations() {
        return evaluationTree.getOrderedEvaluations();
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return getDetectorEvaluations().stream()
                   .filter(DetectorEvaluation::isApplicable)
                   .map(DetectorEvaluation::getDetectorRule)
                   .map(DetectorRule::getDetectorType)
                   .collect(Collectors.toSet());
    }

    public Integer getExtractionCount() {
        return Math.toIntExact(getDetectorEvaluations().stream()
                                   .filter(DetectorEvaluation::isExtractable)
                                   .count());
    }
}
