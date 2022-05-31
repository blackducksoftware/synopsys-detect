package com.synopsys.integration.detect.workflow.nameversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorEvaluationNameVersionDecider {
    private final DetectorNameVersionDecider detectorNameVersionDecider;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DetectorEvaluationNameVersionDecider(DetectorNameVersionDecider detectorNameVersionDecider) {
        this.detectorNameVersionDecider = detectorNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(DetectorEvaluation rootEvaluation, String projectDetector) {
        List<DetectorProjectInfo> detectorProjectInfoList = convertEvaluationToProjectInfo(rootEvaluation);

        DetectorType detectorType = preferredDetectorTypeFromString(projectDetector);

        return detectorNameVersionDecider.decideProjectNameVersion(detectorProjectInfoList, detectorType);
    }

    List<DetectorProjectInfo> convertEvaluationToProjectInfo(DetectorEvaluation evaluation) {
        List<DetectorProjectInfo> projectInfos = new ArrayList<>();
        evaluation.getFoundDetectorRuleEvaluations().forEach(foundDetector -> {
            if (foundDetector.wasExtractionSuccessful() && foundDetector.getExtraction().isPresent()) {
                Extraction extraction = foundDetector.getExtraction().get();

                NameVersion nameVersion = new NameVersion(extraction.getProjectName(), extraction.getProjectVersion());
                projectInfos.add(new DetectorProjectInfo(foundDetector.getRule().getDetectorType(), evaluation.getDepth(), nameVersion));
            }
        });

        evaluation.getChildren()
            .stream()
            .flatMap(child -> convertEvaluationToProjectInfo(child).stream())
            .forEach(projectInfos::add);

        return projectInfos;
    }

    private DetectorType preferredDetectorTypeFromString(String detectorType) {
        DetectorType castDetectorType = null;
        if (StringUtils.isNotBlank(detectorType)) {
            String detectorTypeUppercase = detectorType.toUpperCase();
            if (DetectorType.getPossibleNames().contains(detectorTypeUppercase)) {
                castDetectorType = DetectorType.valueOf(detectorTypeUppercase);
            }
        }
        // In Kotlin this check was to see if castDetectorType != null but that doesn't make any sense...
        if (castDetectorType == null) {
            logger.debug("A valid preferred detector type was not provided, deciding project name automatically.");
        }
        return castDetectorType;
    }
}