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

import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.DockerBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.GradleBomTool
import com.blackducksoftware.integration.hub.detect.bomtool.NugetBomTool
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.detect.help.ValueDescription
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver
import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class)

    static final String DETECT_PROPERTY_PREFIX = 'detect.'
    static final String DOCKER_PROPERTY_PREFIX = 'detect.docker.passthrough.'
    static final String NUGET = 'nuget'
    static final String GRADLE = 'gradle'
    static final String DOCKER = 'docker'

    private static final String GROUP_HUB_CONFIGURATION = 'hub configuration'
    private static final String GROUP_GENERAL = 'general'
    private static final String GROUP_LOGGING = 'logging'
    private static final String GROUP_CLEANUP = 'cleanup'
    private static final String GROUP_PATHS = 'paths'
    private static final String GROUP_BOMTOOL = 'bomtool'
    private static final String GROUP_CONDA = 'conda'
    private static final String GROUP_CPAN = 'cpan'
    private static final String GROUP_DOCKER = 'docker'
    private static final String GROUP_GO = 'go'
    private static final String GROUP_GRADLE = 'gradle'
    private static final String GROUP_HEX = 'hex'
    private static final String GROUP_MAVEN = 'maven'
    private static final String GROUP_NPM = 'npm'
    private static final String GROUP_NUGET = 'nuget'
    private static final String GROUP_PACKAGIST = 'packagist'
    private static final String GROUP_PEAR = 'pear'
    private static final String GROUP_PIP = 'pip'
    private static final String GROUP_POLICY_CHECK = 'policy check'
    private static final String GROUP_PROJECT_INFO = 'project info'
    private static final String GROUP_PYTHON = 'python'
    private static final String GROUP_SBT = 'sbt'
    private static final String GROUP_SIGNATURE_SCANNER = 'signature scanner'

    @Autowired
    ConfigurableEnvironment configurableEnvironment

    @Autowired
    DockerBomTool dockerBomTool

    @Autowired
    NugetBomTool nugetBomTool

    @Autowired
    GradleBomTool gradleBomTool

    @Autowired
    Gson gson

    @Autowired
    TildeInPathResolver tildeInPathResolver

    File sourceDirectory
    File outputDirectory

    Set<String> allDetectPropertyKeys = new HashSet<>()
    Set<String> additionalDockerPropertyNames = new HashSet<>()

    private boolean usingDefaultSourcePath
    private boolean usingDefaultOutputPath

    List<String> excludedScanPaths = []

    void init() {
        String systemUserHome = System.getProperty('user.home')
        if (resolveTildeInPaths) {
            tildeInPathResolver.resolveTildeInAllPathFields(systemUserHome, this)
        }

        if (!sourcePath) {
            usingDefaultSourcePath = true
            sourcePath = System.getProperty('user.dir')
        }

        sourceDirectory = new File(sourcePath)
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            throw new DetectUserFriendlyException("The source path ${sourcePath} either doesn't exist, isn't a directory, or doesn't have appropriate permissions.", ExitCodeType.FAILURE_GENERAL_ERROR)
        }
        //make sure the path is absolute
        sourcePath = sourceDirectory.canonicalPath

        usingDefaultOutputPath = StringUtils.isBlank(outputDirectoryPath)
        outputDirectoryPath = createDirectoryPath(outputDirectoryPath, systemUserHome, 'blackduck')
        bdioOutputDirectoryPath = createDirectoryPath(bdioOutputDirectoryPath, outputDirectoryPath, 'bdio')
        scanOutputDirectoryPath = createDirectoryPath(scanOutputDirectoryPath, outputDirectoryPath, 'scan')

        ensureDirectoryExists(outputDirectoryPath, 'The system property \'user.home\' will be used by default, but the output directory must exist.')
        ensureDirectoryExists(bdioOutputDirectoryPath, 'By default, the directory \'bdio\' will be created in the outputDirectory, but the directory must exist.')
        ensureDirectoryExists(scanOutputDirectoryPath, 'By default, the directory \'scan\' will be created in the outputDirectory, but the directory must exist.')

        outputDirectory = new File(outputDirectoryPath)

        nugetInspectorPackageName = nugetInspectorPackageName.trim()
        nugetInspectorPackageVersion = nugetInspectorPackageVersion.trim()

        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources()
        mutablePropertySources.each { propertySource ->
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource
                enumerablePropertySource.propertyNames.each { String propertyName ->
                    if (propertyName && propertyName.startsWith(DETECT_PROPERTY_PREFIX)) {
                        allDetectPropertyKeys.add(propertyName)
                    }
                }
            }
        }

        if (dockerBomTool.isBomToolApplicable()) {
            configureForDocker()
        }

        if (hubSignatureScannerRelativePathsToExclude) {
            hubSignatureScannerRelativePathsToExclude.each { String path ->
                excludedScanPaths.add(new File(sourceDirectory, path).getCanonicalPath())
            }
        }

        if (hubSignatureScannerHostUrl && hubSignatureScannerOfflineLocalPath) {
            throw new DetectUserFriendlyException('You have provided both a hub signature scanner url AND a local hub signature scanner path. Only one of these properties can be set at a time. If both are used together, the *correct* source of the signature scanner can not be determined.', ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        if (hubSignatureScannerHostUrl) {
            logger.info('A hub signature scanner url was provided, which requires hub offline mode. Setting hub offline mode to true.');
            hubOfflineMode = true
        }

        if (hubSignatureScannerOfflineLocalPath) {
            logger.info('A local hub signature scanner path was provided, which requires hub offline mode. Setting hub offline mode to true.');
            hubOfflineMode = true
        }

        if (gradleInspectorVersion.equals("latest") && gradleBomTool.isBomToolApplicable()) {
            gradleInspectorVersion = gradleBomTool.getInspectorVersion()
            logger.info("Resolved gradle inspector version from latest to: ${gradleInspectorVersion}")
        }
        if (nugetInspectorPackageVersion.equals("latest") && nugetBomTool.isBomToolApplicable()) {
            nugetInspectorPackageVersion = nugetBomTool.getInspectorVersion()
            logger.info("Resolved nuget inspector version from latest to: ${nugetInspectorPackageVersion}")
        }
        if (dockerInspectorVersion.equals("latest") && dockerBomTool.isBomToolApplicable()) {
            dockerInspectorVersion = dockerBomTool.getInspectorVersion()
            logger.info("Resolved docker inspector version from latest to: ${dockerInspectorVersion}")
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

    public String guessDetectJarLocation() {
        String containsDetectJarRegex = '.*hub-detect-[^\\\\/]+\\.jar.*'
        String javaClassPath = System.getProperty('java.class.path')
        if (javaClassPath?.matches(containsDetectJarRegex)) {
            for (String classPathChunk : javaClassPath.split(System.getProperty('path.separator'))) {
                if (classPathChunk?.matches(containsDetectJarRegex)) {
                    logger.debug("Guessed Detect jar location as ${classPathChunk}")
                    return classPathChunk
                }
            }
        }
        return ''
    }

    public ProxyInfo getHubProxyInfo() {
        ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder()
        proxyInfoBuilder.setHost(hubProxyHost)
        proxyInfoBuilder.setPort(hubProxyPort)
        proxyInfoBuilder.setUsername(hubProxyUsername)
        proxyInfoBuilder.setPassword(hubProxyPassword)
        ProxyInfo proxyInfo = ProxyInfo.NO_PROXY_INFO
        try {
            proxyInfo = proxyInfoBuilder.build()
        } catch (IllegalStateException e) {
            throw new DetectUserFriendlyException("Your proxy configuration is not valid: ${e.message}", e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY)
        }
        return proxyInfo
    }

    private String getInspectorAirGapPath(String inspectorLocationProperty, String inspectorName) {
        if (!inspectorLocationProperty?.trim()) {
            try {
                File detectJar = new File(guessDetectJarLocation()).getCanonicalFile()
                File inspectorsDirectory = new File(detectJar.getParentFile(), 'packaged-inspectors')
                File inspectorAirGapDirectory = new File(inspectorsDirectory, inspectorName)
                return inspectorAirGapDirectory.getCanonicalPath()
            } catch (final Exception e) {
                logger.debug("Exception encountered when guessing air gap path for ${inspectorName}, returning the detect property instead")
                logger.debug(e.getMessage())
            }
        }
        return inspectorLocationProperty
    }

    private int convertInt(Integer integerObj) {
        return integerObj == null ? 0 : integerObj.intValue()
    }

    private long convertLong(Long longObj) {
        return longObj == null ? 0L : longObj.longValue()
    }

    private void configureForDocker() {
        allDetectPropertyKeys.each {
            if (it.startsWith(DOCKER_PROPERTY_PREFIX)) {
                additionalDockerPropertyNames.add(it)
            }
        }
    }

    private String createDirectoryPath(String providedDirectoryPath, String defaultDirectoryPath, String defaultDirectoryName) {
        if (StringUtils.isBlank(providedDirectoryPath)) {
            File directory = new File(defaultDirectoryPath, defaultDirectoryName)
            return directory.canonicalPath
        }
        return providedDirectoryPath
    }

    private void ensureDirectoryExists(String directoryPath, String failureMessage) {
        File directory = new File(directoryPath)
        directory.mkdirs()
        if (!directory.exists() || !directory.isDirectory()) {
            throw new DetectUserFriendlyException("The directory ${directoryPath} does not exist. ${failureMessage}", ExitCodeType.FAILURE_GENERAL_ERROR)
        }
    }

    //properties start

    @ValueDescription(description="If true, detect will always exit with code 0.", defaultValue="false", group=DetectConfiguration.GROUP_GENERAL)
    @Value('${detect.force.success:}')
    Boolean forceSuccess

    @ValueDescription(description="If true, the default behavior of printing your configuration properties at startup will be suppressed.", defaultValue="false", group=DetectConfiguration.GROUP_LOGGING)
    @Value('${detect.suppress.configuration.output:}')
    Boolean suppressConfigurationOutput

    @ValueDescription(description="If true, the default behavior of printing the Detect Results will be suppressed.", defaultValue="false", group=DetectConfiguration.GROUP_LOGGING)
    @Value('${detect.suppress.results.output:}')
    Boolean suppressResultsOutput

    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true", group=DetectConfiguration.GROUP_CLEANUP)
    @Value('${detect.cleanup.bdio.files:}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="Test the connection to the Hub with the current configuration", defaultValue="false", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${detect.test.connection:}')
    Boolean testConnection

    @ValueDescription(description="Timeout for response from the hub regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.", defaultValue="300000", group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.api.timeout:}')
    Long apiTimeout

    @ValueDescription(description="URL of the Hub server", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.url:}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.timeout:}')
    Integer hubTimeout

    @ValueDescription(description="Hub username", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.username:}')
    String hubUsername

    @ValueDescription(description="Hub password", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.password:}')
    String hubPassword

    @ValueDescription(description="Proxy host", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.host:}')
    String hubProxyHost

    @ValueDescription(description="Proxy port", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.port:}')
    String hubProxyPort

    @ValueDescription(description="Proxy username", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.username:}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.password:}')
    String hubProxyPassword

    @ValueDescription(description="If true, automatically trust the certificate for the current run of Detect only", defaultValue="false", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.trust.cert:}')
    Boolean hubTrustCertificate

    @ValueDescription(description="This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.", defaultValue="false", group=DetectConfiguration.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.offline.mode:}')
    Boolean hubOfflineMode

    @ValueDescription(description="If set to false we will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.", defaultValue="true", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.resolve.tilde.in.paths:}')
    Boolean resolveTildeInPaths

    @ValueDescription(description="Source path to inspect", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.source.path:}')
    String sourcePath

    @ValueDescription(description="Output path", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.output.path:}')
    String outputDirectoryPath

    @ValueDescription(description="The output directory for all bdio files. If not set, the bdio files will be in a 'bdio' subdirectory of the output path.", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.bdio.output.path:}')
    String bdioOutputDirectoryPath

    @ValueDescription(description="The output directory for all scan files. If not set, the scan files will be in a 'scan' subdirectory of the output path.", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.scan.output.path:}')
    String scanOutputDirectoryPath

    @ValueDescription(description="Depth from source paths to search for files.", defaultValue="3", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.search.depth:}')
    Integer searchDepth

    @ValueDescription(description="By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.", group=DetectConfiguration.GROUP_BOMTOOL)
    @Value('${detect.excluded.bom.tool.types:}')
    String excludedBomToolTypes

    @ValueDescription(description="By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", group=DetectConfiguration.GROUP_BOMTOOL)
    @Value('${detect.included.bom.tool.types:}')
    String includedBomToolTypes

    @ValueDescription(description="An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.", group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.name:}')
    String projectName

    @ValueDescription(description="An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.", group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.name:}')
    String projectVersionName

    @ValueDescription(description="A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.", defaultValue='', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.codelocation.prefix:}')
    String projectCodeLocationPrefix

    @ValueDescription(description="A suffix to the name of the codelocations created by Detect.", defaultValue='', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.codelocation.suffix:}')
    String projectCodeLocationSuffix

    @ValueDescription(description="If set to true, when an old code location format is found in the Hub, instead of logging a warning, the code location will be deleted. USE WITH CAUTION - THIS CAN DELETE CODE LOCATIONS IN THE HUB.", defaultValue='false', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.codelocation.delete.old.names:}')
    Boolean projectCodeLocationDeleteOldNames

    @ValueDescription(description="An override for the Project level matches.", defaultValue="true", group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.level.adjustments:}')
    String projectLevelMatchAdjustments

    @ValueDescription(description="An override for the Project Version phase.", defaultValue="Development",  group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.phase:}')
    String projectVersionPhase

    @ValueDescription(description="An override for the Project Version distribution", defaultValue="External",  group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.distribution:}')
    String projectVersionDistribution

    @ValueDescription(description="Set to true if you would like a policy check from the hub for your project. False by default", defaultValue="false", group=DetectConfiguration.GROUP_POLICY_CHECK)
    @Value('${detect.policy.check:}')
    Boolean policyCheck

    @ValueDescription(description="A comma-separated list of policy violation severities that will fail detect if checking policies is enabled. If no severity is provided, any policy violation will fail detect.", group=DetectConfiguration.GROUP_POLICY_CHECK)
    @Value('${detect.policy.check.fail.on.severities:}')
    String policyCheckFailOnSeverities

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="latest", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.version:}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.build.command:}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.excluded.configurations:}')
    String gradleExcludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.included.configurations:}')
    String gradleIncludedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.excluded.projects:}')
    String gradleExcludedProjectNames

    @ValueDescription(description="The names of the projects to include", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.included.projects:}')
    String gradleIncludedProjectNames

    @ValueDescription(description="Set this to false if you do not want the 'blackduck' directory in your build directory to be deleted.", defaultValue="true", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.cleanup.build.blackduck.directory:}')
    Boolean gradleCleanupBuildBlackduckDirectory

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.inspector.name:}')
    String nugetInspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="latest", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.inspector.version:}')
    String nugetInspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.excluded.modules:}')
    String nugetInspectorExcludedModules

    @ValueDescription(description="The names of the projects in a solution to include (overrides exclude)", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.included.modules:}')
    String nugetInspectorIncludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.ignore.failure:}')
    Boolean nugetInspectorIgnoreFailure

    @ValueDescription(description="The name of the dependency scope to include", group=DetectConfiguration.GROUP_MAVEN)
    @Value('${detect.maven.scope:}')
    String mavenScope

    @ValueDescription(description="Maven build command", group=DetectConfiguration.GROUP_MAVEN)
    @Value('${detect.maven.build.command:}')
    String mavenBuildCommand

    @ValueDescription(description="Path of the Gradle executable", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.path:}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable", group=DetectConfiguration.GROUP_MAVEN)
    @Value('${detect.maven.path:}')
    String mavenPath

    @ValueDescription(description="The names of the module to exclude", group=DetectConfiguration.GROUP_MAVEN)
    @Value('${detect.maven.excluded.modules:}')
    String mavenExcludedModuleNames

    @ValueDescription( description="The names of the module to include", group=DetectConfiguration.GROUP_MAVEN)
    @Value('${detect.maven.included.modules:}')
    String mavenIncludedModuleNames

    @ValueDescription(description="The path of the Nuget executable", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.path:}')
    String nugetPath

    @ValueDescription(description="Override for pip inspector to find your project", group=DetectConfiguration.GROUP_PIP)
    @Value('${detect.pip.project.name:}')
    String pipProjectName

    @ValueDescription(description="If true will use Python 3 if available on class path", defaultValue="false", group=DetectConfiguration.GROUP_PYTHON)
    @Value('${detect.python.python3:}')
    Boolean pythonThreeOverride

    @ValueDescription(description="The path of the Python executable", group=DetectConfiguration.GROUP_PYTHON)
    @Value('${detect.python.path:}')
    String pythonPath

    @ValueDescription(description="The path of the Npm executable", group=DetectConfiguration.GROUP_NPM)
    @Value('${detect.npm.path:}')
    String npmPath

    @ValueDescription(description="Set this value to false if you would like to exclude your dev dependencies when ran", defaultValue='true', group=DetectConfiguration.GROUP_NPM)
    @Value('${detect.npm.include.dev.dependencies:}')
    String npmIncludeDevDependencies

    @ValueDescription(description="The path of the node executable that is used by Npm", group=DetectConfiguration.GROUP_NPM)
    @Value('${detect.npm.node.path:}')
    String npmNodePath

    @ValueDescription(description="The path of the pear executable", group=DetectConfiguration.GROUP_PEAR)
    @Value('${detect.pear.path:}')
    String pearPath

    @ValueDescription(description="Set to true if you would like to include only required packages", defaultValue='false', group=DetectConfiguration.GROUP_PEAR)
    @Value('${detect.pear.only.required.deps:}')
    Boolean pearOnlyRequiredDependencies

    @ValueDescription(description="The path of the requirements.txt file", group=DetectConfiguration.GROUP_PIP)
    @Value('${detect.pip.requirements.path:}')
    String requirementsFilePath

    @ValueDescription(description="Path of the Go Dep executable", group=DetectConfiguration.GROUP_GO)
    @Value('${detect.go.dep.path:}')
    String goDepPath

    @ValueDescription(description="If set to true, we will attempt to run 'init' and 'ensure' which can modify your development environment.", defaultValue='false', group=DetectConfiguration.GROUP_GO)
    @Value('${detect.go.run.dep.init:}')
    Boolean goRunDepInit

    @ValueDescription(description="Path of the docker executable", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.path:}')
    String dockerPath

    @ValueDescription(description="This is used to override using the hosted script by github url. You can provide your own script at this path.", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.inspector.path:}')
    String dockerInspectorPath

    @ValueDescription(description="Version of the Hub Docker Inspector to use", defaultValue="latest", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.inspector.version:}')
    String dockerInspectorVersion

    @ValueDescription(description="A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.tar:}')
    String dockerTar

    @ValueDescription(description="The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.image:}')
    String dockerImage

    @ValueDescription(description="Path of the bash executable", group=DetectConfiguration.GROUP_PATHS)
    @Value('${detect.bash.path:}')
    String bashPath

    @ValueDescription(description="The logging level of Detect (ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)", defaultValue='INFO', group=DetectConfiguration.GROUP_LOGGING)
    @Value('${logging.level.com.blackducksoftware.integration:}')
    String loggingLevel

    @ValueDescription(description="Detect creates temporary files in the output directory. If set to true this will clean them up after execution", defaultValue='true', group=DetectConfiguration.GROUP_CLEANUP)
    @Value('${detect.cleanup.bom.tool.files:}')
    Boolean cleanupBomToolFiles

    @ValueDescription(description="If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.", defaultValue='false', group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.dry.run:}')
    Boolean hubSignatureScannerDryRun

    @ValueDescription(description="If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.", defaultValue='false', group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.snippet.mode:}')
    Boolean hubSignatureScannerSnippetMode

    @ValueDescription(description="Enables you to specify sub-directories to exclude from scans", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.exclusion.patterns:}')
    String[] hubSignatureScannerExclusionPatterns

    @ValueDescription(description="These paths and only these paths will be scanned.", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.paths:}')
    String[] hubSignatureScannerPaths

    @ValueDescription(description="The relative paths of directories to be excluded from scan registration", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.relative.paths.to.exclude:}')
    String[] hubSignatureScannerRelativePathsToExclude

    @ValueDescription(description="The memory for the scanner to use.", defaultValue="4096", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.memory:}')
    Integer hubSignatureScannerMemory

    @ValueDescription(description="Set to true to disable the Hub Signature Scanner.", defaultValue="false", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.disabled:}')
    Boolean hubSignatureScannerDisabled

    @ValueDescription(description="To use a local signature scanner, set its location with this property. This will be the path where the signature scanner was unzipped. This will likely look similar to /some/path/scan.cli-x.y.z", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.offline.local.path:}')
    String hubSignatureScannerOfflineLocalPath

    @ValueDescription(description="If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.", group=DetectConfiguration.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.host.url:}')
    String hubSignatureScannerHostUrl

    @ValueDescription(description="Set this value to false if you would like to exclude your dev requires dependencies when ran", defaultValue='true', group=DetectConfiguration.GROUP_PACKAGIST)
    @Value('${detect.packagist.include.dev.dependencies:}')
    Boolean packagistIncludeDevDependencies

    @ValueDescription(description="The path of the perl executable", group=DetectConfiguration.GROUP_CPAN)
    @Value('${detect.perl.path:}')
    String perlPath

    @ValueDescription(description="The path of the cpan executable", group=DetectConfiguration.GROUP_CPAN)
    @Value('${detect.cpan.path:}')
    String cpanPath

    @ValueDescription(description="The path of the cpanm executable", group=DetectConfiguration.GROUP_CPAN)
    @Value('${detect.cpanm.path:}')
    String cpanmPath

    @ValueDescription(description="The names of the sbt configurations to exclude", group=DetectConfiguration.GROUP_SBT)
    @Value('${detect.sbt.excluded.configurations:}')
    String sbtExcludedConfigurationNames

    @ValueDescription( description="The names of the sbt configurations to include", group=DetectConfiguration.GROUP_SBT)
    @Value('${detect.sbt.included.configurations:}')
    String sbtIncludedConfigurationNames

    @ValueDescription(description="The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'", defaultValue='text', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.scheme:}')
    String defaultProjectVersionScheme

    @ValueDescription(description="The text to use as the default project version", defaultValue='Detect Unknown Version', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.text:}')
    String defaultProjectVersionText

    @ValueDescription(description="The timestamp format to use as the default project version", defaultValue='yyyy-MM-dd\'T\'HH:mm:ss.SSS', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.timeformat:}')
    String defaultProjectVersionTimeformat

    @ValueDescription(description="If set, this will aggregate all the BOMs to create a single BDIO file with the name provided. For Co-Pilot use only", group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.bom.aggregate.name:}')
    String aggregateBomName

    @ValueDescription (description="When set to true, a Black Duck risk report in PDF form will be created", defaultValue='false', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.risk.report.pdf:}')
    Boolean riskReportPdf

    @ValueDescription (description="The output directory for risk report in PDF. Default is the source directory", defaultValue='.', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.risk.report.pdf.path:}')
    String riskReportPdfOutputDirectory

    @ValueDescription (description="When set to true, a Black Duck notices report in text form will be created in your source directory", defaultValue='false', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.notices.report:}')
    Boolean noticesReport

    @ValueDescription (description="The output directory for notices report. Default is the source directory", defaultValue='.', group=DetectConfiguration.GROUP_PROJECT_INFO)
    @Value('${detect.notices.report.path:}')
    String noticesReportOutputDirectory

    @ValueDescription(description="The path of the conda executable", group=DetectConfiguration.GROUP_CONDA)
    @Value('${detect.conda.path:}')
    String condaPath

    @ValueDescription(description="The name of the anaconda environment used by your project", group=DetectConfiguration.GROUP_CONDA)
    @Value('${detect.conda.environment.name:}')
    String condaEnvironmentName

    @ValueDescription(description="The path to the directory containing the docker inspector script, jar, and images", group=DetectConfiguration.GROUP_DOCKER)
    @Value('${detect.docker.inspector.air.gap.path:}')
    String dockerInspectorAirGapPath

    @ValueDescription(description="The path to the directory containing the air gap dependencies for the gradle inspector", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.air.gap.path:}')
    String gradleInspectorAirGapPath

    @ValueDescription(description="The path to the directory containing the nuget inspector nupkg", group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.inspector.air.gap.path:}')
    String nugetInspectorAirGapPath

    @ValueDescription(description="The source for nuget packages", defaultValue='https://www.nuget.org/api/v2/', group=DetectConfiguration.GROUP_NUGET)
    @Value('${detect.nuget.packages.repo.url:}')
    String nugetPackagesRepoUrl

    @ValueDescription(description="The respository gradle should use to look for the gradle inspector", group=DetectConfiguration.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.repository.url:}')
    String gradleInspectorRepositoryUrl

    @ValueDescription(description="The path of the rebar3 executable", group=DetectConfiguration.GROUP_HEX)
    @Value('${detect.hex.rebar3.path:}')
    String hexRebar3Path

    @ValueDescription(description="The number of scans to run in parallel, default to the number of processors", group=DetectConfiguration.GROUP_GENERAL)
    @Value('${detect.scan.parallel.processors:}')
    Integer executionParallelism

    int getExecutionParallelism() {
        return executionParallelism == null ? Runtime.runtime.availableProcessors() : executionParallelism
    }

    public boolean getCleanupBdioFiles() {
        return BooleanUtils.toBoolean(cleanupBdioFiles)
    }
    public boolean getTestConnection() {
        return BooleanUtils.toBoolean(testConnection)
    }
    public long getApiTimeout() {
        return convertLong(apiTimeout)
    }
    public String getHubUrl() {
        return hubUrl
    }
    public int getHubTimeout() {
        return convertInt(hubTimeout)
    }
    public String getHubUsername() {
        return hubUsername
    }
    public String getHubPassword() {
        return hubPassword
    }
    public String getHubProxyHost() {
        return hubProxyHost
    }
    public String getHubProxyPort() {
        return hubProxyPort
    }
    public String getHubProxyUsername() {
        return hubProxyUsername
    }
    public String getHubProxyPassword() {
        return hubProxyPassword
    }
    public boolean getHubOfflineMode() {
        return BooleanUtils.toBoolean(hubOfflineMode)
    }
    public boolean getHubTrustCertificate() {
        return BooleanUtils.toBoolean(hubTrustCertificate)
    }
    public boolean getResolveTildeInPaths() {
        return BooleanUtils.toBoolean(resolveTildeInPaths)
    }
    public String getSourcePath() {
        return sourcePath
    }
    public String getOutputDirectoryPath() {
        return outputDirectoryPath
    }
    public String getBdioOutputDirectoryPath() {
        return bdioOutputDirectoryPath
    }
    public String getScanOutputDirectoryPath() {
        return scanOutputDirectoryPath
    }
    public int getSearchDepth() {
        return convertInt(searchDepth)
    }
    public String getExcludedBomToolTypes() {
        return excludedBomToolTypes?.toUpperCase()
    }
    public String getIncludedBomToolTypes() {
        return includedBomToolTypes?.toUpperCase()
    }
    public String getProjectName() {
        return projectName?.trim()
    }
    public String getProjectVersionName() {
        return projectVersionName?.trim()
    }
    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix?.trim()
    }
    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix?.trim()
    }
    public boolean getProjectCodeLocationDeleteOldNames() {
        return BooleanUtils.toBoolean(projectCodeLocationDeleteOldNames)
    }
    public boolean getProjectLevelMatchAdjustments() {
        return BooleanUtils.toBoolean(projectLevelMatchAdjustments)
    }
    public String getProjectVersionPhase() {
        return projectVersionPhase?.trim()
    }
    public String getProjectVersionDistribution() {
        return projectVersionDistribution?.trim()
    }
    public boolean getPolicyCheck() {
        return BooleanUtils.toBoolean(policyCheck)
    }
    public String getPolicyCheckFailOnSeverities() {
        return policyCheckFailOnSeverities
    }
    public String getGradleInspectorVersion() {
        return gradleInspectorVersion
    }
    public String getGradleBuildCommand() {
        return gradleBuildCommand
    }
    public String getGradleExcludedConfigurationNames() {
        return gradleExcludedConfigurationNames
    }
    public String getGradleIncludedConfigurationNames() {
        return gradleIncludedConfigurationNames
    }
    public String getGradleExcludedProjectNames() {
        return gradleExcludedProjectNames
    }
    public String getGradleIncludedProjectNames() {
        return gradleIncludedProjectNames
    }
    public boolean getGradleCleanupBuildBlackduckDirectory() {
        return BooleanUtils.toBoolean(gradleCleanupBuildBlackduckDirectory)
    }
    public String getNugetInspectorPackageName() {
        return nugetInspectorPackageName
    }
    public String getNugetInspectorPackageVersion() {
        return nugetInspectorPackageVersion
    }
    public String getNugetInspectorExcludedModules() {
        return nugetInspectorExcludedModules
    }
    public String getNugetInspectorIncludedModules() {
        return nugetInspectorIncludedModules
    }
    public boolean getNugetInspectorIgnoreFailure() {
        return BooleanUtils.toBoolean(nugetInspectorIgnoreFailure)
    }
    public String getMavenScope() {
        return mavenScope
    }
    public String getGradlePath() {
        return gradlePath
    }
    public String getMavenPath() {
        return mavenPath
    }
    public String getMavenExcludedModuleNames() {
        return mavenExcludedModuleNames
    }
    public String getMavenIncludedModuleNames() {
        return mavenIncludedModuleNames
    }
    public String getMavenBuildCommand() {
        return mavenBuildCommand
    }
    public String getNugetPath() {
        return nugetPath
    }
    public String getNpmPath() {
        return npmPath
    }
    public boolean getNpmIncludeDevDependencies() {
        return BooleanUtils.toBoolean(npmIncludeDevDependencies)
    }
    public String getNpmNodePath() {
        return npmNodePath
    }
    public String getPearPath() {
        return pearPath
    }
    public boolean getPearOnlyRequiredDependencies() {
        return BooleanUtils.toBoolean(pearOnlyRequiredDependencies)
    }
    public String getPipProjectName() {
        return pipProjectName
    }
    public boolean getPythonThreeOverride() {
        return BooleanUtils.toBoolean(pythonThreeOverride)
    }
    public String getPythonPath() {
        return pythonPath
    }
    public String getRequirementsFilePath() {
        return requirementsFilePath
    }
    public String getGoDepPath() {
        return goDepPath
    }
    public boolean getGoRunDepInit() {
        return BooleanUtils.toBoolean(goRunDepInit)
    }
    public String getDockerPath() {
        return dockerPath
    }
    public String getDockerInspectorPath() {
        return dockerInspectorPath
    }
    public String getDockerInspectorVersion() {
        return dockerInspectorVersion
    }
    public String getDockerTar() {
        return dockerTar
    }
    public String getDockerImage() {
        return dockerImage
    }
    public String getBashPath() {
        return bashPath
    }
    public String getLoggingLevel() {
        return loggingLevel
    }
    public boolean getCleanupBomToolFiles() {
        return BooleanUtils.toBoolean(cleanupBomToolFiles)
    }
    public boolean getSuppressConfigurationOutput() {
        return BooleanUtils.toBoolean(suppressConfigurationOutput)
    }
    public boolean getForceSuccess() {
        return BooleanUtils.toBoolean(forceSuccess)
    }
    public boolean getSuppressResultsOutput() {
        return BooleanUtils.toBoolean(suppressResultsOutput)
    }
    public boolean getHubSignatureScannerDryRun() {
        return hubSignatureScannerDryRun
    }
    public boolean getHubSignatureScannerSnippetMode() {
        return hubSignatureScannerSnippetMode
    }
    public String[] getHubSignatureScannerPaths() {
        return hubSignatureScannerPaths
    }
    public String[] getHubSignatureScannerExclusionPatterns() {
        return hubSignatureScannerExclusionPatterns
    }
    public List<String> getHubSignatureScannerPathsToExclude() {
        return excludedScanPaths
    }
    public String getHubSignatureScannerOfflineLocalPath() {
        return hubSignatureScannerOfflineLocalPath
    }
    public String getHubSignatureScannerHostUrl() {
        return hubSignatureScannerHostUrl
    }
    public boolean getPackagistIncludeDevDependencies() {
        return BooleanUtils.toBoolean(packagistIncludeDevDependencies)
    }
    public int getHubSignatureScannerMemory() {
        return convertInt(hubSignatureScannerMemory)
    }
    public boolean getHubSignatureScannerDisabled() {
        return BooleanUtils.toBoolean(hubSignatureScannerDisabled)
    }
    public String getPerlPath() {
        return perlPath?.trim()
    }
    public String getCpanPath() {
        return cpanPath?.trim()
    }
    public String getCpanmPath() {
        return cpanmPath?.trim()
    }
    public String getSbtExcludedConfigurationNames() {
        return sbtExcludedConfigurationNames
    }
    public String getSbtIncludedConfigurationNames() {
        return sbtIncludedConfigurationNames
    }
    public String getDefaultProjectVersionScheme() {
        return defaultProjectVersionScheme?.trim()
    }
    public String getDefaultProjectVersionText() {
        return defaultProjectVersionText?.trim()
    }
    public String getDefaultProjectVersionTimeformat() {
        return defaultProjectVersionTimeformat?.trim()
    }
    public String getAggregateBomName() {
        return aggregateBomName?.trim()
    }
    public String getCondaPath() {
        return condaPath?.trim()
    }
    public String getCondaEnvironmentName() {
        return condaEnvironmentName?.trim()
    }
    public Boolean getRiskReportPdf() {
        return riskReportPdf
    }
    public String getRiskReportPdfOutputDirectory() {
        return riskReportPdfOutputDirectory
    }
    public Boolean getNoticesReport() {
        return noticesReport
    }
    public String getNoticesReportOutputDirectory() {
        return noticesReportOutputDirectory
    }
    public String getDockerInspectorAirGapPath() {
        return getInspectorAirGapPath(dockerInspectorAirGapPath, DOCKER)
    }
    public String getGradleInspectorAirGapPath() {
        return getInspectorAirGapPath(gradleInspectorAirGapPath, GRADLE)
    }
    public String getNugetInspectorAirGapPath() {
        return getInspectorAirGapPath(nugetInspectorAirGapPath, NUGET)
    }
    public String getNugetPackagesRepoUrl() {
        return nugetPackagesRepoUrl?.trim()
    }
    public String getGradleInspectorRepositoryUrl() {
        return gradleInspectorRepositoryUrl?.trim()
    }
    public String getHexRebar3Path() {
        return hexRebar3Path
    }

    //properties end

}
