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

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.help.ValueDescription

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectProperties {
    private static final String GROUP_HUB_CONFIGURATION = 'hub configuration'
    private static final String GROUP_LOGGING = 'logging'
    private static final String GROUP_CLEANUP = 'cleanup'
    private static final String GROUP_PATHS = 'paths'
    private static final String GROUP_BOMTOOL = 'bomtool'
    private static final String GROUP_CONDA = 'conda'
    private static final String GROUP_CPAN = 'cpan'
    private static final String GROUP_DOCKER = 'docker'
    private static final String GROUP_GO = 'go'
    private static final String GROUP_GRADLE = 'gradle'
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

    @ValueDescription(description="If true, the default behavior of printing your configuration properties at startup will be suppressed.", defaultValue="false", group=DetectProperties.GROUP_LOGGING)
    @Value('${detect.suppress.configuration.output}')
    Boolean suppressConfigurationOutput

    @ValueDescription(description="If true, the default behavior of printing the Detect Results will be suppressed.", defaultValue="false", group=DetectProperties.GROUP_LOGGING)
    @Value('${detect.suppress.results.output}')
    Boolean suppressResultsOutput

    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true", group=DetectProperties.GROUP_CLEANUP)
    @Value('${detect.cleanup.bdio.files}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="Test the connection to the Hub with the current configuration", defaultValue="false", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${detect.test.connection}')
    Boolean testConnection

    @ValueDescription(description="Timeout for response from the hub regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.", defaultValue="300000", group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.api.timeout}')
    Long apiTimeout

    @ValueDescription(description="URL of the Hub server", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.timeout}')
    Integer hubTimeout

    @ValueDescription(description="Hub username", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.username}')
    String hubUsername

    @ValueDescription(description="Hub password", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.password}')
    String hubPassword

    @ValueDescription(description="Proxy host", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(description="Proxy port", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(description="Proxy username", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true, automatically trust the certificate for the current run of Detect only", defaultValue="false", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.trust.cert}')
    Boolean hubTrustCertificate

    @ValueDescription(description="This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.", defaultValue="false", group=DetectProperties.GROUP_HUB_CONFIGURATION)
    @Value('${blackduck.hub.offline.mode}')
    Boolean hubOfflineMode

    @ValueDescription(description = "Source path to inspect", group=DetectProperties.GROUP_PATHS)
    @Value('${detect.source.path}')
    String sourcePath

    @ValueDescription(description = "Output path", group=DetectProperties.GROUP_PATHS)
    @Value('${detect.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue="3", group=DetectProperties.GROUP_PATHS)
    @Value('${detect.search.depth}')
    Integer searchDepth

    @ValueDescription(description = "By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.", group=DetectProperties.GROUP_BOMTOOL)
    @Value('${detect.excluded.bom.tool.types}')
    String excludedBomToolTypes

    @ValueDescription(description = "By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", group=DetectProperties.GROUP_BOMTOOL)
    @Value('${detect.included.bom.tool.types}')
    String includedBomToolTypes

    @ValueDescription(description = "An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.", group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.name}')
    String projectName

    @ValueDescription(description = "An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.", group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.name}')
    String projectVersionName

    @ValueDescription(description = "A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.", defaultValue='', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.codelocation.prefix}')
    String projectCodeLocationPrefix

    @ValueDescription(description = "An override for the Project level matches.", defaultValue="true", group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.level.adjustments}')
    String projectLevelMatchAdjustments

    @ValueDescription(description = "An override for the Project Version phase.", defaultValue="Development",  group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.phase}')
    String projectVersionPhase

    @ValueDescription(description = "An override for the Project Version distribution", defaultValue="External",  group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.project.version.distribution}')
    String projectVersionDistribution

    @ValueDescription(description = "Set to true if you would like a policy check from the hub for your project. False by default", defaultValue="false", group=DetectProperties.GROUP_POLICY_CHECK)
    @Value('${detect.policy.check}')
    Boolean policyCheck

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="0.2.2", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", defaultValue="dependencies", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.build.command}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.excluded.configurations}')
    String gradleExcludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.included.configurations}')
    String gradleIncludedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.excluded.projects}')
    String gradleExcludedProjectNames

    @ValueDescription(description="The names of the projects to include", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.included.projects}')
    String gradleIncludedProjectNames

    @ValueDescription(description="Set this to false if you do not want the 'blackduck' directory in your build directory to be deleted.", defaultValue="true", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.cleanup.build.blackduck.directory}')
    Boolean gradleCleanupBuildBlackduckDirectory

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.inspector.name}')
    String nugetInspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="2.1.2", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.inspector.version}')
    String nugetInspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.excluded.modules}')
    String nugetInspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.ignore.failure}')
    Boolean nugetInspectorIgnoreFailure

    @ValueDescription(description="The name of the dependency scope to include", group=DetectProperties.GROUP_MAVEN)
    @Value('${detect.maven.scope}')
    String mavenScope

    @ValueDescription(description="Maven build command", defaultValue="dependency:tree", group=DetectProperties.GROUP_MAVEN)
    @Value('${detect.maven.build.command}')
    String mavenBuildCommand

    @ValueDescription(description="Path of the Gradle executable", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable", group=DetectProperties.GROUP_MAVEN)
    @Value('${detect.maven.path}')
    String mavenPath

    @ValueDescription(description="The names of the module to exclude", group=DetectProperties.GROUP_MAVEN)
    @Value('${detect.maven.excluded.modules}')
    String mavenExcludedModuleNames

    @ValueDescription( description="The names of the module to include", group=DetectProperties.GROUP_MAVEN)
    @Value('${detect.maven.included.modules}')
    String mavenIncludedModuleNames

    @ValueDescription(description="The path of the Nuget executable", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.path}')
    String nugetPath

    @ValueDescription(description="Override for pip inspector to find your project", group=DetectProperties.GROUP_PIP)
    @Value('${detect.pip.project.name}')
    String pipProjectName

    @ValueDescription(description="If true will use Python 3 if available on class path", defaultValue="false", group=DetectProperties.GROUP_PYTHON)
    @Value('${detect.python.python3}')
    Boolean pythonThreeOverride

    @ValueDescription(description="The path of the Python executable", group=DetectProperties.GROUP_PYTHON)
    @Value('${detect.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Npm executable", group=DetectProperties.GROUP_NPM)
    @Value('${detect.npm.path}')
    String npmPath

    @ValueDescription(description="Set this value to false if you would like to exclude your dev dependencies when ran", defaultValue='true', group=DetectProperties.GROUP_NPM)
    @Value('${detect.npm.include.dev.dependencies}')
    String npmIncludeDevDependencies

    @ValueDescription(description="The path of the node executable that is used by Npm", group=DetectProperties.GROUP_NPM)
    @Value('${detect.npm.node.path}')
    String npmNodePath

    @ValueDescription(description="The path of the pear executable", group=DetectProperties.GROUP_PEAR)
    @Value('${detect.pear.path}')
    String pearPath

    @ValueDescription(description="Set to true if you would like to include only required packages", defaultValue='false', group=DetectProperties.GROUP_PEAR)
    @Value('${detect.pear.only.required.deps}')
    Boolean pearOnlyRequiredDependencies

    @ValueDescription(description="The path of the requirements.txt file", group=DetectProperties.GROUP_PIP)
    @Value('${detect.pip.requirements.path}')
    String requirementsFilePath

    @ValueDescription(description="Path of the Go Dep executable", group=DetectProperties.GROUP_GO)
    @Value('${detect.go.dep.path}')
    String goDepPath

    @ValueDescription(description="Path of the docker executable", group=DetectProperties.GROUP_DOCKER)
    @Value('${detect.docker.path}')
    String dockerPath

    @ValueDescription(description="This is used to override using the hosted script by github url. You can provide your own script at this path.", group=DetectProperties.GROUP_DOCKER)
    @Value('${detect.docker.inspector.path}')
    String dockerInspectorPath

    @ValueDescription(description="Version of the Hub Docker Inspector to use", defaultValue="latest", group=DetectProperties.GROUP_DOCKER)
    @Value('${detect.docker.inspector.version}')
    String dockerInspectorVersion

    @ValueDescription(description="A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.", group=DetectProperties.GROUP_DOCKER)
    @Value('${detect.docker.tar}')
    String dockerTar

    @ValueDescription(description="The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.", group=DetectProperties.GROUP_DOCKER)
    @Value('${detect.docker.image}')
    String dockerImage

    @ValueDescription(description="Path of the bash executable", group=DetectProperties.GROUP_PATHS)
    @Value('${detect.bash.path}')
    String bashPath

    @ValueDescription(description="The logging level of Detect (ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)", defaultValue='INFO', group=DetectProperties.GROUP_LOGGING)
    @Value('${logging.level.com.blackducksoftware.integration}')
    String loggingLevel

    @ValueDescription(description="Detect creates temporary files in the output directory. If set to true this will clean them up after execution", defaultValue='true', group=DetectProperties.GROUP_CLEANUP)
    @Value('${detect.cleanup.bom.tool.files}')
    Boolean cleanupBomToolFiles

    @ValueDescription(description="If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.", defaultValue='false', group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.dry.run}')
    Boolean hubSignatureScannerDryRun

    @ValueDescription(description="Enables you to specify sub-directories to exclude from scans", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.exclusion.patterns}')
    String[] hubSignatureScannerExclusionPatterns

    @ValueDescription(description="These paths and only these paths will be scanned.", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.paths}')
    String[] hubSignatureScannerPaths

    @ValueDescription(description="The relative paths of directories to be excluded from scan registration", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.relative.paths.to.exclude}')
    String[] hubSignatureScannerRelativePathsToExclude

    @ValueDescription(description="The memory for the scanner to use.", defaultValue="4096", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.memory}')
    Integer hubSignatureScannerMemory

    @ValueDescription(description="Set to true to disable the Hub Signature Scanner.", defaultValue="false", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.disabled}')
    Boolean hubSignatureScannerDisabled

    @ValueDescription(description="To use a local signature scanner, set its location with this property. This will be the path that contains the 'Hub_Scan_Installation' directory where the signature scanner was unzipped.", group=DetectProperties.GROUP_SIGNATURE_SCANNER)
    @Value('${detect.hub.signature.scanner.offline.local.path}')
    String hubSignatureScannerOfflineLocalPath

    @ValueDescription(description="Set this value to false if you would like to exclude your dev requires dependencies when ran", defaultValue='true', group=DetectProperties.GROUP_PACKAGIST)
    @Value('${detect.packagist.include.dev.dependencies}')
    Boolean packagistIncludeDevDependencies

    @ValueDescription(description="The path of the perl executable", group=DetectProperties.GROUP_CPAN)
    @Value('${detect.perl.path}')
    String perlPath

    @ValueDescription(description="The path of the cpan executable", group=DetectProperties.GROUP_CPAN)
    @Value('${detect.cpan.path}')
    String cpanPath

    @ValueDescription(description="The path of the cpanm executable", group=DetectProperties.GROUP_CPAN)
    @Value('${detect.cpanm.path}')
    String cpanmPath

    @ValueDescription(description="The names of the sbt configurations to exclude", group=DetectProperties.GROUP_SBT)
    @Value('${detect.sbt.excluded.configurations}')
    String sbtExcludedConfigurationNames

    @ValueDescription( description="The names of the sbt configurations to include", group=DetectProperties.GROUP_SBT)
    @Value('${detect.sbt.included.configurations}')
    String sbtIncludedConfigurationNames

    @ValueDescription(description="The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'", defaultValue='text', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.scheme}')
    String defaultProjectVersionScheme

    @ValueDescription(description="The text to use as the default project version", defaultValue='Detect Unknown Version', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.text}')
    String defaultProjectVersionText

    @ValueDescription(description="The timestamp format to use as the default project version", defaultValue='yyyy-MM-dd\'T\'HH:mm:ss.SSS', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.default.project.version.timeformat}')
    String defaultProjectVersionTimeformat

    @ValueDescription(description="If set, this will aggregate all the BOMs to create a single BDIO file with the name provided. For Co-Pilot use only", group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.bom.aggregate.name}')
    String aggregateBomName

    @ValueDescription (description="When set to true, a Black Duck risk report in PDF form will be created", defaultValue='false', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.risk.report.pdf}')
    Boolean riskReportPdf

    @ValueDescription (description="The output directory for risk report in PDF. Default is the source directory", defaultValue='.', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.risk.report.pdf.path}')
    String riskReportPdfOutputDirectory

    @ValueDescription (description="When set to true, a Black Duck notices report in text form will be created in your source directory", defaultValue='false', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.notices.report}')
    Boolean noticesReport

    @ValueDescription (description="The output directory for notices report. Default is the source directory", defaultValue='.', group=DetectProperties.GROUP_PROJECT_INFO)
    @Value('${detect.notices.report.path}')
    String noticesReportOutputDirectory

    @ValueDescription(description="The path of the conda executable", group=DetectProperties.GROUP_CONDA)
    @Value('${detect.conda.path}')
    String condaPath

    @ValueDescription(description="The name of the anaconda environment used by your project", group=DetectProperties.GROUP_CONDA)
    @Value('${detect.conda.environment.name}')
    String condaEnvironmentName

    @ValueDescription(description="The path to the directory containing the air gap dependencies for the gradle inspector", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.air.gap.path}')
    String gradleInspectorAirGapPath

    @ValueDescription(description="The path to the nuget inspector nupkg", group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.inspector.air.gap.path}')
    String nugetInspectorAirGapPath

    @ValueDescription(description="The source for nuget packages", defaultValue='https://www.nuget.org/api/v2/', group=DetectProperties.GROUP_NUGET)
    @Value('${detect.nuget.packages.repo.url}')
    String nugetPackagesRepoUrl

    @ValueDescription(description="The respository gradle should use to look for the gradle inspector", group=DetectProperties.GROUP_GRADLE)
    @Value('${detect.gradle.inspector.repository.url}')
    String gradleInspectorRepositoryUrl
}

