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
package com.blackducksoftware.integration.hub.detect.manager;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.PreparationSummaryReporter;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyState;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.manager.result.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExceptionStrategyResult;

@Component
public class ExtractionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    @Autowired
    private List<Extractor> autowiredExtractors;

    @Autowired
    private PreparationSummaryReporter preparationSummaryReporter;

    @Autowired
    private ExtractionSummaryReporter extractionSummaryReporter;

    @Autowired
    private ExtractionReporter extractionReporter;

    private void extract(final List<StrategyEvaluation> results) {
        final List<StrategyEvaluation> extractable = results.stream().filter(result -> result.isExtractable()).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            logger.info("Extracting " + Integer.toString(i + 1) + " of " + Integer.toString(extractable.size()) + " (" + Integer.toString((int)Math.floor((i * 100.0f) / extractable.size())) + "%)");
            extract(extractable.get(i));
        }
    }

    private void prepare(final List<StrategyEvaluation> results) {
        for (final StrategyEvaluation result : results) {
            prepare(result);
        }
    }

    private void prepare(final StrategyEvaluation result) {
        if (result.isApplicable()) {
            try {
                result.extractable = result.strategy.extractable(result.environment, result.context);
            } catch (final Exception e) {
                result.extractable = new ExceptionStrategyResult(e);
            }
        }
    }

    private void extract(final StrategyEvaluation result) {
        if (result.isExtractable()) {
            extractionReporter.startedExtraction(result.strategy, result.context);
            result.extraction = execute(result.strategy, result.context);
            extractionReporter.endedExtraction(result.extraction);
        }

    }

    private Extraction execute(final Strategy strategy, final StrategyState context) {
        Extractor extractor = null;
        for (final Extractor possibleExtractor : autowiredExtractors) {
            if (possibleExtractor.getClass().equals(strategy.getExtractorClass())) {
                extractor = possibleExtractor;
            }
        }

        if (extractor == null) {
            return new Extraction.Builder().failure("No extractor was found.").build();
        }

        Extraction result;
        try {
            result = extractor.extract(context);
        } catch (final Exception e) {
            result = new Extraction.Builder().exception(e).build();
        }
        return result;
    }



    public ExtractionResult performExtractions(final List<StrategyEvaluation> strategyEvaluations) throws IntegrationException, DetectUserFriendlyException {

        final float appliedInSource = strategyEvaluations.stream()
                .filter(it -> it.isApplicable())
                .filter(it -> it.environment.getDepth() == 0)
                .count();

        prepare(strategyEvaluations);

        preparationSummaryReporter.print(strategyEvaluations);

        extract(strategyEvaluations);

        final HashSet<BomToolType> succesfulBomTools = new HashSet<>();
        final HashSet<BomToolType> failedBomTools = new HashSet<>();
        for (final StrategyEvaluation evaluation : strategyEvaluations) {
            final BomToolType type = evaluation.strategy.getBomToolType();
            if (evaluation.isApplicable()) {
                if (evaluation.isExtractable() && evaluation.isExtractionSuccess()) {
                    succesfulBomTools.add(type);
                } else {
                    failedBomTools.add(type);
                }
            }
        }

        final List<DetectCodeLocation> codeLocations = strategyEvaluations.stream()
                .filter(it -> it.isExtractionSuccess())
                .flatMap(it -> it.extraction.codeLocations.stream())
                .collect(Collectors.toList());

        final ExtractionResult result = new ExtractionResult(codeLocations, succesfulBomTools, failedBomTools);
        return result;
    }

}
