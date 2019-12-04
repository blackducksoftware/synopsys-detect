/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectorFileFilter;
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectorFilter;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.util.DetectEnumUtil;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.util.EnumUtils;

// TODO: Create private method for accessing properties that assumes a PropertyAuthority of NONE.
public class DetectConfigurationFactory {
    private final DetectConfiguration detectConfiguration;

    public DetectConfigurationFactory(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public RunOptions createRunOptions() {
        Optional<Boolean> sigScanDisabled = Optional.empty();

        // TODO: Fix this when deprecated properties are removed
        // This is because it is double deprecated so we must check if either property is set.
        final boolean originalPropertySet = detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED);
        final boolean newPropertySet = detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED);
        if (originalPropertySet || newPropertySet) {
            sigScanDisabled = Optional.of(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, PropertyAuthority.NONE));
        }

        Optional<Boolean> polarisEnabled = Optional.empty();
        if (detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_SWIP_ENABLED)) {
            polarisEnabled = Optional.of(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SWIP_ENABLED, PropertyAuthority.NONE));
        }
        final String includedTools = detectConfiguration.getProperty(DetectProperty.DETECT_TOOLS, PropertyAuthority.NONE);
        final String excludedTools = detectConfiguration.getProperty(DetectProperty.DETECT_TOOLS_EXCLUDED, PropertyAuthority.NONE);
        final DetectToolFilter detectToolFilter = new DetectToolFilter(excludedTools, includedTools, sigScanDisabled, polarisEnabled);

        final boolean unmapCodeLocations = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_UNMAP, PropertyAuthority.NONE);
        final String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.NONE);
        final String preferredTools = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_TOOL, PropertyAuthority.NONE);
        final boolean useBdio2 = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BDIO2_ENABLED, PropertyAuthority.NONE);

        return new RunOptions(unmapCodeLocations, aggregateName, preferredTools, detectToolFilter, useBdio2);
    }

    public DirectoryOptions createDirectoryOptions() {
        final String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.DIRECTORY_MANAGER);
        final String outputPath = detectConfiguration.getProperty(DetectProperty.DETECT_OUTPUT_PATH, PropertyAuthority.DIRECTORY_MANAGER);
        final String bdioPath = detectConfiguration.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH, PropertyAuthority.DIRECTORY_MANAGER);
        final String scanPath = detectConfiguration.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH, PropertyAuthority.DIRECTORY_MANAGER);
        final String toolsOutputPath = detectConfiguration.getProperty(DetectProperty.DETECT_TOOLS_OUTPUT_PATH, PropertyAuthority.DIRECTORY_MANAGER);

        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath);
    }

    public AirGapOptions createAirGapOptions() {
        final String gradleOverride = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AIR_GAP_MANAGER);
        final String nugetOverride = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AIR_GAP_MANAGER);
        final String dockerOverride = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AIR_GAP_MANAGER);

        return new AirGapOptions(dockerOverride, gradleOverride, nugetOverride);
    }

    public DetectorFinderOptions createSearchOptions(final Path sourcePath) {
        //Normal settings
        final int maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_DETECTOR_SEARCH_DEPTH, PropertyAuthority.NONE);

        //File Filter
        final List<String> excludedDirectories = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION, PropertyAuthority.NONE));
        final List<String> excludedDirectoryPatterns = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS, PropertyAuthority.NONE));
        final List<String> excludedDirectoryPaths = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS, PropertyAuthority.NONE));
        final DetectDetectorFileFilter fileFilter = new DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryPatterns);

        return new DetectorFinderOptions(fileFilter, maxDepth);
    }

    public DetectorEvaluationOptions createDetectorEvaluationOptions() {
        final boolean forceNestedSearch = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DETECTOR_SEARCH_CONTINUE, PropertyAuthority.NONE);

        //Detector Filter
        final String excluded = detectConfiguration.getProperty(DetectProperty.DETECT_EXCLUDED_DETECTOR_TYPES, PropertyAuthority.NONE).toUpperCase();
        final String included = detectConfiguration.getProperty(DetectProperty.DETECT_INCLUDED_DETECTOR_TYPES, PropertyAuthority.NONE).toUpperCase();
        final DetectDetectorFilter detectorFilter = new DetectDetectorFilter(excluded, included);

        return new DetectorEvaluationOptions(forceNestedSearch, detectorFilter);
    }

    public BdioOptions createBdioOptions() {
        final String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.NONE);
        return new BdioOptions(aggregateName);

    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(final String sourceDirectoryName) {
        final String overrideProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.NONE);
        final String overrideProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME, PropertyAuthority.NONE);
        final String defaultProjectVersionText = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT, PropertyAuthority.NONE);
        final String defaultProjectVersionScheme = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME, PropertyAuthority.NONE);
        final String defaultProjectVersionFormat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT, PropertyAuthority.NONE);
        return new ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat);
    }

    public DetectProjectServiceOptions createDetectProjectServiceOptions() throws DetectUserFriendlyException {
        final String projectVersionPhase = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_PHASE, PropertyAuthority.NONE);
        final String projectVersionDistribution = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_DISTRIBUTION, PropertyAuthority.NONE);
        final Integer projectTier = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PROJECT_TIER, PropertyAuthority.NONE);
        final String projectDescription = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DESCRIPTION, PropertyAuthority.NONE);
        final String projectVersionNotes = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NOTES, PropertyAuthority.NONE);
        final String[] cloneCategories = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_CLONE_CATEGORIES, PropertyAuthority.NONE);
        final Boolean projectLevelAdjustments = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_LEVEL_ADJUSTMENTS, PropertyAuthority.NONE);
        final Boolean forceProjectVersionUpdate = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_VERSION_UPDATE, PropertyAuthority.NONE);
        final String cloneVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_CLONE_PROJECT_VERSION_NAME, PropertyAuthority.NONE);
        final String projectVersionNickname = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NICKNAME, PropertyAuthority.NONE);
        final String applicationId = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_APPLICATION_ID, PropertyAuthority.NONE);
        final String[] groups = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_USER_GROUPS, PropertyAuthority.NONE);
        final String[] tags = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_TAGS, PropertyAuthority.NONE);
        final String parentProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PARENT_PROJECT_NAME, PropertyAuthority.NONE);
        final String parentProjectVersion = detectConfiguration.getProperty(DetectProperty.DETECT_PARENT_PROJECT_VERSION_NAME, PropertyAuthority.NONE);
        final Boolean cloneLatestProjectVersion = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLONE_PROJECT_VERSION_LATEST, PropertyAuthority.NONE);

        final DetectCustomFieldParser parser = new DetectCustomFieldParser();
        final CustomFieldDocument customFieldDocument = parser.parseCustomFieldDocument(detectConfiguration.getCurrentUnderlyingProperties());

        return new DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
            projectVersionNickname, applicationId, tags, groups, parentProjectName, parentProjectVersion, cloneLatestProjectVersion, customFieldDocument);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() {
        final String[] signatureScannerPaths = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, PropertyAuthority.NONE);
        final String[] exclusionPatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS, PropertyAuthority.NONE);
        final String[] exclusionNamePatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS, PropertyAuthority.NONE);

        final Integer scanMemory = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY, PropertyAuthority.NONE);
        final Integer parallelProcessors = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PARALLEL_PROCESSORS, PropertyAuthority.NONE);
        final Boolean dryRun = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, PropertyAuthority.NONE);
        final Boolean uploadSource = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE, PropertyAuthority.NONE);
        final String codeLocationPrefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.NONE);
        final String codeLocationSuffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.NONE);
        final String additionalArguments = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS, PropertyAuthority.NONE);
        final Integer maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH, PropertyAuthority.NONE);

        final String offlineLocalScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.NONE);
        final String onlineLocalScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH, PropertyAuthority.NONE);

        final String userProvidedScannerInstallUrl = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.NONE);

        final String snippetMatchingString = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING, PropertyAuthority.NONE).trim().toUpperCase();
        Optional<SnippetMatching> snippetMatchingEnum = DetectEnumUtil.getValueOf(SnippetMatching.class, snippetMatchingString);
        if (snippetMatchingString.equals("NONE") || !snippetMatchingEnum.isPresent()) {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE, PropertyAuthority.NONE)) {
                snippetMatchingEnum = Optional.of(SnippetMatching.SNIPPET_MATCHING);
            }
        }
        return new BlackDuckSignatureScannerOptions(signatureScannerPaths, exclusionPatterns, exclusionNamePatterns, offlineLocalScannerInstallPath, onlineLocalScannerInstallPath, userProvidedScannerInstallUrl, scanMemory,
            parallelProcessors, dryRun,
            snippetMatchingEnum.orElse(null), uploadSource, codeLocationPrefix, codeLocationSuffix, additionalArguments, maxDepth);
    }

    public BlackDuckPostOptions createBlackDuckPostOptions() {
        final boolean waitForResults = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_WAIT_FOR_RESULTS, PropertyAuthority.NONE);
        final boolean runRiskReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.NONE);
        final boolean runNoticesReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.NONE);
        final String riskReportPdfPath = detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH, PropertyAuthority.NONE);
        final String noticesReportPath = detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH, PropertyAuthority.NONE);
        final String policySeverities = detectConfiguration.getPropertyValueAsString(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.NONE);
        final List<PolicySeverityType> severitiesToFailPolicyCheck = EnumUtils.parseCommaDelimitted(policySeverities.toUpperCase(), PolicySeverityType.class);

        return new BlackDuckPostOptions(waitForResults, runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath, severitiesToFailPolicyCheck);
    }

    public long getTimeoutInSeconds() {
        if (detectConfiguration.wasPropertyActuallySet(DetectProperty.DETECT_API_TIMEOUT)) {
            final long timeout = detectConfiguration.getLongProperty(DetectProperty.DETECT_API_TIMEOUT, PropertyAuthority.NONE);
            final long timeoutInSeconds = timeout / 1000;
            return timeoutInSeconds;
        } else {
            return detectConfiguration.getLongProperty(DetectProperty.DETECT_REPORT_TIMEOUT, PropertyAuthority.NONE);
        }

    }

    public BinaryScanOptions createBinaryScanOptions() {
        final String singleTarget = detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.NONE);
        final String[] mutlipleTargets = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS, PropertyAuthority.NONE);
        final String codeLocationPrefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.NONE);
        final String codeLocationSuffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.NONE);
        return new BinaryScanOptions(singleTarget, Arrays.asList(mutlipleTargets), codeLocationPrefix, codeLocationSuffix);
    }
}
