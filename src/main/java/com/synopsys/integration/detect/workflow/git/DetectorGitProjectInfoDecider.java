package com.synopsys.integration.detect.workflow.git;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorGitProjectInfoDecider {
    public Optional<GitInfo> decideSuggestion(List<DetectorEvaluation> detectorEvaluations) {
        return detectorEvaluations.stream()
            .filter(DetectorEvaluation::wasExtractionSuccessful)
            .filter(detectorEvaluation -> detectorEvaluation.getDetectorType().equals(DetectorType.GIT))
            .filter(evaluation -> evaluation.getExtraction().hasMetadata(GitCliExtractor.EXTRACTION_METADATA_KEY))
            .min(Comparator.comparingInt(eval -> eval.getSearchEnvironment().getDepth()))
            .flatMap(evaluation -> evaluation.getExtraction().getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY));
    }
}
