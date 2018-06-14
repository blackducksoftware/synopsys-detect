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

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.manager.result.codelocation.DetectCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.manager.result.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.BomToolSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.summary.Result;

@Component
public class DetectProjectManager implements SummaryResultReporter, ExitCodeReporter {
    private final Map<BomToolType, Result> bomToolSummaryResults = new HashMap<>();
    private ExitCodeType bomToolSearchExitCodeType;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private SearchManager searchManager;

    @Autowired
    private ExtractionManager extractionManager;

    @Autowired
    private DetectCodeLocationManager codeLocationManager;

    @Autowired
    private DetectBdioManager bdioManager;

    @Autowired
    private ExtractionSummaryReporter extractionSummaryReporter;

    public DetectProject createDetectProject() throws DetectUserFriendlyException, IntegrationException {

        final SearchResult searchResult = searchManager.performSearch();

        final ExtractionResult extractionResult = extractionManager.performExtractions(searchResult.getStrategyEvaluations());
        applyBomToolStatus(extractionResult.getSuccessfulBomToolTypes(), extractionResult.getFailedBomToolTypes());

        final String projectName = getProjectName(extractionResult.getRecommendedProjectName());
        final String projectVersion = getProjectVersionName(extractionResult.getRecommendedProjectVersion());

        final List<DetectCodeLocation> codeLocations = extractionResult.getDetectCodeLocations();

        final List<File> bdioFiles = new ArrayList<>();
        if (StringUtils.isBlank(detectConfiguration.getAggregateBomName())) {
            final DetectCodeLocationResult codeLocationResult = codeLocationManager.process(codeLocations, projectName, projectVersion);
            applyFailedBomToolStatus(codeLocationResult.getFailedBomToolTypes());

            final List<File> createdBdioFiles = bdioManager.createBdioFiles(codeLocationResult.getBdioCodeLocations(), projectName, projectVersion);
            bdioFiles.addAll(createdBdioFiles);

            extractionSummaryReporter.print(searchResult.getStrategyEvaluations(), codeLocationResult.getCodeLocationNames());
        } else {
            final File aggregateBdioFile = bdioManager.createAggregateBdioFile(codeLocations, projectName, projectVersion);
            bdioFiles.add(aggregateBdioFile);
        }

        final DetectProject project = new DetectProject(projectName, projectVersion, bdioFiles);

        return project;
    }

    private void applyFailedBomToolStatus(final Set<BomToolType> failedBomToolTypes) {
        for (final BomToolType type : failedBomToolTypes) {
            bomToolSummaryResults.put(type, Result.FAILURE);
        }
    }

    private void applyBomToolStatus(final Set<BomToolType> successBomToolTypes, final Set<BomToolType> failedBomToolTypes) {
        applyFailedBomToolStatus(failedBomToolTypes);
        for (final BomToolType type : successBomToolTypes) {
            if (!bomToolSummaryResults.containsKey(type)) {
                bomToolSummaryResults.put(type, Result.SUCCESS);
            }
        }
    }

    @Override
    public List<BomToolSummaryResult> getDetectSummaryResults() {
        final List<BomToolSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<BomToolType, Result> entry : bomToolSummaryResults.entrySet()) {
            detectSummaryResults.add(new BomToolSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<BomToolType, Result> entry : bomToolSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_BOM_TOOL;
            }
        }
        if (null != bomToolSearchExitCodeType) {
            return bomToolSearchExitCodeType;
        }
        return ExitCodeType.SUCCESS;
    }

    private String getProjectName(final String defaultProjectName) {
        String projectName = null;
        if (null != defaultProjectName) {
            projectName = defaultProjectName.trim();
        }
        if (StringUtils.isNotBlank(detectConfiguration.getProjectName())) {
            projectName = detectConfiguration.getProjectName();
        } else if (StringUtils.isBlank(projectName) && StringUtils.isNotBlank(detectConfiguration.getSourcePath())) {
            final String finalSourcePathPiece = detectFileFinder.extractFinalPieceFromPath(detectConfiguration.getSourcePath());
            projectName = finalSourcePathPiece;
        }
        return projectName;
    }

    private String getProjectVersionName(final String defaultVersionName) {
        String projectVersion = null;
        if (null != defaultVersionName) {
            projectVersion = defaultVersionName.trim();
        }

        if (StringUtils.isNotBlank(detectConfiguration.getProjectVersionName())) {
            projectVersion = detectConfiguration.getProjectVersionName();
        } else if (StringUtils.isBlank(projectVersion)) {
            if ("timestamp".equals(detectConfiguration.getDefaultProjectVersionScheme())) {
                final String timeformat = detectConfiguration.getDefaultProjectVersionTimeformat();
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                projectVersion = timeString;
            } else {
                projectVersion = detectConfiguration.getDefaultProjectVersionText();
            }
        }

        return projectVersion;
    }

}
