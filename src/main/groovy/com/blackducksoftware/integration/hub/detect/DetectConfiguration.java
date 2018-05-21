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
package com.blackducksoftware.integration.hub.detect;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.api.enumeration.PolicySeverityType;
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolFinder;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.AcceptableValues;
import com.blackducksoftware.integration.hub.detect.help.DefaultValue;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.HelpDescription;
import com.blackducksoftware.integration.hub.detect.help.HelpDetailed;
import com.blackducksoftware.integration.hub.detect.help.HelpGroup;
import com.blackducksoftware.integration.hub.detect.help.ValueDeprecation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.rest.proxy.ProxyInfo;
import com.blackducksoftware.integration.rest.proxy.ProxyInfoBuilder;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;
import com.blackducksoftware.integration.util.ResourceUtil;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);

    public static final String DETECT_PROPERTY_PREFIX = "detect.";
    public static final String DOCKER_PROPERTY_PREFIX = "detect.docker.passthrough.";
    public static final String PHONE_HOME_PROPERTY_PREFIX = "detect.phone.home.passthrough.";
    public static final String DOCKER_ENVIRONMENT_PREFIX = "DETECT_DOCKER_PASSTHROUGH_";
    public static final String NUGET = "nuget";
    public static final String GRADLE = "gradle";
    public static final String DOCKER = "docker";

    private static final String GROUP_HUB_CONFIGURATION = "hub configuration";
    private static final String GROUP_GENERAL = "general";
    private static final String GROUP_LOGGING = "logging";
    private static final String GROUP_CLEANUP = "cleanup";
    private static final String GROUP_PATHS = "paths";
    private static final String GROUP_BOMTOOL = "bomtool";
    private static final String GROUP_CONDA = "conda";
    private static final String GROUP_CPAN = "cpan";
    private static final String GROUP_DOCKER = "docker";
    private static final String GROUP_GO = "go";
    private static final String GROUP_GRADLE = "gradle";
    private static final String GROUP_HEX = "hex";
    private static final String GROUP_MAVEN = "maven";
    private static final String GROUP_NPM = "npm";
    private static final String GROUP_NUGET = "nuget";
    private static final String GROUP_PACKAGIST = "packagist";
    private static final String GROUP_PEAR = "pear";
    private static final String GROUP_PIP = "pip";
    private static final String GROUP_POLICY_CHECK = "policy check";
    private static final String GROUP_PROJECT_INFO = "project info";
    private static final String GROUP_PYTHON = "python";
    private static final String GROUP_SBT = "sbt";
    private static final String GROUP_SIGNATURE_SCANNER = "signature scanner";
    private static final String GROUP_YARN = "yarn";

    private static final String SEARCH_GROUP_SIGNATURE_SCANNER = "scanner";
    private static final String SEARCH_GROUP_POLICY = "policy";
    private static final String SEARCH_GROUP_HUB = "hub";
    private static final String SEARCH_GROUP_PROXY = "proxy";
    private static final String SEARCH_GROUP_OFFLINE = "offline";
    private static final String SEARCH_GROUP_PROJECT = "project";
    private static final String SEARCH_GROUP_DEBUG = "debug";

    public static final String PRINT_GROUP_DEFAULT = SEARCH_GROUP_HUB;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Autowired
    private TildeInPathResolver tildeInPathResolver;

    private File sourceDirectory;
    private File outputDirectory;

    private List<DetectOption> detectOptions = new ArrayList<>();

    private final Set<String> allDetectPropertyKeys = new HashSet<>();
    private final Set<String> additionalDockerPropertyNames = new HashSet<>();
    private final Set<String> additionalPhoneHomePropertyNames = new HashSet<>();

    private boolean usingDefaultSourcePath;
    private boolean usingDefaultOutputPath;

    private ExcludedIncludedFilter bomToolFilter;
    private List<String> bomToolSearchDirectoryExclusions;
    private final List<String> excludedScanPaths = new ArrayList<>();

    public void init(final List<DetectOption> detectOptions) throws DetectUserFriendlyException, IOException, IllegalArgumentException, IllegalAccessException {
        this.detectOptions = detectOptions;

        final String systemUserHome = System.getProperty("user.home");
        if (resolveTildeInPaths) {
            tildeInPathResolver.resolveTildeInAllPathFields(systemUserHome, this);
        }

        if (StringUtils.isBlank(sourcePath)) {
            usingDefaultSourcePath = true;
            sourcePath = System.getProperty("user.dir");
        }

        if (!getCleanupBdioFiles()) {
            requestDeprecation("cleanupBdioFiles");
            cleanupDetectFiles = false;
        }
        if (!getCleanupBomToolFiles()) {
            requestDeprecation("cleanupBomToolFiles");
            cleanupDetectFiles = false;
        }
        if (!getGradleCleanupBuildBlackduckDirectory()) {
            requestDeprecation("gradleCleanupBuildBlackduckDirectory");
            cleanupDetectFiles = false;
        }

        sourceDirectory = new File(sourcePath);
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            throw new DetectUserFriendlyException("The source path ${sourcePath} either doesn't exist, isn't a directory, or doesn't have appropriate permissions.", ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        final boolean atLeastOnePolicySeverity = StringUtils.isNotBlank(policyCheckFailOnSeverities);
        if (atLeastOnePolicySeverity) {
            boolean allSeverities = false;
            final String[] splitSeverities = policyCheckFailOnSeverities.split(",");
            for (final String severity : splitSeverities) {
                if (severity.equalsIgnoreCase("ALL")) {
                    allSeverities = true;
                    break;
                }
            }
            if (allSeverities) {
                final List<String> allPolicyTypes = Arrays.stream(PolicySeverityType.values()).filter(type -> type != PolicySeverityType.UNSPECIFIED).map(type -> type.toString()).collect(Collectors.toList());
                policyCheckFailOnSeverities = StringUtils.join(allPolicyTypes, ",");
            }
            if (policyCheck) {
                requestDeprecation("policyCheck");
            } else {
                policyCheck = true;
            }
        }

        // make sure the path is absolute
        sourcePath = sourceDirectory.getCanonicalPath();

        usingDefaultOutputPath = StringUtils.isBlank(outputDirectoryPath);
        outputDirectoryPath = createDirectoryPath(outputDirectoryPath, systemUserHome, "blackduck");
        bdioOutputDirectoryPath = createDirectoryPath(bdioOutputDirectoryPath, outputDirectoryPath, "bdio");
        scanOutputDirectoryPath = createDirectoryPath(scanOutputDirectoryPath, outputDirectoryPath, "scan");

        ensureDirectoryExists(outputDirectoryPath, "The system property 'user.home' will be used by default, but the output directory must exist.");
        ensureDirectoryExists(bdioOutputDirectoryPath, "By default, the directory 'bdio' will be created in the outputDirectory, but the directory must exist.");
        ensureDirectoryExists(scanOutputDirectoryPath, "By default, the directory 'scan' will be created in the outputDirectory, but the directory must exist.");

        outputDirectory = new File(outputDirectoryPath);

        nugetInspectorPackageName = nugetInspectorPackageName.trim();
        nugetInspectorPackageVersion = nugetInspectorPackageVersion.trim();

        final MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
        for (final PropertySource<?> propertySource : mutablePropertySources) {
            if (propertySource instanceof EnumerablePropertySource) {
                final EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
                for (final String propertyName : enumerablePropertySource.getPropertyNames()) {
                    if (StringUtils.isNotBlank(propertyName) && propertyName.startsWith(DETECT_PROPERTY_PREFIX)) {
                        allDetectPropertyKeys.add(propertyName);
                    }
                }
            }
        }

        if (hubSignatureScannerParallelProcessors == -1) {
            hubSignatureScannerParallelProcessors = Runtime.getRuntime().availableProcessors();
        }

        bomToolFilter = new ExcludedIncludedFilter(getExcludedBomToolTypes(), getIncludedBomToolTypes());

        if (hubSignatureScannerRelativePathsToExclude != null && hubSignatureScannerRelativePathsToExclude.length > 0) {
            for (final String path : hubSignatureScannerRelativePathsToExclude) {
                excludedScanPaths.add(new File(sourceDirectory, path).getCanonicalPath());
            }
        }

        if (StringUtils.isNotBlank(hubSignatureScannerHostUrl) && StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            throw new DetectUserFriendlyException(
                    "You have provided both a hub signature scanner url AND a local hub signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                    ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        if (StringUtils.isNotBlank(hubSignatureScannerHostUrl)) {
            logger.info("A hub signature scanner url was provided, which requires hub offline mode. Setting hub offline mode to true.");
            if (hubOfflineMode == false) {
                addFieldWarning("hubSignatureScannerHostUrl", "A hub signature scanner host url was provided but hub offline mode was false. In the future set hub offline mode to true.");
                addFieldWarning("hubOfflineMode", "A signature scanner url was provided, so hub offline mode was forced to true.");
            }
            hubOfflineMode = true;
        }

        if (StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            logger.info("A local hub signature scanner path was provided, which requires hub offline mode. Setting hub offline mode to true.");
            if (hubOfflineMode == false) {
                addFieldWarning("hubSignatureScannerOfflineLocalPath", "A local hub signature scanner was provided but hub offline mode was false. In the future set hub offline mode to true.");
                addFieldWarning("hubOfflineMode", "A signature scanner path was provided, so hub offline mode was forced to true.");
            }
            hubOfflineMode = true;
        }

        //TODO Final home for directories to exclude
        bomToolSearchDirectoryExclusions = new ArrayList<>();
        try {
            if (bomToolSearchExclusionDefaults) {
                final String fileContent = ResourceUtil.getResourceAsString(BomToolFinder.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
                bomToolSearchDirectoryExclusions.addAll(Arrays.asList(fileContent.split("\r?\n")));
            }
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not determine the directories to exclude from the bom tool search. %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        configureForPhoneHome();
    }

    public void addFieldWarning(final String key, final String warning) {
        detectOptions.stream().forEach(option -> {
            if (option.getKey().equals(key)) {
                option.getWarnings().add(warning);
            }
        });
    }

    public void requestDeprecation(final String key) {
        detectOptions.stream().forEach(option -> {
            if (option.getKey().equals(key)) {
                option.requestDeprecation();
            }
        });
    }

    public List<String> getBomToolSearchDirectoryExclusions() {
        return bomToolSearchDirectoryExclusions;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public Set<String> getAllDetectPropertyKeys() {
        return allDetectPropertyKeys;
    }

    public Set<String> getAdditionalDockerPropertyNames() {
        return additionalDockerPropertyNames;
    }

    public Set<String> getAdditionalPhoneHomePropertyNames() {
        return additionalPhoneHomePropertyNames;
    }

    public boolean isUsingDefaultSourcePath() {
        return usingDefaultSourcePath;
    }

    public boolean isUsingDefaultOutputPath() {
        return usingDefaultOutputPath;
    }

    public boolean isBomToolIncluded(final BomToolType type) {
        return bomToolFilter.shouldInclude(type.toString());
    }

    public String getDetectProperty(final String key) {
        return configurableEnvironment.getProperty(key);
    }

    public String guessDetectJarLocation() {
        final String containsDetectJarRegex = ".*hub-detect-[^\\\\/]+\\.jar.*";
        final String javaClasspath = System.getProperty("java.class.path");
        if (javaClasspath != null && javaClasspath.matches(containsDetectJarRegex)) {
            for (final String classpathChunk : javaClasspath.split(System.getProperty("path.separator"))) {
                if (classpathChunk != null && classpathChunk.matches(containsDetectJarRegex)) {
                    logger.debug(String.format("Guessed Detect jar location as %s", classpathChunk));
                    return classpathChunk;
                }
            }
        }
        return "";
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setHost(hubProxyHost);
        proxyInfoBuilder.setPort(hubProxyPort);
        proxyInfoBuilder.setUsername(hubProxyUsername);
        proxyInfoBuilder.setPassword(hubProxyPassword);
        proxyInfoBuilder.setIgnoredProxyHosts(hubProxyIgnoredHosts);
        proxyInfoBuilder.setNtlmDomain(hubProxyNtlmDomain);
        proxyInfoBuilder.setNtlmWorkstation(hubProxyNtlmWorkstation);
        ProxyInfo proxyInfo = ProxyInfo.NO_PROXY_INFO;
        try {
            proxyInfo = proxyInfoBuilder.build();
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
        return proxyInfo;
    }

    private String getInspectorAirGapPath(final String inspectorLocationProperty, final String inspectorName) {
        if (StringUtils.isBlank(inspectorLocationProperty)) {
            try {
                final File detectJar = new File(guessDetectJarLocation()).getCanonicalFile();
                final File inspectorsDirectory = new File(detectJar.getParentFile(), "packaged-inspectors");
                final File inspectorAirGapDirectory = new File(inspectorsDirectory, inspectorName);
                return inspectorAirGapDirectory.getCanonicalPath();
            } catch (final Exception e) {
                logger.debug(String.format("Exception encountered when guessing air gap path for %s, returning the detect property instead", inspectorName));
                logger.debug(e.getMessage());
            }
        }
        return inspectorLocationProperty;
    }

    private int convertInt(final Integer integerObj) {
        return integerObj == null ? 0 : integerObj.intValue();
    }

    private long convertLong(final Long longObj) {
        return longObj == null ? 0L : longObj.longValue();
    }

    private void configureForDocker() {
        for (final String key : allDetectPropertyKeys) {
            if (key.startsWith(DOCKER_PROPERTY_PREFIX)) {
                additionalDockerPropertyNames.add(key);
            }
        }
    }

    private void configureForPhoneHome() {
        for (final String key : allDetectPropertyKeys) {
            if (key.startsWith(PHONE_HOME_PROPERTY_PREFIX)) {
                additionalPhoneHomePropertyNames.add(key);
            }
        }
    }

    private String createDirectoryPath(final String providedDirectoryPath, final String defaultDirectoryPath, final String defaultDirectoryName) throws IOException {
        if (StringUtils.isBlank(providedDirectoryPath)) {
            final File directory = new File(defaultDirectoryPath, defaultDirectoryName);
            return directory.getCanonicalPath();
        }
        return providedDirectoryPath;
    }

    private void ensureDirectoryExists(final String directoryPath, final String failureMessage) throws DetectUserFriendlyException {
        final File directory = new File(directoryPath);
        directory.mkdirs();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new DetectUserFriendlyException(String.format("The directory ${directoryPath} does not exist. %s", failureMessage), ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    public UnauthenticatedRestConnection createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
        restConnectionBuilder.setBaseUrl(url);
        restConnectionBuilder.setTimeout(getHubTimeout());
        restConnectionBuilder.applyProxyInfo(getHubProxyInfo());
        restConnectionBuilder.setLogger(new Slf4jIntLogger(logger));
        restConnectionBuilder.setAlwaysTrustServerCertificate(getHubTrustCertificate());

        return restConnectionBuilder.build();
    }

    // properties start

    @Value("${detect.fail.config.warning:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_GENERAL)
    @HelpDescription("If true, Detect will fail if there are any issues found in the configuration.")
    private Boolean failOnConfigWarning;

    @Value("${detect.force.success:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_GENERAL)
    @HelpDescription("If true, detect will always exit with code 0.")
    private Boolean forceSuccess;

    @Value("${detect.suppress.configuration.output:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing your configuration properties at startup will be suppressed.")
    private Boolean suppressConfigurationOutput;

    @Value("${detect.suppress.results.output:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing the Detect Results will be suppressed.")
    private Boolean suppressResultsOutput;

    @Value("${detect.cleanup:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_CLEANUP)
    @HelpDescription("If true the files created by Detect will be cleaned up.")
    private Boolean cleanupDetectFiles;

    @ValueDeprecation(willRemoveInVersion = "4.0.0", description = "To turn off file cleanup, set --detect.cleanup=false.")
    @Value("${detect.cleanup.bdio.files:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_CLEANUP)
    @HelpDescription("If true the bdio files will be deleted after upload")
    private Boolean cleanupBdioFiles;

    @Value("${detect.test.connection:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Test the connection to the Hub with the current configuration")
    private Boolean testConnection;

    @Value("${detect.api.timeout:}")
    @DefaultValue("300000")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("Timeout for response from the hub regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.")
    private Long apiTimeout;

    @Value("${blackduck.hub.url:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("URL of the Hub server")
    private String hubUrl;

    @Value("${blackduck.hub.timeout:}")
    @DefaultValue("120")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Time to wait for rest connections to complete")
    private Integer hubTimeout;

    @Value("${blackduck.hub.username:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub username")
    private String hubUsername;

    @Value("${blackduck.hub.password:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub password")
    private String hubPassword;

    @Value("${blackduck.hub.api.token:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub API Token")
    private String hubApiToken;

    @Value("${blackduck.hub.proxy.host:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy host")
    private String hubProxyHost;

    @Value("${blackduck.hub.proxy.port:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy port")
    private String hubProxyPort;

    @Value("${blackduck.hub.proxy.username:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy username")
    private String hubProxyUsername;

    @Value("${blackduck.hub.proxy.password:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy password")
    private String hubProxyPassword;

    @Value("${blackduck.hub.proxy.ntlm.domain:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy domain")
    private String hubProxyNtlmDomain;

    @Value("${blackduck.hub.proxy.ignored.hosts:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Comma separated list of host patterns that should not use the proxy")
    private String hubProxyIgnoredHosts;

    @Value("${blackduck.hub.proxy.ntlm.workstation:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy workstation")
    private String hubProxyNtlmWorkstation;

    @Value("${blackduck.hub.trust.cert:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, automatically trust the certificate for the current run of Detect only")
    private Boolean hubTrustCertificate;

    @Value("${blackduck.hub.offline.mode:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_OFFLINE })
    @HelpDescription("This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
    private Boolean hubOfflineMode;

    @Value("${detect.disable.without.hub:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.")
    private Boolean disableWithoutHub;

    @Value("${detect.resolve.tilde.in.paths:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("If set to false we will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.")
    private Boolean resolveTildeInPaths;

    @Value("${detect.source.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Source path to inspect")
    private String sourcePath;

    @Value("${detect.output.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Output path")
    private String outputDirectoryPath;

    @Value("${detect.bdio.output.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("The output directory for all bdio files. If not set, the bdio files will be in a 'bdio' subdirectory of the output path.")
    private String bdioOutputDirectoryPath;

    @Value("${detect.scan.output.path:}")
    @HelpGroup(primary = GROUP_PATHS, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The output directory for all scan files. If not set, the scan files will be in a 'scan' subdirectory of the output path.")
    private String scanOutputDirectoryPath;

    @Value("${detect.search.depth:}")
    @DefaultValue("3")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Depth from source paths to search for files.")
    private Integer searchDepth;

    @Value("${detect.bom.tool.search.depth:}")
    @DefaultValue("0")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Depth from source paths to search for files to determine if a bom tool applies.")
    private Integer bomToolSearchDepth;

    @Value("${detect.bom.tool.search.continue:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("If true, the bom tool search will continue to look for bom tools to the maximum search depth, even if they applied earlier in the path.")
    private Boolean bomToolContinueSearch;

    @Value("${detect.bom.tool.search.exclusion:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("A comma-separated list of directory names to exclude from the bom tool search.")
    private String[] bomToolSearchExclusion;

    @Value("${detect.bom.tool.search.exclusion.defaults:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("If true, the bom tool search will exclude the default directory names.")
    private Boolean bomToolSearchExclusionDefaults;

    @Value("${detect.excluded.bom.tool.types:}")
    @HelpGroup(primary = GROUP_BOMTOOL)
    @HelpDescription("By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.")
    private String excludedBomToolTypes;

    @Value("${detect.included.bom.tool.types:}")
    @HelpGroup(primary = GROUP_BOMTOOL)
    @HelpDescription("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
    private String includedBomToolTypes;

    @Value("${detect.project.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
    private String projectName;

    @Value("${detect.project.description:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project description is specified, your project version will be created with this description.")
    private String projectDescription;

    @Value("${detect.project.version.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
    private String projectVersionName;

    @Value("${detect.project.version.notes:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project version notes are specified, your project version will be created with these notes.")
    private String projectVersionNotes;

    @Value("${detect.project.tier:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If a hub project tier is specified, your project will be created with this tier.")
    @AcceptableValues(value = { "1", "2", "3", "4", "5" }, caseSensitive = false, strict = false)
    private Integer projectTier;

    @Value("${detect.project.codelocation.prefix:}")
    @DefaultValue("")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.")
    private String projectCodeLocationPrefix;

    @Value("${detect.project.codelocation.suffix:}")
    @DefaultValue("")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A suffix to the name of the codelocations created by Detect.")
    private String projectCodeLocationSuffix;

    @Value("${detect.project.codelocation.unmap:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, unmaps all other code locations mapped to the project version produced by the current run of Detect.")
    private Boolean projectCodeLocationUnmap;

    @Value("${detect.project.level.adjustments:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project level matches.")
    private String projectLevelMatchAdjustments;

    @Value("${detect.project.version.phase:}")
    @DefaultValue("Development")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version phase.")
    @AcceptableValues(value = { "PLANNING", "DEVELOPMENT", "RELEASED", "DEPRECATED", "ARCHIVED" }, caseSensitive = false, strict = false)
    private String projectVersionPhase;

    @Value("${detect.project.version.distribution:}")
    @DefaultValue("External")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version distribution")
    @AcceptableValues(value = { "EXTERNAL", "SAAS", "INTERNAL", "OPENSOURCE" }, caseSensitive = false, strict = false)
    private String projectVersionDistribution;

    @Value("${detect.project.version.update:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, will update the Project Version with the configured properties. See detailed help for more information.")
    @HelpDetailed("When set to true, the following properties will be updated on the Project. detect.project.tier, detect.project.level.adjustments.\r\n The following properties will also be updated on the Version. detect.project.version.notes, detect.project.version.phase, detect.project.version.distribution")
    private Boolean projectVersionUpdate;

    @ValueDeprecation(willRemoveInVersion = "4.0.0", description = "To fail on any policy, set --detect.policy.check.fail.on.severities=ALL.")
    @Value("${detect.policy.check:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_POLICY_CHECK, additional = { SEARCH_GROUP_POLICY })
    @HelpDescription("Set to true if you would like a policy check from the hub for your project. False by default")
    private Boolean policyCheck;

    @Value("${detect.policy.check.fail.on.severities:}")
    @HelpGroup(primary = GROUP_POLICY_CHECK, additional = { SEARCH_GROUP_POLICY })
    @HelpDescription("A comma-separated list of policy violation severities that will fail detect if checking policies is enabled. If no severity is provided, any policy violation will fail detect.")
    @AcceptableValues(value = { "ALL", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL" }, caseSensitive = false, strict = false)
    private String policyCheckFailOnSeverities;

    @Value("${detect.gradle.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Version of the Gradle Inspector")
    private String gradleInspectorVersion;

    @Value("${detect.gradle.build.command:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Gradle build command")
    private String gradleBuildCommand;

    @Value("${detect.gradle.excluded.configurations:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to exclude")
    private String gradleExcludedConfigurationNames;

    @Value("${detect.gradle.included.configurations:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to include")
    private String gradleIncludedConfigurationNames;

    @Value("${detect.gradle.excluded.projects:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to exclude")
    private String gradleExcludedProjectNames;

    @Value("${detect.gradle.included.projects:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to include")
    private String gradleIncludedProjectNames;

    @ValueDeprecation(willRemoveInVersion = "4.0.0", description = "To turn off file cleanup, set --detect.cleanup=false.")
    @Value("${detect.gradle.cleanup.build.blackduck.directory:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Set this to false if you do not want the 'blackduck' directory in your build directory to be deleted.")
    private Boolean gradleCleanupBuildBlackduckDirectory;

    @Value("${detect.nuget.inspector.name:}")
    @DefaultValue("IntegrationNugetInspector")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Name of the Nuget Inspector")
    private String nugetInspectorPackageName;

    @Value("${detect.nuget.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Version of the Nuget Inspector")
    private String nugetInspectorPackageVersion;

    @Value("${detect.nuget.excluded.modules:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to exclude")
    private String nugetInspectorExcludedModules;

    @Value("${detect.nuget.included.modules:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to include (overrides exclude)")
    private String nugetInspectorIncludedModules;

    @Value("${detect.nuget.ignore.failure:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("If true errors will be logged and then ignored.")
    private Boolean nugetInspectorIgnoreFailure;

    @Value("${detect.maven.scope:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The name of the dependency scope to include")
    private String mavenScope;

    @Value("${detect.maven.build.command:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("Maven build command")
    private String mavenBuildCommand;

    @Value("${detect.gradle.path:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Path of the Gradle executable")
    private String gradlePath;

    @Value("${detect.maven.path:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The path of the Maven executable")
    private String mavenPath;

    @Value("${detect.maven.excluded.modules:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to exclude")
    private String mavenExcludedModuleNames;

    @Value("${detect.maven.included.modules:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to include")
    private String mavenIncludedModuleNames;

    @Value("${detect.nuget.path:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path of the Nuget executable")
    private String nugetPath;

    @Value("${detect.pip.project.name:}")
    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("Override for pip inspector to find your project")
    private String pipProjectName;

    @Value("${detect.python.python3:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("If true will use Python 3 if available on class path")
    private Boolean pythonThreeOverride;

    @Value("${detect.python.path:}")
    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("The path of the Python executable")
    private String pythonPath;

    @Value("${detect.npm.path:}")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the Npm executable")
    private String npmPath;

    @Value("${detect.npm.include.dev.dependencies:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("Set this value to false if you would like to exclude your dev dependencies when ran")
    private String npmIncludeDevDependencies;

    @Value("${detect.npm.node.path:}")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the node executable that is used by Npm")
    private String npmNodePath;

    @Value("${detect.pear.path:}")
    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("The path of the pear executable")
    private String pearPath;

    @Value("${detect.pear.only.required.deps:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("Set to true if you would like to include only required packages")
    private Boolean pearOnlyRequiredDependencies;

    @Value("${detect.pip.requirements.path:}")
    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("The path of the requirements.txt file")
    private String requirementsFilePath;

    @Value("${detect.go.dep.path:}")
    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("Path of the Go Dep executable")
    private String goDepPath;

    @Value("${detect.go.run.dep.init:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("If set to true, we will attempt to run 'init' and 'ensure' which can modify your development environment.")
    private Boolean goRunDepInit;

    @Value("${detect.docker.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Path of the docker executable")
    private String dockerPath;

    @Value("${detect.docker.inspector.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("This is used to override using the hosted script by github url. You can provide your own script at this path.")
    private String dockerInspectorPath;

    @Value("${detect.docker.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Version of the Hub Docker Inspector to use")
    private String dockerInspectorVersion;

    @Value("${detect.docker.tar:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.")
    private String dockerTar;

    @Value("${detect.docker.image:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.")
    private String dockerImage;

    @Value("${detect.bash.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Path of the bash executable")
    private String bashPath;

    @Value("${logging.level.com.blackducksoftware.integration:}")
    @DefaultValue("INFO")
    @HelpGroup(primary = GROUP_LOGGING, additional = { GROUP_LOGGING, SEARCH_GROUP_DEBUG })
    @HelpDescription("The logging level of Detect")
    @AcceptableValues(value = { "ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF" }, caseSensitive = false, strict = true)
    private String loggingLevel;

    @ValueDeprecation(willRemoveInVersion = "4.0.0", description = "To turn off file cleanup, set --detect.cleanup=false.")
    @Value("${detect.cleanup.bom.tool.files:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_CLEANUP, additional = { GROUP_CLEANUP, SEARCH_GROUP_DEBUG })
    @HelpDescription("Detect creates temporary files in the output directory. If set to true this will clean them up after execution")
    private Boolean cleanupBomToolFiles;

    @Value("${detect.hub.signature.scanner.dry.run:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.")
    private Boolean hubSignatureScannerDryRun;

    @Value("${detect.hub.signature.scanner.snippet.mode:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.")
    private Boolean hubSignatureScannerSnippetMode;

    @Value("${detect.hub.signature.scanner.exclusion.patterns:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("Enables you to specify sub-directories to exclude from scans")
    private String[] hubSignatureScannerExclusionPatterns;

    @Value("${detect.hub.signature.scanner.paths:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("These paths and only these paths will be scanned.")
    private String[] hubSignatureScannerPaths;

    @Value("${detect.hub.signature.scanner.relative.paths.to.exclude:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The relative paths of directories to be excluded from scan registration")
    private String[] hubSignatureScannerRelativePathsToExclude;

    @Value("${detect.hub.signature.scanner.memory:}")
    @DefaultValue("4096")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The memory for the scanner to use.")
    private Integer hubSignatureScannerMemory;

    @Value("${detect.hub.signature.scanner.disabled:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_DEBUG, SEARCH_GROUP_HUB })
    @HelpDescription("Set to true to disable the Hub Signature Scanner.")
    private Boolean hubSignatureScannerDisabled;

    @Value("${detect.hub.signature.scanner.offline.local.path:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_HUB })
    @HelpDescription("To use a local signature scanner, set its location with this property. This will be the path where the signature scanner was unzipped. This will likely look similar to /some/path/scan.cli-x.y.z")
    private String hubSignatureScannerOfflineLocalPath;

    @Value("${detect.hub.signature.scanner.host.url:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.")
    private String hubSignatureScannerHostUrl;

    @Value("${detect.hub.signature.scanner.parallel.processors:}")
    @DefaultValue("1")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
    private Integer hubSignatureScannerParallelProcessors;

    @Value("${detect.packagist.include.dev.dependencies:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PACKAGIST)
    @HelpDescription("Set this value to false if you would like to exclude your dev requires dependencies when ran")
    private Boolean packagistIncludeDevDependencies;

    @Value("${detect.perl.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the perl executable")
    private String perlPath;

    @Value("${detect.cpan.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpan executable")
    private String cpanPath;

    @Value("${detect.cpanm.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpanm executable")
    private String cpanmPath;

    @Value("${detect.sbt.excluded.configurations:}")
    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to exclude")
    private String sbtExcludedConfigurationNames;

    @Value("${detect.sbt.included.configurations:}")
    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to include")
    private String sbtIncludedConfigurationNames;

    @Value("${detect.default.project.version.scheme:}")
    @DefaultValue("text")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'")
    private String defaultProjectVersionScheme;

    @Value("${detect.default.project.version.text:}")
    @DefaultValue("Detect Unknown Version")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The text to use as the default project version")
    private String defaultProjectVersionText;

    @Value("${detect.default.project.version.timeformat:}")
    @DefaultValue("yyyy-MM-dd\\'T\\'HH:mm:ss.SSS")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The timestamp format to use as the default project version")
    private String defaultProjectVersionTimeformat;

    @Value("${detect.bom.aggregate.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided. For Co-Pilot use only")
    private String aggregateBomName;

    @Value("${detect.risk.report.pdf:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck risk report in PDF form will be created")
    private Boolean riskReportPdf;

    @Value("${detect.risk.report.pdf.path:}")
    @DefaultValue(".")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for risk report in PDF. Default is the source directory")
    private String riskReportPdfOutputDirectory;

    @Value("${detect.notices.report:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck notices report in text form will be created in your source directory")
    private Boolean noticesReport;

    @Value("${detect.notices.report.path:}")
    @DefaultValue(".")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for notices report. Default is the source directory")
    private String noticesReportOutputDirectory;

    @Value("${detect.conda.path:}")
    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The path of the conda executable")
    private String condaPath;

    @Value("${detect.conda.environment.name:}")
    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The name of the anaconda environment used by your project")
    private String condaEnvironmentName;

    @Value("${detect.docker.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The path to the directory containing the docker inspector script, jar, and images")
    private String dockerInspectorAirGapPath;

    @Value("${detect.gradle.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The path to the directory containing the air gap dependencies for the gradle inspector")
    private String gradleInspectorAirGapPath;

    @Value("${detect.nuget.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path to the directory containing the nuget inspector nupkg")
    private String nugetInspectorAirGapPath;

    @Value("${detect.nuget.packages.repo.url:}")
    @DefaultValue("https://www.nuget.org/api/v2/")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The source for nuget packages")
    private String[] nugetPackagesRepoUrl;

    @Value("${detect.gradle.inspector.repository.url:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The respository gradle should use to look for the gradle inspector")
    private String gradleInspectorRepositoryUrl;

    @Value("${detect.hex.rebar3.path:}")
    @HelpGroup(primary = GROUP_HEX)
    @HelpDescription("The path of the rebar3 executable")
    private String hexRebar3Path;

    @Value("${detect.yarn.path:}")
    @HelpDescription("The path of the Yarn executable")
    @HelpGroup(primary = GROUP_YARN)
    private String yarnPath;

    @Value("${detect.yarn.prod.only:}")
    @HelpDescription("Set this to true to only scan production dependencies")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_YARN)
    private String yarnProductionDependenciesOnly;

    public boolean getCleanupBdioFiles() {
        return BooleanUtils.toBoolean(cleanupBdioFiles);
    }

    public Boolean getCleanupDetectFiles() {
        return BooleanUtils.toBoolean(cleanupDetectFiles);
    }

    public boolean getTestConnection() {
        return BooleanUtils.toBoolean(testConnection);
    }

    public long getApiTimeout() {
        return convertLong(apiTimeout);
    }

    public String getHubUrl() {
        return hubUrl;
    }

    public int getHubTimeout() {
        return convertInt(hubTimeout);
    }

    public String getHubUsername() {
        return hubUsername;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public String getHubApiToken() {
        return hubApiToken;
    }

    public String getHubProxyHost() {
        return hubProxyHost;
    }

    public String getHubProxyPort() {
        return hubProxyPort;
    }

    public String getHubProxyUsername() {
        return hubProxyUsername;
    }

    public String getHubProxyPassword() {
        return hubProxyPassword;
    }

    public String getHubProxyIgnoredHosts() {
        return hubProxyIgnoredHosts;
    }

    public String getHubProxyNtlmDomain() {
        return hubProxyNtlmDomain;
    }

    public String getHubProxyNtlmWorkstation() {
        return hubProxyNtlmWorkstation;
    }

    public boolean getHubOfflineMode() {
        return BooleanUtils.toBoolean(hubOfflineMode);
    }

    public boolean getDisableWithoutHub() {
        return BooleanUtils.toBoolean(disableWithoutHub);
    }

    public boolean getHubTrustCertificate() {
        return BooleanUtils.toBoolean(hubTrustCertificate);
    }

    public boolean getResolveTildeInPaths() {
        return BooleanUtils.toBoolean(resolveTildeInPaths);
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }

    public String getBdioOutputDirectoryPath() {
        return bdioOutputDirectoryPath;
    }

    public String getScanOutputDirectoryPath() {
        return scanOutputDirectoryPath;
    }

    public int getSearchDepth() {
        return convertInt(searchDepth);
    }

    public int getBomToolSearchDepth() {
        return convertInt(bomToolSearchDepth);
    }

    public Boolean getBomToolContinueSearch() {
        return BooleanUtils.toBoolean(bomToolContinueSearch);
    }

    public String[] getBomToolSearchExclusion() {
        return bomToolSearchExclusion;
    }

    public Boolean getBomToolSearchExclusionDefaults() {
        return BooleanUtils.toBoolean(bomToolSearchExclusionDefaults);
    }

    public String getExcludedBomToolTypes() {
        return excludedBomToolTypes == null ? null : excludedBomToolTypes.toUpperCase();
    }

    public String getIncludedBomToolTypes() {
        return includedBomToolTypes == null ? null : includedBomToolTypes.toUpperCase();
    }

    public String getProjectName() {
        return projectName == null ? null : projectName.trim();
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectVersionName() {
        return projectVersionName == null ? null : projectVersionName.trim();
    }

    public String getProjectVersionNotes() {
        return projectVersionNotes;
    }

    // we want to perserve the possibility of a null tier
    public Integer getProjectTier() {
        return projectTier;
    }

    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix == null ? null : projectCodeLocationPrefix.trim();
    }

    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix == null ? null : projectCodeLocationSuffix.trim();
    }

    public boolean getProjectCodeLocationUnmap() {
        return BooleanUtils.toBoolean(projectCodeLocationUnmap);
    }

    public boolean getProjectLevelMatchAdjustments() {
        return BooleanUtils.toBoolean(projectLevelMatchAdjustments);
    }

    public String getProjectVersionPhase() {
        return projectVersionPhase == null ? null : projectVersionPhase.trim();
    }

    public String getProjectVersionDistribution() {
        return projectVersionDistribution == null ? null : projectVersionDistribution.trim();
    }

    public boolean getProjectVersionUpdate() {
        return BooleanUtils.toBoolean(projectVersionUpdate);
    }

    public boolean getPolicyCheck() {
        return BooleanUtils.toBoolean(policyCheck);
    }

    public String getPolicyCheckFailOnSeverities() {
        return policyCheckFailOnSeverities;
    }

    public String getGradleInspectorVersion() {
        return gradleInspectorVersion;
    }

    public String getGradleBuildCommand() {
        return gradleBuildCommand;
    }

    public String getGradleExcludedConfigurationNames() {
        return gradleExcludedConfigurationNames;
    }

    public String getGradleIncludedConfigurationNames() {
        return gradleIncludedConfigurationNames;
    }

    public String getGradleExcludedProjectNames() {
        return gradleExcludedProjectNames;
    }

    public String getGradleIncludedProjectNames() {
        return gradleIncludedProjectNames;
    }

    public boolean getGradleCleanupBuildBlackduckDirectory() {
        return BooleanUtils.toBoolean(gradleCleanupBuildBlackduckDirectory);
    }

    public String getNugetInspectorPackageName() {
        return nugetInspectorPackageName;
    }

    public String getNugetInspectorPackageVersion() {
        return nugetInspectorPackageVersion;
    }

    public String getNugetInspectorExcludedModules() {
        return nugetInspectorExcludedModules;
    }

    public String getNugetInspectorIncludedModules() {
        return nugetInspectorIncludedModules;
    }

    public boolean getNugetInspectorIgnoreFailure() {
        return BooleanUtils.toBoolean(nugetInspectorIgnoreFailure);
    }

    public String getMavenScope() {
        return mavenScope;
    }

    public String getGradlePath() {
        return gradlePath;
    }

    public String getMavenPath() {
        return mavenPath;
    }

    public String getMavenExcludedModuleNames() {
        return mavenExcludedModuleNames;
    }

    public String getMavenIncludedModuleNames() {
        return mavenIncludedModuleNames;
    }

    public String getMavenBuildCommand() {
        return mavenBuildCommand;
    }

    public String getNugetPath() {
        return nugetPath;
    }

    public String getNpmPath() {
        return npmPath;
    }

    public boolean getNpmIncludeDevDependencies() {
        return BooleanUtils.toBoolean(npmIncludeDevDependencies);
    }

    public String getNpmNodePath() {
        return npmNodePath;
    }

    public String getPearPath() {
        return pearPath;
    }

    public boolean getPearOnlyRequiredDependencies() {
        return BooleanUtils.toBoolean(pearOnlyRequiredDependencies);
    }

    public String getPipProjectName() {
        return pipProjectName;
    }

    public boolean getPythonThreeOverride() {
        return BooleanUtils.toBoolean(pythonThreeOverride);
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public String getRequirementsFilePath() {
        return requirementsFilePath;
    }

    public String getGoDepPath() {
        return goDepPath;
    }

    public boolean getGoRunDepInit() {
        return BooleanUtils.toBoolean(goRunDepInit);
    }

    public String getDockerPath() {
        return dockerPath;
    }

    public String getDockerInspectorPath() {
        return dockerInspectorPath;
    }

    public String getDockerInspectorVersion() {
        return dockerInspectorVersion;
    }

    public String getDockerTar() {
        return dockerTar;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public String getBashPath() {
        return bashPath;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public Boolean getFailOnConfigWarning() {
        return BooleanUtils.toBoolean(failOnConfigWarning);
    }

    public boolean getCleanupBomToolFiles() {
        return BooleanUtils.toBoolean(cleanupBomToolFiles);
    }

    public boolean getSuppressConfigurationOutput() {
        return BooleanUtils.toBoolean(suppressConfigurationOutput);
    }

    public boolean getForceSuccess() {
        return BooleanUtils.toBoolean(forceSuccess);
    }

    public boolean getSuppressResultsOutput() {
        return BooleanUtils.toBoolean(suppressResultsOutput);
    }

    public boolean getHubSignatureScannerDryRun() {
        return BooleanUtils.toBoolean(hubSignatureScannerDryRun);
    }

    public boolean getHubSignatureScannerSnippetMode() {
        return BooleanUtils.toBoolean(hubSignatureScannerSnippetMode);
    }

    public String[] getHubSignatureScannerPaths() {
        return hubSignatureScannerPaths;
    }

    public String[] getHubSignatureScannerExclusionPatterns() {
        return hubSignatureScannerExclusionPatterns;
    }

    public List<String> getHubSignatureScannerPathsToExclude() {
        return excludedScanPaths;
    }

    public String getHubSignatureScannerOfflineLocalPath() {
        return hubSignatureScannerOfflineLocalPath;
    }

    public String getHubSignatureScannerHostUrl() {
        return hubSignatureScannerHostUrl;
    }

    public boolean getPackagistIncludeDevDependencies() {
        return BooleanUtils.toBoolean(packagistIncludeDevDependencies);
    }

    public int getHubSignatureScannerMemory() {
        return convertInt(hubSignatureScannerMemory);
    }

    public boolean getHubSignatureScannerDisabled() {
        return BooleanUtils.toBoolean(hubSignatureScannerDisabled);
    }

    public int getHubSignatureScannerParallelProcessors() {
        return convertInt(hubSignatureScannerParallelProcessors);
    }

    public String getPerlPath() {
        return perlPath == null ? null : perlPath.trim();
    }

    public String getCpanPath() {
        return cpanPath == null ? null : cpanPath.trim();
    }

    public String getCpanmPath() {
        return cpanmPath == null ? null : cpanmPath.trim();
    }

    public String getSbtExcludedConfigurationNames() {
        return sbtExcludedConfigurationNames;
    }

    public String getSbtIncludedConfigurationNames() {
        return sbtIncludedConfigurationNames;
    }

    public String getDefaultProjectVersionScheme() {
        return defaultProjectVersionScheme == null ? null : defaultProjectVersionScheme.trim();
    }

    public String getDefaultProjectVersionText() {
        return defaultProjectVersionText == null ? null : defaultProjectVersionText.trim();
    }

    public String getDefaultProjectVersionTimeformat() {
        return defaultProjectVersionTimeformat == null ? null : defaultProjectVersionTimeformat.trim();
    }

    public String getAggregateBomName() {
        return aggregateBomName == null ? null : aggregateBomName.trim();
    }

    public String getCondaPath() {
        return condaPath == null ? null : condaPath.trim();
    }

    public String getCondaEnvironmentName() {
        return condaEnvironmentName == null ? null : condaEnvironmentName.trim();
    }

    public Boolean getRiskReportPdf() {
        return riskReportPdf;
    }

    public String getRiskReportPdfOutputDirectory() {
        return riskReportPdfOutputDirectory;
    }

    public Boolean getNoticesReport() {
        return noticesReport;
    }

    public String getNoticesReportOutputDirectory() {
        return noticesReportOutputDirectory;
    }

    public String getDockerInspectorAirGapPath() {
        return getInspectorAirGapPath(dockerInspectorAirGapPath, DOCKER);
    }

    public String getGradleInspectorAirGapPath() {
        return getInspectorAirGapPath(gradleInspectorAirGapPath, GRADLE);
    }

    public String getNugetInspectorAirGapPath() {
        return getInspectorAirGapPath(nugetInspectorAirGapPath, NUGET);
    }

    public String[] getNugetPackagesRepoUrl() {
        return nugetPackagesRepoUrl;
    }

    public String getGradleInspectorRepositoryUrl() {
        return gradleInspectorRepositoryUrl == null ? null : gradleInspectorRepositoryUrl.trim();
    }

    public String getHexRebar3Path() {
        return hexRebar3Path;
    }

    public String getYarnPath() {
        return yarnPath;
    }

    public Boolean getYarnProductionDependenciesOnly() {
        return BooleanUtils.toBoolean(yarnProductionDependenciesOnly);
    }

    // properties end

}
