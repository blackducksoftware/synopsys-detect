package com.synopsys.integration.detector.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorEvaluationTree {
    //The following evaluation details are known when the evaluation is created.
    private final File directory;
    private final int depthFromRoot;
    private final DetectorRuleSet detectorRuleSet;
    private final List<DetectorEvaluation> orderedEvaluations;
    private final Set<DetectorEvaluationTree> children;

    public DetectorEvaluationTree(
        File directory,
        int depthFromRoot,
        DetectorRuleSet detectorRuleSet,
        List<DetectorEvaluation> orderedEvaluations,
        Set<DetectorEvaluationTree> children
    ) {
        this.directory = directory;
        this.depthFromRoot = depthFromRoot;
        this.orderedEvaluations = orderedEvaluations;
        this.children = children;
        this.detectorRuleSet = detectorRuleSet;
    }

    public List<DetectorEvaluationTree> asFlatList() {
        List<DetectorEvaluationTree> evaluationTrees = new ArrayList<>();
        evaluationTrees.add(this);
        for (DetectorEvaluationTree detectorEvaluationTree : children) {
            evaluationTrees.addAll(detectorEvaluationTree.asFlatList());
        }
        return evaluationTrees;
    }

    public List<DetectorEvaluation> allDescendentEvaluations() {
        return asFlatList()
            .stream()
            .flatMap(it -> it.getOrderedEvaluations().stream())
            .collect(Collectors.toList());
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepthFromRoot() {
        return depthFromRoot;
    }

    public List<DetectorEvaluation> getOrderedEvaluations() {
        return orderedEvaluations;
    }

    public Optional<DetectorEvaluation> getEvaluation(DetectorRule rule) {
        return orderedEvaluations.stream()
            .filter(detectorEvaluation1 -> detectorEvaluation1.getDetectorRule().equals(rule))
            .findFirst();
    }

    public Set<DetectorEvaluationTree> getChildren() {
        return children;
    }

    public DetectorRuleSet getDetectorRuleSet() {
        return detectorRuleSet;
    }
}
