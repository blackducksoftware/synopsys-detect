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
package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager;
import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;

@Component
public class PreparationSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(PreparationSummaryReporter.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

    public void print(final List<StrategyEvaluation> results) {
        final Map<File, List<StrategyEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.environment.getDirectory()));

        printDirectories(byDirectory);

    }

    private void printDirectories(final Map<File, List<StrategyEvaluation>> byDirectory) {
        logger.info("");
        logger.info("");
        info(ReportConstants.HEADING);
        info("Preparation for extraction");
        info(ReportConstants.HEADING);
        for (final File file : byDirectory.keySet()) {
            final List<StrategyEvaluation> results = byDirectory.get(file);

            final List<String> ready = new ArrayList<>();
            final List<String> failed = new ArrayList<>();

            for (final StrategyEvaluation result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.isApplicable()) {
                    if (result.extractable.getPassed()) {
                        ready.add(strategyName);
                    } else {
                        failed.add("FAILED: " + strategyName + " - " + result.extractable.toDescription());
                    }
                }
            }
            if (ready.size() > 0 || failed.size() > 0) {
                info(file.getAbsolutePath());
                if (ready.size() > 0) {
                    info("\t READY: " + ready.stream().sorted().collect(Collectors.joining(", ")));
                }
                if (failed.size() > 0) {
                    failed.stream().sorted().forEach(it -> info("\t" + it));
                }

            }
        }
        info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

    private void info(final String line) {
        logger.info(line);
        diagnosticsManager.printToPreparationReport(line);
    }

}
