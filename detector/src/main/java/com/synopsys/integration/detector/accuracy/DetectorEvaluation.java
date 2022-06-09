package com.synopsys.integration.detector.accuracy;

import java.io.File;
import java.util.List;

public class DetectorEvaluation {
    private final File directory;
    private final int depth; //Don't love that this is here, but it's not captured elsewhere. Maybe capturing DirectoryFindResult?
    private final List<DetectorRuleEvaluation> foundDetectorRuleEvaluations;
    private final List<DetectorRuleNotFoundResult> notFoundDetectorSearches;
    private final List<DetectorEvaluation> children;

    public DetectorEvaluation(
        File directory,
        int depth,
        List<DetectorRuleEvaluation> foundDetectorRuleEvaluations,
        List<DetectorRuleNotFoundResult> notFoundDetectorSearches, List<DetectorEvaluation> children
    ) {
        this.directory = directory;
        this.depth = depth;
        this.foundDetectorRuleEvaluations = foundDetectorRuleEvaluations;
        this.notFoundDetectorSearches = notFoundDetectorSearches;
        this.children = children;
    }

    public List<DetectorEvaluation> getChildren() {
        return children;
    }

    public List<DetectorRuleEvaluation> getFoundDetectorRuleEvaluations() {
        return foundDetectorRuleEvaluations;
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepth() {
        return depth;
    }

    public List<DetectorRuleNotFoundResult> getNotFoundDetectorSearches() {
        return notFoundDetectorSearches;
    }
}
