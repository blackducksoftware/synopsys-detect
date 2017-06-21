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
import com.blackducksoftware.integration.hub.detect.bomtool.GoGodepsBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.GoVndrBomTool
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
    GoGodepsBomTool goGodepsBomTool

    @Autowired
    GoVndrBomTool goVndrBomTool

    @Autowired
    DockerBomTool dockerBomTool

    File outputDirectory
    Set<String> allDetectPropertyKeys = new HashSet<>()
    Set<String> additionalDockerPropertyNames = new HashSet<>()

    private boolean usingDefaultSourcePaths
    private boolean usingDefaultOutputPath

    void init() {
        if (detectProperties.sourcePaths == null || detectProperties.sourcePaths.length == 0) {
            usingDefaultSourcePaths = true
            detectProperties.sourcePaths = [
                System.getProperty('user.dir')
            ] as String[]
        }

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
    }

    /**
     * If the default source path is being used AND docker is configured, don't run unless the tool is docker
     */
    public boolean shouldRun(BomTool bomTool) {
        if (usingDefaultSourcePaths && dockerBomTool.isBomToolApplicable()) {
            return BomToolType.DOCKER == bomTool.bomToolType
        } else if (BomToolType.GO_DEP == bomTool.bomToolType){
            return !goGodepsBomTool.isBomToolApplicable() && !goVndrBomTool.isBomToolApplicable()
        } else {
            return true
        }
    }

    public String getDetectProperty(String key) {
        configurableEnvironment.getProperty(key)
    }

    private void configureForDocker() {
        if (!detectProperties.dockerInstallPath) {
            detectProperties.dockerInstallPath = detectProperties.outputDirectoryPath + File.separator + 'docker-install'
        }

        if (!detectProperties.dockerSandboxPath) {
            detectProperties.dockerSandboxPath = detectProperties.dockerInstallPath + File.separator + 'sandbox'
        }

        File dockerInstallDirectory = new File(detectProperties.dockerInstallPath)
        dockerInstallDirectory.mkdirs()

        File dockerSandboxDirectory = new File(detectProperties.dockerSandboxPath)
        dockerSandboxDirectory.mkdirs()

        allDetectPropertyKeys.each {
            if (it.startsWith(DOCKER_PROPERTY_PREFIX)) {
                additionalDockerPropertyNames.add(it)
            }
        }
    }

    public void printConfiguration(PrintStream printStream) {
        printStream.println('')
        printStream.println('Current property values:')
        printStream.println('-'.multiply(60))
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
                printStream.println("${fieldName} = ${fieldValue}")
            }
            it.accessible = false
        }
        printStream.println('-'.multiply(60))
        printStream.println('')
    }

    public Boolean getCleanupBdioFiles() {
        return detectProperties.cleanupBdioFiles
    }
    public String getHubUrl() {
        return detectProperties.hubUrl
    }
    public Integer getHubTimeout() {
        return detectProperties.hubTimeout
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
    public Boolean getHubAutoImportCertificate() {
        return detectProperties.hubAutoImportCertificate
    }
    public String[] getSourcePaths() {
        return detectProperties.sourcePaths
    }
    public String getOutputDirectoryPath() {
        return detectProperties.outputDirectoryPath
    }
    public Integer getSearchDepth() {
        return detectProperties.searchDepth
    }
    public String getBomToolTypeOverride() {
        return detectProperties.bomToolTypeOverride
    }
    public String getProjectName() {
        return detectProperties.projectName
    }
    public String getProjectVersionName() {
        return detectProperties.projectVersionName
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
    public Boolean getGradleCleanupBuildBlackduckDirectory() {
        return detectProperties.gradleCleanupBuildBlackduckDirectory
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
    public Boolean getNugetInspectorIgnoreFailure() {
        return detectProperties.nugetInspectorIgnoreFailure
    }
    public Boolean getMavenAggregateBom() {
        return detectProperties.mavenAggregateBom
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
    public Boolean getNugetAggregateBom() {
        return detectProperties.nugetAggregateBom
    }
    public String getNugetPath() {
        return detectProperties.nugetPath
    }
    public String getNpmPath() {
        return detectProperties.npmPath;
    }
    public String getPipProjectName() {
        return detectProperties.pipProjectName
    }
    public Boolean getCreateVirtualEnv() {
        return detectProperties.createVirtualEnv
    }
    public Boolean getPipThreeOverride() {
        return detectProperties.pipThreeOverride
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
    public String getDockerInstallPath() {
        return detectProperties.dockerInstallPath
    }
    public String getDockerSandboxPath() {
        return detectProperties.dockerSandboxPath
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
    public String getCleanupBomToolFiles() {
        return detectProperties.cleanupBomToolFiles
    }
    public Boolean getSuppressConfigurationOutput() {
        return detectProperties.suppressConfigurationOutput
    }
}
