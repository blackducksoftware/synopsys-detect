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
package com.synopsys.integration.detect.configuration

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder
import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.types.enumextended.BaseValue
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedValue
import com.synopsys.integration.configuration.property.types.enumfilterable.populatedValues
import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.exception.DetectUserFriendlyException
import com.synopsys.integration.detect.exitcode.ExitCodeType
import com.synopsys.integration.detect.lifecycle.run.RunOptions
import com.synopsys.integration.detect.tool.binaryscanner.BinaryScanOptions
import com.synopsys.integration.detect.tool.detector.impl.DetectDetectorFileFilter
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
import java.io.File
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern

class DetectConfigurationFactory(private val detectConfiguration: PropertyConfiguration) {
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
        val provided = if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PARALLEL_PROCESSORS)) {
            detectConfiguration.getValue(DetectProperties.DETECT_PARALLEL_PROCESSORS)
        } else if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)) {
            detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)
        } else if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)) {
            detectConfiguration.getValue(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS)
        } else {
            null
        }

        return if (provided != null && provided > 0) {
            provided
        } else {
            return Runtime.getRuntime().availableProcessors()
        }
    }

    //#endregion

    //#region Creating Connections
    //TODO: This should just follow the pattern of the other methods (all properties first, then the work)
    @Throws(DetectUserFriendlyException::class)
    fun createBlackDuckProxyInfo(): ProxyInfo {
        val proxyCredentialsBuilder = CredentialsBuilder()
        proxyCredentialsBuilder.username = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_USERNAME)
        proxyCredentialsBuilder.password = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_PASSWORD)
        val proxyCredentials: Credentials
        try {
            proxyCredentials = proxyCredentialsBuilder.build()
        } catch (e: IllegalArgumentException) {
            throw DetectUserFriendlyException(String.format("Your proxy credentials configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
        }

        val proxyInfoBuilder = ProxyInfoBuilder()

        proxyInfoBuilder.credentials = proxyCredentials
        proxyInfoBuilder.host = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_HOST)
        val proxyPortFromConfiguration = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_PORT)
        val proxyPort = NumberUtils.toInt(proxyPortFromConfiguration, 0)
        proxyInfoBuilder.port = proxyPort
        proxyInfoBuilder.ntlmDomain = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_NTLM_DOMAIN)
        proxyInfoBuilder.ntlmWorkstation = detectConfiguration.getValue(DetectProperties.BLACKDUCK_PROXY_NTLM_WORKSTATION)
        try {
            return proxyInfoBuilder.build()
        } catch (e: IllegalArgumentException) {
            throw DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.message), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
        }
    }

    fun createConnectionDetails(): ConnectionDetails {
        val proxyIgnoredHosts = detectConfiguration.getValueOrDefault(DetectProperties.BLACKDUCK_PROXY_IGNORED_HOSTS);
        val proxyPatterns = proxyIgnoredHosts.map { Pattern.compile(it) }
        val proxyInformation = createBlackDuckProxyInfo()
        val alwaysTrust = detectConfiguration.getValueOrDefault(DetectProperties.BLACKDUCK_TRUST_CERT)
        return ConnectionDetails(createBlackDuckProxyInfo(), proxyPatterns, findTimeoutInSeconds(), alwaysTrust)
    }

    fun createBlackDuckConnectionDetails(): BlackDuckConnectionDetails {
        val offline = detectConfiguration.getValue(DetectProperties.BLACKDUCK_OFFLINE_MODE)
        val blackduckUrl = detectConfiguration.getValue(DetectProperties.BLACKDUCK_URL)

        val allBlackDuckKeys: Set<String> = HashSet(BlackDuckServerConfigBuilder().propertyKeys)
                .filter { !it.toLowerCase().contains("proxy") }
                .toSet()

        val blackDuckProperties = detectConfiguration.getRaw(allBlackDuckKeys)

        return BlackDuckConnectionDetails(offline, blackduckUrl, blackDuckProperties, findParallelProcessors(), createConnectionDetails())
    }
    //#endregion

    fun createPolarisServerConfigBuilder(userHome: File): PolarisServerConfigBuilder {
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
        var sigScanDisabled = Optional.empty<Boolean>()

        // TODO: Fix this when deprecated properties are removed
        // This is because it is double deprecated so we must check if either property is set.
        val originalPropertySet = detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED)
        val newPropertySet = detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)
        if (originalPropertySet || newPropertySet) {
            sigScanDisabled = Optional.of(detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED))
        }

        var polarisEnabled = Optional.empty<Boolean>()
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_SWIP_ENABLED)) {
            polarisEnabled = Optional.of(detectConfiguration.getValue(DetectProperties.DETECT_SWIP_ENABLED))
        }
        val includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS)
        val excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED)
        val filter = ExcludeIncludeEnumFilter<DetectTool>(excludedTools, includedTools);
        val detectToolFilter = DetectToolFilter(filter, sigScanDisabled, polarisEnabled)

        val unmapCodeLocations = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_UNMAP)
        val aggregateName = detectConfiguration.getValue(DetectProperties.DETECT_BOM_AGGREGATE_NAME)
        val aggregateMode = detectConfiguration.getValue(DetectProperties.DETECT_BOM_AGGREGATE_REMEDIATION_MODE)
        val preferredTools = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TOOL)
        val useBdio2 = detectConfiguration.getValue(DetectProperties.DETECT_BDIO2_ENABLED)

        return RunOptions(unmapCodeLocations, aggregateName, aggregateMode, preferredTools, detectToolFilter, useBdio2)
    }

    fun createDirectoryOptions(): DirectoryOptions {
        val sourcePath = detectConfiguration.getValue(DetectProperties.DETECT_SOURCE_PATH)
        val outputPath = detectConfiguration.getValue(DetectProperties.DETECT_OUTPUT_PATH)
        val bdioPath = detectConfiguration.getValue(DetectProperties.DETECT_BDIO_OUTPUT_PATH)
        val scanPath = detectConfiguration.getValue(DetectProperties.DETECT_SCAN_OUTPUT_PATH)
        val toolsOutputPath = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_OUTPUT_PATH)

        return DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath, toolsOutputPath)
    }

    fun createAirGapOptions(): AirGapOptions {
        val gradleOverride = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH)
        val nugetOverride = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH)
        val dockerOverride = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH)

        return AirGapOptions(dockerOverride, gradleOverride, nugetOverride)
    }

    fun createSearchOptions(sourcePath: Path): DetectorFinderOptions {
        //Normal settings
        val maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_DEPTH)

        //File Filter
        val excludedDirectories = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION)
        val excludedDirectoryPatterns = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS)
        val excludedDirectoryPaths = detectConfiguration.getValue(DetectProperties.DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS)
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
        val prefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX)
        val suffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX)
        return BdioOptions(prefix, suffix)
    }

    fun createProjectNameVersionOptions(sourceDirectoryName: String): ProjectNameVersionOptions {
        val overrideProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_NAME)
        val overrideProjectVersionName = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NAME)
        val defaultProjectVersionText = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TEXT)
        val defaultProjectVersionScheme = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_SCHEME)
        val defaultProjectVersionFormat = detectConfiguration.getValue(DetectProperties.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT)
        return ProjectNameVersionOptions(sourceDirectoryName, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat)
    }

    @Throws(DetectUserFriendlyException::class)
    fun createDetectProjectServiceOptions(): DetectProjectServiceOptions {
        val projectVersionPhase = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_PHASE)
        val projectVersionDistribution = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_DISTRIBUTION)
        val projectTier = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TIER)
        val projectDescription = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_DESCRIPTION)
        val projectVersionNotes = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NOTES)
        val cloneCategories = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CLONE_CATEGORIES)
        val projectLevelAdjustments = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_LEVEL_ADJUSTMENTS)
        val forceProjectVersionUpdate = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_UPDATE)
        val cloneVersionName = detectConfiguration.getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_NAME)
        val projectVersionNickname = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_VERSION_NICKNAME)
        val applicationId = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_APPLICATION_ID)
        val groups = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_USER_GROUPS)
        val tags = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_TAGS)
        val parentProjectName = detectConfiguration.getValue(DetectProperties.DETECT_PARENT_PROJECT_NAME)
        val parentProjectVersion = detectConfiguration.getValue(DetectProperties.DETECT_PARENT_PROJECT_VERSION_NAME)
        val cloneLatestProjectVersion = detectConfiguration.getValue(DetectProperties.DETECT_CLONE_PROJECT_VERSION_LATEST) ?: false //TODO: Jake this is a boolean example. Should be required?

        val parser = DetectCustomFieldParser()
        val customFieldDocument = parser.parseCustomFieldDocument(detectConfiguration.getRaw())

        return DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName,
                projectVersionNickname, applicationId, tags, groups, parentProjectName, parentProjectVersion, cloneLatestProjectVersion, customFieldDocument)
    }

    fun createBlackDuckSignatureScannerOptions(): BlackDuckSignatureScannerOptions {
        val signatureScannerPaths = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS)
        val exclusionPatterns = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS)
        val exclusionNamePatterns = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS)

        val scanMemory = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY)
        val dryRun = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN)
        val uploadSource = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE)
        val codeLocationPrefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX)
        val codeLocationSuffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX)
        val additionalArguments = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS)
        val maxDepth = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH)

        val offlineLocalScannerInstallPath = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH)
        val onlineLocalScannerInstallPath = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH)
        val userProvidedScannerInstallUrl = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL)

        if (StringUtils.isNotBlank(offlineLocalScannerInstallPath) && StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
            throw DetectUserFriendlyException(
                    "You have provided both a Black Duck signature scanner url AND a local Black Duck signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.",
                    ExitCodeType.FAILURE_GENERAL_ERROR)
        }

        val snippetMatching = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING)
        fun fromSnippetExtended(value: ExtendedSnippetMode): SnippetMatching? {
            return if (detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE)) {
                SnippetMatching.SNIPPET_MATCHING
            } else {
                null
            }
        }

        var snippetMatchingEnum: SnippetMatching? = when (snippetMatching) {
            is ExtendedValue -> fromSnippetExtended(snippetMatching.value)
            is BaseValue -> snippetMatching.value
        }
        return BlackDuckSignatureScannerOptions(signatureScannerPaths, exclusionPatterns, exclusionNamePatterns, offlineLocalScannerInstallPath, onlineLocalScannerInstallPath, userProvidedScannerInstallUrl, scanMemory,
                findParallelProcessors(), dryRun,
                snippetMatchingEnum, uploadSource, codeLocationPrefix, codeLocationSuffix, additionalArguments, maxDepth)
    }

    fun createBlackDuckPostOptions(): BlackDuckPostOptions {
        val waitForResults = detectConfiguration.getValue(DetectProperties.DETECT_WAIT_FOR_RESULTS)
        val runRiskReport = detectConfiguration.getValue(DetectProperties.DETECT_RISK_REPORT_PDF)
        val runNoticesReport = detectConfiguration.getValue(DetectProperties.DETECT_NOTICES_REPORT)
        val riskReportPdfPath = detectConfiguration.getValue(DetectProperties.DETECT_RISK_REPORT_PDF_PATH)
        val noticesReportPath = detectConfiguration.getValue(DetectProperties.DETECT_NOTICES_REPORT_PATH)
        val policySeverities = detectConfiguration.getValue(DetectProperties.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES)
        val severitiesToFailPolicyCheck = policySeverities.populatedValues(PolicySeverityType.values(), PolicySeverityType::class.java);

        return BlackDuckPostOptions(waitForResults, runRiskReport, runNoticesReport, riskReportPdfPath, noticesReportPath, severitiesToFailPolicyCheck)
    }

    fun createBinaryScanOptions(): BinaryScanOptions {
        val singleTarget = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_FILE)
        val mutlipleTargets = detectConfiguration.getValue(DetectProperties.DETECT_BINARY_SCAN_FILE_NAME_PATTERNS)
        val codeLocationPrefix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX)
        val codeLocationSuffix = detectConfiguration.getValue(DetectProperties.DETECT_PROJECT_CODELOCATION_SUFFIX)
        return BinaryScanOptions(singleTarget, mutlipleTargets, codeLocationPrefix, codeLocationSuffix)
    }
}
