/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.workflow.extraction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.extraction.Extraction.ExtractionResultType;
import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class ExtractionManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExtractionManager() {
    }

    public ExtractionResult performExtractions(final List<DetectorEvaluation> results) {
        final List<DetectorEvaluation> extractable = results.stream().filter(result -> result.isExtractable()).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            final DetectorEvaluation detectorEvaluation = extractable.get(i);
            final String progress = Integer.toString((int) Math.floor((i * 100.0f) / extractable.size()));
            logger.info(String.format("Extracting %d of %d (%s%%)", i + 1, extractable.size(), progress));
            logger.info(ReportConstants.SEPERATOR);

            final ExtractionId extractionId = new ExtractionId(detectorEvaluation.getDetector().getDetectorType(), Integer.toString(i));
            detectorEvaluation.setExtractionId(extractionId);

            extract(extractable.get(i));
        }

        final Set<DetectorType> succesfulBomToolGroups = extractable.stream()
                                                             .filter(it -> it.wasExtractionSuccessful())
                                                             .map(it -> it.getDetector().getDetectorType())
                                                             .collect(Collectors.toSet());

        final Set<DetectorType> failedBomToolGroups = extractable.stream()
                                                          .filter(it -> !it.wasExtractionSuccessful())
                                                          .map(it -> it.getDetector().getDetectorType())
                                                          .collect(Collectors.toSet());

        final List<DetectCodeLocation> codeLocations = extractable.stream()
                                                           .filter(it -> it.wasExtractionSuccessful())
                                                           .flatMap(it -> it.getExtraction().codeLocations.stream())
                                                           .collect(Collectors.toList());

        return new ExtractionResult(codeLocations, succesfulBomToolGroups, failedBomToolGroups);
    }

    private void extract(final DetectorEvaluation result) { //TODO: Replace reporting.

        logger.info("Starting extraction: " + result.getDetector().getDetectorType() + " - " + result.getDetector().getName());
        logger.info("Identifier: " + result.getExtractionId().toUniqueString());
        ObjectPrinter.printObjectPrivate(new InfoLogReportWriter(), result.getDetector());
        logger.info(ReportConstants.SEPERATOR);

        try {
            result.setExtraction(result.getDetector().extract(result.getExtractionId()));
        } catch (final Exception e) {
            result.setExtraction(new Extraction.Builder().exception(e).build());
        }

        logger.info(ReportConstants.SEPERATOR);
        logger.info("Finished extraction: " + result.getExtraction().result.toString());
        logger.info("Code locations found: " + result.getExtraction().codeLocations.size());
        if (result.getExtraction().result == ExtractionResultType.EXCEPTION) {
            logger.error("Exception:", result.getExtraction().error);
        } else if (result.getExtraction().result == ExtractionResultType.FAILURE) {
            logger.info(result.getExtraction().description);
        }
        logger.info(ReportConstants.SEPERATOR);

    }

}
