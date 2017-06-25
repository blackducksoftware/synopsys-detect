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

@Component
class DetectProperties {
    @ValueDescription(description="If true, the default behavior of printing your configuration properties at startup will be suppressed.", defaultValue="false", group="logging")
    @Value('${detect.suppress.configuration.output}')
    Boolean suppressConfigurationOutput

    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true", group="cleanup")
    @Value('${detect.cleanup.bdio.files}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="URL of the Hub server", group="hub configuration")
    @Value('${detect.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120", group="hub configuration")
    @Value('${detect.hub.timeout}')
    Integer hubTimeout

    @ValueDescription(description="Hub username", group="hub configuration")
    @Value('${detect.hub.username}')
    String hubUsername

    @ValueDescription(description="Hub password", group="hub configuration")
    @Value('${detect.hub.password}')
    String hubPassword

    @ValueDescription(description="Proxy host", group="hub configuration")
    @Value('${detect.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(description="Proxy port", group="hub configuration")
    @Value('${detect.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(description="Proxy username", group="hub configuration")
    @Value('${detect.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password", group="hub configuration")
    @Value('${detect.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true the Hub https certificate will be automatically imported", defaultValue="false", group="hub configuration")
    @Value('${detect.hub.auto.import.cert}')
    Boolean hubAutoImportCertificate

    @ValueDescription(description = "Source paths to inspect", group="paths")
    @Value('${detect.source.paths}')
    String[] sourcePaths

    @ValueDescription(description = "Output path", group="paths")
    @Value('${detect.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue="10", group="paths")
    @Value('${detect.search.depth}')
    Integer searchDepth

    @ValueDescription(description = "By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.", group="bomtool")
    @Value('${detect.excluded.bom.tool.types}')
    String excludedBomToolTypes

    @ValueDescription(description = "By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", group="bomtool")
    @Value('${detect.included.bom.tool.types}')
    String includedBomToolTypes

    @ValueDescription(description = "An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.", group="project info")
    @Value('${detect.project.name}')
    String projectName

    @ValueDescription(description = "An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.", group="project info")
    @Value('${detect.project.version.name}')
    String projectVersionName

    @ValueDescription(description = "Code location to use when sending data to the Hub. If the code location already exists, it will use that pre-existing location", group="project info")
    @Value('${detect.project.code.location.name}')
    String projectCodeLocationName

    @ValueDescription(description = "Set to true if you would like a policy check from the hub for your project. False by default", defaultValue="false", group="policy check")
    @Value('${detect.policy.check}')
    String policyCheck

    @ValueDescription(description="Timeout for the Hub's policy check response. When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.", defaultValue="300000", group="policy check")
    @Value('${detect.policy.check.timeout}')
    Integer policyCheckTimeout

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="0.0.7", group="gradle bomtool")
    @Value('${detect.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", defaultValue="dependencies", group="gradle bomtool")
    @Value('${detect.gradle.build.command}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude", group="gradle bomtool")
    @Value('${detect.gradle.excluded.configurations}')
    String gradleExcludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include", group="gradle bomtool")
    @Value('${detect.gradle.included.configurations}')
    String gradleIncludedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude", group="gradle bomtool")
    @Value('${detect.gradle.excluded.projects}')
    String gradleExcludedProjectNames

    @ValueDescription(description="The names of the projects to include", group="gradle bomtool")
    @Value('${detect.gradle.included.projects}')
    String gradleIncludedProjectNames

    @ValueDescription(description="Set this to false if you do not want the 'blackduck' directory in your build directory to be deleted.", defaultValue="true", group="gradle bomtool")
    @Value('${detect.gradle.cleanup.build.blackduck.directory}')
    Boolean gradleCleanupBuildBlackduckDirectory

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector", group="nuget bomtool")
    @Value('${detect.nuget.inspector.name}')
    String nugetInspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="1.0.0")
    @Value('${detect.nuget.inspector.version}')
    String nugetInspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude", group="nuget bomtool")
    @Value('${detect.nuget.excluded.modules}')
    String nugetInspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false", group="nuget bomtool")
    @Value('${detect.nuget.ignore.failure}')
    Boolean nugetInspectorIgnoreFailure

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom", defaultValue="true", group="maven bomtool")
    @Value('${detect.maven.aggregate}')
    Boolean mavenAggregateBom

    @ValueDescription(description="The name of the dependency scope to include", group="maven bomtool")
    @Value('${detect.maven.scope}')
    String mavenScope

    @ValueDescription(description="Path of the Gradle executable", group="gradle bomtool")
    @Value('${detect.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable", group="maven bomtool")
    @Value('${detect.maven.path}')
    String mavenPath

    @ValueDescription(description="If true all nuget projects will be aggregated into a single bom", defaultValue="false", group="nuget bomtool")
    @Value('${detect.nuget.aggregate}')
    Boolean nugetAggregateBom

    @ValueDescription(description="The path of the Nuget executable", group="nuget bomtool")
    @Value('${detect.nuget.path}')
    String nugetPath

    @ValueDescription(description="Override for pip inspector to find your project", group="pip bomtool")
    @Value('${detect.pip.project.name}')
    String pipProjectName

    @ValueDescription(description="If true creates a temporary Python virtual environment", defaultValue="true", group="pip bomtool")
    @Value('${detect.pip.create.virtual.env}')
    Boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path", defaultValue="false", group="pip bomtool")
    @Value('${detect.pip.pip3}')
    Boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable", group="python bomtool")
    @Value('${detect.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable", group="pip bomtool")
    @Value('${detect.pip.path}')
    String pipPath

    @ValueDescription(description="The path of the Npm executable", group="npm bomtool")
    @Value('${detect.npm.path}')
    String npmPath

    @ValueDescription(description="The path to a user's virtual environment", group="pip bomtool")
    @Value('${detect.pip.virtualEnv.path}')
    String virtualEnvPath

    @ValueDescription(description="The path of the requirements.txt file", group="pip bomtool")
    @Value('${detect.pip.requirements.path}')
    String requirementsFilePath

    @ValueDescription(description="Path of the Go Dep executable", group="go bomtool")
    @Value('${detect.go.dep.path}')
    String goDepPath

    @ValueDescription(description="Path of the docker executable", group="docker bomtool")
    @Value('${detect.docker.path}')
    String dockerPath

    @ValueDescription(description="This is used to override using the hosted script by github url. You can provide your own script at this path.", group="docker bomtool")
    @Value('${detect.docker.inspector.path}')
    String dockerInspectorPath

    @ValueDescription(description="Version of the Hub Docker Inspector to use", defaultValue="0.1.1", group="docker bomtool")
    @Value('${detect.docker.inspector.version}')
    String dockerInspectorVersion

    @ValueDescription(description="Where the Hub Docker Inspector should be installed - will default to a 'docker-install' directory in the outputDirectoryPath", group="docker bomtool")
    @Value('${detect.docker.install.path}')
    String dockerInstallPath

    @ValueDescription(description="Where the Hub Docker Inspector will put the files it needs to do its processing - this directory could be cleared by the inspector, so it should not be shared by others - will default to 'sandbox' directory in the dockerInstallPath", group="docker bomtool")
    @Value('${detect.docker.sandbox.path}')
    String dockerSandboxPath

    @ValueDescription(description="A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.", group="docker bomtool")
    @Value('${detect.docker.tar}')
    String dockerTar

    @ValueDescription(description="The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.", group="docker bomtool")
    @Value('${detect.docker.image}')
    String dockerImage

    @ValueDescription(description="Path of the bash executable", group="paths")
    @Value('${detect.bash.path}')
    String bashPath

    @ValueDescription(description="The logging level of Detect (ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)", defaultValue='INFO', group="logging")
    @Value('${logging.level.com.blackducksoftware.integration}')
    String loggingLevel

    @ValueDescription(description="Detect creates temporary files in the output directory. If set to true this will clean them up after execution", defaultValue='true', group="cleanup")
    @Value('${detect.cleanup.bom.tool.files}')
    Boolean cleanupBomToolFiles

    @ValueDescription(description="If detect.blackduck.signature.scanner.paths is not set, these directories will be appended to each source path and will then be scanned.", defaultValue="/target,/build,/bin", group="signature scanner")
    @Value('${detect.blackduck.signature.scanner.default.directories}')
    String[] blackDuckSignatureScannerDefaultDirectories

    @ValueDescription(description="These paths and only these paths will be scanned.", group="signature scanner")
    @Value('${detect.blackduck.signature.scanner.paths}')
    String[] blackDuckSignatureScannerPaths

    @ValueDescription(description="Timeout for the signature scanning to complete.", defaultValue="300000", group="signature scanner")
    @Value('${detect.blackduck.signature.scanner.timeout}')
    Integer blackDuckSignatureScannerTimeout
}
