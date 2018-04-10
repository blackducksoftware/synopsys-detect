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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.GradleBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.NugetBomTool;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.ValueDescription;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
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

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Autowired
    private DockerBomTool dockerBomTool;

    @Autowired
    private NugetBomTool nugetBomTool;

    @Autowired
    private GradleBomTool gradleBomTool;

    @Autowired
    private TildeInPathResolver tildeInPathResolver;

    private File sourceDirectory;
    private File outputDirectory;

    private final Set<String> allDetectPropertyKeys = new HashSet<>();
    private final Set<String> additionalDockerPropertyNames = new HashSet<>();
    private final Set<String> additionalPhoneHomePropertyNames = new HashSet<>();

    private boolean usingDefaultSourcePath;
    private boolean usingDefaultOutputPath;

    private ExcludedIncludedFilter bomToolFilter;
    private final List<String> excludedScanPaths = new ArrayList<>();

    public void init() throws DetectUserFriendlyException, IOException, IllegalArgumentException, IllegalAccessException {
        final String systemUserHome = System.getProperty("user.home");
        if (resolveTildeInPaths) {
            tildeInPathResolver.resolveTildeInAllPathFields(systemUserHome, this);
        }

        if (StringUtils.isBlank(sourcePath)) {
            usingDefaultSourcePath = true;
            sourcePath = System.getProperty("user.dir");
        }

        sourceDirectory = new File(sourcePath);
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            throw new DetectUserFriendlyException("The source path ${sourcePath} either doesn't exist, isn't a directory, or doesn't have appropriate permissions.", ExitCodeType.FAILURE_GENERAL_ERROR);
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

        if (dockerBomTool.isBomToolApplicable() && bomToolFilter.shouldInclude(dockerBomTool.getBomToolType().toString())) {
            configureForDocker();
        }

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
            hubOfflineMode = true;
        }

        if (StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            logger.info("A local hub signature scanner path was provided, which requires hub offline mode. Setting hub offline mode to true.");
            hubOfflineMode = true;
        }

        if (gradleBomTool.isBomToolApplicable() && bomToolFilter.shouldInclude(gradleBomTool.getBomToolType().toString())) {
            gradleInspectorVersion = gradleBomTool.getInspectorVersion();
        }

        if (nugetBomTool.isBomToolApplicable() && bomToolFilter.shouldInclude(nugetBomTool.getBomToolType().toString())) {
            nugetInspectorPackageVersion = nugetBomTool.getInspectorVersion();
        }

        if (dockerBomTool.isBomToolApplicable() && bomToolFilter.shouldInclude(dockerBomTool.getBomToolType().toString())) {
            dockerInspectorVersion = dockerBomTool.getInspectorVersion();
        }

        configureForPhoneHome();
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

    public boolean shouldRun(final BomTool bomTool) {
        return bomToolFilter.shouldInclude(bomTool.getBomToolType().toString()) && bomTool.isBomToolApplicable();
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
        if (StringUtils.isNotBlank(inspectorLocationProperty)) {
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

    @ValueDescription(description = "If true, detect will always exit with code 0.", defaultValue = "false", group = DetectConfiguration.GROUP_GENERAL)
    @Value("${detect.force.success:}")
    private Boolean forceSuccess;

    @ValueDescription(description = "If true, the default behavior of printing your configuration properties at startup will be suppressed.", defaultValue = "false", group = DetectConfiguration.GROUP_LOGGING)
    @Value("${detect.suppress.configuration.output:}")
    private Boolean suppressConfigurationOutput;

    @ValueDescription(description = "If true, the default behavior of printing the Detect Results will be suppressed.", defaultValue = "false", group = DetectConfiguration.GROUP_LOGGING)
    @Value("${detect.suppress.results.output:}")
    private Boolean suppressResultsOutput;

    @ValueDescription(description = "If true the bdio files will be deleted after upload", defaultValue = "true", group = DetectConfiguration.GROUP_CLEANUP)
    @Value("${detect.cleanup.bdio.files:}")
    private Boolean cleanupBdioFiles;

    @ValueDescription(description = "Test the connection to the Hub with the current configuration", defaultValue = "false", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${detect.test.connection:}")
    private Boolean testConnection;

    @ValueDescription(description = "Timeout for response from the hub regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.", defaultValue = "300000", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.api.timeout:}")
    private Long apiTimeout;

    @ValueDescription(description = "URL of the Hub server", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.url:}")
    private String hubUrl;

    @ValueDescription(description = "Time to wait for rest connections to complete", defaultValue = "120", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.timeout:}")
    private Integer hubTimeout;

    @ValueDescription(description = "Hub username", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.username:}")
    private String hubUsername;

    @ValueDescription(description = "Hub password", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.password:}")
    private String hubPassword;

    @ValueDescription(description = "Hub API Token", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.api.token:}")
    private String hubApiToken;

    @ValueDescription(description = "Proxy host", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.host:}")
    private String hubProxyHost;

    @ValueDescription(description = "Proxy port", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.port:}")
    private String hubProxyPort;

    @ValueDescription(description = "Proxy username", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.username:}")
    private String hubProxyUsername;

    @ValueDescription(description = "Proxy password", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.password:}")
    private String hubProxyPassword;

    @ValueDescription(description = "Comma separated list of host patterns that should not use the proxy", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.ignored.hosts:}")
    private String hubProxyIgnoredHosts;

    @ValueDescription(description = "Ntlm Proxy domain", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.ntlm.domain:}")
    private String hubProxyNtlmDomain;

    @ValueDescription(description = "Ntlm Proxy workstation", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.proxy.ntlm.workstation:}")
    private String hubProxyNtlmWorkstation;

    @ValueDescription(description = "If true, automatically trust the certificate for the current run of Detect only", defaultValue = "false", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.trust.cert:}")
    private Boolean hubTrustCertificate;

    @ValueDescription(description = "This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.", defaultValue = "false", group = DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value("${blackduck.hub.offline.mode:}")
    private Boolean hubOfflineMode;

    @ValueDescription(description = "If set to false we will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.", defaultValue = "true", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.resolve.tilde.in.paths:}")
    private Boolean resolveTildeInPaths;

    @ValueDescription(description = "Source path to inspect", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.source.path:}")
    private String sourcePath;

    @ValueDescription(description = "Output path", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.output.path:}")
    private String outputDirectoryPath;

    @ValueDescription(description = "The output directory for all bdio files. If not set, the bdio files will be in a 'bdio' subdirectory of the output path.", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bdio.output.path:}")
    private String bdioOutputDirectoryPath;

    @ValueDescription(description = "The output directory for all scan files. If not set, the scan files will be in a 'scan' subdirectory of the output path.", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.scan.output.path:}")
    private String scanOutputDirectoryPath;

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue = "3", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.search.depth:}")
    private Integer searchDepth;

    @ValueDescription(description = "Depth from source paths to search for files to determine if a bom tool applies.", defaultValue = "0", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bom.tool.search.depth:}")
    private Integer bomToolSearchDepth;

    @ValueDescription(description = "If true, the bom tool search will continue to look for bom tools to the maximum search depth, even if they applied earlier in the path.", defaultValue = "false", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bom.tool.search.continue:}")
    private Boolean bomToolContinueSearch;

    @ValueDescription(description = "A comma-separated list of directory names to exclude from the bom tool search.", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bom.tool.search.exclusion:}")
    private String[] bomToolSearchExclusion;

    @ValueDescription(description = "If true, the bom tool search will exclude the default directory names.", defaultValue = "true", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bom.tool.search.exclusion.defaults:}")
    private Boolean bomToolSearchExclusionDefaults;

    @ValueDescription(description = "By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.", group = DetectConfiguration.GROUP_BOMTOOL)
    @Value("${detect.excluded.bom.tool.types:}")
    private String excludedBomToolTypes;

    @ValueDescription(description = "By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", group = DetectConfiguration.GROUP_BOMTOOL)
    @Value("${detect.included.bom.tool.types:}")
    private String includedBomToolTypes;

    @ValueDescription(description = "An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.name:}")
    private String projectName;

    @ValueDescription(description = "An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.version.name:}")
    private String projectVersionName;

    @ValueDescription(description = "If project version notes are specified, your project version will be created with these notes.", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.version.notes:}")
    private String projectVersionNotes;

    @ValueDescription(description = "If a hub project tier is specified, your project will be created with this tier.", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.tier:}")
    private Integer projectTier;

    @ValueDescription(description = "A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.", defaultValue = "", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.codelocation.prefix:}")
    private String projectCodeLocationPrefix;

    @ValueDescription(description = "A suffix to the name of the codelocations created by Detect.", defaultValue = "", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.codelocation.suffix:}")
    private String projectCodeLocationSuffix;

    @ValueDescription(description = "If set to true, when an old code location format is found in the Hub, instead of logging a warning, the code location will be deleted. USE WITH CAUTION - THIS CAN DELETE CODE LOCATIONS IN THE HUB.", defaultValue = "false", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.codelocation.delete.old.names:}")
    private Boolean projectCodeLocationDeleteOldNames;

    @ValueDescription(description = "An override for the Project level matches.", defaultValue = "true", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.level.adjustments:}")
    private String projectLevelMatchAdjustments;

    @ValueDescription(description = "An override for the Project Version phase.", defaultValue = "Development", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.version.phase:}")
    private String projectVersionPhase;

    @ValueDescription(description = "An override for the Project Version distribution", defaultValue = "External", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.project.version.distribution:}")
    private String projectVersionDistribution;

    @ValueDescription(description = "Set to true if you would like a policy check from the hub for your project. False by default", defaultValue = "false", group = DetectConfiguration.GROUP_POLICY_CHECK)
    @Value("${detect.policy.check:}")
    private Boolean policyCheck;

    @ValueDescription(description = "A comma-separated list of policy violation severities that will fail detect if checking policies is enabled. If no severity is provided, any policy violation will fail detect.", group = DetectConfiguration.GROUP_POLICY_CHECK)
    @Value("${detect.policy.check.fail.on.severities:}")
    private String policyCheckFailOnSeverities;

    @ValueDescription(description = "Version of the Gradle Inspector", defaultValue = "latest", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.inspector.version:}")
    private String gradleInspectorVersion;

    @ValueDescription(description = "Gradle build command", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.build.command:}")
    private String gradleBuildCommand;

    @ValueDescription(description = "The names of the dependency configurations to exclude", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.excluded.configurations:}")
    private String gradleExcludedConfigurationNames;

    @ValueDescription(description = "The names of the dependency configurations to include", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.included.configurations:}")
    private String gradleIncludedConfigurationNames;

    @ValueDescription(description = "The names of the projects to exclude", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.excluded.projects:}")
    private String gradleExcludedProjectNames;

    @ValueDescription(description = "The names of the projects to include", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.included.projects:}")
    private String gradleIncludedProjectNames;

    @ValueDescription(description = "Set this to false if you do not want the 'blackduck' directory in your build directory to be deleted.", defaultValue = "true", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.cleanup.build.blackduck.directory:}")
    private Boolean gradleCleanupBuildBlackduckDirectory;

    @ValueDescription(description = "Name of the Nuget Inspector", defaultValue = "IntegrationNugetInspector", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.inspector.name:}")
    private String nugetInspectorPackageName;

    @ValueDescription(description = "Version of the Nuget Inspector", defaultValue = "latest", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.inspector.version:}")
    private String nugetInspectorPackageVersion;

    @ValueDescription(description = "The names of the projects in a solution to exclude", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.excluded.modules:}")
    private String nugetInspectorExcludedModules;

    @ValueDescription(description = "The names of the projects in a solution to include (overrides exclude)", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.included.modules:}")
    private String nugetInspectorIncludedModules;

    @ValueDescription(description = "If true errors will be logged and then ignored.", defaultValue = "false", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.ignore.failure:}")
    private Boolean nugetInspectorIgnoreFailure;

    @ValueDescription(description = "The name of the dependency scope to include", group = DetectConfiguration.GROUP_MAVEN)
    @Value("${detect.maven.scope:}")
    private String mavenScope;

    @ValueDescription(description = "Maven build command", group = DetectConfiguration.GROUP_MAVEN)
    @Value("${detect.maven.build.command:}")
    private String mavenBuildCommand;

    @ValueDescription(description = "Path of the Gradle executable", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.path:}")
    private String gradlePath;

    @ValueDescription(description = "The path of the Maven executable", group = DetectConfiguration.GROUP_MAVEN)
    @Value("${detect.maven.path:}")
    private String mavenPath;

    @ValueDescription(description = "The names of the module to exclude", group = DetectConfiguration.GROUP_MAVEN)
    @Value("${detect.maven.excluded.modules:}")
    private String mavenExcludedModuleNames;

    @ValueDescription(description = "The names of the module to include", group = DetectConfiguration.GROUP_MAVEN)
    @Value("${detect.maven.included.modules:}")
    private String mavenIncludedModuleNames;

    @ValueDescription(description = "The path of the Nuget executable", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.path:}")
    private String nugetPath;

    @ValueDescription(description = "Override for pip inspector to find your project", group = DetectConfiguration.GROUP_PIP)
    @Value("${detect.pip.project.name:}")
    private String pipProjectName;

    @ValueDescription(description = "If true will use Python 3 if available on class path", defaultValue = "false", group = DetectConfiguration.GROUP_PYTHON)
    @Value("${detect.python.python3:}")
    private Boolean pythonThreeOverride;

    @ValueDescription(description = "The path of the Python executable", group = DetectConfiguration.GROUP_PYTHON)
    @Value("${detect.python.path:}")
    private String pythonPath;

    @ValueDescription(description = "The path of the Npm executable", group = DetectConfiguration.GROUP_NPM)
    @Value("${detect.npm.path:}")
    private String npmPath;

    @ValueDescription(description = "Set this value to false if you would like to exclude your dev dependencies when ran", defaultValue = "true", group = DetectConfiguration.GROUP_NPM)
    @Value("${detect.npm.include.dev.dependencies:}")
    private String npmIncludeDevDependencies;

    @ValueDescription(description = "The path of the node executable that is used by Npm", group = DetectConfiguration.GROUP_NPM)
    @Value("${detect.npm.node.path:}")
    private String npmNodePath;

    @ValueDescription(description = "The path of the pear executable", group = DetectConfiguration.GROUP_PEAR)
    @Value("${detect.pear.path:}")
    private String pearPath;

    @ValueDescription(description = "Set to true if you would like to include only required packages", defaultValue = "false", group = DetectConfiguration.GROUP_PEAR)
    @Value("${detect.pear.only.required.deps:}")
    private Boolean pearOnlyRequiredDependencies;

    @ValueDescription(description = "The path of the requirements.txt file", group = DetectConfiguration.GROUP_PIP)
    @Value("${detect.pip.requirements.path:}")
    private String requirementsFilePath;

    @ValueDescription(description = "Path of the Go Dep executable", group = DetectConfiguration.GROUP_GO)
    @Value("${detect.go.dep.path:}")
    private String goDepPath;

    @ValueDescription(description = "If set to true, we will attempt to run 'init' and 'ensure' which can modify your development environment.", defaultValue = "false", group = DetectConfiguration.GROUP_GO)
    @Value("${detect.go.run.dep.init:}")
    private Boolean goRunDepInit;

    @ValueDescription(description = "Path of the docker executable", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.path:}")
    private String dockerPath;

    @ValueDescription(description = "This is used to override using the hosted script by github url. You can provide your own script at this path.", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.inspector.path:}")
    private String dockerInspectorPath;

    @ValueDescription(description = "Version of the Hub Docker Inspector to use", defaultValue = "latest", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.inspector.version:}")
    private String dockerInspectorVersion;

    @ValueDescription(description = "A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.tar:}")
    private String dockerTar;

    @ValueDescription(description = "The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.image:}")
    private String dockerImage;

    @ValueDescription(description = "Path of the bash executable", group = DetectConfiguration.GROUP_PATHS)
    @Value("${detect.bash.path:}")
    private String bashPath;

    @ValueDescription(description = "The logging level of Detect (ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)", defaultValue = "INFO", group = DetectConfiguration.GROUP_LOGGING)
    @Value("${logging.level.com.blackducksoftware.integration:}")
    private String loggingLevel;

    @ValueDescription(description = "Detect creates temporary files in the output directory. If set to true this will clean them up after execution", defaultValue = "true", group = DetectConfiguration.GROUP_CLEANUP)
    @Value("${detect.cleanup.bom.tool.files:}")
    private Boolean cleanupBomToolFiles;

    @ValueDescription(description = "If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.", defaultValue = "false", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.dry.run:}")
    private Boolean hubSignatureScannerDryRun;

    @ValueDescription(description = "If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.", defaultValue = "false", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.snippet.mode:}")
    private Boolean hubSignatureScannerSnippetMode;

    @ValueDescription(description = "Enables you to specify sub-directories to exclude from scans", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.exclusion.patterns:}")
    private String[] hubSignatureScannerExclusionPatterns;

    @ValueDescription(description = "These paths and only these paths will be scanned.", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.paths:}")
    private String[] hubSignatureScannerPaths;

    @ValueDescription(description = "The relative paths of directories to be excluded from scan registration", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.relative.paths.to.exclude:}")
    private String[] hubSignatureScannerRelativePathsToExclude;

    @ValueDescription(description = "The memory for the scanner to use.", defaultValue = "4096", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.memory:}")
    private Integer hubSignatureScannerMemory;

    @ValueDescription(description = "Set to true to disable the Hub Signature Scanner.", defaultValue = "false", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.disabled:}")
    private Boolean hubSignatureScannerDisabled;

    @ValueDescription(description = "To use a local signature scanner, set its location with this property. This will be the path where the signature scanner was unzipped. This will likely look similar to /some/path/scan.cli-x.y.z", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.offline.local.path:}")
    private String hubSignatureScannerOfflineLocalPath;

    @ValueDescription(description = "If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.host.url:}")
    private String hubSignatureScannerHostUrl;

    @ValueDescription(description = "The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.", defaultValue = "1", group = DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value("${detect.hub.signature.scanner.parallel.processors:}")
    private Integer hubSignatureScannerParallelProcessors;

    @ValueDescription(description = "Set this value to false if you would like to exclude your dev requires dependencies when ran", defaultValue = "true", group = DetectConfiguration.GROUP_PACKAGIST)
    @Value("${detect.packagist.include.dev.dependencies:}")
    private Boolean packagistIncludeDevDependencies;

    @ValueDescription(description = "The path of the perl executable", group = DetectConfiguration.GROUP_CPAN)
    @Value("${detect.perl.path:}")
    private String perlPath;

    @ValueDescription(description = "The path of the cpan executable", group = DetectConfiguration.GROUP_CPAN)
    @Value("${detect.cpan.path:}")
    private String cpanPath;

    @ValueDescription(description = "The path of the cpanm executable", group = DetectConfiguration.GROUP_CPAN)
    @Value("${detect.cpanm.path:}")
    private String cpanmPath;

    @ValueDescription(description = "The names of the sbt configurations to exclude", group = DetectConfiguration.GROUP_SBT)
    @Value("${detect.sbt.excluded.configurations:}")
    private String sbtExcludedConfigurationNames;

    @ValueDescription(description = "The names of the sbt configurations to include", group = DetectConfiguration.GROUP_SBT)
    @Value("${detect.sbt.included.configurations:}")
    private String sbtIncludedConfigurationNames;

    @ValueDescription(description = "The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'", defaultValue = "text", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.default.project.version.scheme:}")
    private String defaultProjectVersionScheme;

    @ValueDescription(description = "The text to use as the default project version", defaultValue = "Detect Unknown Version", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.default.project.version.text:}")
    private String defaultProjectVersionText;

    @ValueDescription(description = "The timestamp format to use as the default project version", defaultValue = "yyyy-MM-dd\'T\'HH:mm:ss.SSS", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.default.project.version.timeformat:}")
    private String defaultProjectVersionTimeformat;

    @ValueDescription(description = "If set, this will aggregate all the BOMs to create a single BDIO file with the name provided. For Co-Pilot use only", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.bom.aggregate.name:}")
    private String aggregateBomName;

    @ValueDescription(description = "When set to true, a Black Duck risk report in PDF form will be created", defaultValue = "false", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.risk.report.pdf:}")
    private Boolean riskReportPdf;

    @ValueDescription(description = "The output directory for risk report in PDF. Default is the source directory", defaultValue = ".", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.risk.report.pdf.path:}")
    private String riskReportPdfOutputDirectory;

    @ValueDescription(description = "When set to true, a Black Duck notices report in text form will be created in your source directory", defaultValue = "false", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.notices.report:}")
    private Boolean noticesReport;

    @ValueDescription(description = "The output directory for notices report. Default is the source directory", defaultValue = ".", group = DetectConfiguration.GROUP_PROJECT_INFO)
    @Value("${detect.notices.report.path:}")
    private String noticesReportOutputDirectory;

    @ValueDescription(description = "The path of the conda executable", group = DetectConfiguration.GROUP_CONDA)
    @Value("${detect.conda.path:}")
    private String condaPath;

    @ValueDescription(description = "The name of the anaconda environment used by your project", group = DetectConfiguration.GROUP_CONDA)
    @Value("${detect.conda.environment.name:}")
    private String condaEnvironmentName;

    @ValueDescription(description = "The path to the directory containing the docker inspector script, jar, and images", group = DetectConfiguration.GROUP_DOCKER)
    @Value("${detect.docker.inspector.air.gap.path:}")
    private String dockerInspectorAirGapPath;

    @ValueDescription(description = "The path to the directory containing the air gap dependencies for the gradle inspector", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.inspector.air.gap.path:}")
    private String gradleInspectorAirGapPath;

    @ValueDescription(description = "The path to the directory containing the nuget inspector nupkg", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.inspector.air.gap.path:}")
    private String nugetInspectorAirGapPath;

    @ValueDescription(description = "The source for nuget packages", defaultValue = "https://www.nuget.org/api/v2/", group = DetectConfiguration.GROUP_NUGET)
    @Value("${detect.nuget.packages.repo.url:}")
    private String[] nugetPackagesRepoUrl;

    @ValueDescription(description = "The respository gradle should use to look for the gradle inspector", group = DetectConfiguration.GROUP_GRADLE)
    @Value("${detect.gradle.inspector.repository.url:}")
    private String gradleInspectorRepositoryUrl;

    @ValueDescription(description = "The path of the rebar3 executable", group = DetectConfiguration.GROUP_HEX)
    @Value("${detect.hex.rebar3.path:}")
    private String hexRebar3Path;

    public boolean getCleanupBdioFiles() {
        return BooleanUtils.toBoolean(cleanupBdioFiles);
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

    public boolean getProjectCodeLocationDeleteOldNames() {
        return BooleanUtils.toBoolean(projectCodeLocationDeleteOldNames);
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

    // properties end

}
