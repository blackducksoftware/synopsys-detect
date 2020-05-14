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
package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.types.path.PathResolver
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions
import com.synopsys.integration.detect.workflow.ArtifactoryConstants
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions
import com.synopsys.integration.rest.proxy.ProxyInfo
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.util.*

class DetectableOptionFactory(private val detectConfiguration: PropertyConfiguration, private val diagnosticSystemOptional: Optional<DiagnosticSystem>, private val pathResolver: PathResolver, private val proxyInfo: ProxyInfo) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createBazelDetectableOptions(): BazelDetectableOptions {
        val targetName = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_TARGET).orElse(null)
        val bazelCqueryAdditionalOptions = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_CQUERY_OPTIONS)

        val bazelDependencyRule = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_DEPENDENCY_RULE)
        return BazelDetectableOptions(targetName, bazelDependencyRule, bazelCqueryAdditionalOptions)
    }

    fun createBitbakeDetectableOptions(): BitbakeDetectableOptions {
        val buildEnvName = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_BUILD_ENV_NAME)
        val sourceArguments = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SOURCE_ARGUMENTS)
        val packageNames = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_PACKAGE_NAMES)
        val searchDepth = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SEARCH_DEPTH)
        return BitbakeDetectableOptions(buildEnvName, sourceArguments, packageNames, searchDepth)
    }

    fun createClangDetectableOptions(): ClangDetectableOptions {
        val cleanup = detectConfiguration.getValue(DetectProperties.DETECT_CLEANUP)
        return ClangDetectableOptions(cleanup)
    }

    fun createComposerLockDetectableOptions(): ComposerLockDetectableOptions {
        val includedDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES)
        return ComposerLockDetectableOptions(includedDevDependencies)
    }

    fun createCondaOptions(): CondaCliDetectableOptions {
        val environmentName = detectConfiguration.getValue(DetectProperties.DETECT_CONDA_ENVIRONMENT_NAME).orElse(null)
        return CondaCliDetectableOptions(environmentName)
    }

    fun createMavenParseOptions(): MavenParseOptions {
        val includePlugins = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDE_PLUGINS)
        return MavenParseOptions(includePlugins)
    }

    fun createDockerDetectableOptions(): DockerDetectableOptions {
        val dockerPathRequired = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PATH_REQUIRED)
        val suppliedDockerImage = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_IMAGE).orElse(null)
        val dockerImageId = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_IMAGE_ID).orElse(null)
        val suppliedDockerTar = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_TAR).orElse(null)
        val dockerInspectorLoggingLevel = detectConfiguration.getValue(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION)
        val dockerInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_VERSION).orElse(null)
        val additionalDockerProperties = detectConfiguration.getRaw(DetectProperties.DOCKER_PASSTHROUGH).toMutableMap()
        diagnosticSystemOptional.ifPresent { diagnosticSystem -> additionalDockerProperties.putAll(diagnosticSystem.additionalDockerProperties) }
        val dockerInspectorPath = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val dockerPlatformTopLayerId = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PLATFORM_TOP_LAYER_ID).orElse(null)
        return DockerDetectableOptions(dockerPathRequired, suppliedDockerImage, dockerImageId, suppliedDockerTar, dockerInspectorLoggingLevel, dockerInspectorVersion, additionalDockerProperties, dockerInspectorPath, dockerPlatformTopLayerId)
    }

    fun createGradleInspectorOptions(): GradleInspectorOptions {
        val excludedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECTS).orElse(null)
        val includedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECTS).orElse(null)
        val excludedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS).orElse(null)
        val includedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_CONFIGURATIONS).orElse(null)
        val configuredGradleInspectorRepositoryUrl = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL).orElse(null)
        var customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO
        if (configuredGradleInspectorRepositoryUrl != null && StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            logger.warn("Using a custom gradle repository will not be supported in the future.")
            customRepository = configuredGradleInspectorRepositoryUrl
        }

        val onlineInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION).orElse(null)
        val scriptOptions = GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, customRepository, onlineInspectorVersion)
        val gradleBuildCommand = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_BUILD_COMMAND).orElse(null)
        return GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo)
    }

    fun createMavenCliOptions(): MavenCliExtractorOptions {
        val mavenBuildCommand = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_BUILD_COMMAND).orElse(null)
        val mavenExcludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_SCOPES).orElse(null)
        val mavenIncludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_SCOPES).orElse(null)
        val mavenExcludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_MODULES).orElse(null)
        val mavenIncludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_MODULES).orElse(null)
        return MavenCliExtractorOptions(mavenBuildCommand, mavenExcludedScopes, mavenIncludedScopes, mavenExcludedModules, mavenIncludedModules)
    }

    fun createNpmCliExtractorOptions(): NpmCliExtractorOptions {
        val includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES)
        val npmArguments = detectConfiguration.getValue(DetectProperties.DETECT_NPM_ARGUMENTS).orElse(null)
        return NpmCliExtractorOptions(includeDevDependencies, npmArguments)
    }

    fun createNpmLockfileOptions(): NpmLockfileOptions {
        val includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES)
        return NpmLockfileOptions(includeDevDependencies)
    }

    fun createNpmPackageJsonParseDetectableOptions(): NpmPackageJsonParseDetectableOptions {
        val includeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES)
        return NpmPackageJsonParseDetectableOptions(includeDevDependencies)
    }

    fun createPearCliDetectableOptions(): PearCliDetectableOptions {
        val onlyGatherRequired = detectConfiguration.getValue(DetectProperties.DETECT_PEAR_ONLY_REQUIRED_DEPS)
        return PearCliDetectableOptions(onlyGatherRequired)
    }

    fun createPipenvDetectableOptions(): PipenvDetectableOptions {
        val pipProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_NAME).orElse(null)
        val pipProjectVersionName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME).orElse(null)
        val pipProjectTreeOnly = detectConfiguration.getValue(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE)
        return PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly)
    }

    fun createPipInspectorDetectableOptions(): PipInspectorDetectableOptions {
        val pipProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PIP_PROJECT_NAME).orElse(null)
        val requirementsFilePath = detectConfiguration.getValue(DetectProperties.DETECT_PIP_REQUIREMENTS_PATH).map { it.resolvePath(pathResolver) }
        return PipInspectorDetectableOptions(pipProjectName, requirementsFilePath)
    }

    fun createGemspecParseDetectableOptions(): GemspecParseDetectableOptions {
        val includeRuntimeDependencies = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES)
        val includeDevDeopendencies = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES)
        return GemspecParseDetectableOptions(includeRuntimeDependencies, includeDevDeopendencies)
    }

    fun createSbtResolutionCacheDetectableOptions(): SbtResolutionCacheDetectableOptions {
        val includedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_INCLUDED_CONFIGURATIONS).orElse(null)
        val excludedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_EXCLUDED_CONFIGURATIONS).orElse(null)
        val reportDepth = detectConfiguration.getValue(DetectProperties.DETECT_SBT_REPORT_DEPTH)
        return SbtResolutionCacheDetectableOptions(includedConfigurations, excludedConfigurations, reportDepth)
    }

    fun createYarnLockOptions(): YarnLockOptions {
        val useProductionOnly = detectConfiguration.getValue(DetectProperties.DETECT_YARN_PROD_ONLY)
        return YarnLockOptions(useProductionOnly)
    }

    fun createNugetInspectorOptions(): NugetInspectorOptions {
        val ignoreFailures = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_IGNORE_FAILURE)
        val excludedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_EXCLUDED_MODULES).orElse(null)
        val includedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INCLUDED_MODULES).orElse(null)
        val packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL)
        val nugetConfigPath = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_CONFIG_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        return NugetInspectorOptions(ignoreFailures, excludedModules, includedModules, packagesRepoUrl, nugetConfigPath)
    }

    fun createNugetInstallerOptions(): NugetLocatorOptions {
        val packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL)
        val nugetInspectorName = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_NAME)
        val nugetInspectorVersion = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_VERSION).orElse(null)
        return NugetLocatorOptions(packagesRepoUrl, nugetInspectorName, nugetInspectorVersion)
    }

    fun createCachedExecutableResolverOptions(): CachedExecutableResolverOptions {
        val python3 = detectConfiguration.getValue(DetectProperties.DETECT_PYTHON_PYTHON3)
        return CachedExecutableResolverOptions(python3)
    }
}
