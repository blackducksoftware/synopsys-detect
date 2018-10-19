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
package com.blackducksoftware.integration.hub.detect.workflow;

import java.io.File;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.RequiredBomToolChecker.RequiredBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.project.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolProjectInfo;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class DetectProjectManager implements ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    private final Set<BomToolGroupType> applicableBomTools = new HashSet<>();
    private final Map<BomToolGroupType, Result> bomToolGroupSummaryResults = new HashMap<>();
    private ExitCodeType bomToolSearchExitCodeType;

    private final SearchManager searchManager;
    private final ExtractionManager extractionManager;
    private final DetectCodeLocationManager codeLocationManager;
    private final BdioManager bdioManager;
    private final BomToolNameVersionDecider bomToolNameVersionDecider;
    private final DetectConfiguration detectConfiguration;
    private final ReportManager reportManager;
    private final EventSystem eventSystem;

    public DetectProjectManager(final SearchManager searchManager, final ExtractionManager extractionManager, final DetectCodeLocationManager codeLocationManager, final BdioManager bdioManager,
        final BomToolNameVersionDecider bomToolNameVersionDecider, final DetectConfiguration detectConfiguration, final ReportManager reportManager, EventSystem eventSystem) {

        this.searchManager = searchManager;
        this.extractionManager = extractionManager;
        this.codeLocationManager = codeLocationManager;
        this.bdioManager = bdioManager;
        this.bomToolNameVersionDecider = bomToolNameVersionDecider;
        this.detectConfiguration = detectConfiguration;
        this.reportManager = reportManager;
        this.eventSystem = eventSystem;
    }

    public DetectProject createDetectProject() throws DetectUserFriendlyException, IntegrationException {
        final SearchResult searchResult = searchManager.performSearch();

        searchResult.getBomToolEvaluations().stream()
            .filter(it -> it.isApplicable())
            .map(it -> it.getBomTool().getBomToolGroupType())
            .forEach(it -> applicableBomTools.add(it));

        final ExtractionResult extractionResult = extractionManager.performExtractions(searchResult.getBomToolEvaluations());

        final NameVersion nameVersion = getProjectNameVersion(searchResult.getBomToolEvaluations());
        final String projectName = nameVersion.getName();
        final String projectVersion = nameVersion.getVersion();

        final List<DetectCodeLocation> codeLocations = extractionResult.getDetectCodeLocations();

        final List<File> bdioFiles = new ArrayList<>();
        if (StringUtils.isBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None))) {
            final DetectCodeLocationResult codeLocationResult = codeLocationManager.process(codeLocations, projectName, projectVersion);

            final List<File> createdBdioFiles = bdioManager.createBdioFiles(codeLocationResult.getBdioCodeLocations(), projectName, projectVersion);
            bdioFiles.addAll(createdBdioFiles);

            reportManager.codeLocationsCompleted(searchResult.getBomToolEvaluations(), codeLocationResult.getCodeLocationNames());
        } else {
            final File aggregateBdioFile = bdioManager.createAggregateBdioFile(codeLocations, projectName, projectVersion);
            bdioFiles.add(aggregateBdioFile);
        }

        final DetectProject project = new DetectProject(projectName, projectVersion, bdioFiles);

        return project;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<BomToolGroupType, Result> entry : bomToolGroupSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_BOM_TOOL;
            }
        }
        if (null != bomToolSearchExitCodeType) {
            return bomToolSearchExitCodeType;
        }
        final String requiredText = detectConfiguration.getProperty(DetectProperty.DETECT_REQUIRED_BOM_TOOL_TYPES, PropertyAuthority.None);
        if (!StringUtils.isBlank(requiredText)) {
            logger.info("Checking for required bom tools: " + requiredText);
            final RequiredBomToolChecker checker = new RequiredBomToolChecker();
            final RequiredBomToolResult result = checker.checkForMissingBomTools(requiredText, applicableBomTools);
            if (result.wereBomToolsMissing()) {
                result.getMissingBomTools().forEach(it -> logger.error("Required a bom tool of type " + it.toString() + " but an applicable bom tool of that type was not found."));
                return ExitCodeType.FAILURE_BOM_TOOL_REQUIRED;
            }
        }
        return ExitCodeType.SUCCESS;
    }

    private NameVersion getProjectNameVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        final Optional<NameVersion> bomToolSuggestedNameVersion = findBomToolProjectNameAndVersion(bomToolEvaluations);

        String projectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        if (StringUtils.isBlank(projectName) && bomToolSuggestedNameVersion.isPresent()) {
            projectName = bomToolSuggestedNameVersion.get().getName();
        }

        if (StringUtils.isBlank(projectName)) {
            logger.info("A project name could not be decided. Using the name of the source path.");
            projectName = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.None)).getName();
        }

        String projectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME, PropertyAuthority.None);
        if (StringUtils.isBlank(projectVersionName) && bomToolSuggestedNameVersion.isPresent()) {
            projectVersionName = bomToolSuggestedNameVersion.get().getVersion();
        }

        if (StringUtils.isBlank(projectVersionName)) {
            if ("timestamp".equals(detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME, PropertyAuthority.None))) {
                logger.info("A project version name could not be decided. Using the current timestamp.");
                final String timeformat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT, PropertyAuthority.None);
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                projectVersionName = timeString;
            } else {
                logger.info("A project version name could not be decided. Using the default version text.");
                projectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT, PropertyAuthority.None);
            }
        }

        return new NameVersion(projectName, projectVersionName);
    }

    private Optional<NameVersion> findBomToolProjectNameAndVersion(final List<BomToolEvaluation> bomToolEvaluations) {
        final String projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_BOM_TOOL, PropertyAuthority.None);
        BomToolGroupType preferredBomToolType = null;
        if (StringUtils.isNotBlank(projectBomTool)) {
            final String projectBomToolFixed = projectBomTool.toUpperCase();
            if (!BomToolGroupType.POSSIBLE_NAMES.contains(projectBomToolFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredBomToolType = BomToolGroupType.valueOf(projectBomToolFixed);
            }
        }

        final List<BomToolProjectInfo> allBomToolProjectInfo = createBomToolProjectInfo(bomToolEvaluations);
        return bomToolNameVersionDecider.decideProjectNameVersion(allBomToolProjectInfo, preferredBomToolType);
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
