/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.configuration;

import org.springframework.beans.factory.annotation.Value;

import com.blackducksoftware.integration.hub.detect.help.AcceptableValues;
import com.blackducksoftware.integration.hub.detect.help.DefaultValue;
import com.blackducksoftware.integration.hub.detect.help.HelpDescription;
import com.blackducksoftware.integration.hub.detect.help.HelpDetailed;
import com.blackducksoftware.integration.hub.detect.help.HelpGroup;

public class ValueContainer {
    private static final String GROUP_HUB_CONFIGURATION = "hub configuration";
    private static final String GROUP_GENERAL = "general";
    private static final String GROUP_LOGGING = "logging";
    private static final String GROUP_CLEANUP = "cleanup";
    private static final String GROUP_PATHS = "paths";
    private static final String GROUP_BOMTOOL = "bomtool";
    private static final String GROUP_CODELOCATION = "codelocation";
    private static final String GROUP_CONDA = "conda";
    private static final String GROUP_CPAN = "cpan";
    private static final String GROUP_DOCKER = "docker";
    private static final String GROUP_GO = "go";
    private static final String GROUP_GRADLE = "gradle";
    private static final String GROUP_HEX = "hex";
    private static final String GROUP_MAVEN = "maven";
    private static final String GROUP_NPM = "npm";
    private static final String GROUP_NUGET = "nuget";
    private static final String GROUP_PACKAGIST = "packagist";
    private static final String GROUP_PEAR = "pear";
    private static final String GROUP_PIP = "pip";
    private static final String GROUP_POLICY_CHECK = "policy check";
    private static final String GROUP_PROJECT_INFO = "project info";
    private static final String GROUP_PYTHON = "python";
    private static final String GROUP_SBT = "sbt";
    private static final String GROUP_SIGNATURE_SCANNER = "signature scanner";
    private static final String GROUP_YARN = "yarn";

    private static final String SEARCH_GROUP_SIGNATURE_SCANNER = "scanner";
    private static final String SEARCH_GROUP_POLICY = "policy";
    private static final String SEARCH_GROUP_HUB = "hub";
    private static final String SEARCH_GROUP_PROXY = "proxy";
    private static final String SEARCH_GROUP_OFFLINE = "offline";
    private static final String SEARCH_GROUP_PROJECT = "project";
    private static final String SEARCH_GROUP_DEBUG = "debug";
    private static final String SEARCH_GROUP_SEARCH = "search";

    public static final String PRINT_GROUP_DEFAULT = SEARCH_GROUP_HUB;

    // properties start

    @Value("${detect.fail.config.warning:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_GENERAL)
    @HelpDescription("If true, Detect will fail if there are any issues found in the configuration.")
    private Boolean failOnConfigWarning;

    @Value("${detect.force.success:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_GENERAL)
    @HelpDescription("If true, detect will always exit with code 0.")
    private Boolean forceSuccess;

    @Value("${detect.suppress.configuration.output:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing your configuration properties at startup will be suppressed.")
    private Boolean suppressConfigurationOutput;

    @Value("${detect.suppress.results.output:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_LOGGING)
    @HelpDescription("If true, the default behavior of printing the Detect Results will be suppressed.")
    private Boolean suppressResultsOutput;

    @Value("${detect.cleanup:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_CLEANUP)
    @HelpDescription("If true the files created by Detect will be cleaned up.")
    private Boolean cleanupDetectFiles;

    @Value("${detect.test.connection:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Test the connection to the Hub with the current configuration")
    private Boolean testConnection;

    @Value("${detect.api.timeout:}")
    @DefaultValue("300000")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("Timeout for response from the hub regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.")
    private Long apiTimeout;

    @Value("${blackduck.hub.url:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("URL of the Hub server")
    private String hubUrl;

    @Value("${blackduck.hub.timeout:}")
    @DefaultValue("120")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Time to wait for rest connections to complete")
    private Integer hubTimeout;

    @Value("${blackduck.hub.username:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub username")
    private String hubUsername;

    @Value("${blackduck.hub.password:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub password")
    private String hubPassword;

    @Value("${blackduck.hub.api.token:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("Hub API Token")
    private String hubApiToken;

    @Value("${blackduck.hub.proxy.host:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy host")
    private String hubProxyHost;

    @Value("${blackduck.hub.proxy.port:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy port")
    private String hubProxyPort;

    @Value("${blackduck.hub.proxy.username:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy username")
    private String hubProxyUsername;

    @Value("${blackduck.hub.proxy.password:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Proxy password")
    private String hubProxyPassword;

    @Value("${blackduck.hub.proxy.ntlm.domain:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy domain")
    private String hubProxyNtlmDomain;

    @Value("${blackduck.hub.proxy.ignored.hosts:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Comma separated list of host patterns that should not use the proxy")
    private String hubProxyIgnoredHosts;

    @Value("${blackduck.hub.proxy.ntlm.workstation:}")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_PROXY })
    @HelpDescription("Ntlm Proxy workstation")
    private String hubProxyNtlmWorkstation;

    @Value("${blackduck.hub.trust.cert:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, automatically trust the certificate for the current run of Detect only")
    private Boolean hubTrustCertificate;

    @Value("${blackduck.hub.offline.mode:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB, SEARCH_GROUP_OFFLINE })
    @HelpDescription("This can disable any Hub communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
    private Boolean hubOfflineMode;

    @Value("${detect.disable.without.hub:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_HUB_CONFIGURATION, additional = { SEARCH_GROUP_HUB })
    @HelpDescription("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.")
    private Boolean disableWithoutHub;

    @Value("${detect.resolve.tilde.in.paths:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("If set to false we will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.")
    private Boolean resolveTildeInPaths;

    @Value("${detect.source.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Source path to inspect")
    private String sourcePath;

    @Value("${detect.output.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Output path")
    private String outputDirectoryPath;

    @Value("${detect.bdio.output.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("The output directory for all bdio files. If not set, the bdio files will be in a 'bdio' subdirectory of the output path.")
    private String bdioOutputDirectoryPath;

    @Value("${detect.scan.output.path:}")
    @HelpGroup(primary = GROUP_PATHS, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The output directory for all scan files. If not set, the scan files will be in a 'scan' subdirectory of the output path.")
    private String scanOutputDirectoryPath;

    @Value("${detect.search.depth:}")
    @DefaultValue("3")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Depth from source paths to search for files.")
    private Integer searchDepth;

    @Value("${detect.project.bom.tool:}")
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("The bom tool to choose when multiple bom tool types are found and one needs to be chosen for project name and version.")
    private String detectProjectBomTool;

    @Value("${detect.bom.tool.search.depth:}")
    @DefaultValue("0")
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("Depth from source paths to search for files to determine if a bom tool applies.")
    private Integer bomToolSearchDepth;

    @Value("${detect.bom.tool.search.continue:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will continue to look for bom tools to the maximum search depth, even if they applied earlier in the path.")
    private Boolean bomToolContinueSearch;

    @Value("${detect.bom.tool.search.exclusion:}")
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("A comma-separated list of directory names to exclude from the bom tool search.")
    private String[] bomToolSearchExclusion;

    @Value("${detect.bom.tool.search.exclusion.defaults:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PATHS, additional = { GROUP_BOMTOOL, SEARCH_GROUP_SEARCH })
    @HelpDescription("If true, the bom tool search will exclude the default directory names.")
    private Boolean bomToolSearchExclusionDefaults;

    @Value("${detect.excluded.bom.tool.types:}")
    @HelpGroup(primary = GROUP_BOMTOOL, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to exclude specific tools, specify the ones to exclude here. Exclusion rules always win.")
    private String excludedBomToolTypes;

    @Value("${detect.included.bom.tool.types:}")
    @HelpGroup(primary = GROUP_BOMTOOL, additional = { SEARCH_GROUP_SEARCH })
    @HelpDescription("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
    private String includedBomToolTypes;

    @Value("${detect.code.location.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the name detect will use for the code location it creates. If supplied and multiple code locations are found, detect will append an index to each code location name.")
    private String codeLocationNameOverride;

    @Value("${detect.project.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the name to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
    private String projectName;

    @Value("${detect.project.description:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project description is specified, your project version will be created with this description.")
    private String projectDescription;

    @Value("${detect.project.version.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the version to use for the Hub project. If not supplied, detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
    private String projectVersionName;

    @Value("${detect.project.version.notes:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If project version notes are specified, your project version will be created with these notes.")
    private String projectVersionNotes;

    @Value("${detect.project.tier:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If a hub project tier is specified, your project will be created with this tier.")
    @AcceptableValues(value = { "1", "2", "3", "4", "5" }, caseSensitive = false, strict = false)
    private Integer projectTier;

    @Value("${detect.project.codelocation.prefix:}")
    @DefaultValue("")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A prefix to the name of the codelocations created by Detect. Useful for running against the same projects on multiple machines.")
    private String projectCodeLocationPrefix;

    @Value("${detect.project.codelocation.suffix:}")
    @DefaultValue("")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("A suffix to the name of the codelocations created by Detect.")
    private String projectCodeLocationSuffix;

    @Value("${detect.project.codelocation.unmap:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, unmaps all other code locations mapped to the project version produced by the current run of Detect.")
    private Boolean projectCodeLocationUnmap;

    @Value("${detect.project.level.adjustments:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project level matches.")
    private String projectLevelMatchAdjustments;

    @Value("${detect.project.version.phase:}")
    @DefaultValue("Development")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version phase.")
    @AcceptableValues(value = { "PLANNING", "DEVELOPMENT", "RELEASED", "DEPRECATED", "ARCHIVED" }, caseSensitive = false, strict = false)
    private String projectVersionPhase;

    @Value("${detect.project.version.distribution:}")
    @DefaultValue("External")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("An override for the Project Version distribution")
    @AcceptableValues(value = { "EXTERNAL", "SAAS", "INTERNAL", "OPENSOURCE" }, caseSensitive = false, strict = false)
    private String projectVersionDistribution;

    @Value("${detect.project.version.update:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set to true, will update the Project Version with the configured properties. See detailed help for more information.")
    @HelpDetailed("When set to true, the following properties will be updated on the Project. Project tier (detect.project.tier) and Project Level Adjustments (detect.project.level.adjustments).\r\n The following properties will also be updated on the Version. Version notes (detect.project.version.notes), phase (detect.project.version.phase), distribution (detect.project.version.distribution)")
    private Boolean projectVersionUpdate;

    @Value("${detect.policy.check.fail.on.severities:}")
    @HelpGroup(primary = GROUP_POLICY_CHECK, additional = { SEARCH_GROUP_POLICY })
    @HelpDescription("A comma-separated list of policy violation severities that will fail detect. If this is not set, detect will not fail due to policy violations.")
    @AcceptableValues(value = { "ALL", "BLOCKER", "CRITICAL", "MAJOR", "MINOR", "TRIVIAL" }, caseSensitive = false, strict = false, isCommaSeparatedList = true)
    private String policyCheckFailOnSeverities;

    @Value("${detect.gradle.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Version of the Gradle Inspector")
    private String gradleInspectorVersion;

    @Value("${detect.gradle.build.command:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Gradle build command")
    private String gradleBuildCommand;

    @Value("${detect.gradle.excluded.configurations:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to exclude")
    private String gradleExcludedConfigurationNames;

    @Value("${detect.gradle.included.configurations:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the dependency configurations to include")
    private String gradleIncludedConfigurationNames;

    @Value("${detect.gradle.excluded.projects:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to exclude")
    private String gradleExcludedProjectNames;

    @Value("${detect.gradle.included.projects:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The names of the projects to include")
    private String gradleIncludedProjectNames;

    @Value("${detect.nuget.config.path:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path to the Nuget.Config file to supply to the nuget exe")
    private String nugetConfigPath;

    @Value("${detect.nuget.inspector.name:}")
    @DefaultValue("IntegrationNugetInspector")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Name of the Nuget Inspector")
    private String nugetInspectorPackageName;

    @Value("${detect.nuget.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("Version of the Nuget Inspector")
    private String nugetInspectorPackageVersion;

    @Value("${detect.nuget.excluded.modules:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to exclude")
    private String nugetInspectorExcludedModules;

    @Value("${detect.nuget.included.modules:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The names of the projects in a solution to include (overrides exclude)")
    private String nugetInspectorIncludedModules;

    @Value("${detect.nuget.ignore.failure:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("If true errors will be logged and then ignored.")
    private Boolean nugetInspectorIgnoreFailure;

    @Value("${detect.maven.scope:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The name of the dependency scope to include")
    private String mavenScope;

    @Value("${detect.maven.build.command:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("Maven build command")
    private String mavenBuildCommand;

    @Value("${detect.gradle.path:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("Path of the Gradle executable")
    private String gradlePath;

    @Value("${detect.maven.path:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The path of the Maven executable")
    private String mavenPath;

    @Value("${detect.maven.excluded.modules:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to exclude")
    private String mavenExcludedModuleNames;

    @Value("${detect.maven.included.modules:}")
    @HelpGroup(primary = GROUP_MAVEN)
    @HelpDescription("The names of the module to include")
    private String mavenIncludedModuleNames;

    @Value("${detect.nuget.path:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path of the Nuget executable")
    private String nugetPath;

    @Value("${detect.pip.project.name:}")
    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("Override for pip inspector to find your project")
    private String pipProjectName;

    @Value("${detect.python.python3:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("If true will use Python 3 if available on class path")
    private Boolean pythonThreeOverride;

    @Value("${detect.python.path:}")
    @HelpGroup(primary = GROUP_PYTHON)
    @HelpDescription("The path of the Python executable")
    private String pythonPath;

    @Value("${detect.npm.path:}")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the Npm executable")
    private String npmPath;

    @Value("${detect.npm.include.dev.dependencies:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("Set this value to false if you would like to exclude your dev dependencies when ran")
    private String npmIncludeDevDependencies;

    @Value("${detect.npm.node.path:}")
    @HelpGroup(primary = GROUP_NPM)
    @HelpDescription("The path of the node executable that is used by Npm")
    private String npmNodePath;

    @Value("${detect.pear.path:}")
    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("The path of the pear executable")
    private String pearPath;

    @Value("${detect.pear.only.required.deps:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PEAR)
    @HelpDescription("Set to true if you would like to include only required packages")
    private Boolean pearOnlyRequiredDependencies;

    @Value("${detect.pip.requirements.path:}")
    @HelpGroup(primary = GROUP_PIP)
    @HelpDescription("The path of the requirements.txt file")
    private String requirementsFilePath;

    @Value("${detect.go.dep.path:}")
    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("Path of the Go Dep executable")
    private String goDepPath;

    @Value("${detect.go.run.dep.init:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_GO)
    @HelpDescription("If set to true, we will attempt to run 'init' and 'ensure' which can modify your development environment.")
    private Boolean goRunDepInit;

    @Value("${detect.docker.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Path of the docker executable")
    private String dockerPath;

    @Value("${detect.docker.path.required:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("If set to false, detect will attempt to run docker even if it cannot find a docker path.")
    private Boolean dockerPathRequired;

    @Value("${detect.docker.inspector.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("This is used to override using the hosted script by github url. You can provide your own script at this path.")
    private String dockerInspectorPath;

    @Value("${detect.docker.inspector.version:}")
    @DefaultValue("latest")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("Version of the Hub Docker Inspector to use")
    private String dockerInspectorVersion;

    @Value("${detect.docker.tar:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("A saved docker image - must be a .tar file. For detect to run docker either this property or detect.docker.image must be set.")
    private String dockerTar;

    @Value("${detect.docker.image:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The docker image name to inspect. For detect to run docker either this property or detect.docker.tar must be set.")
    private String dockerImage;

    @Value("${detect.bash.path:}")
    @HelpGroup(primary = GROUP_PATHS)
    @HelpDescription("Path of the bash executable")
    private String bashPath;

    @Value("${logging.level.com.blackducksoftware.integration:}")
    @DefaultValue("INFO")
    @HelpGroup(primary = GROUP_LOGGING, additional = { GROUP_LOGGING, SEARCH_GROUP_DEBUG })
    @HelpDescription("The logging level of Detect")
    @AcceptableValues(value = { "ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF" }, caseSensitive = false, strict = true)
    private String loggingLevel;

    @Value("${detect.hub.signature.scanner.dry.run:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.")
    private Boolean hubSignatureScannerDryRun;

    @Value("${detect.hub.signature.scanner.snippet.mode:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.")
    private Boolean hubSignatureScannerSnippetMode;

    @Value("${detect.hub.signature.scanner.exclusion.patterns:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("Enables you to specify sub-directories to exclude from scans")
    private String[] hubSignatureScannerExclusionPatterns;

    @Value("${detect.hub.signature.scanner.paths:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("These paths and only these paths will be scanned.")
    private String[] hubSignatureScannerPaths;

    @Value("${detect.hub.signature.scanner.exclusion.name.patterns:}")
    @DefaultValue("node_modules")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("Comma separated list of file name patterns to exclude from the signature scan.")
    @HelpDetailed("Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner.\r\nThese patterns will be added to the patterns provided by detect.hub.signature.scanner.exclusion.patterns")
    private String[] hubSignatureScannerExclusionNamePatterns;

    @Value("${detect.hub.signature.scanner.memory:}")
    @DefaultValue("4096")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The memory for the scanner to use.")
    private Integer hubSignatureScannerMemory;

    @Value("${detect.hub.signature.scanner.disabled:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_DEBUG, SEARCH_GROUP_HUB })
    @HelpDescription("Set to true to disable the Hub Signature Scanner.")
    private Boolean hubSignatureScannerDisabled;

    @Value("${detect.hub.signature.scanner.offline.local.path:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_OFFLINE, SEARCH_GROUP_HUB })
    @HelpDescription("To use a local signature scanner, set its location with this property. This will be the path where the signature scanner was unzipped. This will likely look similar to /some/path/scan.cli-x.y.z")
    private String hubSignatureScannerOfflineLocalPath;

    @Value("${detect.hub.signature.scanner.host.url:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.")
    private String hubSignatureScannerHostUrl;

    @Value("${detect.hub.signature.scanner.parallel.processors:}")
    @DefaultValue("1")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
    private Integer hubSignatureScannerParallelProcessors;

    @Value("${detect.hub.signature.scanner.arguments:}")
    @HelpGroup(primary = GROUP_SIGNATURE_SCANNER, additional = { SEARCH_GROUP_SIGNATURE_SCANNER, SEARCH_GROUP_HUB })
    @HelpDescription("Additional arguments to use when running the Hub signature scanner.")
    private String hubSignatureScannerArguments;

    @Value("${detect.packagist.include.dev.dependencies:}")
    @DefaultValue("true")
    @HelpGroup(primary = GROUP_PACKAGIST)
    @HelpDescription("Set this value to false if you would like to exclude your dev requires dependencies when ran")
    private Boolean packagistIncludeDevDependencies;

    @Value("${detect.perl.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the perl executable")
    private String perlPath;

    @Value("${detect.cpan.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpan executable")
    private String cpanPath;

    @Value("${detect.cpanm.path:}")
    @HelpGroup(primary = GROUP_CPAN)
    @HelpDescription("The path of the cpanm executable")
    private String cpanmPath;

    @Value("${detect.sbt.excluded.configurations:}")
    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to exclude")
    private String sbtExcludedConfigurationNames;

    @Value("${detect.sbt.included.configurations:}")
    @HelpGroup(primary = GROUP_SBT)
    @HelpDescription("The names of the sbt configurations to include")
    private String sbtIncludedConfigurationNames;

    @Value("${detect.default.project.version.scheme:}")
    @DefaultValue("text")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'")
    private String defaultProjectVersionScheme;

    @Value("${detect.default.project.version.text:}")
    @DefaultValue("Default Detect Version")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The text to use as the default project version")
    private String defaultProjectVersionText;

    @Value("${detect.default.project.version.timeformat:}")
    @DefaultValue("yyyy-MM-dd\\'T\\'HH:mm:ss.SSS")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The timestamp format to use as the default project version")
    private String defaultProjectVersionTimeformat;

    @Value("${detect.bom.aggregate.name:}")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.")
    private String aggregateBomName;

    @Value("${detect.risk.report.pdf:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck risk report in PDF form will be created")
    private Boolean riskReportPdf;

    @Value("${detect.risk.report.pdf.path:}")
    @DefaultValue(".")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for risk report in PDF. Default is the source directory")
    private String riskReportPdfOutputDirectory;

    @Value("${detect.notices.report:}")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("When set to true, a Black Duck notices report in text form will be created in your source directory")
    private Boolean noticesReport;

    @Value("${detect.notices.report.path:}")
    @DefaultValue(".")
    @HelpGroup(primary = GROUP_PROJECT_INFO, additional = { SEARCH_GROUP_PROJECT })
    @HelpDescription("The output directory for notices report. Default is the source directory")
    private String noticesReportOutputDirectory;

    @Value("${detect.conda.path:}")
    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The path of the conda executable")
    private String condaPath;

    @Value("${detect.conda.environment.name:}")
    @HelpGroup(primary = GROUP_CONDA)
    @HelpDescription("The name of the anaconda environment used by your project")
    private String condaEnvironmentName;

    @Value("${detect.docker.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_DOCKER)
    @HelpDescription("The path to the directory containing the docker inspector script, jar, and images")
    private String dockerInspectorAirGapPath;

    @Value("${detect.gradle.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The path to the directory containing the air gap dependencies for the gradle inspector")
    private String gradleInspectorAirGapPath;

    @Value("${detect.nuget.inspector.air.gap.path:}")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The path to the directory containing the nuget inspector nupkg")
    private String nugetInspectorAirGapPath;

    @Value("${detect.nuget.packages.repo.url:}")
    @DefaultValue("https://www.nuget.org/api/v2/")
    @HelpGroup(primary = GROUP_NUGET)
    @HelpDescription("The source for nuget packages")
    private String[] nugetPackagesRepoUrl;

    @Value("${detect.gradle.inspector.repository.url:}")
    @HelpGroup(primary = GROUP_GRADLE)
    @HelpDescription("The respository gradle should use to look for the gradle inspector")
    private String gradleInspectorRepositoryUrl;

    @Value("${detect.hex.rebar3.path:}")
    @HelpGroup(primary = GROUP_HEX)
    @HelpDescription("The path of the rebar3 executable")
    private String hexRebar3Path;

    @Value("${detect.yarn.path:}")
    @HelpDescription("The path of the Yarn executable")
    @HelpGroup(primary = GROUP_YARN)
    private String yarnPath;

    @Value("${detect.yarn.prod.only:}")
    @HelpDescription("Set this to true to only scan production dependencies")
    @DefaultValue("false")
    @HelpGroup(primary = GROUP_YARN)
    private String yarnProductionDependenciesOnly;

    public Boolean getFailOnConfigWarning() {
        return failOnConfigWarning;
    }

    public Boolean getForceSuccess() {
        return forceSuccess;
    }

    public Boolean getSuppressConfigurationOutput() {
        return suppressConfigurationOutput;
    }

    public Boolean getSuppressResultsOutput() {
        return suppressResultsOutput;
    }

    public Boolean getCleanupDetectFiles() {
        return cleanupDetectFiles;
    }

    public Boolean getTestConnection() {
        return testConnection;
    }

    public Long getApiTimeout() {
        return apiTimeout;
    }

    public String getHubUrl() {
        return hubUrl;
    }

    public Integer getHubTimeout() {
        return hubTimeout;
    }

    public String getHubUsername() {
        return hubUsername;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public String getHubApiToken() {
        return hubApiToken;
    }

    public String getHubProxyHost() {
        return hubProxyHost;
    }

    public String getHubProxyPort() {
        return hubProxyPort;
    }

    public String getHubProxyUsername() {
        return hubProxyUsername;
    }

    public String getHubProxyPassword() {
        return hubProxyPassword;
    }

    public String getHubProxyNtlmDomain() {
        return hubProxyNtlmDomain;
    }

    public String getHubProxyIgnoredHosts() {
        return hubProxyIgnoredHosts;
    }

    public String getHubProxyNtlmWorkstation() {
        return hubProxyNtlmWorkstation;
    }

    public Boolean getHubTrustCertificate() {
        return hubTrustCertificate;
    }

    public Boolean getHubOfflineMode() {
        return hubOfflineMode;
    }

    public Boolean getDisableWithoutHub() {
        return disableWithoutHub;
    }

    public Boolean getResolveTildeInPaths() {
        return resolveTildeInPaths;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }

    public String getBdioOutputDirectoryPath() {
        return bdioOutputDirectoryPath;
    }

    public String getScanOutputDirectoryPath() {
        return scanOutputDirectoryPath;
    }

    public Integer getSearchDepth() {
        return searchDepth;
    }

    public String getDetectProjectBomTool() {
        return detectProjectBomTool;
    }

    public Integer getBomToolSearchDepth() {
        return bomToolSearchDepth;
    }

    public Boolean getBomToolContinueSearch() {
        return bomToolContinueSearch;
    }

    public String[] getBomToolSearchExclusion() {
        return bomToolSearchExclusion;
    }

    public Boolean getBomToolSearchExclusionDefaults() {
        return bomToolSearchExclusionDefaults;
    }

    public String getExcludedBomToolTypes() {
        return excludedBomToolTypes;
    }

    public String getIncludedBomToolTypes() {
        return includedBomToolTypes;
    }

    public String getCodeLocationNameOverride() {
        return codeLocationNameOverride;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public String getProjectVersionNotes() {
        return projectVersionNotes;
    }

    public Integer getProjectTier() {
        return projectTier;
    }

    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix;
    }

    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix;
    }

    public Boolean getProjectCodeLocationUnmap() {
        return projectCodeLocationUnmap;
    }

    public String getProjectLevelMatchAdjustments() {
        return projectLevelMatchAdjustments;
    }

    public String getProjectVersionPhase() {
        return projectVersionPhase;
    }

    public String getProjectVersionDistribution() {
        return projectVersionDistribution;
    }

    public Boolean getProjectVersionUpdate() {
        return projectVersionUpdate;
    }

    public String getPolicyCheckFailOnSeverities() {
        return policyCheckFailOnSeverities;
    }

    public String getGradleInspectorVersion() {
        return gradleInspectorVersion;
    }

    public String getGradleBuildCommand() {
        return gradleBuildCommand;
    }

    public String getGradleExcludedConfigurationNames() {
        return gradleExcludedConfigurationNames;
    }

    public String getGradleIncludedConfigurationNames() {
        return gradleIncludedConfigurationNames;
    }

    public String getGradleExcludedProjectNames() {
        return gradleExcludedProjectNames;
    }

    public String getGradleIncludedProjectNames() {
        return gradleIncludedProjectNames;
    }

    public String getNugetConfigPath() {
        return nugetConfigPath;
    }

    public String getNugetInspectorPackageName() {
        return nugetInspectorPackageName;
    }

    public String getNugetInspectorPackageVersion() {
        return nugetInspectorPackageVersion;
    }

    public String getNugetInspectorExcludedModules() {
        return nugetInspectorExcludedModules;
    }

    public String getNugetInspectorIncludedModules() {
        return nugetInspectorIncludedModules;
    }

    public Boolean getNugetInspectorIgnoreFailure() {
        return nugetInspectorIgnoreFailure;
    }

    public String getMavenScope() {
        return mavenScope;
    }

    public String getMavenBuildCommand() {
        return mavenBuildCommand;
    }

    public String getGradlePath() {
        return gradlePath;
    }

    public String getMavenPath() {
        return mavenPath;
    }

    public String getMavenExcludedModuleNames() {
        return mavenExcludedModuleNames;
    }

    public String getMavenIncludedModuleNames() {
        return mavenIncludedModuleNames;
    }

    public String getNugetPath() {
        return nugetPath;
    }

    public String getPipProjectName() {
        return pipProjectName;
    }

    public Boolean getPythonThreeOverride() {
        return pythonThreeOverride;
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public String getNpmPath() {
        return npmPath;
    }

    public String getNpmIncludeDevDependencies() {
        return npmIncludeDevDependencies;
    }

    public String getNpmNodePath() {
        return npmNodePath;
    }

    public String getPearPath() {
        return pearPath;
    }

    public Boolean getPearOnlyRequiredDependencies() {
        return pearOnlyRequiredDependencies;
    }

    public String getRequirementsFilePath() {
        return requirementsFilePath;
    }

    public String getGoDepPath() {
        return goDepPath;
    }

    public Boolean getGoRunDepInit() {
        return goRunDepInit;
    }

    public String getDockerPath() {
        return dockerPath;
    }

    public Boolean getDockerPathRequired() {
        return dockerPathRequired;
    }

    public String getDockerInspectorPath() {
        return dockerInspectorPath;
    }

    public String getDockerInspectorVersion() {
        return dockerInspectorVersion;
    }

    public String getDockerTar() {
        return dockerTar;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public String getBashPath() {
        return bashPath;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public Boolean getHubSignatureScannerDryRun() {
        return hubSignatureScannerDryRun;
    }

    public Boolean getHubSignatureScannerSnippetMode() {
        return hubSignatureScannerSnippetMode;
    }

    public String[] getHubSignatureScannerExclusionPatterns() {
        return hubSignatureScannerExclusionPatterns;
    }

    public String[] getHubSignatureScannerPaths() {
        return hubSignatureScannerPaths;
    }

    public String[] getHubSignatureScannerExclusionNamePatterns() {
        return hubSignatureScannerExclusionNamePatterns;
    }

    public Integer getHubSignatureScannerMemory() {
        return hubSignatureScannerMemory;
    }

    public Boolean getHubSignatureScannerDisabled() {
        return hubSignatureScannerDisabled;
    }

    public String getHubSignatureScannerOfflineLocalPath() {
        return hubSignatureScannerOfflineLocalPath;
    }

    public String getHubSignatureScannerHostUrl() {
        return hubSignatureScannerHostUrl;
    }

    public Integer getHubSignatureScannerParallelProcessors() {
        return hubSignatureScannerParallelProcessors;
    }

    public String getHubSignatureScannerArguments() {
        return hubSignatureScannerArguments;
    }

    public Boolean getPackagistIncludeDevDependencies() {
        return packagistIncludeDevDependencies;
    }

    public String getPerlPath() {
        return perlPath;
    }

    public String getCpanPath() {
        return cpanPath;
    }

    public String getCpanmPath() {
        return cpanmPath;
    }

    public String getSbtExcludedConfigurationNames() {
        return sbtExcludedConfigurationNames;
    }

    public String getSbtIncludedConfigurationNames() {
        return sbtIncludedConfigurationNames;
    }

    public String getDefaultProjectVersionScheme() {
        return defaultProjectVersionScheme;
    }

    public String getDefaultProjectVersionText() {
        return defaultProjectVersionText;
    }

    public String getDefaultProjectVersionTimeformat() {
        return defaultProjectVersionTimeformat;
    }

    public String getAggregateBomName() {
        return aggregateBomName;
    }

    public Boolean getRiskReportPdf() {
        return riskReportPdf;
    }

    public String getRiskReportPdfOutputDirectory() {
        return riskReportPdfOutputDirectory;
    }

    public Boolean getNoticesReport() {
        return noticesReport;
    }

    public String getNoticesReportOutputDirectory() {
        return noticesReportOutputDirectory;
    }

    public String getCondaPath() {
        return condaPath;
    }

    public String getCondaEnvironmentName() {
        return condaEnvironmentName;
    }

    public String getDockerInspectorAirGapPath() {
        return dockerInspectorAirGapPath;
    }

    public String getGradleInspectorAirGapPath() {
        return gradleInspectorAirGapPath;
    }

    public String getNugetInspectorAirGapPath() {
        return nugetInspectorAirGapPath;
    }

    public String[] getNugetPackagesRepoUrl() {
        return nugetPackagesRepoUrl;
    }

    public String getGradleInspectorRepositoryUrl() {
        return gradleInspectorRepositoryUrl;
    }

    public String getHexRebar3Path() {
        return hexRebar3Path;
    }

    public String getYarnPath() {
        return yarnPath;
    }

    public String getYarnProductionDependenciesOnly() {
        return yarnProductionDependenciesOnly;
    }

    // properties end
}
