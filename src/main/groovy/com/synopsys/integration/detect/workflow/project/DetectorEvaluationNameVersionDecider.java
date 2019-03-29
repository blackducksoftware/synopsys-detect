/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.base.DetectorEvaluation;
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
                logger.info("A valid preferred detector type was not provided, deciding project name automatically.");
            } else {
                preferredDetectorType = DetectorType.valueOf(projectDetectorFixed);
            }
        }

        final List<DetectorProjectInfo> detectorProjectInfo = transformIntoProjectInfo(detectorEvaluations);

        return detectorNameVersionDecider.decideProjectNameVersion(detectorProjectInfo, preferredDetectorType);
    }

    private List<DetectorProjectInfo> transformIntoProjectInfo(final List<DetectorEvaluation> detectorEvaluations) {
        return detectorEvaluations.stream()
                   .filter(DetectorEvaluation::wasExtractionSuccessful)
                   .filter(detectorEvaluation -> StringUtils.isNotBlank(detectorEvaluation.getExtraction().getProjectName()))
                   .map(this::transformDetectorEvaluation)
                   .collect(Collectors.toList());
    }

    private DetectorProjectInfo transformDetectorEvaluation(DetectorEvaluation detectorEvaluation) {
        final NameVersion nameVersion = new NameVersion(detectorEvaluation.getExtraction().getProjectName(), detectorEvaluation.getExtraction().getProjectVersion());
        final DetectorProjectInfo possibility = new DetectorProjectInfo(detectorEvaluation.getDetectorRule().getDetectorType(), detectorEvaluation.getSearchEnvironment().getDepth(), nameVersion);
        return possibility;
    }
}
