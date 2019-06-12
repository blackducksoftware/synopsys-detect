/**
 * detect-configuration
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
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.detect.DetectMajorVersion;
import com.synopsys.integration.detect.help.AcceptableValues;
import com.synopsys.integration.detect.help.DetectDeprecation;
import com.synopsys.integration.detect.property.PropertyType;

public enum DetectProperty {

    // [YAML HELP] The API token used to authenticate with the Black Duck Server.
    BLACKDUCK_API_TOKEN("blackduck.api.token", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.
    BLACKDUCK_OFFLINE_MODE("blackduck.offline.mode", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] Black Duck password.
    BLACKDUCK_PASSWORD("blackduck.password", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Hostname for proxy server.
    BLACKDUCK_PROXY_HOST("blackduck.proxy.host", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma separated list of host patterns that should not use the proxy.
    BLACKDUCK_PROXY_IGNORED_HOSTS("blackduck.proxy.ignored.hosts", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] NTLM Proxy domain.
    BLACKDUCK_PROXY_NTLM_DOMAIN("blackduck.proxy.ntlm.domain", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] NTLM Proxy workstation.
    BLACKDUCK_PROXY_NTLM_WORKSTATION("blackduck.proxy.ntlm.workstation", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Proxy password.
    BLACKDUCK_PROXY_PASSWORD("blackduck.proxy.password", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Proxy port.
    BLACKDUCK_PROXY_PORT("blackduck.proxy.port", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Proxy username.
    BLACKDUCK_PROXY_USERNAME("blackduck.proxy.username", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The time to wait for network connections to complete (in seconds).
    BLACKDUCK_TIMEOUT("blackduck.timeout", PropertyType.INTEGER, PropertyAuthority.None, "120"),

    // [YAML HELP] If true, automatically trust the certificate for the current run of Detect only.
    BLACKDUCK_TRUST_CERT("blackduck.trust.cert", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] URL of the Black Duck server.
    BLACKDUCK_URL("blackduck.url", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Black Duck username.
    BLACKDUCK_USERNAME("blackduck.username", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Path to the Bash executable.
    DETECT_BASH_PATH("detect.bash.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Bazel executable.
    DETECT_BAZEL_PATH("detect.bazel.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The Bazel target (for example, //foo:foolib) for which dependencies are collected. For Detect to run Bazel, this property must be set.
    DETECT_BAZEL_TARGET("detect.bazel.target", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to a file containing a list of BazelExternalIdExtractionXPathRule objects in json for overriding the default behavior).
    DETECT_BAZEL_ADVANCED_RULES_PATH("detect.bazel.advanced.rules.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the output directory for all BDIO files.
    DETECT_BDIO_OUTPUT_PATH("detect.bdio.output.path", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    // [YAML HELP] The path to a binary file to scan.
    DETECT_BINARY_SCAN_FILE("detect.binary.scan.file.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The name of the build environment init script.
    DETECT_BITBAKE_BUILD_ENV_NAME("detect.bitbake.build.env.name", PropertyType.STRING, PropertyAuthority.None, "oe-init-build-env"),

    // [YAML HELP] The reference implementation of the Yocto project. These characters are stripped from the discovered target architecture.
    DETECT_BITBAKE_REFERENCE_IMPL("detect.bitbake.reference.impl", PropertyType.STRING, PropertyAuthority.None, "-poky-linux"),

    // [YAML HELP] A comma-separated list of package names from which dependencies are extracted.
    DETECT_BITBAKE_PACKAGE_NAMES("detect.bitbake.package.names", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    // [YAML HELP] Additional arguments to use when running the Black Duck signature scanner.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS("detect.blackduck.signature.scanner.arguments", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to true, the signature scanner results are not uploaded to Black Duck, and the scanner results are written to disk.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN("detect.blackduck.signature.scanner.dry.run", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] A comma-separated list of directory name patterns for which Detect searches and adds to the signature scanner --exclude flag values.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS("detect.blackduck.signature.scanner.exclusion.name.patterns", PropertyType.STRING_ARRAY, PropertyAuthority.None, "node_modules"),

    // [YAML HELP] Enables you to adjust the depth to which Detect will search when creating signature scanner exclusion patterns.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH("detect.blackduck.signature.scanner.exclusion.pattern.search.depth", PropertyType.INTEGER, PropertyAuthority.None, "4"),

    // [YAML HELP] A comma-separated list of values to be used with the Signature Scanner --exclude flag.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS("detect.blackduck.signature.scanner.exclusion.patterns", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    // [YAML HELP] If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Black Duck's urls for different operating systems.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL("detect.blackduck.signature.scanner.host.url", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH("detect.blackduck.signature.scanner.local.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The memory for the scanner to use.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY("detect.blackduck.signature.scanner.memory", PropertyType.INTEGER, PropertyAuthority.None, "4096"),

    // [YAML HELP] To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH("detect.blackduck.signature.scanner.offline.local.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS("detect.blackduck.signature.scanner.parallel.processors", PropertyType.INTEGER, PropertyAuthority.None, "1"),

    // [YAML HELP] These paths and only these paths will be scanned.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS("detect.blackduck.signature.scanner.paths", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @AcceptableValues(value = { "SNIPPET_MATCHING", "SNIPPET_MATCHING_ONLY", "FULL_SNIPPET_MATCHING", "FULL_SNIPPET_MATCHING_ONLY", "NONE" }, strict = true)
    // [YAML HELP] Use this value to enable the various snippet scanning modes. For a full explanation, please refer to the Black Duck Signature Scanner documentation.
        DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING("detect.blackduck.signature.scanner.snippet.matching", PropertyType.STRING, PropertyAuthority.None, "NONE"),

    // [YAML HELP] If set to true, the signature scanner will, if supported by your Black Duck version, upload source code to Black Duck.
    DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE("detect.blackduck.signature.scanner.upload.source.mode", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.
    DETECT_BOM_AGGREGATE_NAME("detect.bom.aggregate.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to true, only Detector's capable of running without a build will be run.
    DETECT_BUILDLESS("detect.detector.buildless", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] If true, the files created by Detect will be cleaned up.
    DETECT_CLEANUP("detect.cleanup", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] The name of the project version to clone this project version from. Respects the Clone Categories as set on the Black Duck server (project settings: Select the attributes you\u2019d like to clone for any new versions of this project).
    DETECT_CLONE_PROJECT_VERSION_NAME("detect.clone.project.version.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] An override for the name Detect will use for the scan file it creates. If supplied and multiple scans are found, Detect will append an index to each scan name.
    DETECT_CODE_LOCATION_NAME("detect.code.location.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The name of the anaconda environment used by your project.
    DETECT_CONDA_ENVIRONMENT_NAME("detect.conda.environment.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the conda executable.
    DETECT_CONDA_PATH("detect.conda.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the cpan executable.
    DETECT_CPAN_PATH("detect.cpan.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the cpanm executable.
    DETECT_CPANM_PATH("detect.cpanm.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'.
    DETECT_DEFAULT_PROJECT_VERSION_SCHEME("detect.default.project.version.scheme", PropertyType.STRING, PropertyAuthority.None, "text"),

    // [YAML HELP] The text to use as the default project version.
    DETECT_DEFAULT_PROJECT_VERSION_TEXT("detect.default.project.version.text", PropertyType.STRING, PropertyAuthority.None, "Default Detect Version"),

    // [YAML HELP] The timestamp format to use as the default project version.
    DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT("detect.default.project.version.timeformat", PropertyType.STRING, PropertyAuthority.None, "yyyy-MM-dd\\'T\\'HH:mm:ss.SSS"),

    // [YAML HELP] Depth of subdirectories within the source directory to which Detect will search for files that indicate whether a detector applies.
    DETECT_DETECTOR_SEARCH_DEPTH("detect.detector.search.depth", PropertyType.INTEGER, PropertyAuthority.None, "0"),

    // [YAML HELP] If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.
    DETECT_DETECTOR_SEARCH_CONTINUE("detect.detector.search.continue", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] A comma-separated list of directory names to exclude from detector search.
    DETECT_DETECTOR_SEARCH_EXCLUSION("detect.detector.search.exclusion", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of directory name patterns to exclude from detector search.
    DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS("detect.detector.search.exclusion.patterns", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of directory paths to exclude from detector search. (E.g. 'foo/bar/biz' will only exclude the 'biz' directory if the parent directory structure is 'foo/bar/'.)
    DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS("detect.detector.search.exclusion.paths", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    // [YAML HELP] If true, the bom tool search will exclude the default directory names. See the detailed help for more information.
    DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS("detect.detector.search.exclusion.defaults", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] If true, Detect will ignore any products that it cannot connect to.
    DETECT_IGNORE_CONNECTION_FAILURES("detect.ignore.connection.failures", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The Docker image name to inspect. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.
    DETECT_DOCKER_IMAGE("detect.docker.image", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the directory containing the Docker Inspector jar and images.
    DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH("detect.docker.inspector.air.gap.path", PropertyType.STRING, PropertyAuthority.AirGapManager),

    // [YAML HELP] This is used to override using the hosted Docker Inspector .jar file by binary repository url. You can use a local Docker Inspector .jar file at this path.
    DETECT_DOCKER_INSPECTOR_PATH("detect.docker.inspector.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Version of the Docker Inspector to use. By default Detect will attempt to automatically determine the version to use.
    DETECT_DOCKER_INSPECTOR_VERSION("detect.docker.inspector.version", PropertyType.STRING, PropertyAuthority.None, ""),

    // [YAML HELP] Path to the docker executable.
    DETECT_DOCKER_PATH("detect.docker.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to true, Detect will attempt to run the Docker Inspector only if it finds a docker client executable.
    DETECT_DOCKER_PATH_REQUIRED("detect.docker.path.required", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] A saved Docker image - must be a .tar file. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.
    DETECT_DOCKER_TAR("detect.docker.tar", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the dotnet executable.
    DETECT_DOTNET_PATH("detect.dotnet.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] By default, all detectors will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all detectors, specify \"ALL\". Exclusion rules always win.
    DETECT_EXCLUDED_DETECTOR_TYPES("detect.excluded.detector.types", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If true, Detect will always exit with code 0.
    DETECT_FORCE_SUCCESS("detect.force.success", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] Path of the git executable
    DETECT_GIT_PATH("detect.git.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Path to the Godep executable.
    DETECT_GO_DEP_PATH("detect.go.dep.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to true, Detect will attempt to run 'init' and 'ensure' which can modify your development environment.
    DETECT_GO_RUN_DEP_INIT("detect.go.run.dep.init", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] Gradle command line arguments to add to the mvn/mvnw command line.
    DETECT_GRADLE_BUILD_COMMAND("detect.gradle.build.command", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Gradle configurations to exclude.
    DETECT_GRADLE_EXCLUDED_CONFIGURATIONS("detect.gradle.excluded.configurations", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Gradle sub-projects to exclude.
    DETECT_GRADLE_EXCLUDED_PROJECTS("detect.gradle.excluded.projects", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Gradle configurations to include.
    DETECT_GRADLE_INCLUDED_CONFIGURATIONS("detect.gradle.included.configurations", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Maven sub-projects to include.
    DETECT_GRADLE_INCLUDED_PROJECTS("detect.gradle.included.projects", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the directory containing the air gap dependencies for the gradle inspector.
    DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH("detect.gradle.inspector.air.gap.path", PropertyType.STRING, PropertyAuthority.AirGapManager),

    // [YAML HELP] The version of the Gradle Inspector that Detect should use. By default, Detect will try to automatically determine the correct Gradle Inspector version.
    DETECT_GRADLE_INSPECTOR_VERSION("detect.gradle.inspector.version", PropertyType.STRING, PropertyAuthority.None, ""),

    // [YAML HELP] The path to the Gradle executable (gradle or gradlew).
    DETECT_GRADLE_PATH("detect.gradle.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the rebar3 executable.
    DETECT_HEX_REBAR3_PATH("detect.hex.rebar3.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.
    DETECT_INCLUDED_DETECTOR_TYPES("detect.included.detector.types", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Path to the java executable.
    DETECT_JAVA_PATH("detect.java.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Maven command line arguments to add to the mvn/mvnw command line.
    DETECT_MAVEN_BUILD_COMMAND("detect.maven.build.command", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Maven modules (sub-projects) to exclude.
    DETECT_MAVEN_EXCLUDED_MODULES("detect.maven.excluded.modules", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of Maven modules (sub-projects) to include.
    DETECT_MAVEN_INCLUDED_MODULES("detect.maven.included.modules", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Maven executable (mvn or mvnw).
    DETECT_MAVEN_PATH("detect.maven.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The name of a Maven scope. Output will be limited to dependencies with this scope.
    DETECT_MAVEN_SCOPE("detect.maven.scope", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] When set to true, a Black Duck notices report in text form will be created in your source directory.
    DETECT_NOTICES_REPORT("detect.notices.report", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The output directory for notices report. Default is the source directory.
    DETECT_NOTICES_REPORT_PATH("detect.notices.report.path", PropertyType.STRING, PropertyAuthority.None, "."),

    // [YAML HELP] A space-separated list of additional arguments to use when running Detect against an NPM project.
    DETECT_NPM_ARGUMENTS("detect.npm.arguments", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Set this value to false if you would like to exclude your dev dependencies when ran.
    DETECT_NPM_INCLUDE_DEV_DEPENDENCIES("detect.npm.include.dev.dependencies", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] The path to the node executable that is used by Npm.
    DETECT_NPM_NODE_PATH("detect.npm.node.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Npm executable.
    DETECT_NPM_PATH("detect.npm.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Nuget.Config file to supply to the nuget exe.
    DETECT_NUGET_CONFIG_PATH("detect.nuget.config.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The names of the projects in a solution to exclude.
    DETECT_NUGET_EXCLUDED_MODULES("detect.nuget.excluded.modules", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If true errors will be logged and then ignored.
    DETECT_NUGET_IGNORE_FAILURE("detect.nuget.ignore.failure", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The names of the projects in a solution to include (overrides exclude).
    DETECT_NUGET_INCLUDED_MODULES("detect.nuget.included.modules", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the directory containing the nuget inspector nupkg.
    DETECT_NUGET_INSPECTOR_AIR_GAP_PATH("detect.nuget.inspector.air.gap.path", PropertyType.STRING, PropertyAuthority.AirGapManager),

    // [YAML HELP] Version of the Nuget Inspector. By default Detect will communicate with Artifactory.
    DETECT_NUGET_INSPECTOR_VERSION("detect.nuget.inspector.version", PropertyType.STRING, PropertyAuthority.None, ""),

    // [YAML HELP] The source for nuget packages
    DETECT_NUGET_PACKAGES_REPO_URL("detect.nuget.packages.repo.url", PropertyType.STRING_ARRAY, PropertyAuthority.None, "https://api.nuget.org/v3/index.json"),

    // [YAML HELP] The path to the output directory.
    DETECT_OUTPUT_PATH("detect.output.path", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    // [YAML HELP] Set this value to false if you would like to exclude your dev requires dependencies when ran.
    DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES("detect.packagist.include.dev.dependencies", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] Set to true if you would like to include only required packages.
    DETECT_PEAR_ONLY_REQUIRED_DEPS("detect.pear.only.required.deps", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The path to the pear executable.
    DETECT_PEAR_PATH("detect.pear.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the perl executable.
    DETECT_PERL_PATH("detect.perl.path", PropertyType.STRING, PropertyAuthority.None),

    @DetectDeprecation(description = "This property is being removed. Please use --detect.project.name in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)

    // [YAML HELP] The name of your PIP project, to be used if your project's name cannot be correctly inferred from its setup.py file.
        DETECT_PIP_PROJECT_NAME("detect.pip.project.name", PropertyType.STRING, PropertyAuthority.None),

    @DetectDeprecation(description = "This property is being removed. Please use --detect.project.version.name in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)

    // [YAML HELP] The version of your PIP project, to be used if your project's version name cannot be correctly inferred from its setup.py file.
        DETECT_PIP_PROJECT_VERSION_NAME("detect.pip.project.version.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the requirements.txt file.
    DETECT_PIP_REQUIREMENTS_PATH("detect.pip.requirements.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Pipenv executable.
    DETECT_PIPENV_PATH("detect.pipenv.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The url of your polaris instance.
    POLARIS_URL("polaris.url", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The access token for your polaris instance.
    POLARIS_ACCESS_TOKEN("polaris.access.token", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Additional arguments to pass to polaris.
    POLARIS_ARGUMENTS("polaris.arguments", PropertyType.STRING, PropertyAuthority.None),

    @AcceptableValues(value = { "ALL", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL", "UNSPECIFIED" }, caseSensitive = false, strict = false, isCommaSeparatedList = true)
    // [YAML HELP] A comma-separated list of policy violation severities that will fail Detect. If this is not set, Detect will not fail due to policy violations. A value of ALL is equivalent to all of the other possible values except UNSPECIFIED.
        DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES("detect.policy.check.fail.on.severities", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Sets the 'Application ID' project setting.
    DETECT_PROJECT_APPLICATION_ID("detect.project.application.id", PropertyType.STRING, PropertyAuthority.None, null),

    @AcceptableValues(value = { "COMPONENT_DATA", "VULN_DATA" }, caseSensitive = false, strict = false, isCommaSeparatedList = true)
    // [YAML HELP] An override for the Project Clone Categories that are used when cloning a version. If the project already exists, make sure to use --detect.project.version.update to make sure these are set.
        DETECT_PROJECT_CLONE_CATEGORIES("detect.project.clone.categories", PropertyType.STRING_ARRAY, PropertyAuthority.None, "COMPONENT_DATA,VULN_DATA"),

    // [YAML HELP] A prefix to the name of the scans created by Detect. Useful for running against the same projects on multiple machines.
    DETECT_PROJECT_CODELOCATION_PREFIX("detect.project.codelocation.prefix", PropertyType.STRING, PropertyAuthority.None, ""),

    // [YAML HELP] A suffix to the name of the scans created by Detect.
    DETECT_PROJECT_CODELOCATION_SUFFIX("detect.project.codelocation.suffix", PropertyType.STRING, PropertyAuthority.None, ""),

    // [YAML HELP] If set to true, unmaps all other scans mapped to the project version produced by the current run of Detect.
    DETECT_PROJECT_CODELOCATION_UNMAP("detect.project.codelocation.unmap", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] If project description is specified, your project version will be created with this description.
    DETECT_PROJECT_DESCRIPTION("detect.project.description", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] A comma-separated list of names of user groups to add to the project.
    DETECT_PROJECT_USER_GROUPS("detect.project.user.groups", PropertyType.STRING_ARRAY, PropertyAuthority.None, ""),

    // [YAML HELP] The detector that will be used to determine the project name and version when multiple detector types. This property should be used with the detect.project.tool.
    DETECT_PROJECT_DETECTOR("detect.project.detector", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] An override for the Project level matches.
    DETECT_PROJECT_LEVEL_ADJUSTMENTS("detect.project.level.adjustments", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] An override for the name to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.
    DETECT_PROJECT_NAME("detect.project.name", PropertyType.STRING, PropertyAuthority.None),

    @AcceptableValues(value = { "1", "5" }, caseSensitive = false, strict = false)
    // [YAML HELP] If a Black Duck project tier is specified, your project will be created with this tier.
        DETECT_PROJECT_TIER("detect.project.tier", PropertyType.INTEGER, PropertyAuthority.None),

    @AcceptableValues(value = { "DETECTOR", "DOCKER", "BAZEL" }, caseSensitive = true, strict = true, isCommaSeparatedList = true)
    // [YAML HELP] The tool priority for project name and version. The project name and version will be determined by the first tool in this list that provides them.
        DETECT_PROJECT_TOOL("detect.project.tool", PropertyType.STRING, PropertyAuthority.None, "DOCKER,DETECTOR,BAZEL"),

    @AcceptableValues(value = { "EXTERNAL", "SAAS", "INTERNAL", "OPENSOURCE" }, caseSensitive = false, strict = false)
    // [YAML HELP] An override for the Project Version distribution
        DETECT_PROJECT_VERSION_DISTRIBUTION("detect.project.version.distribution", PropertyType.STRING, PropertyAuthority.None, "External"),

    // [YAML HELP] An override for the version to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.
    DETECT_PROJECT_VERSION_NAME("detect.project.version.name", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If a project version nickname is specified, your project version will be created with this nickname.
    DETECT_PROJECT_VERSION_NICKNAME("detect.project.version.nickname", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If project version notes are specified, your project version will be created with these notes.
    DETECT_PROJECT_VERSION_NOTES("detect.project.version.notes", PropertyType.STRING, PropertyAuthority.None),

    @AcceptableValues(value = { "ARCHIVED", "DEPRECATED", "DEVELOPMENT", "PLANNING", "PRERELEASE", "RELEASED" }, caseSensitive = false, strict = false)
    // [YAML HELP] An override for the Project Version phase.
        DETECT_PROJECT_VERSION_PHASE("detect.project.version.phase", PropertyType.STRING, PropertyAuthority.None, "Development"),

    // [YAML HELP] If set to true, will update the Project Version with the configured properties. See detailed help for more information.
    DETECT_PROJECT_VERSION_UPDATE("detect.project.version.update", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The path to the Python executable.
    DETECT_PYTHON_PATH("detect.python.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If true will use Python 3 if available on class path.
    DETECT_PYTHON_PYTHON3("detect.python.python3", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The amount of time in seconds Detect will wait for scans to finish and to generate reports (i.e. risk and policy check). When changing this value, keep in mind the checking of policies might have to wait for scans to process which can take some time.
    DETECT_REPORT_TIMEOUT("detect.report.timeout", PropertyType.LONG, PropertyAuthority.None, "300"),

    // [YAML HELP] The set of required detectors.
    DETECT_REQUIRED_DETECTOR_TYPES("detect.required.detector.types", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to false Detect will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.
    DETECT_RESOLVE_TILDE_IN_PATHS("detect.resolve.tilde.in.paths", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] When set to true, a Black Duck risk report in PDF form will be created.
    DETECT_RISK_REPORT_PDF("detect.risk.report.pdf", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The output directory for risk report in PDF. Default is the source directory.
    DETECT_RISK_REPORT_PDF_PATH("detect.risk.report.pdf.path", PropertyType.STRING, PropertyAuthority.None, "."),

    // [YAML HELP] The names of the sbt configurations to exclude.
    DETECT_SBT_EXCLUDED_CONFIGURATIONS("detect.sbt.excluded.configurations", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] If set to false, runtime dependencies will not be included when parsing *.gemspec files.
    DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES("detect.ruby.include.runtime.dependencies", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    // [YAML HELP] If set to true, development dependencies will be included when parsing *.gemspec files.
    DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES("detect.ruby.include.dev.dependencies", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    // [YAML HELP] The names of the sbt configurations to include.
    DETECT_SBT_INCLUDED_CONFIGURATIONS("detect.sbt.included.configurations", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Depth the sbt detector will use to search for report files.
    DETECT_SBT_REPORT_DEPTH("detect.sbt.report.search.depth", PropertyType.INTEGER, PropertyAuthority.None, "3"),

    // [YAML HELP] The output directory for all signature scanner output files. If not set, the signature scanner output files will be in a 'scan' subdirectory of the output directory.
    DETECT_SCAN_OUTPUT_PATH("detect.scan.output.path", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    // [YAML HELP] The path to the project directory to inspect.
    DETECT_SOURCE_PATH("detect.source.path", PropertyType.STRING, PropertyAuthority.DirectoryManager),

    // [YAML HELP] Test the connection to Black Duck with the current configuration.
    DETECT_TEST_CONNECTION("detect.test.connection", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @AcceptableValues(value = { "BAZEL", "DETECTOR", "DOCKER", "SIGNATURE_SCAN", "BINARY_SCAN", "POLARIS", "NONE", "ALL" }, caseSensitive = true, strict = false, isCommaSeparatedList = true)
    // [YAML HELP] The tools Detect should allow in a comma-separated list. Tools in this list (as long as they are not also in the excluded list) will be allowed to run if all criteria of the tool are met. Exclusion rules always win.
        DETECT_TOOLS("detect.tools", PropertyType.STRING, PropertyAuthority.None),

    @AcceptableValues(value = { "BAZEL", "DETECTOR", "DOCKER", "SIGNATURE_SCAN", "BINARY_SCAN", "POLARIS", "NONE", "ALL" }, caseSensitive = true, strict = false, isCommaSeparatedList = true)
    // [YAML HELP] The tools Detect should not allow, in a comma-separated list. Excluded tools will not be run even if all criteria for the tool is met. Exclusion rules always win.
        DETECT_TOOLS_EXCLUDED("detect.tools.excluded", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] The path to the Yarn executable.
    DETECT_YARN_PATH("detect.yarn.path", PropertyType.STRING, PropertyAuthority.None),

    // [YAML HELP] Set this to true to only scan production dependencies.
    DETECT_YARN_PROD_ONLY("detect.yarn.prod.only", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @AcceptableValues(value = { "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF" }, caseSensitive = false, strict = true)
    // [YAML HELP] The logging level of Detect.
        LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION("logging.level.com.synopsys.integration", PropertyType.STRING, PropertyAuthority.None, "INFO"),

    // [YAML HELP] If set to true, Detect will wait for Synopsys products until results are available or the blackduck.timeout is exceeded.
    DETECT_WAIT_FOR_RESULTS("detect.wait.for.results", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    /**********************************************************************************************
     * DEPRECATED START
     *********************************************************************************************/

    @Deprecated
    @DetectDeprecation(description = "This property is now deprecated. Please use --detect.report.timeout in the future. NOTE the new property is in SECONDS not MILLISECONDS.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Timeout for response from Black Duck regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.
        DETECT_API_TIMEOUT("detect.api.timeout", PropertyType.LONG, PropertyAuthority.None, "300000"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.url in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] URL of the Hub server.
        BLACKDUCK_HUB_URL("blackduck.hub.url", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.timeout in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The time to wait for rest connections to complete in seconds.
        BLACKDUCK_HUB_TIMEOUT("blackduck.hub.timeout", PropertyType.INTEGER, PropertyAuthority.None, "120"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.username in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Hub username.
        BLACKDUCK_HUB_USERNAME("blackduck.hub.username", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.password in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Hub password.
        BLACKDUCK_HUB_PASSWORD("blackduck.hub.password", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.api.token in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Hub API Token.
        BLACKDUCK_HUB_API_TOKEN("blackduck.hub.api.token", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.host in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Proxy host.
        BLACKDUCK_HUB_PROXY_HOST("blackduck.hub.proxy.host", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.port in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Proxy port.
        BLACKDUCK_HUB_PROXY_PORT("blackduck.hub.proxy.port", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.username in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Proxy username.
        BLACKDUCK_HUB_PROXY_USERNAME("blackduck.hub.proxy.username", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.password in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Proxy password.
        BLACKDUCK_HUB_PROXY_PASSWORD("blackduck.hub.proxy.password", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ntlm.domain in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] NTLM Proxy domain.
        BLACKDUCK_HUB_PROXY_NTLM_DOMAIN("blackduck.hub.proxy.ntlm.domain", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ignored.hosts in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] A comma-separated list of host patterns that should not use the proxy.
        BLACKDUCK_HUB_PROXY_IGNORED_HOSTS("blackduck.hub.proxy.ignored.hosts", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.proxy.ntlm.workstation in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] NTLM Proxy workstation.
        BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION("blackduck.hub.proxy.ntlm.workstation", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.trust.cert in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, automatically trusts the certificate for the current run of Detect only.
        BLACKDUCK_HUB_TRUST_CERT("blackduck.hub.trust.cert", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --blackduck.offline.mode in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] This disables any Hub communication. If true, Detect does not upload BDIO files, does not check policies, and does not download and install the signature scanner.
        BLACKDUCK_HUB_OFFLINE_MODE("blackduck.hub.offline.mode", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.ignore.connection.failures in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.
        DETECT_DISABLE_WITHOUT_HUB("detect.disable.without.hub", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.ignore.connection.failures in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, during initialization Detect will check for Black Duck connectivity and exit with status code 0 if it cannot connect.
        DETECT_DISABLE_WITHOUT_BLACKDUCK("detect.disable.without.blackduck", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is being removed. Configuration can no longer be suppressed individually. Log level can be used.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, the default behavior of printing your configuration properties at startup will be suppressed.
        DETECT_SUPPRESS_CONFIGURATION_OUTPUT("detect.suppress.configuration.output", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is being removed. Results can no longer be suppressed individually. Log level can be used.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, the default behavior of printing the Detect Results will be suppressed.
        DETECT_SUPPRESS_RESULTS_OUTPUT("detect.suppress.results.output", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.excluded.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.
        DETECT_EXCLUDED_BOM_TOOL_TYPES("detect.excluded.bom.tool.types", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.exclusion.defaults in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, the bom tool search will exclude the default directory names. See the detailed help for more information.
        DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS("detect.bom.tool.search.exclusion.defaults", PropertyType.BOOLEAN, PropertyAuthority.None, "true"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.exclusion in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] A comma-separated list of directory names to exclude from the bom tool search.
        DETECT_BOM_TOOL_SEARCH_EXCLUSION("detect.bom.tool.search.exclusion", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.included.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.
        DETECT_INCLUDED_BOM_TOOL_TYPES("detect.included.bom.tool.types", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.sbt.report.depth in the future.", failInVersion = DetectMajorVersion.FIVE, removeInVersion = DetectMajorVersion.SIX)
    // [YAML HELP] Depth from source paths to search for sbt report files.
        DETECT_SEARCH_DEPTH("detect.search.depth", PropertyType.INTEGER, PropertyAuthority.None, "3"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.project.detector in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.
        DETECT_PROJECT_BOM_TOOL("detect.project.bom.tool", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.depth in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Depth of subdirectories within the source directory to search for files that indicate whether a detector applies.
        DETECT_BOM_TOOL_SEARCH_DEPTH("detect.bom.tool.search.depth", PropertyType.INTEGER, PropertyAuthority.None, "0"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.required.detector.types in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If set, Detect will fail if it does not find the bom tool types supplied here.
        DETECT_REQUIRED_BOM_TOOL_TYPES("detect.required.bom.tool.types", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.detector.search.continue in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.
        DETECT_BOM_TOOL_SEARCH_CONTINUE("detect.bom.tool.search.continue", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "In the future, the gradle inspector will no longer be downloaded from a custom repository, please use Detect Air Gap instead.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The respository gradle should use to look for the gradle inspector dependencies.
        DETECT_GRADLE_INSPECTOR_REPOSITORY_URL("detect.gradle.inspector.repository.url", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "In the future, Detect will not look for a custom named inspector.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Name of the Nuget Inspector package and the Nuget Inspector exe. (Do not include '.exe'.)
        DETECT_NUGET_INSPECTOR_NAME("detect.nuget.inspector.name", PropertyType.STRING, PropertyAuthority.None, "IntegrationNugetInspector"),

    @Deprecated
    @DetectDeprecation(description = "In the future, Detect will no longer need a nuget executable as it will download the inspector from Artifactory exclusively.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The path to the Nuget executable. Nuget is used to download the classic inspectors nuget package.
        DETECT_NUGET_PATH("detect.nuget.path", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.dry.run in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.
        DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN("detect.hub.signature.scanner.dry.run", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.snippet.mode in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.
        DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE("detect.hub.signature.scanner.snippet.mode", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.patterns in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] A comma-separated list of values to be used with the Signature Scanner --exclude flag.
        DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS("detect.hub.signature.scanner.exclusion.patterns", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.paths in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] These paths and only these paths will be scanned.
        DETECT_HUB_SIGNATURE_SCANNER_PATHS("detect.hub.signature.scanner.paths", PropertyType.STRING_ARRAY, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.name.patterns in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] A comma-separated list of directory name patterns Detect will search for and add to the Signature Scanner --exclude flag values.
        DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS("detect.hub.signature.scanner.exclusion.name.patterns", PropertyType.STRING_ARRAY, PropertyAuthority.None, "node_modules"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.memory in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The memory for the scanner to use.
        DETECT_HUB_SIGNATURE_SCANNER_MEMORY("detect.hub.signature.scanner.memory", PropertyType.INTEGER, PropertyAuthority.None, "4096"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Set to true to disable the Hub Signature Scanner.
        DETECT_HUB_SIGNATURE_SCANNER_DISABLED("detect.hub.signature.scanner.disabled", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Set to true to disable the Black Duck Signature Scanner.
        DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED("detect.blackduck.signature.scanner.disabled", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.offline.local.path in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.
        DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH("detect.hub.signature.scanner.offline.local.path", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.local.path in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.
        DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH("detect.hub.signature.scanner.local.path", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.host.url in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.
        DETECT_HUB_SIGNATURE_SCANNER_HOST_URL("detect.hub.signature.scanner.host.url", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.parallel.processors in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.
        DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS("detect.hub.signature.scanner.parallel.processors", PropertyType.INTEGER, PropertyAuthority.None, "1"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.blackduck.signature.scanner.arguments in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Additional arguments to use when running the Hub signature scanner.
        DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS("detect.hub.signature.scanner.arguments", PropertyType.STRING, PropertyAuthority.None),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --detect.tools and POLARIS in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] Set to false to disable the Synopsys Polaris Tool.
        DETECT_SWIP_ENABLED("detect.polaris.enabled", PropertyType.BOOLEAN, PropertyAuthority.None, "false"),

    @Deprecated
    @DetectDeprecation(description = "This property is changing. Please use --logging.level.com.synopsys.integration in the future.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    @AcceptableValues(value = { "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF" }, caseSensitive = false, strict = true)
    // [YAML HELP] The logging level of Detect.
        LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION("logging.level.com.blackducksoftware.integration", PropertyType.STRING, PropertyAuthority.None, "INFO"),

    @Deprecated
    @DetectDeprecation(description = "This property is now deprecated. Please use --detect.blackduck.signature.scanner.snippet.matching in the future. NOTE the new property is one of a particular set of values. You will need to consult the documentation for the Signature Scanner in Black Duck for details.", failInVersion = DetectMajorVersion.SIX, removeInVersion = DetectMajorVersion.SEVEN)
    // [YAML HELP] If set to true, the signature scanner will, if supported by your Black Duck version, run in snippet scanning mode.
        DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE("detect.blackduck.signature.scanner.snippet.mode", PropertyType.BOOLEAN, PropertyAuthority.None, "false");

    /**********************************************************************************************
     * DEPRECATED END
     *********************************************************************************************/

    private final String propertyKey;
    private final PropertyType propertyType;
    private final String defaultValue;
    private final PropertyAuthority propertyAuthority;

    DetectProperty(final String propertyKey, final PropertyType propertyType, final PropertyAuthority propertyAuthority) {
        this(propertyKey, propertyType, propertyAuthority, null);
    }

    DetectProperty(final String propertyKey, final PropertyType propertyType, final PropertyAuthority propertyAuthority, final String defaultValue) {
        this.propertyKey = propertyKey;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
        this.propertyAuthority = propertyAuthority;
    }

    public PropertyAuthority getPropertyAuthority() {
        return propertyAuthority;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Boolean isEqualToDefault(final String value) {
        String defaultValue = "";
        if (null != getDefaultValue()) {
            defaultValue = getDefaultValue();
        }
        return value.equals(defaultValue);
    }
}
