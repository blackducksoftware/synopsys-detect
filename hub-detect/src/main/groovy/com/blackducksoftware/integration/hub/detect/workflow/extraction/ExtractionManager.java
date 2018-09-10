/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.DetectProjectManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExceptionBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction.ExtractionResultType;
import com.blackducksoftware.integration.hub.detect.workflow.report.LogReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ObjectPrinter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.synopsys.integration.exception.IntegrationException;

public class ExtractionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    private final ReportManager reportManager;

    public ExtractionManager(final ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    private void extract(final List<BomToolEvaluation> results) {
        final List<BomToolEvaluation> extractable = results.stream().filter(result -> result.isExtractable()).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            final BomToolEvaluation bomToolEvaluation = extractable.get(i);
            final String progress = Integer.toString((int) Math.floor((i * 100.0f) / extractable.size()));
            logger.info(String.format("Extracting %d of %d (%s%%)", i + 1, extractable.size(), progress));
            logger.info(ReportConstants.SEPERATOR);

            final ExtractionId extractionId = new ExtractionId(bomToolEvaluation.getBomTool().getBomToolGroupType(), Integer.toString(i));
            bomToolEvaluation.setExtractionId(extractionId);

            extract(extractable.get(i));
        }
    }

    private void prepare(final List<BomToolEvaluation> results) {
        for (final BomToolEvaluation result : results) {
            prepare(result);
        }
    }

    private void prepare(final BomToolEvaluation result) {
        if (result.isApplicable()) {
            reportManager.extractableStarted(result.getBomTool());
            try {
                result.setExtractable(result.getBomTool().extractable());
            } catch (final Exception e) {
                result.setExtractable(new ExceptionBomToolResult(e));
            }
            reportManager.extractableEnded(result.getBomTool());
        }
    }

    private void extract(final BomToolEvaluation result) {
        reportManager.extractionStarted(result);

        logger.info("Starting extraction: " + result.getBomTool().getBomToolGroupType() + " - " + result.getBomTool().getName());
        logger.info("Identifier: " + result.getExtractionId().toUniqueString());
        ObjectPrinter.printObjectPrivate(new LogReportWriter(), result.getBomTool());
        logger.info(ReportConstants.SEPERATOR);

        try {
            result.setExtraction(result.getBomTool().extract(result.getExtractionId()));
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

        reportManager.extractionEnded(result);
    }

    public ExtractionResult performExtractions(final List<BomToolEvaluation> bomToolEvaluations) throws IntegrationException, DetectUserFriendlyException {
        prepare(bomToolEvaluations);

        reportManager.preparationCompleted(bomToolEvaluations);

        extract(bomToolEvaluations);

        final HashSet<BomToolGroupType> succesfulBomToolGroups = new HashSet<>();
        final HashSet<BomToolGroupType> failedBomToolGroups = new HashSet<>();
        for (final BomToolEvaluation evaluation : bomToolEvaluations) {
            final BomToolGroupType type = evaluation.getBomTool().getBomToolGroupType();
            if (evaluation.isApplicable()) {
                if (evaluation.isExtractable() && evaluation.wasExtractionSuccessful()) {
                    succesfulBomToolGroups.add(type);
                } else {
                    failedBomToolGroups.add(type);
                }
            }
        }

        final List<DetectCodeLocation> codeLocations = bomToolEvaluations.stream()
                .filter(it -> it.wasExtractionSuccessful())
                .flatMap(it -> it.getExtraction().codeLocations.stream())
                .collect(Collectors.toList());

        reportManager.extractionsCompleted(bomToolEvaluations);

        final ExtractionResult result = new ExtractionResult(codeLocations, succesfulBomToolGroups, failedBomToolGroups);
        return result;
    }

}
