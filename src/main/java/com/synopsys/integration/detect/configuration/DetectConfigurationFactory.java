package com.synopsys.integration.detect.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.manual.temporary.enumeration.ProjectVersionPhaseType;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.IndividualFileMatching;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ReducedPersistence;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllNoneEnumCollection;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllNoneEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.list.NoneEnumList;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumProperty;
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumValue;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DefaultDetectorSearchExcludedDirectories;
import com.synopsys.integration.detect.configuration.enumeration.DefaultSignatureScannerExcludedDirectories;
import com.synopsys.integration.detect.configuration.enumeration.DetectTargetType;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.RunDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableOptions;
import com.synopsys.integration.detect.tool.iac.IacScanOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedIndividualFileMatchingMode;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedReducedPersistanceMode;
import com.synopsys.integration.detect.tool.signaturescanner.enums.ExtendedSnippetMode;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.util.finder.DetectDirectoryFileFilter;
import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.options.FindCloneOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ParentProjectMapOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectGroupOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectSyncOptions;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectVersionLicenseOptions;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeOptions;
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions;
import com.synopsys.integration.detector.accuracy.search.SearchOptions;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DirectoryFinderOptions;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

public class DetectConfigurationFactory {
    private final DetectPropertyConfiguration detectConfiguration;
    private final Gson gson;

    public DetectConfigurationFactory(DetectPropertyConfiguration detectConfiguration, Gson gson) {
        this.detectConfiguration = detectConfiguration;
        this.gson = gson;
    }
    
    //#region Prefer These Over Any Property
    public Long findTimeoutInSeconds() {
        return detectConfiguration.getValue(DetectProperties.DETECT_TIMEOUT);
    }

    public int findParallelProcessors() {
        int provided = detectConfiguration.getValue(DetectProperties.DETECT_PARALLEL_PROCESSORS);
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
        ExtendedEnumValue<ExtendedSnippetMode, SnippetMatching> snippetMatching = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING);

        if (snippetMatching.getBaseValue().isPresent()) {
            return snippetMatching.getBaseValue().get();
        }

        return null;
    }

    @Nullable
    private IndividualFileMatching findIndividualFileMatching() {
        ExtendedEnumValue<ExtendedIndividualFileMatchingMode, IndividualFileMatching> individualFileMatching = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING);

        if (individualFileMatching.getBaseValue().isPresent()) {
            return individualFileMatching.getBaseValue().get();
        }

        return null;
    }
    
    @Nullable
    private ReducedPersistence findReducedPersistence() {
        ExtendedEnumValue<ExtendedReducedPersistanceMode, ReducedPersistence> reducedPersistence = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_REDUCED_PERSISTENCE);
        
        if (reducedPersistence.getBaseValue().isPresent()) {
            return reducedPersistence.getBaseValue().get();
        }
        
        return null;
    }

    //#endregion

    //#region Creating Connections
    public ProxyInfo createBlackDuckProxyInfo() throws DetectUserFriendlyException {
        String proxyUsername = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_USERNAME);
        String proxyPassword = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_PASSWORD);
        String proxyHost = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_HOST);
        String proxyPort = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_PORT);
        String proxyNtlmDomain = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN);
        String proxyNtlmWorkstation = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION);

        CredentialsBuilder proxyCredentialsBuilder = new CredentialsBuilder();
        proxyCredentialsBuilder.setUsername(proxyUsername);
        proxyCredentialsBuilder.setPassword(proxyPassword);
        Credentials proxyCredentials;
        try {
            proxyCredentials = proxyCredentialsBuilder.build();
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(
                String.format("Your proxy credentials configuration is not valid: %s", e.getMessage()),
                e,
                ExitCodeType.FAILURE_PROXY_CONNECTIVITY
            );
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
        Boolean ignoreFailures = detectConfiguration.getValue(DetectProperties.DETECT_IGNORE_CONNECTION_FAILURES);
        Boolean testConnections = detectConfiguration.getValue(DetectProperties.DETECT_TEST_CONNECTION);
        return new ProductBootOptions(ignoreFailures, testConnections);
    }

    public ConnectionDetails createConnectionDetails() throws DetectUserFriendlyException {
        Boolean alwaysTrust = detectConfiguration.getValue(DetectProperties.BLACKDUCK_TRUST_CERT);
        List<String> proxyIgnoredHosts = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_IGNORED_HOSTS);
        List<Pattern> proxyPatterns = proxyIgnoredHosts.stream()
            .map(Pattern::compile)
            .collect(Collectors.toList());
        ProxyInfo proxyInformation = createBlackDuckProxyInfo();
        return new ConnectionDetails(gson, proxyInformation, proxyPatterns, findTimeoutInSeconds(), alwaysTrust);
    }

    public BlackDuckConnectionDetails createBlackDuckConnectionDetails() throws DetectUserFriendlyException {
        Boolean offline = detectConfiguration.getValue(DetectProperties.BLACKDUCK_OFFLINE_MODE);
        String blackduckUrl = detectConfiguration.getNullableValue(DetectProperties.BLACKDUCK_URL);
        Set<String> allBlackDuckKeys = BlackDuckServerConfig.newApiTokenBuilder().getPropertyKeys().stream()
            .filter(it -> !(it.toLowerCase().contains("proxy")))
            .collect(Collectors.toSet());
        Map<String, String> blackDuckProperties = detectConfiguration.getRaw(allBlackDuckKeys);

        return new BlackDuckConnectionDetails(offline, blackduckUrl, blackDuckProperties, findParallelProcessors(), createConnectionDetails());
    }
    //#endregion

    public PhoneHomeOptions createPhoneHomeOptions() {
        Map<String, String> phoneHomePassthrough = detectConfiguration.getRaw(DetectProperties.PHONEHOME_PASSTHROUGH);
        return new PhoneHomeOptions(phoneHomePassthrough);
    }

    public boolean createHasSignatureScan() {
        boolean hss = false;
        hss = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS).containsValue(DetectTool.SIGNATURE_SCAN);
        return hss;
    }

    public DetectToolFilter createToolFilter(RunDecision runDecision, BlackDuckDecision blackDuckDecision) {
        Optional<Boolean> impactEnabled = Optional.of(detectConfiguration.getValue(DetectProperties.DETECT_IMPACT_ANALYSIS_ENABLED));

        AllNoneEnumCollection<DetectTool> includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS);
        AllNoneEnumCollection<DetectTool> excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED);
        ExcludeIncludeEnumFilter<DetectTool> filter = new ExcludeIncludeEnumFilter<>(excludedTools, includedTools);

        boolean iacEnabled = includedTools.containsValue(DetectTool.IAC_SCAN) || !detectConfiguration.getValue(DetectProperties.DETECT_IAC_SCAN_PATHS).isEmpty();

        return new DetectToolFilter(filter, impactEnabled.orElse(false), iacEnabled, runDecision, blackDuckDecision);
    }

    public RapidScanOptions createRapidScanOptions() {
        RapidCompareMode rapidCompareMode = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_RAPID_COMPARE_MODE);
        BlackduckScanMode scanMode= detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE);
        return new RapidScanOptions(rapidCompareMode, scanMode);
    }

    public BlackduckScanMode createScanMode() {
        return detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE);
    }

    public DetectTargetType createDetectTarget() {
        return detectConfiguration.getValue(DetectProperties.DETECT_TARGET_TYPE);
    }

    public List<DetectTool> createPreferredProjectTools() {
        return detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TOOL);
    }

    public DirectoryOptions createDirectoryOptions() throws IOException {
        Path sourcePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_SOURCE_PATH);
        Path outputPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_OUTPUT_PATH);
        Path bdioPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_BDIO_OUTPUT_PATH);
        Path scanPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_SCAN_OUTPUT_PATH);
        Path toolsOutputPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_TOOLS_OUTPUT_PATH);
        Path impactOutputPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_IMPACT_ANALYSIS_OUTPUT_PATH);
        Path statusJsonPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_STATUS_JSON_OUTPUT_PATH);
        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath, impactOutputPath, statusJsonPath);
    }

    public List<String> collectSignatureScannerDirectoryExclusions() {
        List<String> directoryExclusionPatterns = new ArrayList<>(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES));

        if (Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES_DEFAULTS_DISABLED))) {
            directoryExclusionPatterns.addAll(DefaultSignatureScannerExcludedDirectories.getDirectoryNames());
        }

        return directoryExclusionPatterns;
    }

    public List<String> collectDetectorSearchDirectoryExclusions() {
        List<String> directoryExclusionPatterns = new ArrayList<>(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES));

        if (Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES_DEFAULTS_DISABLED))) {
            directoryExclusionPatterns.addAll(DefaultDetectorSearchExcludedDirectories.getDirectoryNames());
        }

        return directoryExclusionPatterns;
    }

    public DirectoryFinderOptions createDetectorFinderOptions() {
        //Normal settings
        Integer maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_DEPTH);
        DetectExcludedDirectoryFilter fileFilter = new DetectExcludedDirectoryFilter(collectDirectoryExclusions(DefaultDetectorSearchExcludedDirectories.getDirectoryNames()));

        return new DirectoryFinderOptions(fileFilter, maxDepth, getFollowSymLinks());
    }

    public SearchOptions createDetectorSearchOptions() {
        Boolean forceNestedSearch = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_CONTINUE);

        //Detector Filter
        NoneEnumList<DetectorType> excluded = detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DETECTOR_TYPES);
        AllEnumList<DetectorType> included = detectConfiguration.getValue(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES);
        ExcludeIncludeEnumFilter<DetectorType> detectorFilter = new ExcludeIncludeEnumFilter<>(excluded, included);

        return new SearchOptions(detectorFilter::shouldInclude, forceNestedSearch);
    }

    public BdioOptions createBdioOptions() {
        String prefix = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String suffix = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        String bdioFileName = detectConfiguration.getNullableValue(DetectProperties.DETECT_BDIO_FILE_NAME);
        return new BdioOptions(prefix, suffix, bdioFileName);
    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(String sourceDirectoryName) {
        String overrideProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_NAME);
        String overrideProjectVersionName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NAME);
        return new ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName);
    }

    public boolean createShouldUnmapCodeLocations() {
        return detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_UNMAP);
    }

    public CustomFieldDocument createCustomFieldDocument() throws DetectUserFriendlyException {
        DetectCustomFieldParser parser = new DetectCustomFieldParser();
        return parser.parseCustomFieldDocument(detectConfiguration.getRaw());
    }

    public ProjectSyncOptions createDetectProjectServiceOptions() {
        ProjectVersionPhaseType projectVersionPhase = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_PHASE);
        ProjectVersionDistributionType projectVersionDistribution = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_DISTRIBUTION);
        Integer projectTier = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_TIER);
        String projectDescription = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_DESCRIPTION);
        String projectVersionNotes = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NOTES);
        List<ProjectCloneCategoriesType> cloneCategories = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES).representedValues();
        Boolean projectLevelAdjustments = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_LEVEL_ADJUSTMENTS);
        Boolean forceProjectVersionUpdate = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_UPDATE);
        String projectVersionNickname = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_NICKNAME);

        return new ProjectSyncOptions(
            projectVersionPhase,
            projectVersionDistribution,
            projectTier,
            projectDescription,
            projectVersionNotes,
            cloneCategories,
            forceProjectVersionUpdate,
            projectVersionNickname,
            projectLevelAdjustments
        );
    }

    public ProjectVersionLicenseOptions createProjectVersionLicenseOptions() {
        String licenseName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_VERSION_LICENSE);
        return new ProjectVersionLicenseOptions(licenseName);
    }

    public ParentProjectMapOptions createParentProjectMapOptions() {
        String parentProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PARENT_PROJECT_NAME);
        String parentProjectVersion = detectConfiguration.getNullableValue(DetectProperties.DETECT_PARENT_PROJECT_VERSION_NAME);
        return new ParentProjectMapOptions(parentProjectName, parentProjectVersion);
    }

    public FindCloneOptions createCloneFindOptions() {
        String cloneVersionName = detectConfiguration.getNullableValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_NAME);
        Boolean cloneLatestProjectVersion = detectConfiguration.getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_LATEST);

        return new FindCloneOptions(cloneVersionName, cloneLatestProjectVersion);
    }

    @Nullable
    public String createApplicationId() {
        return detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_APPLICATION_ID);
    }

    @Nullable
    public List<String> createTags() {
        return detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TAGS);
    }

    @Nullable
    public List<String> createGroups() {
        return detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_USER_GROUPS);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() {
        List<Path> signatureScannerPaths = detectConfiguration.getPaths(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS);
        List<String> exclusionPatterns = collectSignatureScannerDirectoryExclusions();

        Integer scanMemory = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY);
        Boolean dryRun = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN);
        Boolean uploadSource = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE);
        Boolean licenseSearch = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH);
        Boolean copyrightSearch = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_COPYRIGHT_SEARCH);
        Boolean followSymLinks = getFollowSymLinks();
        String additionalArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS);
        Path localScannerInstallPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH);
        Integer maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES_SEARCH_DEPTH);
        Boolean treatSkippedScansAsSuccess = detectConfiguration.getValue(DetectProperties.DETECT_FORCE_SUCCESS_ON_SKIP);
        Boolean isEphemeral = BlackduckScanMode.EPHEMERAL.equals(detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE));
        

        return new BlackDuckSignatureScannerOptions(
            signatureScannerPaths,
            exclusionPatterns,
            localScannerInstallPath,
            scanMemory,
            findParallelProcessors(),
            dryRun,
            findSnippetMatching(),
            uploadSource,
            additionalArguments,
            maxDepth,
            findIndividualFileMatching(),
            licenseSearch,
            copyrightSearch,
            followSymLinks,
            treatSkippedScansAsSuccess,
            isEphemeral,
            findReducedPersistence()
        );
    }

    public BlackDuckPostOptions createBlackDuckPostOptions() {
        Boolean waitForResults = detectConfiguration.getValue(DetectProperties.DETECT_WAIT_FOR_RESULTS);
        Boolean runRiskReport = detectConfiguration.getValue(DetectProperties.DETECT_RISK_REPORT_PDF);
        Boolean runNoticesReport = detectConfiguration.getValue(DetectProperties.DETECT_NOTICES_REPORT);
        Path riskReportPdfPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_RISK_REPORT_PDF_PATH);
        Path noticesReportPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_NOTICES_REPORT_PATH);
        List<PolicyRuleSeverityType> severitiesToFailPolicyCheck = detectConfiguration.getValue(DetectProperties.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES).representedValues();
        List<String> policyNamesToFailPolicyCheck = detectConfiguration.getValue(DetectProperties.DETECT_POLICY_CHECK_FAIL_ON_NAMES);

        return new BlackDuckPostOptions(
            waitForResults,
            runRiskReport,
            runNoticesReport,
            riskReportPdfPath,
            noticesReportPath,
            severitiesToFailPolicyCheck,
            policyNamesToFailPolicyCheck
        );
    }

    public BinaryScanOptions createBinaryScanOptions() {
        Path singleTarget = detectConfiguration.getPathOrNull(DetectProperties.DETECT_BINARY_SCAN_FILE);
        List<String> fileInclusionPatterns = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS);
        DetectDirectoryFileFilter fileFilter = null;
        if (fileInclusionPatterns.stream().anyMatch(StringUtils::isNotBlank)) {
            fileFilter = new DetectDirectoryFileFilter(collectDirectoryExclusions(), fileInclusionPatterns);
        }
        Integer searchDepth = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_SEARCH_DEPTH);
        return new BinaryScanOptions(singleTarget, fileFilter, searchDepth, getFollowSymLinks());
    }

    public IacScanOptions createIacScanOptions() {
        List<Path> iacScanPaths = detectConfiguration.getPaths(DetectProperties.DETECT_IAC_SCAN_PATHS);
        Path localIacScannerPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_IAC_SCANNER_LOCAL_PATH);
        String additionalArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_IAC_SCAN_ARGUMENTS);
        String codeLocationPrefix = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX);
        String codeLocationSuffix = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX);
        return new IacScanOptions(iacScanPaths, localIacScannerPath, additionalArguments, codeLocationPrefix, codeLocationSuffix);
    }

    public DetectExecutableOptions createDetectExecutableOptions() {
        return new DetectExecutableOptions(
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_BASH_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_BAZEL_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_CONAN_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_CONDA_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_CPAN_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_CPANM_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_DART_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_FLUTTER_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_GRADLE_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_MAVEN_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_NPM_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_PEAR_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_PIP_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_PIPENV_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_PYTHON_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_HEX_REBAR3_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_JAVA_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_DOCKER_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_GIT_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_GO_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_SWIFT_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_SBT_PATH),
            detectConfiguration.getPathOrNull(DetectProperties.DETECT_LERNA_PATH)
        );
    }

    private boolean getFollowSymLinks() {
        return detectConfiguration.getValue(DetectProperties.DETECT_FOLLOW_SYMLINKS);
    }

    public Optional<String> createCodeLocationOverride() {
        return Optional.ofNullable(detectConfiguration.getNullableValue(DetectProperties.DETECT_CODE_LOCATION_NAME));
    }

    public DetectorToolOptions createDetectorToolOptions() {
        String projectBomTool = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_DETECTOR);
        List<DetectorType> requiredDetectors = detectConfiguration.getValue(DetectProperties.DETECT_REQUIRED_DETECTOR_TYPES);
        AllNoneEnumList<DetectorType> accuracyRequired = detectConfiguration.getValue(DetectProperties.DETECT_ACCURACY_REQUIRED);
        ExcludeIncludeEnumFilter<DetectorType> accuracyFilter = new ExcludeIncludeEnumFilter<>(
            new AllNoneEnumList<>(new ArrayList<>(), DetectorType.class),
            accuracyRequired
        );
        return new DetectorToolOptions(projectBomTool, requiredDetectors, accuracyFilter);
    }

    public ProjectGroupOptions createProjectGroupOptions() {
        String projectGroupName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PROJECT_GROUP_NAME);
        return new ProjectGroupOptions(projectGroupName);
    }

    private List<String> collectDirectoryExclusions() {
        return collectDirectoryExclusions(Collections.emptyList());
    }

    private List<String> collectDirectoryExclusions(@NotNull List<String> givenExclusions) {
        List<String> directoryExclusionPatterns = new ArrayList<>(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES));

        if (Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DIRECTORIES_DEFAULTS_DISABLED))) {
            directoryExclusionPatterns.addAll(givenExclusions);
        }

        return directoryExclusionPatterns;
    }
}
