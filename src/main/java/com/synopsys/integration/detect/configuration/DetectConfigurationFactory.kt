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

import com.synopsys.integration.blackduck.api.generated.enumeration.PolicyRuleSeverityType
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder
import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils
import com.synopsys.integration.configuration.property.types.path.PathResolver
import com.synopsys.integration.detect.PropertyConfigUtils
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.exitcode.ExitCodeType
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions
import com.synopsys.integration.detect.lifecycle.run.RunOptions
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectorFileFilter
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableOptions
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions
import com.synopsys.integration.detect.util.filter.DetectToolFilter
import com.synopsys.integration.detect.workflow.airgap.AirGapOptions
import com.synopsys.integration.detect.workflow.bdio.BdioOptions
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions
import com.synopsys.integration.detect.workflow.file.DirectoryOptions
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeOptions
import com.synopsys.integration.detect.workflow.project.ProjectNameVersionOptions
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions
import com.synopsys.integration.detector.finder.DetectorFinderOptions
import com.synopsys.integration.log.SilentIntLogger
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder
import com.synopsys.integration.rest.credentials.Credentials
import com.synopsys.integration.rest.credentials.CredentialsBuilder
import com.synopsys.integration.rest.proxy.ProxyInfo
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.jetbrains.annotations.Nullable
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

open class DetectConfigurationFactory(private val detectConfiguration: PropertyConfiguration, private val pathResolver: PathResolver) {

    //#region Prefer These Over Any Property
    fun findTimeoutInSeconds(): Long {
        return if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_API_TIMEOUT)) {
            val timeout = detectConfiguration.getValue(DetectProperties.DETECT_API_TIMEOUT)
            timeout / 1000
        } else {
            detectConfiguration.getValue(DetectProperties.DETECT_REPORT_TIMEOUT)
        }
    }

    fun findParallelProcessors(): Int {
        val provided = when {
            detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PARALLEL_PROCESSORS) -> detectConfiguration.getValue(DetectProperties.DETECT_PARALLEL_PROCESSORS)
            detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS) -> detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)
            detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS) -> detectConfiguration.getValue(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)
            else -> null
        }

        return if (provided != null && provided > 0) {
            provided
        } else {
            return findRuntimeProcessors()
        }
    }

    open fun findRuntimeProcessors(): Int {
        return Runtime.getRuntime().availableProcessors()
    }

    @Nullable
    fun findSnippetMatching(): SnippetMatching? {
        val snippetMatching = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING)

        val deprecatedSnippetMatching = when (detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE)) {
            true -> SnippetMatching.SNIPPET_MATCHING
            false -> null
        }

        if (snippetMatching.baseValue.isPresent) {
            return snippetMatching.baseValue.get()
        }

        if (snippetMatching.extendedValue.isPresent) {
            return deprecatedSnippetMatching
        }

        return null
    }

    //#endregion

    //#region Creating Connections
    @Throws(DetectUserFriendlyException::class)
    fun createBlackDuckProxyInfo(): ProxyInfo {
        val proxyUsername = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_USERNAME, DetectProperties.BLACKDUCK_HUB_PROXY_USERNAME).orElse(null)
        val proxyPassword = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_PASSWORD, DetectProperties.BLACKDUCK_HUB_PROXY_PASSWORD).orElse(null)
        val proxyHost = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_HOST, DetectProperties.BLACKDUCK_HUB_PROXY_HOST).orElse(null)
        val proxyPort = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_PORT, DetectProperties.BLACKDUCK_HUB_PROXY_PORT).orElse(null)
        val proxyNtlmDomain = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN, DetectProperties.BLACKDUCK_HUB_PROXY_NTLM_DOMAIN).orElse(null)
        val proxyNtlmWorkstation = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION, DetectProperties.BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION).orElse(null)

        val proxyCredentialsBuilder = CredentialsBuilder()
        proxyCredentialsBuilder.username = proxyUsername
        proxyCredentialsBuilder.password = proxyPassword
        val proxyCredentials: Credentials
        try {
            proxyCredentials = proxyCredentialsBuilder.build()
        } catch (e: IllegalArgumentException) {
            throw DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
        }

        val proxyInfoBuilder = ProxyInfoBuilder()

        proxyInfoBuilder.credentials = proxyCredentials
        proxyInfoBuilder.host = proxyHost
        proxyInfoBuilder.port = NumberUtils.toInt(proxyPort, 0)
        proxyInfoBuilder.ntlmDomain = proxyNtlmDomain
        proxyInfoBuilder.ntlmWorkstation = proxyNtlmWorkstation
        try {
            return proxyInfoBuilder.build()
        } catch (e: IllegalArgumentException) {
            throw DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
        }
    }

    fun createProductBootOptions(): ProductBootOptions {
        val ignoreFailures = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_IGNORE_CONNECTION_FAILURES)
        val testConnections = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_TEST_CONNECTION)
        return ProductBootOptions(ignoreFailures, testConnections);
    }

    fun createConnectionDetails(): ConnectionDetails {
        val alwaysTrust = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_TRUST_CERT, DetectProperties.BLACKDUCK_HUB_TRUST_CERT)
        val proxyIgnoredHosts = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_PROXY_IGNORED_HOSTS, DetectProperties.BLACKDUCK_HUB_PROXY_IGNORED_HOSTS)
        val proxyPatterns = proxyIgnoredHosts.map { Pattern.compile(it) }
        val proxyInformation = createBlackDuckProxyInfo()
        return ConnectionDetails(proxyInformation, proxyPatterns, findTimeoutInSeconds(), alwaysTrust)
    }

    open fun createBlackDuckConnectionDetails(): BlackDuckConnectionDetails {
        val offline = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.BLACKDUCK_OFFLINE_MODE, DetectProperties.BLACKDUCK_HUB_OFFLINE_MODE)
        val blackduckUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.BLACKDUCK_URL, DetectProperties.BLACKDUCK_HUB_URL).orElse(null)
        val allBlackDuckKeys: Set<String> = HashSet(BlackDuckServerConfigBuilder().propertyKeys)
                .filter { !it.toLowerCase().contains("proxy") }
                .toSet()
        val blackDuckProperties = detectConfiguration.getRaw(allBlackDuckKeys)

        return BlackDuckConnectionDetails(offline, blackduckUrl, blackDuckProperties, findParallelProcessors(), createConnectionDetails())
    }
    //#endregion

    open fun createPolarisServerConfigBuilder(userHome: File): PolarisServerConfigBuilder {
        val polarisServerConfigBuilder = PolarisServerConfig.newBuilder()
        val allPolarisKeys = polarisServerConfigBuilder.propertyKeys
        val polarisProperties = detectConfiguration.getRaw(allPolarisKeys)
        polarisServerConfigBuilder.logger = SilentIntLogger()

        polarisServerConfigBuilder.setProperties(polarisProperties.entries)
        polarisServerConfigBuilder.userHome = userHome.absolutePath
        polarisServerConfigBuilder.timeoutInSeconds = findTimeoutInSeconds().toInt()
        return polarisServerConfigBuilder
    }

    fun createPhoneHomeOptions(): PhoneHomeOptions {
        val phoneHomePassthrough = detectConfiguration.getRaw(DetectProperties.PHONEHOME_PASSTHROUGH)
        return PhoneHomeOptions(phoneHomePassthrough)
    }

    fun createRunOptions(): RunOptions {
        // This is because it is double deprecated so we must check if either property is set.
        val sigScanDisabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)
        val polarisEnabled = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_SWIP_ENABLED)

        val includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS)
        val excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED)
        val filter = ExcludeIncludeEnumFilter(excludedTools, includedTools);
        val detectToolFilter = DetectToolFilter(filter, sigScanDisabled, polarisEnabled)

        val unmapCodeLocations = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_UNMAP)
        val aggregateName = detectConfiguration.getValue(DetectProperties.DETECT_BOM_AGGREGATE_NAME).orElse(null)
        val aggregateMode = detectConfiguration.getValue(DetectProperties.DETECT_BOM_AGGREGATE_REMEDIATION_MODE)
        val preferredTools = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TOOL)
        val useBdio2 = detectConfiguration.getValue(DetectProperties.DETECT_BDIO2_ENABLED)

        return RunOptions(unmapCodeLocations, aggregateName, aggregateMode, preferredTools, detectToolFilter, useBdio2)
    }

    fun createDirectoryOptions(): DirectoryOptions {
        val sourcePath = detectConfiguration.getValue(DetectProperties.DETECT_SOURCE_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val outputPath = detectConfiguration.getValue(DetectProperties.DETECT_OUTPUT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val bdioPath = detectConfiguration.getValue(DetectProperties.DETECT_BDIO_OUTPUT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val scanPath = detectConfiguration.getValue(DetectProperties.DETECT_SCAN_OUTPUT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val toolsOutputPath = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_OUTPUT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)

        return DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath)
    }

    fun createAirGapOptions(): AirGapOptions {
        val gradleOverride = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val nugetOverride = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val dockerOverride = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)

        return AirGapOptions(dockerOverride, gradleOverride, nugetOverride)
    }

    fun createSearchOptions(sourcePath: Path): DetectorFinderOptions {
        //Normal settings
        val maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_DEPTH)

        //File Filter
        val excludedFiles = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_FILES).toMutableList()
        val excludedDirectories = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION)
        val excludedDirectoryPatterns = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS)
        val excludedDirectoryPaths = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS)

        if (detectConfiguration.getValueOrDefault(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS)) {
            val defaultExcluded = DetectorSearchExcludedDirectories.values()
                    .map { it.directoryName }
                    .toList()
            excludedFiles.addAll(defaultExcluded)
        }
        val fileFilter = DetectDetectorFileFilter(sourcePath, excludedDirectories, excludedDirectoryPaths, excludedDirectoryPatterns)

        return DetectorFinderOptions(fileFilter, maxDepth)
    }

    fun createDetectorEvaluationOptions(): DetectorEvaluationOptions {
        val forceNestedSearch = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_CONTINUE)

        //Detector Filter
        val excluded = detectConfiguration.getValue(DetectProperties.DETECT_EXCLUDED_DETECTOR_TYPES)
        val included = detectConfiguration.getValue(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES)
        val detectorFilter = ExcludeIncludeEnumFilter(excluded, included)

        return DetectorEvaluationOptions(forceNestedSearch) { rule -> detectorFilter.shouldInclude(rule.detectorType) }
    }

    fun createBdioOptions(): BdioOptions {
        val prefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX).orElse(null)
        val suffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX).orElse(null)
        return BdioOptions(prefix, suffix)
    }

    fun createProjectNameVersionOptions(sourceDirectoryName: String): ProjectNameVersionOptions {
        val overrideProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_NAME).orElse(null)
        val overrideProjectVersionName = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NAME).orElse(null)
        val defaultProjectVersionText = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TEXT)
        val defaultProjectVersionScheme = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_SCHEME)
        val defaultProjectVersionFormat = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT)
        return ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat)
    }

    @Throws(DetectUserFriendlyException::class)
    fun createDetectProjectServiceOptions(): DetectProjectServiceOptions {
        val projectVersionPhase = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_PHASE)
        val projectVersionDistribution = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_DISTRIBUTION)
        val projectTier = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TIER).orElse(null)
        val projectDescription = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_DESCRIPTION).orElse(null)
        val projectVersionNotes = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NOTES).orElse(null)
        val cloneCategories = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES)
        val projectLevelAdjustments = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_LEVEL_ADJUSTMENTS)
        val forceProjectVersionUpdate = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_UPDATE)
        val cloneVersionName = detectConfiguration.getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_NAME).orElse(null)
        val projectVersionNickname = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NICKNAME).orElse(null)
        val applicationId = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_APPLICATION_ID).orElse(null)
        val groups = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_USER_GROUPS)
        val tags = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TAGS)
        val parentProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PARENT_PROJECT_NAME).orElse(null)
        val parentProjectVersion = detectConfiguration.getValue(DetectProperties.DETECT_PARENT_PROJECT_VERSION_NAME).orElse(null)
        val cloneLatestProjectVersion = detectConfiguration.getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_LATEST)

        val parser = DetectCustomFieldParser()
        val customFieldDocument = parser.parseCustomFieldDocument(detectConfiguration.getRaw())

        return DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
                projectVersionNickname, applicationId, tags, groups, parentProjectName, parentProjectVersion, cloneLatestProjectVersion, customFieldDocument)
    }

    open fun createBlackDuckSignatureScannerOptions(): BlackDuckSignatureScannerOptions {
        val signatureScannerPaths = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PATHS).orElse(null)
                ?.map { it.resolvePath(pathResolver) }
                ?: emptyList()
        val exclusionPatterns = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS).orElse(null)
                ?: emptyList()
        val exclusionNamePatterns = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS)

        val scanMemory = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_MEMORY)
        val dryRun = PropertyConfigUtils.getFirstProvidedValueOrDefault(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN)
        val uploadSource = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE)
        val licenseSearch = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LICENSE_SEARCH);
        val individualFileMatching = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_INDIVIDUAL_FILE_MATCHING)
        val codeLocationPrefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX).orElse(null)
        val codeLocationSuffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX).orElse(null)
        val additionalArguments = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS).orElse(null)
        val maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH)
        val offlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val onlineLocalScannerInstallPath = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val userProvidedScannerInstallUrl = PropertyConfigUtils.getFirstProvidedValueOrEmpty(detectConfiguration, DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_HOST_URL).orElse(null)

        if (offlineLocalScannerInstallPath != null && StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
            throw DetectUserFriendlyException(
                    "You have provided both a Black Duck signature scanner url AND a local Black Duck signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                    ExitCodeType.FAILURE_GENERAL_ERROR
            )
        }



        return BlackDuckSignatureScannerOptions(
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
                individualFileMatching,
                licenseSearch
        )
    }

    fun createBlackDuckPostOptions(): BlackDuckPostOptions {
        val waitForResults = detectConfiguration.getValue(DetectProperties.DETECT_WAIT_FOR_RESULTS)
        val runRiskReport = detectConfiguration.getValue(DetectProperties.DETECT_RISK_REPORT_PDF)
        val runNoticesReport = detectConfiguration.getValue(DetectProperties.DETECT_NOTICES_REPORT)
        val riskReportPdfPath = detectConfiguration.getValue(DetectProperties.DETECT_RISK_REPORT_PDF_PATH).resolvePath(pathResolver)
        val noticesReportPath = detectConfiguration.getValue(DetectProperties.DETECT_NOTICES_REPORT_PATH).resolvePath(pathResolver)
        val policySeverities = detectConfiguration.getValue(DetectProperties.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES)
        val severitiesToFailPolicyCheck = FilterableEnumUtils.populatedValues(policySeverities, PolicyRuleSeverityType::class.java);

        return BlackDuckPostOptions(waitForResults, runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath, severitiesToFailPolicyCheck)
    }

    fun createBinaryScanOptions(): BinaryScanOptions {
        val singleTarget = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_FILE).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        val mutlipleTargets = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS)
        val codeLocationPrefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX).orElse(null)
        val codeLocationSuffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX).orElse(null)
        return BinaryScanOptions(singleTarget, mutlipleTargets, codeLocationPrefix, codeLocationSuffix)
    }

    fun createExecutablePaths(): DetectExecutableOptions {
        return DetectExecutableOptions(
                detectConfiguration.getValue(DetectProperties.DETECT_BASH_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_CONDA_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_CPAN_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_CPANM_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_NPM_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_PEAR_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_PIPENV_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_PYTHON_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_HEX_REBAR3_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_JAVA_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_DOTNET_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_GIT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_GO_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null),
                detectConfiguration.getValue(DetectProperties.DETECT_SWIFT_PATH).map { path -> path.resolvePath(pathResolver) }.orElse(null)
        )
    }
}
