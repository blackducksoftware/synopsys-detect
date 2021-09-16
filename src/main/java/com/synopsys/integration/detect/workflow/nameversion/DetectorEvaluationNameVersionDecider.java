/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorEvaluationNameVersionDecider {
    private DetectorNameVersionDecider detectorNameVersionDecider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public DetectorEvaluationNameVersionDecider(DetectorNameVersionDecider detectorNameVersionDecider) {
        this.detectorNameVersionDecider = detectorNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(List<DetectorEvaluation> detectorEvaluations, String projectDetector) {
        List<DetectorProjectInfo> detectorProjectInfoList = detectorEvaluations.stream()
                                                                .filter(DetectorEvaluation::wasExtractionSuccessful)
                                                                .filter(detectorEvaluation -> StringUtils.isNotBlank(detectorEvaluation.getExtraction().getProjectName()))
                                                                .map(this::toProjectInfo)
                                                                .collect(Collectors.toList());

        DetectorType detectorType = preferredDetectorTypeFromString(projectDetector);

        return detectorNameVersionDecider.decideProjectNameVersion(detectorProjectInfoList, detectorType);
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

    private DetectorProjectInfo toProjectInfo(DetectorEvaluation detectorEvaluation) {
        NameVersion nameVersion = new NameVersion(detectorEvaluation.getExtraction().getProjectName(), detectorEvaluation.getExtraction().getProjectVersion());
        return new DetectorProjectInfo(detectorEvaluation.getDetectorRule().getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth(), nameVersion);
    }
}