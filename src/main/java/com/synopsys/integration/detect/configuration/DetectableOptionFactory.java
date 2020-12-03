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
package com.synopsys.integration.detect.configuration;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.ConanCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class DetectableOptionFactory {

    private final PropertyConfiguration detectConfiguration;
    @Nullable
    private final DiagnosticSystem diagnosticSystem;
    private final PathResolver pathResolver;
    private final ProxyInfo proxyInfo;

    private final Logger logger = LoggerFactory.getLogger(DetectableOptionFactory.class);

    public DetectableOptionFactory(PropertyConfiguration detectConfiguration, @Nullable DiagnosticSystem diagnosticSystem, PathResolver pathResolver, ProxyInfo proxyInfo) {
        this.detectConfiguration = detectConfiguration;
        this.diagnosticSystem = diagnosticSystem;
        this.pathResolver = pathResolver;
        this.proxyInfo = proxyInfo;
    }

    public BazelDetectableOptions createBazelDetectableOptions() {
        String targetName = getNullableValue(DetectProperties.DETECT_BAZEL_TARGET);
        List<String> bazelCqueryAdditionalOptions = getValue(DetectProperties.DETECT_BAZEL_CQUERY_OPTIONS);

        List<FilterableEnumValue<WorkspaceRule>> bazelDependencyRulesPropertyValues = getValue(DetectProperties.DETECT_BAZEL_DEPENDENCY_RULE);
        Set<WorkspaceRule> bazelDependencyRules = deriveBazelDependencyRules(bazelDependencyRulesPropertyValues);
        return new BazelDetectableOptions(targetName, bazelDependencyRules, bazelCqueryAdditionalOptions);
    }

    public BitbakeDetectableOptions createBitbakeDetectableOptions() {
        String buildEnvName = getValue(DetectProperties.DETECT_BITBAKE_BUILD_ENV_NAME);
        List<String> sourceArguments = getValue(DetectProperties.DETECT_BITBAKE_SOURCE_ARGUMENTS);
        List<String> packageNames = getValue(DetectProperties.DETECT_BITBAKE_PACKAGE_NAMES);
        Integer searchDepth = getValue(DetectProperties.DETECT_BITBAKE_SEARCH_DEPTH);
        return new BitbakeDetectableOptions(buildEnvName, sourceArguments, packageNames, searchDepth);
    }

    public ClangDetectableOptions createClangDetectableOptions() {
        Boolean cleanup = getValue(DetectProperties.DETECT_CLEANUP);
        return new ClangDetectableOptions(cleanup);
    }

    public ComposerLockDetectableOptions createComposerLockDetectableOptions() {
        Boolean includedDevDependencies = getValue(DetectProperties.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES);
        return new ComposerLockDetectableOptions(includedDevDependencies);
    }

    public CondaCliDetectableOptions createCondaOptions() {
        String environmentName = getNullableValue(DetectProperties.DETECT_CONDA_ENVIRONMENT_NAME);
        return new CondaCliDetectableOptions(environmentName);
    }

    public MavenParseOptions createMavenParseOptions() {
        Boolean includePlugins = getValue(DetectProperties.DETECT_MAVEN_INCLUDE_PLUGINS);
        return new MavenParseOptions(includePlugins);
    }

    public DockerDetectableOptions createDockerDetectableOptions() {
        Boolean dockerPathRequired = getValue(DetectProperties.DETECT_DOCKER_PATH_REQUIRED);
        String suppliedDockerImage = getNullableValue(DetectProperties.DETECT_DOCKER_IMAGE);
        String dockerImageId = getNullableValue(DetectProperties.DETECT_DOCKER_IMAGE_ID);
        String suppliedDockerTar = getNullableValue(DetectProperties.DETECT_DOCKER_TAR);
        LogLevel dockerInspectorLoggingLevel;
        if (detectConfiguration.wasKeyProvided(DetectProperties.LOGGING_LEVEL_DETECT.getProperty().getKey())) {
            dockerInspectorLoggingLevel = getValue(DetectProperties.LOGGING_LEVEL_DETECT);
        } else {
            dockerInspectorLoggingLevel = getValue(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION);
        }
        String dockerInspectorVersion = getNullableValue(DetectProperties.DETECT_DOCKER_INSPECTOR_VERSION);
        Map<String, String> additionalDockerProperties = detectConfiguration.getRaw(DetectProperties.DOCKER_PASSTHROUGH.getProperty());
        if (diagnosticSystem != null) {
            additionalDockerProperties.putAll(diagnosticSystem.getAdditionalDockerProperties());
        }

        Path dockerInspectorPath = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        String dockerPlatformTopLayerId = getNullableValue(DetectProperties.DETECT_DOCKER_PLATFORM_TOP_LAYER_ID);
        return new DockerDetectableOptions(dockerPathRequired, suppliedDockerImage, dockerImageId, suppliedDockerTar, dockerInspectorLoggingLevel, dockerInspectorVersion, additionalDockerProperties, dockerInspectorPath,
            dockerPlatformTopLayerId);
    }

    public GradleInspectorOptions createGradleInspectorOptions() {
        List<String> excludedProjectNames = getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECTS);
        List<String> includedProjectNames = getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECTS);
        List<String> excludedConfigurationNames = getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS);
        List<String> includedConfigurationNames = getValue(DetectProperties.DETECT_GRADLE_INCLUDED_CONFIGURATIONS);
        String configuredGradleInspectorRepositoryUrl = getNullableValue(DetectProperties.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;
        if (configuredGradleInspectorRepositoryUrl != null && StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            logger.warn("Using a custom gradle repository will not be supported in the future.");
            customRepository = configuredGradleInspectorRepositoryUrl;
        }

        String onlineInspectorVersion = getNullableValue(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION);
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, customRepository, onlineInspectorVersion);
        String gradleBuildCommand = getNullableValue(DetectProperties.DETECT_GRADLE_BUILD_COMMAND);
        return new GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo);
    }

    public LernaOptions createLernaOptions() {
        Boolean includePrivate = getValue(DetectProperties.DETECT_LERNA_INCLUDE_PRIVATE);
        return new LernaOptions(includePrivate);
    }

    public MavenCliExtractorOptions createMavenCliOptions() {
        String mavenBuildCommand = getNullableValue(DetectProperties.DETECT_MAVEN_BUILD_COMMAND);
        List<String> mavenExcludedScopes = getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_SCOPES);
        List<String> mavenIncludedScopes = getValue(DetectProperties.DETECT_MAVEN_INCLUDED_SCOPES);
        List<String> mavenExcludedModules = getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_MODULES);
        List<String> mavenIncludedModules = getValue(DetectProperties.DETECT_MAVEN_INCLUDED_MODULES);
        return new MavenCliExtractorOptions(mavenBuildCommand, mavenExcludedScopes, mavenIncludedScopes, mavenExcludedModules, mavenIncludedModules);
    }

    public ConanCliExtractorOptions createConanCliOptions() {
        String additionalArguments = getNullableValue(DetectProperties.DETECT_CONAN_ARGUMENTS);
        Boolean includeBuildDependencies = getValue(DetectProperties.DETECT_CONAN_INCLUDE_BUILD_DEPENDENCIES);
        return new ConanCliExtractorOptions(additionalArguments, includeBuildDependencies);
    }

    public NpmCliExtractorOptions createNpmCliExtractorOptions() {
        Boolean includeDevDependencies = getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
        String npmArguments = getNullableValue(DetectProperties.DETECT_NPM_ARGUMENTS);
        return new NpmCliExtractorOptions(includeDevDependencies, npmArguments);
    }

    public NpmLockfileOptions createNpmLockfileOptions() {
        Boolean includeDevDependencies = getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
        return new NpmLockfileOptions(includeDevDependencies);
    }

    public NpmPackageJsonParseDetectableOptions createNpmPackageJsonParseDetectableOptions() {
        Boolean includeDevDependencies = getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
        return new NpmPackageJsonParseDetectableOptions(includeDevDependencies);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() {
        Boolean onlyGatherRequired = getValue(DetectProperties.DETECT_PEAR_ONLY_REQUIRED_DEPS);
        return new PearCliDetectableOptions(onlyGatherRequired);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() {
        String pipProjectName = getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        String pipProjectVersionName = getNullableValue(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME);
        Boolean pipProjectTreeOnly = getValue(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE);
        return new PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly);
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions() {
        String pipProjectName = getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        List<Path> requirementsFilePath = getValue(DetectProperties.DETECT_PIP_REQUIREMENTS_PATH).stream()
                                              .map(it -> it.resolvePath(pathResolver))
                                              .collect(Collectors.toList());
        return new PipInspectorDetectableOptions(pipProjectName, requirementsFilePath);
    }

    public GemspecParseDetectableOptions createGemspecParseDetectableOptions() {
        Boolean includeRuntimeDependencies = getValue(DetectProperties.DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES);
        Boolean includeDevDeopendencies = getValue(DetectProperties.DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES);
        return new GemspecParseDetectableOptions(includeRuntimeDependencies, includeDevDeopendencies);
    }

    public SbtResolutionCacheDetectableOptions createSbtResolutionCacheDetectableOptions() {
        List<String> includedConfigurations = getValue(DetectProperties.DETECT_SBT_INCLUDED_CONFIGURATIONS);
        List<String> excludedConfigurations = getValue(DetectProperties.DETECT_SBT_EXCLUDED_CONFIGURATIONS);
        Integer reportDepth = getValue(DetectProperties.DETECT_SBT_REPORT_DEPTH);
        return new SbtResolutionCacheDetectableOptions(includedConfigurations, excludedConfigurations, reportDepth);
    }

    public YarnLockOptions createYarnLockOptions() {
        Boolean useProductionOnly = getValue(DetectProperties.DETECT_YARN_PROD_ONLY);
        return new YarnLockOptions(useProductionOnly);
    }

    public NugetInspectorOptions createNugetInspectorOptions() {
        Boolean ignoreFailures = getValue(DetectProperties.DETECT_NUGET_IGNORE_FAILURE);
        List<String> excludedModules = getValue(DetectProperties.DETECT_NUGET_EXCLUDED_MODULES);
        List<String> includedModules = getValue(DetectProperties.DETECT_NUGET_INCLUDED_MODULES);
        List<String> packagesRepoUrl = getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL);
        Path nugetConfigPath = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_CONFIG_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        return new NugetInspectorOptions(ignoreFailures, excludedModules, includedModules, packagesRepoUrl, nugetConfigPath);
    }

    public NugetLocatorOptions createNugetInstallerOptions() {
        List<String> packagesRepoUrl = getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL);
        String nugetInspectorName = getValue(DetectProperties.DETECT_NUGET_INSPECTOR_NAME);
        String nugetInspectorVersion = getNullableValue(DetectProperties.DETECT_NUGET_INSPECTOR_VERSION);
        return new NugetLocatorOptions(packagesRepoUrl, nugetInspectorName, nugetInspectorVersion);
    }

    private Set<WorkspaceRule> deriveBazelDependencyRules(List<FilterableEnumValue<WorkspaceRule>> bazelDependencyRulesPropertyValues) {
        Set<WorkspaceRule> bazelDependencyRules = new HashSet<>();
        if (noneSpecified(bazelDependencyRulesPropertyValues)) {
            // Leave bazelDependencyRules empty
        } else if (allSpecified(bazelDependencyRulesPropertyValues)) {
            bazelDependencyRules.addAll(Arrays.asList(WorkspaceRule.values()));
        } else {
            bazelDependencyRules.addAll(FilterableEnumUtils.toPresentValues(bazelDependencyRulesPropertyValues));
        }
        return bazelDependencyRules;
    }

    private boolean noneSpecified(List<FilterableEnumValue<WorkspaceRule>> rulesPropertyValues) {
        boolean noneWasSpecified = false;
        if (rulesPropertyValues == null ||
                FilterableEnumUtils.containsNone(rulesPropertyValues) ||
                (FilterableEnumUtils.toPresentValues(rulesPropertyValues).isEmpty() && !FilterableEnumUtils.containsAll(rulesPropertyValues))) {
            noneWasSpecified = true;
        }
        return noneWasSpecified;
    }

    private boolean allSpecified(List<FilterableEnumValue<WorkspaceRule>> userProvidedRules) {
        boolean allWasSpecified = false;
        if (userProvidedRules != null && FilterableEnumUtils.containsAll(userProvidedRules)) {
            allWasSpecified = true;
        }
        return allWasSpecified;
    }

    private <P, T extends NullableProperty<P>> P getNullableValue(DetectProperty<T> detectProperty) {
        return detectConfiguration.getValue(detectProperty.getProperty()).orElse(null);
    }

    private <P, T extends ValuedProperty<P>> P getValue(DetectProperty<T> detectProperty) {
        return detectConfiguration.getValue(detectProperty.getProperty());
    }
}
