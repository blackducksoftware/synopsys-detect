package com.synopsys.integration.detector.accuracy.directory;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detector.accuracy.entrypoint.DetectorRuleEvaluation;

public class DirectoryEvaluation {
    private final File directory;
    private final int depth; //Don't love that this is here, but it's not captured elsewhere. Maybe capturing DirectoryFindResult?
    private final List<DetectorRuleEvaluation> evaluations;
    private final List<DirectoryEvaluation> children;

    public DirectoryEvaluation(
        File directory,
        int depth,
        List<DetectorRuleEvaluation> evaluations,
        List<DirectoryEvaluation> children
    ) {
        this.directory = directory;
        this.depth = depth;
        this.evaluations = evaluations;
        this.children = children;
    }

    public List<DirectoryEvaluation> getChildren() {
        return children;
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepth() {
        return depth;
    }

    public List<DetectorRuleEvaluation> getEvaluations() {
        return evaluations;
    }
}
