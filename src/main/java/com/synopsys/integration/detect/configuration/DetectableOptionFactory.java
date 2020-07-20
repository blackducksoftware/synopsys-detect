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
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
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
        String targetName = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_TARGET.getProperty()).orElse(null);
        List<String> bazelCqueryAdditionalOptions = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_CQUERY_OPTIONS.getProperty());

        List<FilterableEnumValue<WorkspaceRule>> bazelDependencyRulesPropertyValues = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_DEPENDENCY_RULE.getProperty());
        Set<WorkspaceRule> bazelDependencyRules = deriveBazelDependencyRules(bazelDependencyRulesPropertyValues);
        return new BazelDetectableOptions(targetName, bazelDependencyRules, bazelCqueryAdditionalOptions);
    }

    public BitbakeDetectableOptions createBitbakeDetectableOptions() {
        String buildEnvName = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_BUILD_ENV_NAME.getProperty());
        List<String> sourceArguments = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SOURCE_ARGUMENTS.getProperty());
        List<String> packageNames = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_PACKAGE_NAMES.getProperty());
        Integer searchDepth = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SEARCH_DEPTH.getProperty());
        return new BitbakeDetectableOptions(buildEnvName, sourceArguments, packageNames, searchDepth);
    }

    public ClangDetectableOptions createClangDetectableOptions() {
        Boolean cleanup = detectConfiguration.getValue(DetectProperties.DETECT_CLEANUP.getProperty());
        return new ClangDetectableOptions(cleanup);
    }

    public ComposerLockDetectableOptions createComposerLockDetectableOptions() {
        Boolean includedDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES.getProperty());
        return new ComposerLockDetectableOptions(includedDevDependencies);
    }

    public CondaCliDetectableOptions createCondaOptions() {
        String environmentName = detectConfiguration.getValue(DetectProperties.DETECT_CONDA_ENVIRONMENT_NAME.getProperty()).orElse(null);
        return new CondaCliDetectableOptions(environmentName);
    }

    public MavenParseOptions createMavenParseOptions() {
        Boolean includePlugins = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDE_PLUGINS.getProperty());
        return new MavenParseOptions(includePlugins);
    }

    public DockerDetectableOptions createDockerDetectableOptions() {
        Boolean dockerPathRequired = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PATH_REQUIRED.getProperty());
        String suppliedDockerImage = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_IMAGE.getProperty()).orElse(null);
        String dockerImageId = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_IMAGE_ID.getProperty()).orElse(null);
        String suppliedDockerTar = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_TAR.getProperty()).orElse(null);
        LogLevel dockerInspectorLoggingLevel = detectConfiguration.getValue(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION.getProperty());
        String dockerInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_VERSION.getProperty()).orElse(null);
        Map<String, String> additionalDockerProperties = detectConfiguration.getRaw(DetectProperties.DOCKER_PASSTHROUGH.getProperty());
        if (diagnosticSystem != null) {
            additionalDockerProperties.putAll(diagnosticSystem.getAdditionalDockerProperties());
        }

        Path dockerInspectorPath = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        String dockerPlatformTopLayerId = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PLATFORM_TOP_LAYER_ID.getProperty()).orElse(null);
        return new DockerDetectableOptions(dockerPathRequired, suppliedDockerImage, dockerImageId, suppliedDockerTar, dockerInspectorLoggingLevel, dockerInspectorVersion, additionalDockerProperties, dockerInspectorPath,
            dockerPlatformTopLayerId);
    }

    public GradleInspectorOptions createGradleInspectorOptions() {
        String excludedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECTS.getProperty()).orElse(null);
        String includedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECTS.getProperty()).orElse(null);
        String excludedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS.getProperty()).orElse(null);
        String includedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_CONFIGURATIONS.getProperty()).orElse(null);
        String configuredGradleInspectorRepositoryUrl = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL.getProperty()).orElse(null);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;
        if (configuredGradleInspectorRepositoryUrl != null && StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            logger.warn("Using a custom gradle repository will not be supported in the future.");
            customRepository = configuredGradleInspectorRepositoryUrl;
        }

        String onlineInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty()).orElse(null);
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, customRepository, onlineInspectorVersion);
        String gradleBuildCommand = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_BUILD_COMMAND.getProperty()).orElse(null);
        return new GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo);
    }

    public LernaOptions createLernaOptions() {
        Boolean includePrivate = detectConfiguration.getValue(DetectProperties.DETECT_LERNA_INCLUDE_PRIVATE.getProperty());
        return new LernaOptions(includePrivate);
    }

    public MavenCliExtractorOptions createMavenCliOptions() {
        String mavenBuildCommand = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_BUILD_COMMAND.getProperty()).orElse(null);
        String mavenExcludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_SCOPES.getProperty()).orElse(null);
        String mavenIncludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_SCOPES.getProperty()).orElse(null);
        String mavenExcludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_MODULES.getProperty()).orElse(null);
        String mavenIncludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_MODULES.getProperty()).orElse(null);
        return new MavenCliExtractorOptions(mavenBuildCommand, mavenExcludedScopes, mavenIncludedScopes, mavenExcludedModules, mavenIncludedModules);
    }

    public NpmCliExtractorOptions createNpmCliExtractorOptions() {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES.getProperty());
        String npmArguments = detectConfiguration.getValue(DetectProperties.DETECT_NPM_ARGUMENTS.getProperty()).orElse(null);
        return new NpmCliExtractorOptions(includeDevDependencies, npmArguments);
    }

    public NpmLockfileOptions createNpmLockfileOptions() {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES.getProperty());
        return new NpmLockfileOptions(includeDevDependencies);
    }

    public NpmPackageJsonParseDetectableOptions createNpmPackageJsonParseDetectableOptions() {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES.getProperty());
        return new NpmPackageJsonParseDetectableOptions(includeDevDependencies);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() {
        Boolean onlyGatherRequired = detectConfiguration.getValue(DetectProperties.DETECT_PEAR_ONLY_REQUIRED_DEPS.getProperty());
        return new PearCliDetectableOptions(onlyGatherRequired);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() {
        String pipProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_NAME.getProperty()).orElse(null);
        String pipProjectVersionName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME.getProperty()).orElse(null);
        Boolean pipProjectTreeOnly = detectConfiguration.getValue(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE.getProperty());
        return new PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly);
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions() {
        String pipProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_NAME.getProperty()).orElse(null);
        List<Path> requirementsFilePath = detectConfiguration.getValue(DetectProperties.DETECT_PIP_REQUIREMENTS_PATH.getProperty()).stream()
                                              .map(it -> it.resolvePath(pathResolver))
                                              .collect(Collectors.toList());
        return new PipInspectorDetectableOptions(pipProjectName, requirementsFilePath);
    }

    public GemspecParseDetectableOptions createGemspecParseDetectableOptions() {
        Boolean includeRuntimeDependencies = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES.getProperty());
        Boolean includeDevDeopendencies = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES.getProperty());
        return new GemspecParseDetectableOptions(includeRuntimeDependencies, includeDevDeopendencies);
    }

    public SbtResolutionCacheDetectableOptions createSbtResolutionCacheDetectableOptions() {
        String includedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_INCLUDED_CONFIGURATIONS.getProperty()).orElse(null);
        String excludedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_EXCLUDED_CONFIGURATIONS.getProperty()).orElse(null);
        Integer reportDepth = detectConfiguration.getValue(DetectProperties.DETECT_SBT_REPORT_DEPTH.getProperty());
        return new SbtResolutionCacheDetectableOptions(includedConfigurations, excludedConfigurations, reportDepth);
    }

    public YarnLockOptions createYarnLockOptions() {
        Boolean useProductionOnly = detectConfiguration.getValue(DetectProperties.DETECT_YARN_PROD_ONLY.getProperty());
        return new YarnLockOptions(useProductionOnly);
    }

    public NugetInspectorOptions createNugetInspectorOptions() {
        Boolean ignoreFailures = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_IGNORE_FAILURE.getProperty());
        String excludedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_EXCLUDED_MODULES.getProperty()).orElse(null);
        String includedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INCLUDED_MODULES.getProperty()).orElse(null);
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL.getProperty());
        Path nugetConfigPath = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_CONFIG_PATH.getProperty()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        return new NugetInspectorOptions(ignoreFailures, excludedModules, includedModules, packagesRepoUrl, nugetConfigPath);
    }

    public NugetLocatorOptions createNugetInstallerOptions() {
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL.getProperty());
        String nugetInspectorName = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_NAME.getProperty());
        String nugetInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_VERSION.getProperty()).orElse(null);
        return new NugetLocatorOptions(packagesRepoUrl, nugetInspectorName, nugetInspectorVersion);
    }

    public CachedExecutableResolverOptions createCachedExecutableResolverOptions() {
        Boolean python3 = detectConfiguration.getValue(DetectProperties.DETECT_PYTHON_PYTHON3.getProperty());
        return new CachedExecutableResolverOptions(python3);
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
}
