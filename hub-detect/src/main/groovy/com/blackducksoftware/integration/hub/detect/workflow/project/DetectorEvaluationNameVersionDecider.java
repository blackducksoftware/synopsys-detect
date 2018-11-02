package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.util.NameVersion;

public class DetectorEvaluationNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(DetectorEvaluationNameVersionDecider.class);

    private final DetectorNameVersionDecider detectorNameVersionDecider;

    public DetectorEvaluationNameVersionDecider(DetectorNameVersionDecider detectorNameVersionDecider) {
        this.detectorNameVersionDecider = detectorNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(final List<DetectorEvaluation> detectorEvaluations, String projectDetector) {
        DetectorType preferredDetectorType = null;
        if (StringUtils.isNotBlank(projectDetector)) {
            final String projectDetectorFixed = projectDetector.toUpperCase();
            if (!DetectorType.POSSIBLE_NAMES.contains(projectDetectorFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredDetectorType = DetectorType.valueOf(projectDetectorFixed);
            }
        }

        final List<DetectorProjectInfo> allDetectorProjectInfo = transformIntoProjectInfo(detectorEvaluations);
        return detectorNameVersionDecider.decideProjectNameVersion(allDetectorProjectInfo, preferredDetectorType);
    }

    private List<DetectorProjectInfo> transformIntoProjectInfo(final List<DetectorEvaluation> detectorEvaluations) {
        return detectorEvaluations.stream()
                   .filter(it -> it.wasExtractionSuccessful())
                   .filter(it -> it.getExtraction().projectName != null)
                   .map(it -> {
                       final NameVersion nameVersion = new NameVersion(it.getExtraction().projectName, it.getExtraction().projectVersion);
                       final DetectorProjectInfo possibility = new DetectorProjectInfo(it.getDetector().getDetectorType(), it.getEnvironment().getDepth(), nameVersion);
                       return possibility;
                   })
                   .collect(Collectors.toList());
    }
}
