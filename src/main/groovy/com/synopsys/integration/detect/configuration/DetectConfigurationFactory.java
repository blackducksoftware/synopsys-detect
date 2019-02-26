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
package com.synopsys.integration.detect.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.util.EnumUtilExtension;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.workflow.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.file.AirGapOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.hub.BlackduckReportOptions;
import com.synopsys.integration.detect.workflow.hub.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.hub.PolicyCheckOptions;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detector.search.SearchOptions;
import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;

public class DetectConfigurationFactory {
    DetectConfiguration detectConfiguration;

    public DetectConfigurationFactory(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public RunOptions createRunOptions() {
        Optional<Boolean> sigScanDisabled = Optional.empty();

        //TODO: Fix this when deprecated properties are removed
        //This is because it is double deprecated so we must check if either property is set.
        final boolean originalPropertySet = detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED);
        final boolean newPropertySet = detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED);
        if (originalPropertySet || newPropertySet) {
            sigScanDisabled = Optional.of(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.None));
        }

        Optional<Boolean> polarisEnabled = Optional.empty();
        if (detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_SWIP_ENABLED)) {
            polarisEnabled = Optional.of(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SWIP_ENABLED, PropertyAuthority.None));
        }
        final String includedTools = detectConfiguration.getProperty(DetectProperty.DETECT_TOOLS, PropertyAuthority.None);
        final String excludedTools = detectConfiguration.getProperty(DetectProperty.DETECT_TOOLS_EXCLUDED, PropertyAuthority.None);
        final DetectToolFilter detectToolFilter = new DetectToolFilter(excludedTools, includedTools, sigScanDisabled, polarisEnabled);

        final boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.None);
        final String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);
        final String preferredTools = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_TOOL, PropertyAuthority.None);
        return new RunOptions(unmapCodeLocations, aggregateName, preferredTools, detectToolFilter);
    }

    public DirectoryOptions createDirectoryOptions() {
        final String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.DirectoryManager);
        final String outputPath = detectConfiguration.getProperty(DetectProperty.DETECT_OUTPUT_PATH, PropertyAuthority.DirectoryManager);
        final String bdioPath = detectConfiguration.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH, PropertyAuthority.DirectoryManager);
        final String scanPath = detectConfiguration.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH, PropertyAuthority.DirectoryManager);

        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath);
    }

    public AirGapOptions createAirGapOptions() {
        final String gradleOverride = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);
        final String nugetOverride = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);
        final String dockerOverride = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);

        return new AirGapOptions(dockerOverride, gradleOverride, nugetOverride);
    }

    public SearchOptions createSearchOptions(final File directory) {
        final List<String> excludedDirectories = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION, PropertyAuthority.None));
        final List<String> excludedDirectoryPatterns = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS, PropertyAuthority.None));
        final boolean forceNestedSearch = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DETECTOR_SEARCH_CONTINUE, PropertyAuthority.None);
        final int maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_DETECTOR_SEARCH_DEPTH, PropertyAuthority.None);
        final String excluded = detectConfiguration.getProperty(DetectProperty.DETECT_EXCLUDED_DETECTOR_TYPES, PropertyAuthority.None).toUpperCase();
        final String included = detectConfiguration.getProperty(DetectProperty.DETECT_INCLUDED_DETECTOR_TYPES, PropertyAuthority.None).toUpperCase();
        final DetectOverrideableFilter bomToolFilter = new DetectOverrideableFilter(excluded, included);
        return new SearchOptions(directory, excludedDirectories, excludedDirectoryPatterns, forceNestedSearch, maxDepth, null);//TODO Create filter
    }

    public BdioOptions createBdioOptions() {
        final String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);
        return new BdioOptions(aggregateName);

    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(final String sourceDirectoryName) {
        final String overrideProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        final String overrideProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME, PropertyAuthority.None);
        final String defaultProjectVersionText = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT, PropertyAuthority.None);
        final String defaultProjectVersionScheme = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME, PropertyAuthority.None);
        final String defaultProjectVersionFormat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT, PropertyAuthority.None);
        return new ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat);
    }

    public DetectProjectServiceOptions createDetectProjectServiceOptions() {
        final String projectVersionPhase = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_PHASE, PropertyAuthority.None);
        final String projectVersionDistribution = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_DISTRIBUTION, PropertyAuthority.None);
        final Integer projectTier = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PROJECT_TIER, PropertyAuthority.None);
        final String projectDescription = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DESCRIPTION, PropertyAuthority.None);
        final String projectVersionNotes = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NOTES, PropertyAuthority.None);
        final String[] cloneCategories = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_CLONE_CATEGORIES, PropertyAuthority.None);
        final Boolean projectLevelAdjustments = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_LEVEL_ADJUSTMENTS, PropertyAuthority.None);
        final Boolean forceProjectVersionUpdate = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_VERSION_UPDATE, PropertyAuthority.None);
        final String cloneVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_CLONE_PROJECT_VERSION_NAME, PropertyAuthority.None);
        final String projectVersionNickname = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NICKNAME, PropertyAuthority.None);
        final String applicationId = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_APPLICATION_ID, PropertyAuthority.None);
        return new DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
            projectVersionNickname, applicationId);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() {
        final String[] signatureScannerPaths = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, PropertyAuthority.None);
        final String[] exclusionPatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS, PropertyAuthority.None);
        final String[] exclusionNamePatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS, PropertyAuthority.None);

        final Integer scanMemory = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY, PropertyAuthority.None);
        final Integer parrallelProcessors = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, PropertyAuthority.None);
        final Boolean cleanupOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None);
        final Boolean dryRun = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, PropertyAuthority.None);
        final Boolean snippetMatching = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE, PropertyAuthority.None);
        final String codeLocationPrefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
        final String codeLocationSuffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);
        final String additionalArguments = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS, PropertyAuthority.None);
        final Integer maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH, PropertyAuthority.None);
        return new BlackDuckSignatureScannerOptions(signatureScannerPaths, exclusionPatterns, exclusionNamePatterns, scanMemory, parrallelProcessors, cleanupOutput, dryRun,
            snippetMatching, codeLocationPrefix, codeLocationSuffix, additionalArguments, maxDepth);
    }

    public BlackduckReportOptions createReportOptions() {
        final boolean runRiskReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None);
        final boolean runNoticesReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None);
        final String riskReportPdfPath = detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH, PropertyAuthority.None);
        final String noticesReportPath = detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH, PropertyAuthority.None);
        return new BlackduckReportOptions(runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath);
    }

    public PolicyCheckOptions createPolicyCheckOptions() {
        String policySeverities = detectConfiguration.getPropertyValueAsString(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None);
        final List<PolicySeverityType> severitiesToFailPolicyCheck = EnumUtilExtension.parseCommaDelimitted(policySeverities.toUpperCase(), PolicySeverityType.class);
        return new PolicyCheckOptions(severitiesToFailPolicyCheck);
    }

    public long getTimeoutInSeconds() {
        if (detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_API_TIMEOUT)) {
            final long timeout = detectConfiguration.getLongProperty(DetectProperty.DETECT_API_TIMEOUT, PropertyAuthority.None);
            final long timeoutInSeconds = timeout / 1000;
            return timeoutInSeconds;
        } else {
            return detectConfiguration.getLongProperty(DetectProperty.DETECT_REPORT_TIMEOUT, PropertyAuthority.None);
        }

    }
}
