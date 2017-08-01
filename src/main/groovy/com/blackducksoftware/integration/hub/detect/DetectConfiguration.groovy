/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect

import java.lang.reflect.Modifier

import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.DockerBomTool
import com.blackducksoftware.integration.hub.detect.exception.DetectException
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectProperties.class)

    static final String DETECT_PROPERTY_PREFIX = 'detect.'
    static final String DOCKER_PROPERTY_PREFIX = 'detect.docker.passthrough.'

    @Autowired
    ConfigurableEnvironment configurableEnvironment

    @Autowired
    DetectProperties detectProperties

    @Autowired
    DockerBomTool dockerBomTool

    File sourceDirectory
    File outputDirectory
    Set<String> allDetectPropertyKeys = new HashSet<>()
    Set<String> additionalDockerPropertyNames = new HashSet<>()

    private boolean usingDefaultSourcePath
    private boolean usingDefaultOutputPath

    List<String> excludedScanPaths = []

    void init() {
        if (!detectProperties.sourcePath) {
            usingDefaultSourcePath = true
            detectProperties.sourcePath = System.getProperty('user.dir')
        }

        sourceDirectory = new File(detectProperties.sourcePath)
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            throw new DetectException("The source path ${detectProperties.sourcePath} either doesn't exist, isn't a directory, or doesn't have appropriate permissions.")
        }
        //make sure the path is absolute
        detectProperties.sourcePath = sourceDirectory.canonicalPath

        if (StringUtils.isBlank(detectProperties.outputDirectoryPath)) {
            usingDefaultOutputPath = true
            detectProperties.outputDirectoryPath = System.getProperty('user.home') + File.separator + 'blackduck'
        }

        detectProperties.nugetInspectorPackageName = detectProperties.nugetInspectorPackageName.trim()
        detectProperties.nugetInspectorPackageVersion = detectProperties.nugetInspectorPackageVersion.trim()

        outputDirectory = new File(detectProperties.outputDirectoryPath)
        outputDirectory.mkdirs()
        if (!outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new DetectException("The output directory ${detectProperties.outputDirectoryPath} does not exist. The system property 'user.home' will be used by default, but the output directory must exist.")
        }
        detectProperties.outputDirectoryPath = detectProperties.outputDirectoryPath.trim()

        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources()
        mutablePropertySources.each { propertySource ->
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource
                enumerablePropertySource.propertyNames.each { propertyName ->
                    if (propertyName && propertyName.startsWith(DETECT_PROPERTY_PREFIX)) {
                        allDetectPropertyKeys.add(propertyName)
                    }
                }
            }
        }

        if (dockerBomTool.isBomToolApplicable()) {
            configureForDocker()
        }

        if (detectProperties.hubSignatureScannerRelativePathsToExclude) {
            detectProperties.hubSignatureScannerRelativePathsToExclude.each {
                excludedScanPaths.add(new File(sourceDirectory, it).getCanonicalPath())
            }
        }
    }

    /**
     * If the default source path is being used AND docker is configured, don't run unless the tool is docker
     */
    public boolean shouldRun(BomTool bomTool) {
        if (usingDefaultSourcePath && dockerBomTool.isBomToolApplicable()) {
            return BomToolType.DOCKER == bomTool.bomToolType
        } else {
            return true
        }
    }

    public String getDetectProperty(String key) {
        configurableEnvironment.getProperty(key)
    }

    private void configureForDocker() {
        allDetectPropertyKeys.each {
            if (it.startsWith(DOCKER_PROPERTY_PREFIX)) {
                additionalDockerPropertyNames.add(it)
            }
        }
    }

    public void logConfiguration() {
        StringBuilder configurationBuilder = new StringBuilder()
        configurationBuilder.append('' + System.lineSeparator())
        configurationBuilder.append("Detect Version: ${Application.VERSION}" + System.lineSeparator)
        configurationBuilder.append('Current property values:' + System.lineSeparator())
        configurationBuilder.append('-'.multiply(60) + System.lineSeparator())
        def propertyFields = DetectProperties.class.getDeclaredFields().findAll {
            int modifiers = it.modifiers
            !Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers)
        }.sort { a, b ->
            a.name <=> b.name
        }

        propertyFields.each {
            it.accessible = true
            String fieldName = it.name
            Object fieldValue = it.get(detectProperties)
            if (it.type.isArray()) {
                fieldValue = fieldValue.join(', ')
            }
            if (fieldName && fieldValue && 'metaClass' != fieldName) {
                if (fieldName.toLowerCase().contains('password')) {
                    fieldValue = '*'.multiply(fieldValue.length())
                }
                configurationBuilder.append("${fieldName} = ${fieldValue}" + System.lineSeparator())
            }
            it.accessible = false
        }
        configurationBuilder.append('-'.multiply(60) + System.lineSeparator())
        configurationBuilder.append('' + System.lineSeparator())
        logger.info(configurationBuilder.toString())
    }

    private int convertInt(Integer integerObj) {
        return integerObj == null ? 0 : integerObj.intValue()
    }

    private long convertLong(Long longObj) {
        return longObj == null ? 0L : longObj.longValue()
    }

    public boolean getCleanupBdioFiles() {
        return BooleanUtils.toBoolean(detectProperties.cleanupBdioFiles)
    }
    public String getHubUrl() {
        return detectProperties.hubUrl
    }
    public int getHubTimeout() {
        return convertInt(detectProperties.hubTimeout)
    }
    public String getHubUsername() {
        return detectProperties.hubUsername
    }
    public String getHubPassword() {
        return detectProperties.hubPassword
    }
    public String getHubProxyHost() {
        return detectProperties.hubProxyHost
    }
    public String getHubProxyPort() {
        return detectProperties.hubProxyPort
    }
    public String getHubProxyUsername() {
        return detectProperties.hubProxyUsername
    }
    public String getHubProxyPassword() {
        return detectProperties.hubProxyPassword
    }
    public boolean getHubAutoImportCertificate() {
        return BooleanUtils.toBoolean(detectProperties.hubAutoImportCertificate)
    }
    public String getSourcePath() {
        return detectProperties.sourcePath
    }
    public String getOutputDirectoryPath() {
        return detectProperties.outputDirectoryPath
    }
    public int getSearchDepth() {
        return convertInt(detectProperties.searchDepth)
    }
    public String getExcludedBomToolTypes() {
        return detectProperties.excludedBomToolTypes
    }
    public String getIncludedBomToolTypes() {
        return detectProperties.includedBomToolTypes
    }
    public String getProjectName() {
        return detectProperties.projectName?.trim()
    }
    public String getProjectVersionName() {
        return detectProperties.projectVersionName?.trim()
    }
    public String getProjectCodeLocationPrefix() {
        return detectProperties.projectCodeLocationPrefix?.trim()
    }
    public boolean getProjectLevelMatchAdjustments() {
        return BooleanUtils.toBoolean(detectProperties.projectLevelMatchAdjustments)
    }
    public String getProjectVersionPhase() {
        return detectProperties.projectVersionPhase?.trim()
    }
    public String getProjectVersionDistribution() {
        return detectProperties.projectVersionDistribution?.trim()
    }
    public boolean getPolicyCheck() {
        return BooleanUtils.toBoolean(detectProperties.policyCheck)
    }
    public long getPolicyCheckTimeout() {
        return convertLong(detectProperties.policyCheckTimeout)
    }
    public String getGradleInspectorVersion() {
        return detectProperties.gradleInspectorVersion
    }
    public String getGradleBuildCommand() {
        return detectProperties.gradleBuildCommand
    }
    public String getGradleExcludedConfigurationNames() {
        return detectProperties.gradleExcludedConfigurationNames
    }
    public String getGradleIncludedConfigurationNames() {
        return detectProperties.gradleIncludedConfigurationNames
    }
    public String getGradleExcludedProjectNames() {
        return detectProperties.gradleExcludedProjectNames
    }
    public String getGradleIncludedProjectNames() {
        return detectProperties.gradleIncludedProjectNames
    }
    public boolean getGradleCleanupBuildBlackduckDirectory() {
        return BooleanUtils.toBoolean(detectProperties.gradleCleanupBuildBlackduckDirectory)
    }
    public String getNugetInspectorPackageName() {
        return detectProperties.nugetInspectorPackageName
    }
    public String getNugetInspectorPackageVersion() {
        return detectProperties.nugetInspectorPackageVersion
    }
    public String getNugetInspectorExcludedModules() {
        return detectProperties.nugetInspectorExcludedModules
    }
    public boolean getNugetInspectorIgnoreFailure() {
        return BooleanUtils.toBoolean(detectProperties.nugetInspectorIgnoreFailure)
    }
    public String getMavenScope() {
        return detectProperties.mavenScope
    }
    public String getGradlePath() {
        return detectProperties.gradlePath
    }
    public String getMavenPath() {
        return detectProperties.mavenPath
    }
    public String getNugetPath() {
        return detectProperties.nugetPath
    }
    public String getNpmPath() {
        return detectProperties.npmPath
    }
    public String getPearPath() {
        return detectProperties.pearPath
    }
    public boolean getPearNotRequiredDependencies() {
        return BooleanUtils.toBoolean(detectProperties.pearNotRequiredDependencies)
    }
    public String getPipProjectName() {
        return detectProperties.pipProjectName
    }
    public boolean getPipThreeOverride() {
        return BooleanUtils.toBoolean(detectProperties.pipThreeOverride)
    }
    public String getPythonPath() {
        return detectProperties.pythonPath
    }
    public String getPipPath() {
        return detectProperties.pipPath
    }
    public String getVirtualEnvPath() {
        return detectProperties.virtualEnvPath
    }
    public String getRequirementsFilePath() {
        return detectProperties.requirementsFilePath
    }
    public String getGoDepPath() {
        return detectProperties.goDepPath
    }
    public String getDockerPath() {
        return detectProperties.dockerPath
    }
    public String getDockerInspectorPath() {
        return detectProperties.dockerInspectorPath
    }
    public String getDockerInspectorVersion() {
        return detectProperties.dockerInspectorVersion
    }
    public String getDockerTar() {
        return detectProperties.dockerTar
    }
    public String getDockerImage() {
        return detectProperties.dockerImage
    }
    public String getBashPath() {
        return detectProperties.bashPath
    }
    public String getLoggingLevel() {
        return detectProperties.loggingLevel
    }
    public boolean getCleanupBomToolFiles() {
        return BooleanUtils.toBoolean(detectProperties.cleanupBomToolFiles)
    }
    public boolean getSuppressConfigurationOutput() {
        return BooleanUtils.toBoolean(detectProperties.suppressConfigurationOutput)
    }
    public boolean getHubSignatureScannerDryRun() {
        return detectProperties.hubSignatureScannerDryRun
    }
    public String[] getHubSignatureScannerPaths() {
        return detectProperties.hubSignatureScannerPaths
    }
    public String[] getHubSignatureScannerExclusionPatterns() {
        return detectProperties.hubSignatureScannerExclusionPatterns
    }
    public List<String> getHubSignatureScannerPathsToExclude() {
        return excludedScanPaths
    }
    public boolean getPackagistIncludeDevDependencies() {
        return BooleanUtils.toBoolean(detectProperties.packagistIncludeDevDependencies)
    }
    public int getHubSignatureScannerMemory() {
        return convertInt(detectProperties.hubSignatureScannerMemory)
    }
    public boolean getHubSignatureScannerDisabled() {
        BooleanUtils.toBoolean(detectProperties.getHubSignatureScannerDisabled())
    }
    public String getPerlPath() {
        return detectProperties.perlPath?.trim()
    }
    public String getCpanPath() {
        return detectProperties.cpanPath?.trim()
    }
    public String getCpanmPath() {
        return detectProperties.cpanmPath?.trim()
    }
    public String getSbtExcludedConfigurationNames() {
        return detectProperties.sbtExcludedConfigurationNames
    }
    public String getSbtIncludedConfigurationNames() {
        return detectProperties.sbtIncludedConfigurationNames
    }
    public String getVersionTimeFormat() {
        return detectProperties.versionTimeFormat?.trim()
    }
    public String getAggregateBomName() {
        return detectProperties.aggregateBomName?.trim()
    }
    public String getCondaPath() {
        return detectProperties.condaPath?.trim()
    }
    public String getCondaEnvironmentName() {
        return detectProperties.condaEnvironmentName?.trim()
    }
    public Boolean getRiskreportPDF() {
        return detectProperties.riskreportPDF
    }
}
