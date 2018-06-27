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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.model.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.manager.result.codelocation.DetectCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.manager.result.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.BomToolGroupSummaryResult;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.SummaryResultProvider;
import com.blackducksoftware.integration.hub.detect.manager.result.summary.SummaryStatus;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.project.BomToolProjectInfo;
import com.blackducksoftware.integration.hub.detect.project.BomToolProjectInfoDecider;
import com.blackducksoftware.integration.util.NameVersion;

@Component
public class DetectProjectManager implements SummaryResultProvider, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    private final Map<BomToolGroupType, SummaryStatus> bomToolGroupSummaryResults = new HashMap<>();
    private ExitCodeType bomToolSearchExitCodeType;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private SearchManager searchManager;

    @Autowired
    private ExtractionManager extractionManager;

    @Autowired
    private DetectCodeLocationManager codeLocationManager;

    @Autowired
    private BdioManager bdioManager;

    @Autowired
    private ExtractionSummaryReporter extractionSummaryReporter;

    @Autowired
    private BomToolProjectInfoDecider bomToolProjectInfoDecider;

    public DetectProject createDetectProject() throws DetectUserFriendlyException, IntegrationException {
        final SearchResult searchResult = searchManager.performSearch();

        final ExtractionResult extractionResult = extractionManager.performExtractions(searchResult.getBomToolEvaluations());
        applyBomToolGroupStatus(extractionResult.getSuccessfulBomToolTypes(), extractionResult.getFailedBomToolTypes());

        final NameVersion nameVersion = getProjectNameVersion(searchResult.getBomToolEvaluations());
        final String projectName = nameVersion.getName();
        final String projectVersion = nameVersion.getVersion();

        final List<DetectCodeLocation> codeLocations = extractionResult.getDetectCodeLocations();

        final List<File> bdioFiles = new ArrayList<>();
        if (StringUtils.isBlank(detectConfiguration.getAggregateBomName())) {
            final DetectCodeLocationResult codeLocationResult = codeLocationManager.process(codeLocations, projectName, projectVersion);
            applyFailedBomToolGroupStatus(codeLocationResult.getFailedBomToolGroupTypes());

            final List<File> createdBdioFiles = bdioManager.createBdioFiles(codeLocationResult.getBdioCodeLocations(), projectName, projectVersion);
            bdioFiles.addAll(createdBdioFiles);

            extractionSummaryReporter.print(searchResult.getBomToolEvaluations(), codeLocationResult.getCodeLocationNames());
        } else {
            final File aggregateBdioFile = bdioManager.createAggregateBdioFile(codeLocations, projectName, projectVersion);
            bdioFiles.add(aggregateBdioFile);
        }

        final DetectProject project = new DetectProject(projectName, projectVersion, bdioFiles);

        return project;
    }

    @Override
    public List<BomToolGroupSummaryResult> getDetectSummaryResults() {
        final List<BomToolGroupSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<BomToolGroupType, SummaryStatus> entry : bomToolGroupSummaryResults.entrySet()) {
            detectSummaryResults.add(new BomToolGroupSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<BomToolGroupType, SummaryStatus> entry : bomToolGroupSummaryResults.entrySet()) {
            if (SummaryStatus.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_BOM_TOOL;
            }
        }
        if (null != bomToolSearchExitCodeType) {
            return bomToolSearchExitCodeType;
        }
        return ExitCodeType.SUCCESS;
    }

    private void applyFailedBomToolGroupStatus(final Set<BomToolGroupType> failedBomToolGroupTypes) {
        for (final BomToolGroupType type : failedBomToolGroupTypes) {
            bomToolGroupSummaryResults.put(type, SummaryStatus.FAILURE);
        }
    }

    private void applyBomToolGroupStatus(final Set<BomToolGroupType> succeededBomToolGroupTypes, final Set<BomToolGroupType> failedBomToolGroupTypes) {
        applyFailedBomToolGroupStatus(failedBomToolGroupTypes);
        for (final BomToolGroupType type : succeededBomToolGroupTypes) {
            if (!bomToolGroupSummaryResults.containsKey(type)) {
                bomToolGroupSummaryResults.put(type, SummaryStatus.SUCCESS);
            }
        }
    }

    private NameVersion getProjectNameVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        final Optional<NameVersion> bomToolSuggestedNameVersion = findBomToolProjectNameAndVersion(bomToolEvaluations);

        String projectName = detectConfiguration.getProjectName();
        if (StringUtils.isBlank(projectName) && bomToolSuggestedNameVersion.isPresent()) {
            projectName = bomToolSuggestedNameVersion.get().getName();
        }

        if (StringUtils.isBlank(projectName)) {
            logger.info("A project name could not be decided. Using the name of the source path.");
            projectName = detectConfiguration.getSourceDirectory().getName();
        }

        String projectVersionName = detectConfiguration.getProjectVersionName();
        if (StringUtils.isBlank(projectVersionName) && bomToolSuggestedNameVersion.isPresent()) {
            projectVersionName = bomToolSuggestedNameVersion.get().getVersion();
        }

        if (StringUtils.isBlank(projectVersionName)) {
            if ("timestamp".equals(detectConfiguration.getDefaultProjectVersionScheme())) {
                logger.info("A project version name could not be decided. Using the current timestamp.");
                final String timeformat = detectConfiguration.getDefaultProjectVersionTimeformat();
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                projectVersionName = timeString;
            } else {
                logger.info("A project version name could not be decided. Using the default version text.");
                projectVersionName = detectConfiguration.getDefaultProjectVersionText();
            }
        }

        return new NameVersion(projectName, projectVersionName);
    }

    private Optional<NameVersion> findBomToolProjectNameAndVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        final String projectBomTool = detectConfiguration.getDetectProjectBomTool();
        Optional<BomToolGroupType> preferredBomToolType = Optional.empty();
        if (StringUtils.isNotBlank(projectBomTool)) {
            final String projectBomToolFixed = projectBomTool.toUpperCase();
            if (!BomToolGroupType.POSSIBLE_NAMES.contains(projectBomToolFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredBomToolType = Optional.of(BomToolGroupType.valueOf(projectBomToolFixed));
            }
        }

        final List<BomToolProjectInfo> allBomToolProjectInfo = createBomToolProjectInfo(bomToolEvaluations);
        return bomToolProjectInfoDecider.decideProjectInfo(allBomToolProjectInfo, preferredBomToolType);
    }

    private List<BomToolProjectInfo> createBomToolProjectInfo(final List<BomToolEvaluation> bomToolEvaluations) {
        return bomToolEvaluations.stream()
                .filter(it -> it.wasExtractionSuccessful())
                .filter(it -> it.getExtraction().projectName != null)
                .map(it -> {
                    final NameVersion nameVersion = new NameVersion(it.getExtraction().projectName, it.getExtraction().projectVersion);
                    final BomToolProjectInfo possibility = new BomToolProjectInfo(it.getBomTool().getBomToolGroupType(), it.getEnvironment().getDepth(), nameVersion);
                    return possibility;
                })
                .collect(Collectors.toList());
    }

}
