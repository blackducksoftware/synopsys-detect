/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
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

import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.manual.temporary.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.IndividualFileMatching;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.path.PathValue;
import com.synopsys.integration.detect.PropertyConfigUtils;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DefaultDetectorExcludedDirectories;
import com.synopsys.integration.detect.configuration.enumeration.DefaultVersionNameScheme;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableOptions;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedIndividualFileMatchingMode;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedSnippetMode;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapOptions;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeOptions;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
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

public class DetectConfigurationFactory {
    private final PropertyConfiguration detectConfiguration;
    private final PathResolver pathResolver;

    public DetectConfigurationFactory(PropertyConfiguration detectConfiguration, PathResolver pathResolver) {
        this.detectConfiguration = detectConfiguration;
        this.pathResolver = pathResolver;
    }

    //#region Prefer These Over Any Property
    public Long findTimeoutInSeconds() {
        long timeout = getValue(DetectProperties.DETECT_TIMEOUT);
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_TIMEOUT.getProperty())) {
            return timeout;
        }

        // If no timeout was passed, check deprecated properties.
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_API_TIMEOUT.getProperty())) {
            // DETECT_API_TIMEOUT is read in milliseconds.
            timeout = getValue(DetectProperties.DETECT_API_TIMEOUT) / 1000;
        } else if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_REPORT_TIMEOUT.getProperty())) {
            timeout = getValue(DetectProperties.DETECT_REPORT_TIMEOUT);
        }
        return timeout;
    }

    public int findParallelProcessors() {
        int provided;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PARALLEL_PROCESSORS.getProperty())) {
            provided = getValue(DetectProperties.DETECT_PARALLEL_PROCESSORS);
        } else if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getProperty())) {
            provided = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS);
        } else if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getProperty())) {
            provided = getValue(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS);
        } else {
            provided = getValue(DetectProperties.DETECT_PARALLEL_PROCESSORS);
        }

        if (provided > 0) {
            return provided;
        } else {
            return findRuntimeProcessors();
        }
    }

    public int findRuntimeProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Nullable
    public SnippetMatching findSnippetMatching() {
        ExtendedEnumValue<ExtendedSnippetMode, SnippetMatching> snippetMatching = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING);

        SnippetMatching deprecatedSnippetMatching;
        if (Boolean.TRUE.equals(getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE))) {
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
    private IndividualFileMatching findIndividualFileMatching() {
        ExtendedEnumValue<ExtendedIndividualFileMatchingMode, IndividualFileMatching> individualFileMatching = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING);

        if (individualFileMatching.getBaseValue().isPresent()) {
            return individualFileMatching.getBaseValue().get();
        }

        return null;
    }

    //#endregion

    //#region Creating Connections
    public ProxyInfo createBlackDuckProxyInfo() throws DetectUserFriendlyException {
        String proxyUsername = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_USERNAME.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_USERNAME.getProperty()).orElse(null);
        String proxyPassword = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_PASSWORD.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_PASSWORD.getProperty()).orElse(null);
        String proxyHost = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_HOST.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_HOST.getProperty()).orElse(null);
        String proxyPort = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_PORT.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_PORT.getProperty()).orElse(null);
        String proxyNtlmDomain = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_NTLM_DOMAIN.getProperty()).orElse(null);
        String proxyNtlmWorkstation = PropertyConfigUtils
                                          .getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION.getProperty()).orElse(null);

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
        Boolean ignoreFailures = getValue(DetectProperties.DETECT_IGNORE_CONNECTION_FAILURES);
        Boolean testConnections = getValue(DetectProperties.DETECT_TEST_CONNECTION);
        return new ProductBootOptions(ignoreFailures, testConnections);
    }

    public ConnectionDetails createConnectionDetails() throws DetectUserFriendlyException {
        Boolean alwaysTrust = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_TRUST_CERT.getProperty(), DetectProperties.BLACKDUCK_HUB_TRUST_CERT.getProperty());
        List<String> proxyIgnoredHosts = PropertyConfigUtils
                                             .getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_IGNORED_HOSTS.getProperty(), DetectProperties.BLACKDUCK_HUB_PROXY_IGNORED_HOSTS.getProperty());
        List<Pattern> proxyPatterns = proxyIgnoredHosts.stream()
                                          .map(Pattern::compile)
                                          .collect(Collectors.toList());
        ProxyInfo proxyInformation = createBlackDuckProxyInfo();
        return new ConnectionDetails(proxyInformation, proxyPatterns, findTimeoutInSeconds(), alwaysTrust);
    }

    public BlackDuckConnectionDetails createBlackDuckConnectionDetails() throws DetectUserFriendlyException {
        Boolean offline = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), DetectProperties.BLACKDUCK_HUB_OFFLINE_MODE.getProperty());
        String blackduckUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_URL.getProperty(), DetectProperties.BLACKDUCK_HUB_URL.getProperty()).orElse(null);
        Set<String> allBlackDuckKeys = new BlackDuckServerConfigBuilder().getPropertyKeys().stream()
                                           .filter(it -> !(it.toLowerCase().contains("proxy")))
                                           .collect(Collectors.toSet());
        Map<String, String> blackDuckProperties = detectConfiguration.getRaw(allBlackDuckKeys);

        return new BlackDuckConnectionDetails(offline, blackduckUrl, blackDuckProperties, findParallelProcessors(), createConnectionDetails());
    }
    //#endregion

    public PolarisServerConfigBuilder createPolarisServerConfigBuilder(File userHome) {
        PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        Set<String> allPolarisKeys = polarisServerConfigBuilder.getPropertyKeys();
        Map<String, String> polarisProperties = detectConfiguration.getRaw(allPolarisKeys);

        // Detect and polaris-common use different property keys for the Polaris URL,
        // so we need to pull it from they Detect config using Detect's key,
        // and write it to the polaris-common config using the polaris-common key.
        String polarisUrlValue = detectConfiguration.getRaw(DetectProperties.POLARIS_URL.getProperty()).orElse(null);
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
        Map<String, String> phoneHomePassthrough = detectConfiguration.getRaw(DetectProperties.PHONEHOME_PASSTHROUGH.getProperty());
        return new PhoneHomeOptions(phoneHomePassthrough);
    }

    public RunOptions createRunOptions() {
        // This is because it is double deprecated so we must check if either property is set.
        Optional<Boolean> sigScanDisabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED.getProperty(),
            DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_DISABLED.getProperty());
        Optional<Boolean> polarisEnabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_SWIP_ENABLED.getProperty());

        List<FilterableEnumValue<DetectTool>> includedTools = getValue(DetectProperties.DETECT_TOOLS);
        List<FilterableEnumValue<DetectTool>> excludedTools = getValue(DetectProperties.DETECT_TOOLS_EXCLUDED);
        ExcludeIncludeEnumFilter filter = new ExcludeIncludeEnumFilter(excludedTools, includedTools);
        DetectToolFilter detectToolFilter = new DetectToolFilter(filter, sigScanDisabled, polarisEnabled);

        Boolean unmapCodeLocations = getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_UNMAP);
        String aggregateName = getNullableValue(DetectProperties.DETECT_BOM_AGGREGATE_NAME);
        AggregateMode aggregateMode = getValue(DetectProperties.DETECT_BOM_AGGREGATE_REMEDIATION_MODE);
        List<DetectTool> preferredTools = getValue(DetectProperties.DETECT_PROJECT_TOOL);
        Boolean useBdio2 = getValue(DetectProperties.DETECT_BDIO2_ENABLED);
        BlackduckScanMode scanMode = getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE);

        return new RunOptions(unmapCodeLocations, aggregateName, aggregateMode, preferredTools, detectToolFilter, useBdio2, scanMode);
    }

    public DirectoryOptions createDirectoryOptions() throws IOException {
        Path sourcePath = getPathOrNull(DetectProperties.DETECT_SOURCE_PATH.getProperty());
        Path outputPath = getPathOrNull(DetectProperties.DETECT_OUTPUT_PATH.getProperty());
        Path bdioPath = getPathOrNull(DetectProperties.DETECT_BDIO_OUTPUT_PATH.getProperty());
        Path scanPath = getPathOrNull(DetectProperties.DETECT_SCAN_OUTPUT_PATH.getProperty());
        Path toolsOutputPath = getPathOrNull(DetectProperties.DETECT_TOOLS_OUTPUT_PATH.getProperty());

        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath);
    }

    public AirGapOptions createAirGapOptions() {
        Path gradleOverride = getPathOrNull(DetectProperties.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH.getProperty());
        Path nugetOverride = getPathOrNull(DetectProperties.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH.getProperty());
        Path dockerOverride = getPathOrNull(DetectProperties.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH.getProperty());
        return new AirGapOptions(dockerOverride, gradleOverride, nugetOverride);
    }

    public DetectExcludedDirectoryFilter createDetectDirectoryFileFilter(Path sourcePath) {
        List<String> directoryExclusionPatterns = collectDirectoryExclusions();

        return new DetectExcludedDirectoryFilter(sourcePath, directoryExclusionPatterns);
    }

    private List<String> collectDirectoryExclusions() {
        List<String> directoryExclusionPatterns = new ArrayList(getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES));

        if (getValue(DetectProperties.DETECT_EXCLUDE_DEFAULT_DIRECTORIES)) {
            List<String> defaultExcluded = Arrays.stream(DefaultDetectorExcludedDirectories.values())
                                               .map(DefaultDetectorExcludedDirectories::getDirectoryName)
                                               .collect(Collectors.toList());
            directoryExclusionPatterns.addAll(defaultExcluded);
        }

        return directoryExclusionPatterns;
    }

    public DetectorFinderOptions createDetectorFinderOptions(Path sourcePath) {
        //Normal settings
        Integer maxDepth = getValue(DetectProperties.DETECT_DETECTOR_SEARCH_DEPTH);
        DetectExcludedDirectoryFilter fileFilter = createDetectDirectoryFileFilter(sourcePath);

        return new DetectorFinderOptions(fileFilter, maxDepth);
    }

    public DetectorEvaluationOptions createDetectorEvaluationOptions() {
        Boolean forceNestedSearch = getValue(DetectProperties.DETECT_DETECTOR_SEARCH_CONTINUE);

        //Detector Filter
        List<FilterableEnumValue<DetectorType>> excluded = getValue(DetectProperties.DETECT_EXCLUDED_DETECTOR_TYPES);
        List<FilterableEnumValue<DetectorType>> included = getValue(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES);
        ExcludeIncludeEnumFilter detectorFilter = new ExcludeIncludeEnumFilter(excluded, included);

        return new DetectorEvaluationOptions(forceNestedSearch, (rule -> detectorFilter.shouldInclude(rule.getDetectorType())));
    }

    public BdioOptions createBdioOptions() {
        String prefix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String suffix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        return new BdioOptions(prefix, suffix);
    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(String sourceDirectoryName) {
        String overrideProjectName = getNullableValue(DetectProperties.DETECT_PROJECT_NAME);
        String overrideProjectVersionName = getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NAME);
        String defaultProjectVersionText = getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TEXT);
        DefaultVersionNameScheme defaultProjectVersionScheme = getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_SCHEME);
        String defaultProjectVersionFormat = getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT);
        return new ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat);
    }

    public DetectProjectServiceOptions createDetectProjectServiceOptions() throws DetectUserFriendlyException {
        ProjectVersionPhaseType projectVersionPhase = getValue(DetectProperties.DETECT_PROJECT_VERSION_PHASE);
        ProjectVersionDistributionType projectVersionDistribution = getValue(DetectProperties.DETECT_PROJECT_VERSION_DISTRIBUTION);
        Integer projectTier = getNullableValue(DetectProperties.DETECT_PROJECT_TIER);
        String projectDescription = getNullableValue(DetectProperties.DETECT_PROJECT_DESCRIPTION);
        String projectVersionNotes = getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NOTES);
        List<ProjectCloneCategoriesType> cloneCategories = getValue(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES);
        Boolean projectLevelAdjustments = getValue(DetectProperties.DETECT_PROJECT_LEVEL_ADJUSTMENTS);
        Boolean forceProjectVersionUpdate = getValue(DetectProperties.DETECT_PROJECT_VERSION_UPDATE);
        String cloneVersionName = getNullableValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_NAME);
        String projectVersionNickname = getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NICKNAME);
        String applicationId = getNullableValue(DetectProperties.DETECT_PROJECT_APPLICATION_ID);
        List<String> groups = getValue(DetectProperties.DETECT_PROJECT_USER_GROUPS);
        List<String> tags = getValue(DetectProperties.DETECT_PROJECT_TAGS);
        String parentProjectName = getNullableValue(DetectProperties.DETECT_PARENT_PROJECT_NAME);
        String parentProjectVersion = getNullableValue(DetectProperties.DETECT_PARENT_PROJECT_VERSION_NAME);
        Boolean cloneLatestProjectVersion = getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_LATEST);

        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        CustomFieldDocument customFieldDocument = parser.parseCustomFieldDocument(detectConfiguration.getRaw());

        return new DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
            projectVersionNickname, applicationId, tags, groups, parentProjectName, parentProjectVersion, cloneLatestProjectVersion, customFieldDocument);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() throws DetectUserFriendlyException {
        List<PathValue> signatureScannerPathValues = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS.getProperty(),
            DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PATHS.getProperty()).orElse(null);
        List<Path> signatureScannerPaths;
        if (signatureScannerPathValues != null) {
            signatureScannerPaths = signatureScannerPathValues.stream()
                                        .map(it -> it.resolvePath(pathResolver))
                                        .collect(Collectors.toList());
        } else {
            signatureScannerPaths = emptyList();
        }
        List<String> exclusionPatterns = collectDirectoryExclusions();

        Integer scanMemory = PropertyConfigUtils
                                 .getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY.getProperty(), DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_MEMORY.getProperty());
        Boolean dryRun = PropertyConfigUtils
                             .getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty(), DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN.getProperty());
        Boolean uploadSource = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE);
        Boolean licenseSearch = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH);
        Boolean copyrightSearch = getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_COPYRIGHT_SEARCH);
        String codeLocationPrefix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String codeLocationSuffix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        String additionalArguments = PropertyConfigUtils
                                         .getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS.getProperty(), DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS.getProperty())
                                         .orElse(null);
        Integer maxDepth = getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORY_SEARCH_DEPTH);
        Path offlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty(),
            DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        Path onlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH.getProperty(),
            DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        String userProvidedScannerInstallUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL.getProperty(),
            DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL.getProperty()).orElse(null);

        if (offlineLocalScannerInstallPath != null && StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
            throw new DetectUserFriendlyException(
                "You have provided both a Black Duck signature scanner url AND a local Black Duck signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                ExitCodeType.FAILURE_GENERAL_ERROR
            );
        }

        return new BlackDuckSignatureScannerOptions(
            signatureScannerPaths,
            exclusionPatterns,
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

    public BlackDuckPostOptions createBlackDuckPostOptions() {
        Boolean waitForResults = getValue(DetectProperties.DETECT_WAIT_FOR_RESULTS);
        Boolean runRiskReport = getValue(DetectProperties.DETECT_RISK_REPORT_PDF);
        Boolean runNoticesReport = getValue(DetectProperties.DETECT_NOTICES_REPORT);
        Path riskReportPdfPath = getValue(DetectProperties.DETECT_RISK_REPORT_PDF_PATH).resolvePath(pathResolver);
        Path noticesReportPath = getValue(DetectProperties.DETECT_NOTICES_REPORT_PATH).resolvePath(pathResolver);
        List<FilterableEnumValue<PolicyRuleSeverityType>> policySeverities = getValue(DetectProperties.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES);
        List<PolicyRuleSeverityType> severitiesToFailPolicyCheck = FilterableEnumUtils.populatedValues(policySeverities, PolicyRuleSeverityType.class);

        return new BlackDuckPostOptions(waitForResults, runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath, severitiesToFailPolicyCheck);
    }

    public BinaryScanOptions createBinaryScanOptions() {
        Path singleTarget = getPathOrNull(DetectProperties.DETECT_BINARY_SCAN_FILE.getProperty());
        List<String> multipleTargets = getValue(DetectProperties.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS);
        String codeLocationPrefix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String codeLocationSuffix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        Integer searchDepth = getValue(DetectProperties.DETECT_BINARY_SCAN_SEARCH_DEPTH);
        return new BinaryScanOptions(singleTarget, multipleTargets, codeLocationPrefix, codeLocationSuffix, searchDepth);
    }

    public ImpactAnalysisOptions createImpactAnalysisOptions() {
        Boolean enabled = detectConfiguration.getValue(DetectProperties.DETECT_IMPACT_ANALYSIS_ENABLED.getProperty());
        Path outputDirectory = getPathOrNull(DetectProperties.DETECT_IMPACT_ANALYSIS_OUTPUT_PATH.getProperty());
        String codeLocationPrefix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String codeLocationSuffix = getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        return new ImpactAnalysisOptions(enabled, codeLocationPrefix, codeLocationSuffix, outputDirectory);
    }

    public DetectExecutableOptions createDetectExecutableOptions() {
        return new DetectExecutableOptions(
            getValue(DetectProperties.DETECT_PYTHON_PYTHON3),
            getPathOrNull(DetectProperties.DETECT_BASH_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_BAZEL_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_CONAN_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_CONDA_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_CPAN_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_CPANM_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_GRADLE_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_MAVEN_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_NPM_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_PEAR_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_PIP_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_PIPENV_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_PYTHON_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_HEX_REBAR3_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_JAVA_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_DOCKER_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_DOTNET_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_GIT_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_GO_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_SWIFT_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_SBT_PATH.getProperty()),
            getPathOrNull(DetectProperties.DETECT_LERNA_PATH.getProperty())
        );
    }

    private Path getPathOrNull(NullablePathProperty property) {
        return detectConfiguration.getValue(property).map(path -> path.resolvePath(pathResolver)).orElse(null);
    }

    private <P, T extends NullableProperty<P>> P getNullableValue(DetectProperty<T> detectProperty) {
        return detectConfiguration.getValue(detectProperty.getProperty()).orElse(null);
    }

    private <P, T extends ValuedProperty<P>> P getValue(DetectProperty<T> detectProperty) {
        return detectConfiguration.getValue(detectProperty.getProperty());
    }

}
