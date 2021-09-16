/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.HashSet;
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
        return getDetectorEvaluations(evaluationTree);
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return getApplicableDetectorTypes(evaluationTree);
    }

    public Set<DetectorType> getApplicableDetectorTypesRecursively() {
        return getApplicableDetectorTypesRecursively(evaluationTree);
    }

    public Integer getExtractionCount() {
        return Math.toIntExact(getDetectorEvaluations().stream()
                                   .filter(DetectorEvaluation::isExtractable)
                                   .count());
    }

    private Set<DetectorType> getApplicableDetectorTypesRecursively(DetectorEvaluationTree givenEvaluationTree) {
        Set<DetectorType> applied = new HashSet<>();
        applied.addAll(getApplicableDetectorTypes(givenEvaluationTree));
        for (DetectorEvaluationTree childEvaluationTree : givenEvaluationTree.getChildren()) {
            applied.addAll(getApplicableDetectorTypesRecursively(childEvaluationTree));
        }
        return applied;
    }

    private List<DetectorEvaluation> getDetectorEvaluations(DetectorEvaluationTree givenEvaluationTree) {
        return givenEvaluationTree.getOrderedEvaluations();
    }

    private Set<DetectorType> getApplicableDetectorTypes(DetectorEvaluationTree givenEvaluationTree) {
        return getDetectorEvaluations(givenEvaluationTree).stream()
                .filter(DetectorEvaluation::isApplicable)
                .map(DetectorEvaluation::getDetectorRule)
                .map(DetectorRule::getDetectorType)
                .collect(Collectors.toSet());
    }
}
