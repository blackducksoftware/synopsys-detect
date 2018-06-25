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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction.ExtractionResultType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class ExtractionSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(ExtractionSummaryReporter.class);

    public void print(final List<BomToolEvaluation> results, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final Map<File, List<BomToolEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.environment.getDirectory()));

        final List<ExtractionSummaryData> data = createData(byDirectory, codeLocationNameMap);

        final List<ExtractionSummaryData> sorted = sortByFilesystem(data);

        printDirectories(sorted);
    }

    private List<ExtractionSummaryData> createData(final Map<File, List<BomToolEvaluation>> byDirectory, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final List<ExtractionSummaryData> datas = new ArrayList<>();

        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final ExtractionSummaryData data = new ExtractionSummaryData();
            data.directory = file.toString();
            datas.add(data);

            for (final BomToolEvaluation result : results) {
                if (result.isSearchable()) {
                    data.searchable++;
                }
                if (result.isApplicable()) {
                    data.applicable++;
                }
                if (result.isExtractable()) {
                    data.extractable++;

                    if (result.extraction != null) {
                        data.codeLocationsExtracted += result.extraction.codeLocations.size();
                        result.extraction.codeLocations.stream().forEach(it -> {
                            final String name = codeLocationNameMap.get(it);
                            data.codeLocationNames.add(name);
                        });
                        if (result.extraction.result == ExtractionResultType.Success) {
                            data.success.add(result);
                        } else if (result.extraction.result == ExtractionResultType.Failure) {
                            data.failed.add(result);
                        } else if (result.extraction.result == ExtractionResultType.Exception) {
                            data.exception.add(result);
                        }
                    } else {
                        logger.warn("A bomTool was searchable, applicable and extractable but produced no extraction.");
                    }
                }
            }
        }

        return datas;
    }

    private List<ExtractionSummaryData> sortByFilesystem(final List<ExtractionSummaryData> raw) {
        return raw.stream().sorted((o1, o2) -> {
            final String[] pieces1 = o1.directory.split(Pattern.quote(File.separator));
            final String[] pieces2 = o2.directory.split(Pattern.quote(File.separator));
            final int min = Math.min(pieces1.length, pieces2.length);
            for (int i = 0; i < min; i++) {
                final int compared = pieces1[i].compareTo(pieces2[i]);
                if (compared != 0) {
                    return compared;
                }
            }
            return Integer.compare(pieces1.length, pieces2.length);
        }).collect(Collectors.toList());
    }

    private void printDirectories(final List<ExtractionSummaryData> data) {
        logger.info("");
        logger.info("");
        logger.info(ReportConstants.HEADING);
        logger.info("Extraction results:");
        logger.info(ReportConstants.HEADING);
        data.stream().forEach(it -> {
            if (it.applicable > 0) {
                logger.info(it.directory);
                logger.info("\tCode locations: " + it.codeLocationsExtracted);
                it.codeLocationNames.stream().forEach(name -> logger.info("\t\t" + name));
                if (it.success.size() > 0) {
                    logger.info("\tSuccess: " + it.success.stream().map(success -> success.bomTool.getDescriptiveName()).collect(Collectors.joining(", ")));
                }
                if (it.failed.size() > 0) {
                    logger.error("\tFailure: " + it.failed.stream().map(failed -> failed.bomTool.getDescriptiveName()).collect(Collectors.joining(", ")));
                }
                if (it.exception.size() > 0) {
                    logger.error("\tException: " + it.exception.stream().map(exception -> exception.bomTool.getDescriptiveName()).collect(Collectors.joining(", ")));
                }
            }
        });
        logger.info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

}
