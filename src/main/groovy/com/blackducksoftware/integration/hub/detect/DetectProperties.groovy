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
    @ValueDescription(description="If true the bdio files will be deleted after upload", defaultValue="true")
    @Value('${detect.cleanup.bdio.files}')
    Boolean cleanupBdioFiles

    @ValueDescription(description="URL of the Hub server")
    @Value('${detect.hub.url}')
    String hubUrl

    @ValueDescription(description="Time to wait for rest connections to complete", defaultValue="120")
    @Value('${detect.hub.timeout}')
    Integer hubTimeout

    @ValueDescription(description="Hub username")
    @Value('${detect.hub.username}')
    String hubUsername

    @ValueDescription(description="Hub password")
    @Value('${detect.hub.password}')
    String hubPassword

    @ValueDescription(description="Proxy host")
    @Value('${detect.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(description="Proxy port")
    @Value('${detect.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(description="Proxy username")
    @Value('${detect.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(description="Proxy password")
    @Value('${detect.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(description="If true the Hub https certificate will be automatically imported", defaultValue="false")
    @Value('${detect.hub.auto.import.cert}')
    Boolean hubAutoImportCertificate

    @ValueDescription(description = "Source paths to inspect")
    @Value('${detect.source.paths}')
    String[] sourcePaths

    @ValueDescription(description = "Output path")
    @Value('${detect.output.path}')
    String outputDirectoryPath

    @ValueDescription(description = "Depth from source paths to search for files.", defaultValue="10")
    @Value('${detect.search.depth}')
    Integer searchDepth

    @ValueDescription(description = "By default, all tools will be active. If you wish to limit the tools used, specify the ones to use here.")
    @Value('${detect.bom.tool.type.override}')
    String bomToolTypeOverride

    @ValueDescription(description = "An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
    @Value('${detect.project.name}')
    String projectName

    @ValueDescription(description = "An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
    @Value('${detect.project.version.name}')
    String projectVersionName

    @ValueDescription(description="Version of the Gradle Inspector", defaultValue="0.0.6")
    @Value('${detect.gradle.inspector.version}')
    String gradleInspectorVersion

    @ValueDescription(description="Gradle build command", defaultValue="dependencies")
    @Value('${detect.gradle.build.command}')
    String gradleBuildCommand

    @ValueDescription(description="The names of the dependency configurations to exclude")
    @Value('${detect.gradle.excluded.configurations}')
    String gradleExcludedConfigurationNames

    @ValueDescription( description="The names of the dependency configurations to include")
    @Value('${detect.gradle.included.configurations}')
    String gradleIncludedConfigurationNames

    @ValueDescription(description="The names of the projects to exclude")
    @Value('${detect.gradle.excluded.projects}')
    String gradleExcludedProjectNames

    @ValueDescription(description="The names of the projects to include")
    @Value('${detect.gradle.included.projects}')
    String gradleIncludedProjectNames

    @ValueDescription(description="Name of the Nuget Inspector", defaultValue="IntegrationNugetInspector")
    @Value('${detect.nuget.inspector.name}')
    String nugetInspectorPackageName

    @ValueDescription(description="Version of the Nuget Inspector", defaultValue="0.0.3-alpha")
    @Value('${detect.nuget.inspector.version}')
    String nugetInspectorPackageVersion

    @ValueDescription(description="The names of the projects in a solution to exclude")
    @Value('${detect.nuget.excluded.modules}')
    String nugetInspectorExcludedModules

    @ValueDescription(description="If true errors will be logged and then ignored.", defaultValue="false")
    @Value('${detect.nuget.ignore.failure}')
    Boolean nugetInspectorIgnoreFailure

    @ValueDescription(description="If true all maven projects will be aggregated into a single bom", defaultValue="true")
    @Value('${detect.maven.aggregate}')
    Boolean mavenAggregateBom

    @ValueDescription(description="The name of the dependency scope to include")
    @Value('${detect.maven.scope}')
    String mavenScope

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${detect.gradle.path}')
    String gradlePath

    @ValueDescription(description="The path of the Maven executable")
    @Value('${detect.maven.path}')
    String mavenPath

    @ValueDescription(description="If true all nuget projects will be aggregated into a single bom", defaultValue="false")
    @Value('${detect.nuget.aggregate}')
    Boolean nugetAggregateBom

    @ValueDescription(description="The path of the Nuget executable")
    @Value('${detect.nuget.path}')
    String nugetPath

    @ValueDescription(description="Override for pip inspector to find your project")
    @Value('${detect.pip.project.name}')
    String pipProjectName

    @ValueDescription(description="If true creates a temporary Python virtual environment", defaultValue="true")
    @Value('${detect.pip.create.virtual.env}')
    Boolean createVirtualEnv

    @ValueDescription(description="If true will use pip3 if available on class path", defaultValue="false")
    @Value('${detect.pip.pip3}')
    Boolean pipThreeOverride

    @ValueDescription(description="The path of the Python executable")
    @Value('${detect.python.path}')
    String pythonPath

    @ValueDescription(description="The path of the Pip executable")
    @Value('${detect.pip.path}')
    String pipPath
	
	@ValueDescription(description="The path of the Npm executable")
	@Value('${detect.npm.path}')
	String npmPath

    @ValueDescription(description="The path to a user's virtual environment")
    @Value('${detect.pip.virtualEnv.path}')
    String virtualEnvPath

    @ValueDescription(description="The path of the requirements.txt file")
    @Value('${detect.pip.requirements.path}')
    String requirementsFilePath

    @ValueDescription(description="Path of the GoDep executable")
    @Value('${detect.godep.path}')
    String godepPath

    @ValueDescription(description="If true all Go results will be aggregated into a single bom", defaultValue="true")
    @Value('${detect.go.aggregate}')
    Boolean goAggregate

    @ValueDescription(description="Path of the docker executable")
    @Value('${detect.docker.path}')
    String dockerPath

    @ValueDescription(description="This is used to override using the hosted script by github url. You can provide your own script at this path.")
    @Value('${detect.docker.inspector.path}')
    String dockerInspectorPath

    @ValueDescription(description="Version of the Hub Docker Inspector to use", defaultValue="0.0.4-SNAPSHOT")
    @Value('${detect.docker.inspector.version}')
    String dockerInspectorVersion

    @ValueDescription(description="Where the Hub Docker Inspector should be installed - will default to a 'docker-install' directory in the outputDirectoryPath")
    @Value('${detect.docker.install.path}')
    String dockerInstallPath

    @ValueDescription(description="Where the Hub Docker Inspector will put the files it needs to do its processing - this directory could be cleared by the inspector, so it should not be shared by others - will default to 'sandbox' directory in the dockerInstallPath")
    @Value('${detect.docker.sandbox.path}')
    String dockerSandboxPath

    @ValueDescription(description="A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.")
    @Value('${detect.docker.tar}')
    String dockerTar

    @ValueDescription(description="The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.")
    @Value('${detect.docker.image}')
    String dockerImage

    @ValueDescription(description="Path of the bash executable")
    @Value('${detect.bash.path}')
    String bashPath

    @ValueDescription(description="The logging level of Detect (ALL|TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)", defaultValue='INFO')
    @Value('${logging.level.com.blackducksoftware.integration}')
    String loggingLevel
}
