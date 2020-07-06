/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect;

import static java.util.Collections.emptyList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.api.generated.enumeration.LicenseFamilyLicenseFamilyRiskRulesReleaseDistributionType;
import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.manual.throwaway.generated.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.IndividualFileMatching;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.path.PathValue;
import com.synopsys.integration.detect.configuration.DetectCustomFieldParser;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectorSearchExcludedDirectories;
import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.enums.DefaultVersionNameScheme;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.DetectFileFinder;
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectorFileFilter;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedIndividualFileMatchingMode;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedSnippetMode;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapOptions;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeOptions;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

public class DetectConfigurationFactoryJava {

    private PropertyConfiguration detectConfiguration;
    private PathResolver pathResolver;

    //#region Prefer These Over Any Property
    public Long findTimeoutInSeconds() throws InvalidPropertyException {
        if (detectConfiguration.wasPropertyProvided(DetectProperties.Companion.getDETECT_API_TIMEOUT())) {
            Long timeout = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_API_TIMEOUT());
            return timeout / 1000;
        } else {
            return detectConfiguration.getValue(DetectProperties.Companion.getDETECT_REPORT_TIMEOUT());
        }
    }

    public int findParallelProcessors() throws InvalidPropertyException {
        int provided = 0;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS())) {
            provided = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS());
        }
        else if (detectConfiguration.wasPropertyProvided(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS())) {
            provided = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS());
        }
        else if (detectConfiguration.wasPropertyProvided(DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS())) {
            provided = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS());
        }
        else {
            provided = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PARALLEL_PROCESSORS());
        }

        if (provided > 0) {
            return provided;
        } else {
            return findRuntimeProcessors();
        }
    }

    private int findRuntimeProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Nullable
    public SnippetMatching findSnippetMatching() throws InvalidPropertyException {
        ExtendedEnumValue<ExtendedSnippetMode, SnippetMatching> snippetMatching = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING());

        SnippetMatching deprecatedSnippetMatching;
        if (Boolean.TRUE.equals(detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE()))) {
            deprecatedSnippetMatching = SnippetMatching.SNIPPET_MATCHING;
        } else {
            deprecatedSnippetMatching = null;
        }

        if (snippetMatching.getBaseValue().isPresent()) {
            return snippetMatching.getBaseValue().get();
        }

        if (snippetMatching.getExtendedValue().isPresent()) {
            return deprecatedSnippetMatching;
        }

        return null;
    }

    @Nullable
    private IndividualFileMatching findIndividualFileMatching() throws InvalidPropertyException {
        ExtendedEnumValue<ExtendedIndividualFileMatchingMode, IndividualFileMatching> individualFileMatching = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING());

        if (individualFileMatching.getBaseValue().isPresent()) {
            return individualFileMatching.getBaseValue().get();
        }

        return null;
    }

    //#endregion

    //#region Creating Connections
    private ProxyInfo createBlackDuckProxyInfo() throws DetectUserFriendlyException{
        String proxyUsername = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_USERNAME(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_USERNAME()).orElse(null);
        String proxyPassword = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_PASSWORD(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_PASSWORD()).orElse(null);
        String proxyHost = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_HOST(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_HOST()).orElse(null);
        String proxyPort = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_PORT(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_PORT()).orElse(null);
        String proxyNtlmDomain = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_NTLM_DOMAIN(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_NTLM_DOMAIN()).orElse(null);
        String proxyNtlmWorkstation = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_NTLM_WORKSTATION(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_NTLM_WORKSTATION()).orElse(null);

        CredentialsBuilder proxyCredentialsBuilder = new CredentialsBuilder();
        proxyCredentialsBuilder.setUsername(proxyUsername);
        proxyCredentialsBuilder.setPassword(proxyPassword);
        Credentials proxyCredentials;
        try {
            proxyCredentials = proxyCredentialsBuilder.build();
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }

        ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();

        proxyInfoBuilder.setCredentials(proxyCredentials);
        proxyInfoBuilder.setHost(proxyHost);
        proxyInfoBuilder.setPort(NumberUtils.toInt(proxyPort, 0));
        proxyInfoBuilder.setNtlmDomain(proxyNtlmDomain);
        proxyInfoBuilder.setNtlmWorkstation(proxyNtlmWorkstation);
        try {
            return proxyInfoBuilder.build();
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
    }

    public ProductBootOptions createProductBootOptions() {
        Boolean ignoreFailures = detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_IGNORE_CONNECTION_FAILURES());
        Boolean testConnections = detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_TEST_CONNECTION());
        return new ProductBootOptions(ignoreFailures, testConnections);
    }

    private ConnectionDetails createConnectionDetails() throws DetectUserFriendlyException, InvalidPropertyException {
        Boolean alwaysTrust = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_TRUST_CERT(), DetectProperties.Companion.getBLACKDUCK_HUB_TRUST_CERT());
        List<String> proxyIgnoredHosts = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_PROXY_IGNORED_HOSTS(), DetectProperties.Companion.getBLACKDUCK_HUB_PROXY_IGNORED_HOSTS());
        List<Pattern> proxyPatterns = proxyIgnoredHosts.stream()
        .map(it -> Pattern.compile(it))
            .collect(Collectors.toList());
        ProxyInfo proxyInformation = createBlackDuckProxyInfo();
        return new ConnectionDetails(proxyInformation, proxyPatterns, findTimeoutInSeconds(), alwaysTrust);
    }

    public BlackDuckConnectionDetails createBlackDuckConnectionDetails() throws InvalidPropertyException, DetectUserFriendlyException {
        Boolean offline = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_OFFLINE_MODE(), DetectProperties.Companion.getBLACKDUCK_HUB_OFFLINE_MODE());
        String blackduckUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getBLACKDUCK_URL(), DetectProperties.Companion.getBLACKDUCK_HUB_URL()).orElse(null);
        Set<String> allBlackDuckKeys = new BlackDuckServerConfigBuilder().getPropertyKeys().stream()
                                           .filter(it -> !(it.toLowerCase().contains("proxy")))
                                           .collect(Collectors.toSet());
        Map<String, String> blackDuckProperties = detectConfiguration.getRaw(allBlackDuckKeys);

        return new BlackDuckConnectionDetails(offline, blackduckUrl, blackDuckProperties, findParallelProcessors(), createConnectionDetails());
    }
    //#endregion

    public PolarisServerConfigBuilder createPolarisServerConfigBuilder(File userHome) throws InvalidPropertyException {
        PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        Set<String> allPolarisKeys = polarisServerConfigBuilder.getPropertyKeys();
        Map<String, String> polarisProperties = detectConfiguration.getRaw(allPolarisKeys);

        // Detect and polaris-common use different property keys for the Polaris URL,
        // so we need to pull it from they Detect config using Detect's key,
        // and write it to the polaris-common config using the polaris-common key.
        String polarisUrlValue = detectConfiguration.getRaw(DetectProperties.Companion.getPOLARIS_URL()).orElse(null);
        if (StringUtils.isNotBlank(polarisUrlValue)) {
            polarisProperties.put(PolarisServerConfigBuilder.URL_KEY.getKey(), polarisUrlValue);
        }

        polarisServerConfigBuilder.setLogger(new SilentIntLogger());

        polarisServerConfigBuilder.setProperties(polarisProperties.entrySet());
        polarisServerConfigBuilder.setUserHome(userHome.getAbsolutePath());
        polarisServerConfigBuilder.setTimeoutInSeconds(findTimeoutInSeconds().intValue());
        return polarisServerConfigBuilder;
    }

    public PhoneHomeOptions createPhoneHomeOptions() {
        Map<String, String> phoneHomePassthrough = detectConfiguration.getRaw(DetectProperties.Companion.getPHONEHOME_PASSTHROUGH());
        return new PhoneHomeOptions(phoneHomePassthrough);
    }

    public RunOptions createRunOptions() throws InvalidPropertyException {
        // This is because it is double deprecated so we must check if either property is set.
        Optional<Boolean> sigScanDisabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_DISABLED());
        Optional<Boolean> polarisEnabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_SWIP_ENABLED());

        List<FilterableEnumValue<DetectTool>> includedTools = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_TOOLS());
        List<FilterableEnumValue<DetectTool>> excludedTools = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_TOOLS_EXCLUDED());
        ExcludeIncludeEnumFilter filter = new ExcludeIncludeEnumFilter(excludedTools, includedTools);
        DetectToolFilter detectToolFilter = new DetectToolFilter(filter, sigScanDisabled, polarisEnabled);

        Boolean unmapCodeLocations = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_UNMAP());
        String aggregateName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BOM_AGGREGATE_NAME()).orElse(null);
        AggregateMode aggregateMode = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BOM_AGGREGATE_REMEDIATION_MODE());
        List<DetectTool> preferredTools = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_TOOL());
        Boolean useBdio2 = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BDIO2_ENABLED());

        return new RunOptions(unmapCodeLocations, aggregateName, aggregateMode, preferredTools, detectToolFilter, useBdio2);
    }

    public DirectoryOptions createDirectoryOptions() throws InvalidPropertyException {
        Path sourcePath = getPathOrNull(DetectProperties.Companion.getDETECT_SOURCE_PATH());
        Path outputPath = getPathOrNull(DetectProperties.Companion.getDETECT_OUTPUT_PATH());
        Path bdioPath = getPathOrNull(DetectProperties.Companion.getDETECT_BDIO_OUTPUT_PATH());
        Path scanPath = getPathOrNull(DetectProperties.Companion.getDETECT_SCAN_OUTPUT_PATH());
        Path toolsOutputPath = getPathOrNull(DetectProperties.Companion.getDETECT_TOOLS_OUTPUT_PATH());

        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath);
    }

    public AirGapOptions createAirGapOptions() throws InvalidPropertyException {
        Path gradleOverride = getPathOrNull(DetectProperties.Companion.getDETECT_GRADLE_INSPECTOR_AIR_GAP_PATH());
        Path nugetOverride = getPathOrNull(DetectProperties.Companion.getDETECT_NUGET_INSPECTOR_AIR_GAP_PATH());
        Path dockerOverride = getPathOrNull(DetectProperties.Companion.getDETECT_DOCKER_INSPECTOR_AIR_GAP_PATH());

        return new AirGapOptions(dockerOverride, gradleOverride, nugetOverride);
    }

    public FileFinder createFilteredFileFinder(Path sourcePath) {
        List<String> userProvidedExcludedFiles = detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_FILES());
        return new DetectFileFinder(userProvidedExcludedFiles);
    }

    public DetectorFinderOptions createSearchOptions(Path sourcePath) throws InvalidPropertyException {
        //Normal settings
        Integer maxDepth = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_DEPTH());

        //File Filter
        List<String> userProvidedExcludedDirectories = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION());
        List<String> excludedDirectoryPatterns = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS());
        List<String> excludedDirectoryPaths = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_PATHS());

        List<String> excludedDirectories = new ArrayList<>();
        excludedDirectories.addAll(userProvidedExcludedDirectories);
        if (detectConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS())) {
            List<String> defaultExcluded = Arrays.stream(DetectorSearchExcludedDirectories.values())
                                      .map(DetectorSearchExcludedDirectories::getDirectoryName)
                                      .collect(Collectors.toList());
            excludedDirectories.addAll(defaultExcluded);
        }

        DetectDetectorFileFilter fileFilter = new DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryPatterns);

        return new DetectorFinderOptions(fileFilter, maxDepth);
    }

    public DetectorEvaluationOptions createDetectorEvaluationOptions() throws InvalidPropertyException {
        Boolean forceNestedSearch = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DETECTOR_SEARCH_CONTINUE());

        //Detector Filter
        List<FilterableEnumValue<DetectorType>> excluded = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_EXCLUDED_DETECTOR_TYPES());
        List<FilterableEnumValue<DetectorType>> included = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_INCLUDED_DETECTOR_TYPES());
        ExcludeIncludeEnumFilter detectorFilter = new ExcludeIncludeEnumFilter(excluded, included);

        return new DetectorEvaluationOptions(forceNestedSearch, (rule -> detectorFilter.shouldInclude(rule.getDetectorType())));
    }

    public BdioOptions createBdioOptions() throws InvalidPropertyException {
        String prefix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_PREFIX()).orElse(null);
        String suffix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_SUFFIX()).orElse(null);
        return new BdioOptions(prefix, suffix);
    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(String sourceDirectoryName) throws InvalidPropertyException {
        String overrideProjectName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_NAME()).orElse(null);
        String overrideProjectVersionName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_NAME()).orElse(null);
        String defaultProjectVersionText = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DEFAULT_PROJECT_VERSION_TEXT());
        DefaultVersionNameScheme defaultProjectVersionScheme = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DEFAULT_PROJECT_VERSION_SCHEME());
        String defaultProjectVersionFormat = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT());
        return new ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat);
    }

    public DetectProjectServiceOptions createDetectProjectServiceOptions() throws InvalidPropertyException, DetectUserFriendlyException {
        ProjectVersionPhaseType projectVersionPhase = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_PHASE());
        LicenseFamilyLicenseFamilyRiskRulesReleaseDistributionType projectVersionDistribution = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_DISTRIBUTION());
        Integer projectTier = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_TIER()).orElse(null);
        String projectDescription = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_DESCRIPTION()).orElse(null);
        String projectVersionNotes = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_NOTES()).orElse(null);
        List<ProjectCloneCategoriesType> cloneCategories = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CLONE_CATEGORIES());
        Boolean projectLevelAdjustments = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_LEVEL_ADJUSTMENTS());
        Boolean forceProjectVersionUpdate = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_UPDATE());
        String cloneVersionName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_CLONE_PROJECT_VERSION_NAME()).orElse(null);
        String projectVersionNickname = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_VERSION_NICKNAME()).orElse(null);
        String applicationId = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_APPLICATION_ID()).orElse(null);
        List<String> groups = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_USER_GROUPS());
        List<String> tags = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_TAGS());
        String parentProjectName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PARENT_PROJECT_NAME()).orElse(null);
        String parentProjectVersion = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PARENT_PROJECT_VERSION_NAME()).orElse(null);
        Boolean cloneLatestProjectVersion = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_CLONE_PROJECT_VERSION_LATEST());

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument customFieldDocument = parser.parseCustomFieldDocument(detectConfiguration.getRaw());

        return new DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
            projectVersionNickname, applicationId, tags, groups, parentProjectName, parentProjectVersion, cloneLatestProjectVersion, customFieldDocument);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() throws InvalidPropertyException, DetectUserFriendlyException {
        List<PathValue> signatureScannerPathValues = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_PATHS()).orElse(null);
        List<Path> signatureScannerPaths;
        if (signatureScannerPathValues != null) {
            signatureScannerPaths = signatureScannerPathValues.stream()
                .map(it -> it.resolvePath(pathResolver))
                .collect(Collectors.toList());
        } else {
            signatureScannerPaths = emptyList();
        }
        List<String> exclusionPatterns = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS()).orElse(emptyList());
        List<String> exclusionNamePatterns = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS());

        Integer scanMemory = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_MEMORY());
        Boolean dryRun = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_DRY_RUN());
        Boolean uploadSource = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE());
        Boolean licenseSearch = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH());
        Boolean copyrightSearch = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_COPYRIGHT_SEARCH());
        String codeLocationPrefix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_PREFIX()).orElse(null);
        String codeLocationSuffix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_SUFFIX()).orElse(null);
        String additionalArguments = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS()).orElse(null);
        Integer maxDepth = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH());
        Path offlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        Path onlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        String userProvidedScannerInstallUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.Companion.getDETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL(), DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_HOST_URL()).orElse(null);

        if (offlineLocalScannerInstallPath != null && StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
            throw new DetectUserFriendlyException(
                "You have provided both a Black Duck signature scanner url AND a local Black Duck signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                ExitCodeType.FAILURE_GENERAL_ERROR
            );
        }



        return new BlackDuckSignatureScannerOptions(
            signatureScannerPaths,
            exclusionPatterns,
            exclusionNamePatterns,
            offlineLocalScannerInstallPath,
            onlineLocalScannerInstallPath,
            userProvidedScannerInstallUrl,
            scanMemory,
            findParallelProcessors(),
            dryRun,
            findSnippetMatching(),
            uploadSource,
            codeLocationPrefix,
            codeLocationSuffix,
            additionalArguments,
            maxDepth,
            findIndividualFileMatching(),
            licenseSearch,
            copyrightSearch
        );
    }


    public BlackDuckPostOptions createBlackDuckPostOptions() throws InvalidPropertyException {
        Boolean waitForResults = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_WAIT_FOR_RESULTS());
        Boolean runRiskReport = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_RISK_REPORT_PDF());
        Boolean runNoticesReport = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NOTICES_REPORT());
        Path riskReportPdfPath = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_RISK_REPORT_PDF_PATH()).resolvePath(pathResolver);
        Path noticesReportPath = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NOTICES_REPORT_PATH()).resolvePath(pathResolver);
        List<FilterableEnumValue<PolicyRuleSeverityType>> policySeverities = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_POLICY_CHECK_FAIL_ON_SEVERITIES());
        List<PolicyRuleSeverityType> severitiesToFailPolicyCheck = FilterableEnumUtils.populatedValues(policySeverities, PolicyRuleSeverityType.class);

        return new BlackDuckPostOptions(waitForResults, runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath, severitiesToFailPolicyCheck);
    }

    public BinaryScanOptions createBinaryScanOptions() throws InvalidPropertyException {
        Path singleTarget = getPathOrNull(DetectProperties.Companion.getDETECT_BINARY_SCAN_FILE());
        List<String> mutlipleTargets = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BINARY_SCAN_FILE_NAME_PATTERNS());
        String codeLocationPrefix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_PREFIX()).orElse(null);
        String codeLocationSuffix = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PROJECT_CODELOCATION_SUFFIX()).orElse(null);
        return new BinaryScanOptions(singleTarget, mutlipleTargets, codeLocationPrefix, codeLocationSuffix);
    }

    public DetectExecutableOptions createExecutablePaths() throws InvalidPropertyException {
        return new DetectExecutableOptions(
            getPathOrNull(DetectProperties.Companion.getDETECT_BASH_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_BAZEL_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_CONDA_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_CPAN_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_CPANM_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_GRADLE_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_MAVEN_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_NPM_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_PEAR_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_PIPENV_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_PYTHON_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_HEX_REBAR3_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_JAVA_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_DOCKER_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_DOTNET_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_GIT_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_GO_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_SWIFT_PATH()),
            getPathOrNull(DetectProperties.Companion.getDETECT_LERNA_PATH())
        );
    }

    private Path getPathOrNull(NullablePathProperty property) throws InvalidPropertyException {
        return detectConfiguration.getValue(property).map(path -> path.resolvePath(pathResolver)).orElse(null);
    }


}
