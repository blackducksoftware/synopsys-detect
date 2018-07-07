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
package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class SearchSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(SearchSummaryReporter.class);

    public void print(final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));

        printDirectoriesInfo(byDirectory);
        printDirectoriesDebug(byDirectory);

    }

    private void printDirectoriesInfo(final Map<File, List<BomToolEvaluation>> byDirectory) {
        logger.info("");
        logger.info("");
        logger.info(ReportConstants.HEADING);
        logger.info("Search results");
        logger.info(ReportConstants.HEADING);
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final List<String> applied = results.stream()
                    .filter(result -> result.isApplicable())
                    .map(result -> result.getBomTool().getDescriptiveName())
                    .collect(Collectors.toList());

            if (applied.size() > 0) {
                logger.info(file.getAbsolutePath());
                logger.info("\tAPPLIES: " + applied.stream().sorted().collect(Collectors.joining(", ")));
            }
        }
        logger.info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

    private void printDirectoriesDebug(final Map<File, List<BomToolEvaluation>> byDirectory) {
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);
            final List<String> toPrint = new ArrayList<>();

            for (final BomToolEvaluation result : results) {
                final String bomToolName = result.getBomTool().getDescriptiveName();
                if (result.isApplicable()) {
                    toPrint.add("      APPLIED: " + bomToolName + " - Search: " + result.getSearchabilityMessage() + " Applicable: " + result.getApplicabilityMessage());
                } else {

                    final String didNotApplyPrefix = "DID NOT APPLY: " + bomToolName + " - ";
                    if (BomToolEvaluation.NO_MESSAGE.equals(result.getApplicabilityMessage())) {
                        toPrint.add(didNotApplyPrefix + result.getSearchabilityMessage());
                    } else {
                        toPrint.add(didNotApplyPrefix + result.getApplicabilityMessage());
                    }
                }
            }
            if (toPrint.size() > 0) {

                debug(ReportConstants.HEADING);
                debug("Detailed search results for directory");
                debug(file.getAbsolutePath());
                debug(ReportConstants.HEADING);
                toPrint.stream().sorted().forEach(it -> debug(it));
                debug(ReportConstants.HEADING);
            }
        }
    }

    private void debug(final String line) {
        logger.debug(line);
    }

}
