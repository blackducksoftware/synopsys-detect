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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

@Component
public class PreparationSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(PreparationSummaryReporter.class);

    public void print(final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));

        printDirectories(byDirectory);

    }

    private void printDirectories(final Map<File, List<BomToolEvaluation>> byDirectory) {
        logger.info("");
        logger.info("");
        logger.info(ReportConstants.HEADING);
        logger.info("Preparation for extraction");
        logger.info(ReportConstants.HEADING);
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final List<String> ready = new ArrayList<>();
            final List<String> failed = new ArrayList<>();

            for (final BomToolEvaluation result : results) {
                if (result.isApplicable()) {
                    if (result.isExtractable()) {
                        ready.add(result.getBomTool().getDescriptiveName());
                    } else {
                        failed.add("FAILED: " + result.getBomTool().getDescriptiveName() + " - " + result.getExtractabilityMessage());
                    }
                }
            }
            if (ready.size() > 0 || failed.size() > 0) {
                logger.info(file.getAbsolutePath());
                if (ready.size() > 0) {
                    logger.info("\t READY: " + ready.stream().sorted().collect(Collectors.joining(", ")));
                }
                if (failed.size() > 0) {
                    failed.stream().sorted().forEach(it -> logger.error("\t" + it));
                }

            }
        }
        logger.info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

}
