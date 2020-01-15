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

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionPhaseType
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.configuration.property.Property
import com.synopsys.integration.configuration.property.base.PassthroughProperty
import com.synopsys.integration.configuration.property.types.bool.BooleanProperty
import com.synopsys.integration.configuration.property.types.bool.NullableBooleanProperty
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedEnumProperty
import com.synopsys.integration.configuration.property.types.enumextended.ExtendedValue
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumListProperty
import com.synopsys.integration.configuration.property.types.enumfilterable.None
import com.synopsys.integration.configuration.property.types.enumlist.EnumListProperty
import com.synopsys.integration.configuration.property.types.enums.EnumProperty
import com.synopsys.integration.configuration.property.types.integer.IntegerProperty
import com.synopsys.integration.configuration.property.types.integer.NullableIntegerProperty
import com.synopsys.integration.configuration.property.types.longs.LongProperty
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty
import com.synopsys.integration.configuration.property.types.string.StringProperty
import com.synopsys.integration.configuration.property.types.stringlist.NullableStringListProperty
import com.synopsys.integration.configuration.property.types.stringlist.StringListProperty
import com.synopsys.integration.detect.DetectMajorVersion
import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.workflow.bdio.AggregateMode
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule
import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.log.LogLevel
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

enum class DefaultVersionNameScheme {
    TIMESTAMP,
    DEFAULT
}

enum class ExtendedPolicySeverityType {
    NONE
}

enum class ExtendedSnippetMode {
    NONE
}

class DetectProperties {
    companion object {
        //#region Active Properties

        val BLACKDUCK_API_TOKEN = NullableStringProperty("blackduck.api.token").apply {
            info("Black Duck API Token", "4.2.0")
            help("The API token used to authenticate with the Black Duck Server.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
        }
        val BLACKDUCK_OFFLINE_MODE = BooleanProperty("blackduck.offline.mode", false).apply {
            info("Offline Mode", "4.2.0")
            help("This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Offline, DetectGroup.Default)
        }
        val BLACKDUCK_PASSWORD = NullableStringProperty("blackduck.password").apply {
            info("Black Duck Password", "4.2.0")
            help("Black Duck password.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
        }
        val BLACKDUCK_PROXY_HOST = NullableStringProperty("blackduck.proxy.host").apply {
            info("Proxy Host", "4.2.0")
            help("Hostname for proxy server.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_IGNORED_HOSTS = StringListProperty("blackduck.proxy.ignored.hosts", emptyList()).apply {
            info("Bypass Proxy Hosts", "4.2.0")
            help("A comma separated list of regular expression host patterns that should not use the proxy.", "These patterns must adhere to Java regular expressions: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_NTLM_DOMAIN = NullableStringProperty("blackduck.proxy.ntlm.domain").apply {
            info("NTLM Proxy Domain", "4.2.0")
            help("NTLM Proxy domain.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_NTLM_WORKSTATION = NullableStringProperty("blackduck.proxy.ntlm.workstation").apply {
            info("NTLM Proxy Workstation", "4.2.0")
            help("NTLM Proxy workstation.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_PASSWORD = NullableStringProperty("blackduck.proxy.password").apply {
            info("Proxy Password", "4.2.0")
            help("Proxy password.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_PORT = NullableStringProperty("blackduck.proxy.port").apply {
            info("Proxy Port", "4.2.0")
            help("Proxy port.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_PROXY_USERNAME = NullableStringProperty("blackduck.proxy.username").apply {
            info("Proxy Username", "4.2.0")
            help("Proxy username.")
            groups(DetectGroup.Proxy, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_TIMEOUT = IntegerProperty("blackduck.timeout", 120).apply {
            info("Black Duck Timeout", "4.2.0")
            help("The time to wait for network connections to complete (in seconds).")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_TRUST_CERT = BooleanProperty("blackduck.trust.cert", false).apply {
            info("Trust All SSL Certificates", "4.2.0")
            help("If true, automatically trust the certificate for the current run of Detect only.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val BLACKDUCK_URL = NullableStringProperty("blackduck.url").apply {
            info("Black Duck URL", "4.2.0")
            help("URL of the Black Duck server.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
        }
        val BLACKDUCK_USERNAME = NullableStringProperty("blackduck.username").apply {
            info("Black Duck Username", "4.2.0")
            help("Black Duck username.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
        }
        val DETECT_PARALLEL_PROCESSORS = IntegerProperty("detect.parallel.processors", 1).apply {
            info("Detect Parallel Processors", "6.0.0")
            help("The number of threads to run processes in parallel, defaults to 1, but if you specify less than or equal to 0, the number of processors on the machine will be used.")
            groups(DetectGroup.General, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_BASH_PATH = NullableStringProperty("detect.bash.path").apply {
            info("Bash Executable", "3.0.0")
            help("Path to the Bash executable.", "If set, Detect will use the given Bash executable instead of searching for one.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_BAZEL_PATH = NullableStringProperty("detect.bazel.path").apply {
            info("Bazel Executable", "5.2.0")
            help("The path to the Bazel executable.")
            groups(DetectGroup.Bazel, DetectGroup.Global)
        }
        val DETECT_BAZEL_TARGET = NullableStringProperty("detect.bazel.target").apply {
            info("Bazel Target", "5.2.0")
            help("The Bazel target (for example, //foo:foolib) for which dependencies are collected. For Detect to run Bazel, this property must be set.")
            groups(DetectGroup.Bazel, DetectGroup.SourceScan)
        }
        val DETECT_BAZEL_CQUERY_OPTIONS = StringListProperty("detect.bazel.cquery.options", emptyList()).apply {
            info("Bazel cquery additional options", "6.1.0")
            help("A comma-separated list of additional options to pass to the bazel cquery command.")
            groups(DetectGroup.Bazel, DetectGroup.SourceScan)
        }
        val DETECT_BAZEL_DEPENDENCY_RULE = EnumProperty("detect.bazel.dependency.type", WorkspaceRule.UNSPECIFIED, WorkspaceRule::valueOf, WorkspaceRule.values().toList()).apply {
            info("Bazel workspace external dependency rule", "6.0.0")
            help("The Bazel workspace rule used to pull in external dependencies. If not set, Detect will attempt to determine the rule from the contents of the WORKSPACE file.")
            groups(DetectGroup.Bazel, DetectGroup.SourceScan)
        }
        val DETECT_BDIO_OUTPUT_PATH = NullableStringProperty("detect.bdio.output.path").apply {
            info("BDIO Output Directory", "3.0.0")
            help("The path to the output directory for all BDIO files.", "If not set, the BDIO files are placed in a 'BDIO' subdirectory of the output directory.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_BDIO2_ENABLED = BooleanProperty("detect.bdio2.enabled", false).apply {
            info("BDIO 2 Enabled", "6.1.0")
            help("The version of BDIO files to generate.", "If set to false, BDIO version 1 will be generated. If set to true, BDIO version 2 will be generated.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_BINARY_SCAN_FILE = NullableStringProperty("detect.binary.scan.file.path").apply {
            info("Binary Scan Target", "4.2.0")
            help("If specified, this file and this file only will be uploaded for binary scan analysis. This property takes precedence over detect.binary.scan.file.name.patterns.")
            groups(DetectGroup.SignatureScanner, DetectGroup.SourcePath)
        }
        val DETECT_BINARY_SCAN_FILE_NAME_PATTERNS = NullableStringListProperty("detect.binary.scan.file.name.patterns").apply {
            info("Binary Scan Filename Patterns", "6.0.0")
            help("If specified, all files in the source directory whose names match these file name patterns will be zipped and uploaded for binary scan analysis. This property will not be used if detect.binary.scan.file.path is specified.")
            groups(DetectGroup.SignatureScanner, DetectGroup.SourcePath)
        }
        val DETECT_BITBAKE_BUILD_ENV_NAME = StringProperty("detect.bitbake.build.env.name", "oe-init-build-env").apply {
            info("BitBake Init Script Name", "4.4.0")
            help("The name of the build environment init script.")
            groups(DetectGroup.Bitbake, DetectGroup.SourceScan)
        }
        val DETECT_BITBAKE_PACKAGE_NAMES = NullableStringListProperty("detect.bitbake.package.names").apply {
            info("BitBake Package Names", "4.4.0")
            help("A comma-separated list of package names from which dependencies are extracted.")
            groups(DetectGroup.Bitbake, DetectGroup.SourceScan)
        }
        val DETECT_BITBAKE_SOURCE_ARGUMENTS = StringListProperty("detect.bitbake.source.arguments", emptyList()).apply {
            info("BitBake Source Arguments", "6.0.0")
            help("A comma-separated list of arguments to supply when sourcing the build environment init script.")
            groups(DetectGroup.Bitbake, DetectGroup.SourceScan)
        }
        val DETECT_BITBAKE_SEARCH_DEPTH = IntegerProperty("detect.bitbake.search.depth", 1).apply {
            info("BitBake Search Depth", "6.1.0")
            help("The depth at which Detect will search for the recipe-depends.dot or package-depends.dot files.")
            groups(DetectGroup.Bitbake, DetectGroup.SourceScan)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS = NullableStringProperty("detect.blackduck.signature.scanner.arguments").apply {
            info("Signature Scanner Arguments", "4.2.0")
            help("Additional arguments to use when running the Black Duck signature scanner.", "For example: Suppose you are running in bash on Linux and want to use the signature scanner's ability to read a list of directories to exclude from a file (using the signature scanner --exclude-from option). You tell the signature scanner read excluded directories from a file named excludes.txt in your home directory with: --detect.blackduck.signature.scanner.arguments='--exclude-from \${HOME}/excludes.txt'")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN = BooleanProperty("detect.blackduck.signature.scanner.dry.run", false).apply {
            info("Signature Scanner Dry Run", "4.2.0")
            help("If set to true, the signature scanner results are not uploaded to Black Duck, and the scanner results are written to disk.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = StringListProperty("detect.blackduck.signature.scanner.exclusion.name.patterns", listOf("node_modules")).apply {
            info("Directory Name Exclusion Patterns", "4.2.0")
            help("A comma-separated list of directory name patterns for which Detect searches and adds to the signature scanner --exclude flag values.", "These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters). Detect will recursively search within the scan targets for files/directories that match these patterns and will create the corresponding exclusion patterns (paths relative to the scan target directory) for the signature scanner (Black Duck scan CLI). Please note that the signature scanner will only exclude directories; matched filenames will be passed to the signature scanner but will have no effect. These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns and passed as --exclude values. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.name.patterns=blackduck-common, --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-common', --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-*'. Use this property when you want Detect to convert the given patterns to actual paths. Use detect.blackduck.signature.scanner.exclusion.patterns to pass patterns directly to the signature scanner as-is.")
            groups(DetectGroup.SignatureScanner, DetectGroup.SourceScan)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH = IntegerProperty("detect.blackduck.signature.scanner.exclusion.pattern.search.depth", 4).apply {
            info("Exclusion Patterns Search Depth", "5.0.0")
            help("Enables you to adjust the depth to which Detect will search when creating signature scanner exclusion patterns.")
            groups(DetectGroup.SignatureScanner, DetectGroup.SourceScan)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = NullableStringListProperty("detect.blackduck.signature.scanner.exclusion.patterns").apply {
            info("Exclusion Patterns", "4.2.0")
            help("A comma-separated list of values to be used with the Signature Scanner --exclude flag.", "Each pattern provided is passed to the signature scanner (Black Duck scan CLI) as a value for an --exclude option. The signature scanner requires that these exclusion patterns start and end with a forward slash (/) and may not contain double asterisks (**). These patterns will be added to the paths created from detect.blackduck.signature.scanner.exclusion.name.patterns and passed as --exclude values. Use this property to pass patterns directly to the signature scanner as-is. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude from signature scanning. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.patterns=/blackduck-common/, --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-common/', --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-*/'. Use detect.blackduck.signature.scanner.exclusion.name.patterns when you want Detect to convert the given patterns to actual paths.")
            groups(DetectGroup.SignatureScanner, DetectGroup.SourceScan)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL = NullableStringProperty("detect.blackduck.signature.scanner.host.url").apply {
            info("Signature Scanner Host URL", "4.2.0")
            help("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Black Duck's urls for different operating systems.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH = NullableStringProperty("detect.blackduck.signature.scanner.local.path").apply {
            info("Signature Scanner Local Path", "4.2.0")
            help("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY = IntegerProperty("detect.blackduck.signature.scanner.memory", 4096).apply {
            info("Signature Scanner Memory", "4.2.0")
            help("The memory for the scanner to use.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = NullableStringProperty("detect.blackduck.signature.scanner.offline.local.path").apply {
            info("Signature Scanner Local Path (Offline)", "4.2.0")
            help("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS = NullableStringListProperty("detect.blackduck.signature.scanner.paths").apply {
            info("Signature Scanner Target Paths", "4.2.0")
            help("These paths and only these paths will be scanned.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING = ExtendedEnumProperty("detect.blackduck.signature.scanner.snippet.matching", ExtendedValue(ExtendedSnippetMode.NONE), ExtendedSnippetMode::valueOf, SnippetMatching::valueOf, ExtendedSnippetMode.values().toList(), SnippetMatching.values().toList()).apply {
            info("Snippet Matching", "5.5.0")
            help("Use this value to enable the various snippet scanning modes. For a full explanation, please refer to the 'Running a component scan using the Signature Scanner command line' section in your Black Duck server's online help.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE = BooleanProperty("detect.blackduck.signature.scanner.upload.source.mode", false).apply {
            info("Upload source mode", "5.4.0")
            help("If set to true, the signature scanner will, if supported by your Black Duck version, upload source code to Black Duck.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_BOM_AGGREGATE_NAME = NullableStringProperty("detect.bom.aggregate.name").apply {
            info("Aggregate BDIO File Name", "3.0.0")
            help("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_BOM_AGGREGATE_REMEDIATION_MODE = EnumProperty("detect.bom.aggregate.remediation.mode", AggregateMode.TRANSITIVE, AggregateMode::valueOf, AggregateMode.values().toList()).apply {
            info("BDIO Aggregate Remediation Mode", "6.1.0")
            help("If an aggregate BDIO file is being generated and this property is set to DIRECT, the aggregate BDIO file will exclude code location nodes from the top layer of the dependency tree to preserve the correct identification of direct dependencies in the resulting Black Duck BOM. When this property is set to TRANSITIVE (the default), component source information is preserved by including code location nodes at the top of the dependency tree, but all components will appear as TRANSITIVE in the BOM.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_BUILDLESS = BooleanProperty("detect.detector.buildless", false).apply {
            info("Buildless Mode", "5.4.0")
            help("If set to true, only Detector's capable of running without a build will be run.")
            groups(DetectGroup.General, DetectGroup.Global)
        }
        val DETECT_CLEANUP = BooleanProperty("detect.cleanup", true).apply {
            info("Cleanup Output", "3.2.0")
            help("If true, the files created by Detect will be cleaned up.")
            groups(DetectGroup.Cleanup, DetectGroup.Global)
        }
        val DETECT_CLONE_PROJECT_VERSION_NAME = NullableStringProperty("detect.clone.project.version.name").apply {
            info("Clone Project Version Name", "4.2.0")
            help("The name of the project version to clone this project version from. Respects the given Clone Categories in detect.project.clone.categories or as set on the Black Duck server.")
            groups(DetectGroup.Project, DetectGroup.Global, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_CLONE_PROJECT_VERSION_LATEST = NullableBooleanProperty("detect.clone.project.version.latest").apply {
            info("Clone Latest Project Version", "5.6.0")
            help("If set to true, detect will attempt to use the latest project version as the clone for this project. The project must exist and have at least one version.")
            groups(DetectGroup.Project, DetectGroup.Global, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_CODE_LOCATION_NAME = NullableStringProperty("detect.code.location.name").apply {
            info("Scan Name", "4.0.0")
            help("An override for the name Detect will use for the scan file it creates. If supplied and multiple scans are found, Detect will append an index to each scan name.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_CONDA_ENVIRONMENT_NAME = NullableStringProperty("detect.conda.environment.name").apply {
            info("Anaconda Environment Name", "3.0.0")
            help("The name of the anaconda environment used by your project.")
            groups(DetectGroup.Conda, DetectGroup.SourceScan)
        }
        val DETECT_CONDA_PATH = NullableStringProperty("detect.conda.path").apply {
            info("Conda Executable", "3.0.0")
            help("The path to the conda executable.")
            groups(DetectGroup.Conda, DetectGroup.Global)
        }
        val DETECT_CPAN_PATH = NullableStringProperty("detect.cpan.path").apply {
            info("cpan Executable", "3.0.0")
            help("The path to the cpan executable.")
            groups(DetectGroup.Cpan, DetectGroup.Global)
        }
        val DETECT_CPANM_PATH = NullableStringProperty("detect.cpanm.path").apply {
            info("cpanm Executable", "3.0.0")
            help("The path to the cpanm executable.")
            groups(DetectGroup.Cpan, DetectGroup.Global)
        }
        val DETECT_DEFAULT_PROJECT_VERSION_SCHEME = EnumProperty("detect.default.project.version.scheme", DefaultVersionNameScheme.DEFAULT, DefaultVersionNameScheme::valueOf, DefaultVersionNameScheme.values().toList()).apply {
            info("Default Project Version Name Scheme", "3.0.0")
            help("The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'.")
            groups(DetectGroup.Project, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DEFAULT_PROJECT_VERSION_TEXT = StringProperty("detect.default.project.version.text", "Default Detect Version").apply {
            info("Default Project Version Name Text", "3.0.0")
            help("The text to use as the default project version.")
            groups(DetectGroup.Project, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT = StringProperty("detect.default.project.version.timeformat", "yyyy-MM-dd\'T\'HH:mm:ss.SSS").apply {
            info("Default Project Version Name Timestamp Format", "3.0.0")
            help("The timestamp format to use as the default project version.")
            groups(DetectGroup.Project, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_DEPTH = IntegerProperty("detect.detector.search.depth", 0).apply {
            info("Detector Search Depth", "3.2.0")
            help("Depth of subdirectories within the source directory to which Detect will search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_DETECTOR_SEARCH_CONTINUE = BooleanProperty("detect.detector.search.continue", false).apply {
            info("Detector Search Continue", "3.2.0")
            help("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. " +
                    "If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_EXCLUSION = NullableStringListProperty("detect.detector.search.exclusion").apply {
            info("Detector Directory Exclusions", "3.2.0")
            help("A comma-separated list of directory names to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name appear in this list will not be searched.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS = NullableStringListProperty("detect.detector.search.exclusion.patterns").apply {
            info(" Detector Directory Patterns Exclusions", "3.2.0")
            help("A comma-separated list of directory name patterns to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name match a pattern in this list will not be searched. These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters).For example, suppose you're running in bash on Linux, you've set--detect.detector.search.depth = 1, and have a subdirectory named blackduck-common (a gradle project) that you want to exclude from the detector search. Any of the following would exclude it:--detect.detector.search.exclusion.patterns = blackduck-common, --detect.detector.search.exclusion.patterns = 'blackduck-common', --detect.detector.search.exclusion.patterns = 'blackduck-*'")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS = NullableStringListProperty("detect.detector.search.exclusion.paths").apply {
            info(" Detector Directory Path Exclusions", "5.5.0")
            help("A comma-separated list of directory paths to exclude from detector search. (E.g. 'foo/bar/biz' will only exclude the 'biz' directory if the parent directory structure is 'foo/bar/'.)", "This property performs the same basic function as detect.detector.search.exclusion, but lets you be more specific.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_EXCLUSION_FILES = StringListProperty("detect.detector.search.exclusion.files", emptyList()).apply {
            info(" Detector File Exclusions", "6.0.0")
            help("A comma-separated list of file names to exclude from detector search.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS = BooleanProperty("detect.detector.search.exclusion.defaults", true).apply {
            info("Detector Exclude Default Directories", "3.2.0")
            help("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the detector search: bin, build, .git, .gradle, node_modules, out, packages, target.")
            groups(DetectGroup.Paths, DetectGroup.Detector, DetectGroup.Global, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_IGNORE_CONNECTION_FAILURES = BooleanProperty("detect.ignore.connection.failures", false).apply {
            info("Detect Ignore Connection Failures", "5.3.0")
            help("If true, Detect will ignore any products that it cannot connect to.", "If true, when Detect attempts to boot a product it will also check if it can communicate with it - if it cannot, it will not run the product.")
            groups(DetectGroup.General, DetectGroup.BlackduckServer, DetectGroup.Polaris)
            category(DetectCategory.Advanced)
        }
        val PHONEHOME_PASSTHROUGH = PassthroughProperty("detect.phone.home.passthrough").apply {
            info("Phone Home Passthrough", "6.0.0")
            help("Additional values may be sent home for usage information. The keys will be sent without the prefix.")
            groups(DetectGroup.Docker, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val DOCKER_PASSTHROUGH = PassthroughProperty("detect.docker.passthrough").apply {
            info("Docker Passthrough", "6.0.0")
            help("Additional properties may be passed to the docker inspector by adding the prefix detect.docker.passthrough. The keys will be given to docker inspector without the prefix.")
            groups(DetectGroup.Docker, DetectGroup.Default)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_IMAGE = NullableStringProperty("detect.docker.image").apply {
            info("Docker Image Name", "3.0.0")
            help("The Docker image name to inspect. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.")
            groups(DetectGroup.Docker, DetectGroup.SourcePath)
        }
        val DETECT_DOCKER_IMAGE_ID = NullableStringProperty("detect.docker.image.id").apply {
            info("Docker Image ID", "6.1.0")
            help("The Docker image ID to inspect.")
            groups(DetectGroup.Docker, DetectGroup.SourcePath)
        }
        val DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH = NullableStringProperty("detect.docker.inspector.air.gap.path").apply {
            info("Docker Inspector AirGap Path", "3.0.0")
            help("The path to the directory containing the Docker Inspector jar and images.")
            groups(DetectGroup.Docker, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_INSPECTOR_PATH = NullableStringProperty("detect.docker.inspector.path").apply {
            info("Docker Inspector .jar File Path", "3.0.0")
            help("This is used to override using the hosted Docker Inspector .jar file by binary repository url. You can use a local Docker Inspector .jar file at this path.")
            groups(DetectGroup.Docker, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_INSPECTOR_VERSION = NullableStringProperty("detect.docker.inspector.version").apply {
            info("Docker Inspector Version", "3.0.0")
            help("Version of the Docker Inspector to use. By default Detect will attempt to automatically determine the version to use.")
            groups(DetectGroup.Docker, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_PATH = NullableStringProperty("detect.docker.path").apply {
            info("Docker Executable", "3.0.0")
            help("Path to the docker executable.")
            groups(DetectGroup.Docker, DetectGroup.Global)
        }
        val DETECT_DOCKER_PATH_REQUIRED = BooleanProperty("detect.docker.path.required", false).apply {
            info("Run Without Docker in Path", "4.0.0")
            help("If set to true, Detect will attempt to run the Docker Inspector only if it finds a docker client executable.")
            groups(DetectGroup.Docker, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_PLATFORM_TOP_LAYER_ID = NullableStringProperty("detect.docker.platform.top.layer.id").apply {
            info("Platform Top Layer ID", "6.1.0")
            help("To exclude components from platform layers from the results, assign to this property the ID of the top layer of the platform image. Get the platform top layer ID from the output of 'docker inspect platformimage:tag'. The platform top layer ID is the last item in RootFS.Layers. For more information, see 'Isolating application components' in the Docker Inspector documentation.", "If you are interested in components from the application layers of your image, but not interested in components from the underlying platform layers, you can exclude components from platform layers from the results by using this property to specify the boundary between platform layers and application layers. ")
            groups(DetectGroup.Docker, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_DOCKER_TAR = NullableStringProperty("detect.docker.tar").apply {
            info("Docker Image Archive File", "3.0.0")
            help("A saved Docker image - must be a .tar file. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.")
            groups(DetectGroup.Docker, DetectGroup.SourcePath)
        }
        val DETECT_DOTNET_PATH = NullableStringProperty("detect.dotnet.path").apply {
            info("dotnet Executable", "4.4.0")
            help("The path to the dotnet executable.")
            groups(DetectGroup.Nuget, DetectGroup.Global)
        }
        val DETECT_EXCLUDED_DETECTOR_TYPES = FilterableEnumListProperty("detect.excluded.detector.types", emptyList(), DetectorType::valueOf, DetectorType.values().toList()).apply {
            info("Detector Types Excluded", "3.0.0")
            help("By default, all detectors will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all detectors, specify \"ALL\". Exclusion rules always win.", "If Detect runs one or more detector on your project that you would like to exclude, you can use this property to prevent Detect from running them.")
            groups(DetectGroup.Detector, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_FORCE_SUCCESS = BooleanProperty("detect.force.success", false).apply {
            info("Force Success", "3.0.0")
            help("If true, Detect will always exit with code 0.")
            groups(DetectGroup.General, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_GIT_PATH = NullableStringProperty("detect.git.path").apply {
            info("Git Executable", "5.5.0")
            help("Path of the git executable")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_GO_PATH = NullableStringProperty("detect.go.path").apply {
            info("Go Executable", "3.0.0")
            help("Path to the Go executable.")
            groups(DetectGroup.Go, DetectGroup.Global)
        }
        val DETECT_GRADLE_BUILD_COMMAND = NullableStringProperty("detect.gradle.build.command").apply {
            info("Gradle Build Command", "3.0.0")
            help("Gradle command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the gradle (or gradlew) command with one task: dependencies. You can use this property to insert one or more additional gradle command line arguments (options or tasks) before the dependencies argument.")
            groups(DetectGroup.Gradle, DetectGroup.SourceScan)
        }
        val DETECT_GRADLE_EXCLUDED_CONFIGURATIONS = NullableStringProperty("detect.gradle.excluded.configurations").apply {
            info("Gradle Exclude Configurations", "3.0.0")
            help("A comma-separated list of Gradle configurations to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle configurations specified via this property.")
            groups(DetectGroup.Gradle, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_EXCLUDED_PROJECTS = NullableStringProperty("detect.gradle.excluded.projects").apply {
            info("Gradle Exclude Projects", "3.0.0")
            help("A comma-separated list of Gradle sub-projects to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle sub-projects specified via this property.")
            groups(DetectGroup.Gradle, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_INCLUDED_CONFIGURATIONS = NullableStringProperty("detect.gradle.included.configurations").apply {
            info("Gradle Include Configurations", "3.0.0")
            help("A comma-separated list of Gradle configurations to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those Gradle configurations specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
            groups(DetectGroup.Gradle, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_INCLUDED_PROJECTS = NullableStringProperty("detect.gradle.included.projects").apply {
            info("Gradle Include Projects", "3.0.0")
            help("A comma-separated list of Gradle sub-projects to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those sub-projects specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
            groups(DetectGroup.Gradle, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH = NullableStringProperty("detect.gradle.inspector.air.gap.path").apply {
            info("Gradle Inspector AirGap Path", "3.0.0")
            help("The path to the directory containing the air gap dependencies for the gradle inspector.", "Use this property when running Detect on a Gradle project in 'air gap' mode (offline). Download and unzip the Detect air gap zip file, and point this property to the packaged-inspectors/gradle directory.")
            groups(DetectGroup.Gradle, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_INSPECTOR_VERSION = NullableStringProperty("detect.gradle.inspector.version").apply {
            info("Gradle Inspector Version", "3.0.0")
            help("The version of the Gradle Inspector that Detect should use. By default, Detect will try to automatically determine the correct Gradle Inspector version.", "The Detect Gradle detector uses a separate program, the Gradle Inspector, to discover dependencies from Gradle projects. Detect automatically downloads the Gradle Inspector as needed. Use the property to use a specific version of the Gradle Inspector.")
            groups(DetectGroup.Gradle, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_GRADLE_PATH = NullableStringProperty("detect.gradle.path").apply {
            info("Gradle Executable", "3.0.0")
            help("The path to the Gradle executable (gradle or gradlew).", "If set, Detect will use the given Gradle executable instead of searching for one.")
            groups(DetectGroup.Gradle)
        }
        val DETECT_HEX_REBAR3_PATH = NullableStringProperty("detect.hex.rebar3.path").apply {
            info("Rebar3 Executable", "3.0.0")
            help("The path to the rebar3 executable.")
            groups(DetectGroup.Hex, DetectGroup.Global)
        }
        val DETECT_INCLUDED_DETECTOR_TYPES = FilterableEnumListProperty("detect.included.detector.types", emptyList(), DetectorType::valueOf, DetectorType.values().toList()).apply {
            info("Detector Types Included", "3.0.0")
            help("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", "If you want to limit Detect to a subset of its detectors, use this property to specify that subset.")
            groups(DetectGroup.Detector, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_JAVA_PATH = NullableStringProperty("detect.java.path").apply {
            info("Java Executable", "5.0.0")
            help("Path to the java executable.", "If set, Detect will use the given java executable instead of searching for one.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_MAVEN_BUILD_COMMAND = NullableStringProperty("detect.maven.build.command").apply {
            info("Maven Build Command", "3.0.0")
            help("Maven command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the mvn (or mvnw) command with one argument: dependency:tree. You can use this property to insert one or more additional mvn command line arguments (goals, etc.) before the dependency:tree argument. For example: suppose you are running in bash on Linux, and want to point maven to your settings file (maven_dev_settings.xml in your home directory) and assign the value 'other' to property 'reason'. You could do this with: --detect.maven.build.command='--settings \${HOME}/maven_dev_settings.xml --define reason=other'")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
        }
        val DETECT_MAVEN_EXCLUDED_MODULES = NullableStringProperty("detect.maven.excluded.modules").apply {
            info("Maven Modules Excluded", "3.0.0")
            help("A comma-separated list of Maven modules (sub-projects) to exclude.", "As Detect parses the mvn dependency:tree output for dependencies, Detect will skip any Maven modules specified via this property.")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_MAVEN_INCLUDED_MODULES = NullableStringProperty("detect.maven.included.modules").apply {
            info("Maven Modules Included", "3.0.0")
            help("A comma-separated list of Maven modules (sub-projects) to include.", "As Detect parses the mvn dependency:tree output for dependencies, if this property is set, Detect will include only those Maven modules specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_MAVEN_PATH = NullableStringProperty("detect.maven.path").apply {
            info("Maven Executable", "3.0.0")
            help("The path to the Maven executable (mvn or mvnw).", "If set, Detect will use the given Maven executable instead of searching for one.")
            groups(DetectGroup.Maven, DetectGroup.Global)
        }
        val DETECT_MAVEN_INCLUDED_SCOPES = NullableStringProperty("detect.maven.included.scopes").apply {
            info("Dependency Scope Included", "6.0.0")
            help("A comma separated list of Maven scopes. Output will be limited to dependencies within these scopes (overridden by exclude).", "If set, Detect will include only dependencies of the given Maven scope.")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
        }
        val DETECT_MAVEN_EXCLUDED_SCOPES = NullableStringProperty("detect.maven.excluded.scopes").apply {
            info("Dependency Scope Excluded", "6.0.0")
            help("A comma separated list of Maven scopes. Output will be limited to dependencies outside these scopes (overrides include).", "If set, Detect will include only dependencies outside of the given Maven scope.")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
        }
        val DETECT_MAVEN_INCLUDE_PLUGINS = BooleanProperty("detect.maven.include.plugins", false).apply {
            info("Maven Include Plugins", "5.6.0")
            help("Whether or not detect will include the plugins section when parsing a pom.xml.")
            groups(DetectGroup.Maven, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_NOTICES_REPORT = BooleanProperty("detect.notices.report", false).apply {
            info("Generate Notices Report", "3.0.0")
            help("When set to true, a Black Duck notices report in text form will be created in your source directory.")
            groups(DetectGroup.Report, DetectGroup.Global)
        }
        val DETECT_NOTICES_REPORT_PATH = NullableStringProperty("detect.notices.report.path").apply {
            info("Notices Report Path", "3.0.0")
            help("The output directory for notices report. Default is the source directory.")
            groups(DetectGroup.Report, DetectGroup.Global, DetectGroup.ReportSetting)
        }
        val DETECT_NPM_ARGUMENTS = NullableStringProperty("detect.npm.arguments").apply {
            info("Additional NPM Command Arguments", "4.3.0")
            help("A space-separated list of additional arguments to add to the npm command line when running Detect against an NPM project.")
            groups(DetectGroup.Npm, DetectGroup.SourceScan)
        }
        val DETECT_NPM_INCLUDE_DEV_DEPENDENCIES = BooleanProperty("detect.npm.include.dev.dependencies", true).apply {
            info("Include NPM Development Dependencies", "3.0.0")
            help("Set this value to false if you would like to exclude your dev dependencies when ran.")
            groups(DetectGroup.Npm, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_NPM_PATH = NullableStringProperty("detect.npm.path").apply {
            info("NPM Executable", "3.0.0")
            help("The path to the Npm executable.")
            groups(DetectGroup.Npm, DetectGroup.Global)
        }
        val DETECT_NUGET_CONFIG_PATH = NullableStringProperty("detect.nuget.config.path").apply {
            info("Nuget Config File", "4.0.0")
            help("The path to the Nuget.Config file to supply to the nuget exe.")
            groups(DetectGroup.Nuget, DetectGroup.SourceScan)
        }
        val DETECT_NUGET_EXCLUDED_MODULES = NullableStringProperty("detect.nuget.excluded.modules").apply {
            info("Nuget Projects Excluded", "3.0.0")
            help("The names of the projects in a solution to exclude.")
            groups(DetectGroup.Nuget, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_NUGET_IGNORE_FAILURE = BooleanProperty("detect.nuget.ignore.failure", false).apply {
            info("Ignore Nuget Failures", "3.0.0")
            help("If true errors will be logged and then ignored.")
            groups(DetectGroup.Nuget, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_NUGET_INCLUDED_MODULES = NullableStringProperty("detect.nuget.included.modules").apply {
            info("Nuget Modules Included", "3.0.0")
            help("The names of the projects in a solution to include (overrides exclude).")
            groups(DetectGroup.Nuget, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_NUGET_INSPECTOR_AIR_GAP_PATH = NullableStringProperty("detect.nuget.inspector.air.gap.path").apply {
            info("Nuget Inspector AirGap Path", "3.0.0")
            help("The path to the directory containing the nuget inspector nupkg.")
            groups(DetectGroup.Nuget, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_NUGET_INSPECTOR_VERSION = NullableStringProperty("detect.nuget.inspector.version").apply {
            info("Nuget Inspector Version", "3.0.0")
            help("Version of the Nuget Inspector. By default Detect will run the latest version that is compatible with the Detect version.")
            groups(DetectGroup.Nuget, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_NUGET_PACKAGES_REPO_URL = StringListProperty("detect.nuget.packages.repo.url", listOf("https://api.nuget.org/v3/index.json")).apply {
            info("Nuget Packages Repository URL", "3.0.0")
            help("The source for nuget packages", "Set this to \"https://www.nuget.org/api/v2/\" if your are still using a nuget client expecting the v2 api.")
            groups(DetectGroup.Nuget, DetectGroup.Global)
        }
        val DETECT_OUTPUT_PATH = NullableStringProperty("detect.output.path").apply {
            info("Detect Output Path", "3.0.0")
            help("The path to the output directory.", "If set, Detect will use the given directory to store files that it downloads and creates, instead of using the default location (~/blackduck).")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_TOOLS_OUTPUT_PATH = NullableStringProperty("detect.tools.output.path").apply {
            info("Detect Tools Output Path", "5.6.0")
            help("The path to the tools directory where detect should download and/or access things like the Signature Scanner that it shares over multiple runs.", "If set, Detect will use the given directory instead of using the default location of output path plus tools.")
            groups(DetectGroup.Paths, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES = BooleanProperty("detect.packagist.include.dev.dependencies", true).apply {
            info("Include Packagist Development Dependencies", "3.0.0")
            help("Set this value to false if you would like to exclude your dev requires dependencies when ran.")
            groups(DetectGroup.Packagist, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_PEAR_ONLY_REQUIRED_DEPS = BooleanProperty("detect.pear.only.required.deps", false).apply {
            info("Include Only Required Pear Dependencies", "3.0.0")
            help("Set to true if you would like to include only required packages.")
            groups(DetectGroup.Pear, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_PEAR_PATH = NullableStringProperty("detect.pear.path").apply {
            info("Pear Executable", "3.0.0")
            help("The path to the pear executable.")
            groups(DetectGroup.Pear, DetectGroup.Global)
        }
        val DETECT_PIP_PROJECT_NAME = NullableStringProperty("detect.pip.project.name").apply {
            info("PIP Project Name", "3.0.0")
            help("The name of your PIP project, to be used if your project's name cannot be correctly inferred from its setup.py file.")
            groups(DetectGroup.Pip, DetectGroup.SourceScan)
        }
        val DETECT_PIP_PROJECT_VERSION_NAME = NullableStringProperty("detect.pip.project.version.name").apply {
            info("PIP Project Version Name", "4.1.0")
            help("The version of your PIP project, to be used if your project's version name cannot be correctly inferred from its setup.py file.")
            groups(DetectGroup.Pip, DetectGroup.SourceScan)
        }
        val DETECT_PIP_REQUIREMENTS_PATH = StringListProperty("detect.pip.requirements.path", emptyList()).apply {
            info("PIP Requirements Path", "3.0.0")
            help("A comma-separated list of paths to requirements.txt files.")
            groups(DetectGroup.Pip, DetectGroup.SourceScan)
        }
        val DETECT_PIP_ONLY_PROJECT_TREE = BooleanProperty("detect.pip.only.project.tree", false).apply {
            info("PIP Include Only Project Tree", "6.1.0")
            help("By default, pipenv includes all dependencies found in the graph. Set to true to only include dependencies found underneath the dependency that matches the provided pip project and version name.")
            groups(DetectGroup.Pip, DetectGroup.SourceScan)
        }
        val DETECT_PIPENV_PATH = NullableStringProperty("detect.pipenv.path").apply {
            info("Pipenv Executable", "4.1.0")
            help("The path to the Pipenv executable.")
            groups(DetectGroup.Pip, DetectGroup.Global)
        }
        val DETECT_SWIFT_PATH = NullableStringProperty("detect.swift.path").apply {
            info("Swift Executable", "6.0.0")
            help("Path of the swift executable.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES = FilterableEnumListProperty("detect.policy.check.fail.on.severities", listOf(None<PolicySeverityType>()), PolicySeverityType::valueOf, PolicySeverityType.values().toList()).apply {
            info("Fail on Policy Violation Severities", "3.0.0")
            help("A comma-separated list of policy violation severities that will fail Detect. If this is set to NONE, Detect will not fail due to policy violations. A value of ALL is equivalent to all of the other possible values except NONE.")
            groups(DetectGroup.Project, DetectGroup.Global, DetectGroup.ProjectSetting, DetectGroup.Policy)
        }
        val DETECT_PROJECT_APPLICATION_ID = NullableStringProperty("detect.project.application.id").apply {
            info("Application ID", "5.2.0")
            help("Sets the 'Application ID' project setting.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_CUSTOM_FIELDS_PROJECT = NullableStringProperty("detect.custom.fields.project").apply {
            info("Custom Fields", "5.6.0")
            help("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.project[0].label='example' and detect.custom.fields.project[0].value='one,two'. Note that these will not show up in the detect configuration log.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_CUSTOM_FIELDS_VERSION = NullableStringProperty("detect.custom.fields.version").apply {
            info("Custom Fields", "5.6.0")
            help("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.version[0].label='example' and detect.custom.fields.version[0].value='one,two'. Note that these will not show up in the detect configuration log.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_CLONE_CATEGORIES = EnumListProperty("detect.project.clone.categories", listOf(ProjectCloneCategoriesType.COMPONENT_DATA, ProjectCloneCategoriesType.VULN_DATA), ProjectCloneCategoriesType::valueOf, ProjectCloneCategoriesType.values().toList()).apply {
            info("Clone Project Categories", "4.2.0")
            help("An override for the Project Clone Categories that are used when cloning a version. If the project already exists, make sure to use --detect.project.version.update to make sure these are set.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_CODELOCATION_PREFIX = NullableStringProperty("detect.project.codelocation.prefix").apply {
            info("Scan Name Prefix", "3.0.0")
            help("A prefix to the name of the scans created by Detect. Useful for running against the same projects on multiple machines.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_CODELOCATION_SUFFIX = NullableStringProperty("detect.project.codelocation.suffix").apply {
            info("Scan Name Suffix", "3.0.0")
            help("A suffix to the name of the scans created by Detect.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_CODELOCATION_UNMAP = BooleanProperty("detect.project.codelocation.unmap", false).apply {
            info("Unmap All Other Scans for Project", "4.0.0")
            help("If set to true, unmaps all other scans mapped to the project version produced by the current run of Detect.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_DESCRIPTION = NullableStringProperty("detect.project.description").apply {
            info("Project Description", "4.0.0")
            help("If project description is specified, your project version will be created with this description.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_USER_GROUPS = StringListProperty("detect.project.user.groups", emptyList()).apply {
            info("Project User Groups", "5.4.0")
            help("A comma-separated list of names of user groups to add to the project.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_TAGS = StringListProperty("detect.project.tags", emptyList()).apply {
            info("Project Tags", "5.6.0")
            help("A comma-separated list of tags to add to the project.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_DETECTOR = NullableStringProperty("detect.project.detector").apply {
            info("Project Name/Version Detector", "4.0.0")
            help("The detector that will be used to determine the project name and version when multiple detector types. This property should be used with the detect.project.tool.", "If Detect finds that multiple detectors apply, this property can be used to select the detector that will provide the project name and version. When using this property, you should also set detect.project.tool=DETECTOR")
            groups(DetectGroup.Paths, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_LEVEL_ADJUSTMENTS = BooleanProperty("detect.project.level.adjustments", true).apply {
            info("Allow Project Level Adjustments", "3.0.0")
            help("An override for the Project level matches.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_NAME = NullableStringProperty("detect.project.name").apply {
            info("Project Name", "3.0.0")
            help("An override for the name to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PARENT_PROJECT_NAME = NullableStringProperty("detect.parent.project.name").apply {
            info("Parent Project Name", "3.0.0")
            help("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PARENT_PROJECT_VERSION_NAME = NullableStringProperty("detect.parent.project.version.name").apply {
            info("Parent Project Version Name", "3.0.0")
            help("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_TIER = NullableIntegerProperty("detect.project.tier").apply {
            info("Project Tier", "3.1.0")
            help("If a Black Duck project tier is specified, your project will be created with this tier.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_TOOL = EnumListProperty("detect.project.tool", listOf(DetectTool.DOCKER, DetectTool.DETECTOR, DetectTool.BAZEL), DetectTool::valueOf, DetectTool.values().toList()).apply {
            info("Detector Tool Priority", "5.0.0")
            help("The tool priority for project name and version. The project name and version will be determined by the first tool in this list that provides them.", "This allows you to control which tool provides the project name and version when more than one tool are capable of providing it.")
            groups(DetectGroup.Paths, DetectGroup.Global)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_VERSION_DISTRIBUTION = EnumProperty("detect.project.version.distribution", ProjectVersionDistributionType.EXTERNAL, ProjectVersionDistributionType::valueOf, ProjectVersionDistributionType.values().toList()).apply {
            info("Version Distribution", "3.0.0")
            help("An override for the Project Version distribution")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
            category(DetectCategory.Advanced)
        }
        val DETECT_PROJECT_VERSION_NAME = NullableStringProperty("detect.project.version.name").apply {
            info("Version Name", "3.0.0")
            help("An override for the version to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_VERSION_NICKNAME = NullableStringProperty("detect.project.version.nickname").apply {
            info("Version Nickname", "5.2.0")
            help("If a project version nickname is specified, your project version will be created with this nickname.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_VERSION_NOTES = NullableStringProperty("detect.project.version.notes").apply {
            info("Version Notes", "3.1.0")
            help("If project version notes are specified, your project version will be created with these notes.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_VERSION_PHASE = EnumProperty("detect.project.version.phase", ProjectVersionPhaseType.DEVELOPMENT, ProjectVersionPhaseType::valueOf, ProjectVersionPhaseType.values().toList()).apply {
            info("Version Phase", "3.0.0")
            help("An override for the Project Version phase.")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PROJECT_VERSION_UPDATE = BooleanProperty("detect.project.version.update", false).apply {
            info("Update Project Version", "4.0.0")
            help("If set to true, will update the Project Version with the configured properties. See detailed help for more information.", "When set to true, the following properties will be updated on the Project. Project tier (detect.project.tier) and Project Level Adjustments (detect.project.level.adjustments). " +
                    "The following properties will also be updated on the Version.Version notes (detect.project.version.notes), phase(detect.project.version.phase), distribution(detect.project.version.distribution).")
            groups(DetectGroup.Project, DetectGroup.ProjectSetting)
        }
        val DETECT_PYTHON_PATH = NullableStringProperty("detect.python.path").apply {
            info("Python Executable", "3.0.0")
            help("The path to the Python executable.")
            groups(DetectGroup.Python, DetectGroup.Global)
        }
        val DETECT_PYTHON_PYTHON3 = BooleanProperty("detect.python.python3", false).apply {
            info("Use Python3", "3.0.0")
            help("If true will use Python 3 if available on class path.")
            groups(DetectGroup.Python, DetectGroup.Global)
        }
        val DETECT_REPORT_TIMEOUT = LongProperty("detect.report.timeout", 300).apply {
            info("Report Generation Timeout", "5.2.0")
            help("The amount of time in seconds Detect will wait for scans to finish and to generate reports (i.e. risk and policy check). When changing this value, keep in mind the checking of policies might have to wait for scans to process which can take some time.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Global)
        }
        val DETECT_REQUIRED_DETECTOR_TYPES = NullableStringProperty("detect.required.detector.types").apply {
            info("Required Detect Types", "4.3.0")
            help("The set of required detectors.", "If you want one or more detectors to be required (must be found to apply), use this property to specify the set of required detectors. If this property is set, and one (or more) of the given detectors is not found to apply, Detect will fail.")
            groups(DetectGroup.Detector, DetectGroup.Global)
        }
        val DETECT_RESOLVE_TILDE_IN_PATHS = BooleanProperty("detect.resolve.tilde.in.paths", true).apply {
            info("Resolve Tilde in Paths", "3.0.0")
            help("If set to false Detect will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_RISK_REPORT_PDF = BooleanProperty("detect.risk.report.pdf", false).apply {
            info("Generate Risk Report (PDF)", "3.0.0")
            help("When set to true, a Black Duck risk report in PDF form will be created.")
            groups(DetectGroup.Report, DetectGroup.Global, DetectGroup.ReportSetting)
        }
        val DETECT_RISK_REPORT_PDF_PATH = NullableStringProperty("detect.risk.report.pdf.path").apply {
            info("Risk Report Output Path", "3.0.0")
            help("The output directory for risk report in PDF. Default is the source directory.")
            groups(DetectGroup.Report, DetectGroup.Global)
        }
        val DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES = BooleanProperty("detect.ruby.include.runtime.dependencies", true).apply {
            info("Ruby Runtime Dependencies", "5.4.0")
            help("If set to false, runtime dependencies will not be included when parsing *.gemspec files.")
            groups(DetectGroup.Ruby, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES = BooleanProperty("detect.ruby.include.dev.dependencies", false).apply {
            info("Ruby Development Dependencies", "5.4.0")
            help("If set to true, development dependencies will be included when parsing *.gemspec files.")
            groups(DetectGroup.Ruby, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val DETECT_SBT_EXCLUDED_CONFIGURATIONS = NullableStringProperty("detect.sbt.excluded.configurations").apply {
            info("SBT Configurations Excluded", "3.0.0")
            help("The names of the sbt configurations to exclude.")
            groups(DetectGroup.Sbt, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_SBT_INCLUDED_CONFIGURATIONS = NullableStringProperty("detect.sbt.included.configurations").apply {
            info("SBT Configurations Included", "3.0.0")
            help("The names of the sbt configurations to include.")
            groups(DetectGroup.Sbt, DetectGroup.SourceScan)
            category(DetectCategory.Advanced)
        }
        val DETECT_SBT_REPORT_DEPTH = IntegerProperty("detect.sbt.report.search.depth", 3).apply {
            info("SBT Report Search Depth", "4.3.0")
            help("Depth the sbt detector will use to search for report files.")
            groups(DetectGroup.Sbt, DetectGroup.SourceScan)
        }
        val DETECT_SCAN_OUTPUT_PATH = NullableStringProperty("detect.scan.output.path").apply {
            info("Scan Output Path", "3.0.0")
            help("The output directory for all signature scanner output files. If not set, the signature scanner output files will be in a 'scan' subdirectory of the output directory.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_SOURCE_PATH = NullableStringProperty("detect.source.path").apply {
            info("Source Path", "3.0.0")
            help("The path to the project directory to inspect.", "Detect will search the given directory for hints that indicate which package manager(s) the project uses, and will attempt to run the corresponding detector(s).")
            groups(DetectGroup.Paths, DetectGroup.SourcePath)
        }
        val DETECT_TEST_CONNECTION = BooleanProperty("detect.test.connection", false).apply {
            info("Test Connection to Black Duck", "3.0.0")
            help("Test the connection to Black Duck with the current configuration.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Global)
        }
        val DETECT_TOOLS = FilterableEnumListProperty("detect.tools", emptyList(), DetectTool::valueOf, DetectTool.values().toList()).apply {
            info("Detect Tools Included", "5.0.0")
            help("The tools Detect should allow in a comma-separated list. Tools in this list (as long as they are not also in the excluded list) will be allowed to run if all criteria of the tool are met. Exclusion rules always win.", "This property and detect.tools.excluded provide control over which tools Detect runs.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_TOOLS_EXCLUDED = FilterableEnumListProperty("detect.tools.excluded", emptyList(), DetectTool::valueOf, DetectTool.values().toList()).apply {
            info("Detect Tools Excluded", "5.0.0")
            help("The tools Detect should not allow, in a comma-separated list. Excluded tools will not be run even if all criteria for the tool is met. Exclusion rules always win.", "This property and detect.tools provide control over which tools Detect runs.")
            groups(DetectGroup.Paths, DetectGroup.Global)
        }
        val DETECT_YARN_PROD_ONLY = BooleanProperty("detect.yarn.prod.only", false).apply {
            info("Include Yarn Production Dependencies Only", "4.0.0")
            help("Set this to true to only scan production dependencies.")
            groups(DetectGroup.Yarn, DetectGroup.Global, DetectGroup.SourceScan)
        }
        val LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION = EnumProperty("logging.level.com.synopsys.integration", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList()).apply {
            info("Logging Level", "5.3.0")
            help("The logging level of Detect.")
            groups(DetectGroup.Logging, DetectGroup.Global)
        }
        val LOGGING_LEVEL_DETECT = EnumProperty("logging.level.detect", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList()).apply {
            info("Logging Level Shorthand", "5.5.0")
            help("Shorthand for the logging level of detect. Equivalent to setting logging.level.com.synopsys.integration.")
            groups(DetectGroup.Logging, DetectGroup.Global)
        }

        val DETECT_WAIT_FOR_RESULTS = BooleanProperty("detect.wait.for.results", false).apply {
            info("Wait For Results", "5.5.0")
            help("If set to true, Detect will wait for Synopsys products until results are available or the blackduck.timeout is exceeded.")
            groups(DetectGroup.General, DetectGroup.Global)
        }

        //#endregion Active Properties

        //#region Deprecated Properties
        private const val DEPRECATED_PROPERTY_MESSAGE = "This property is deprecated."

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BITBAKE_REFERENCE_IMPL = StringProperty("detect.bitbake.reference.impl", "-poky-linux").apply {
            info("Reference implementation", "4.4.0")
            help("The reference implementation of the Yocto project. These characters are stripped from the discovered target architecture.")
            groups(DetectGroup.Bitbake, DetectGroup.SourceScan)
            deprecated("This property is no longer required and will not be used in the Bitbake Detector.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_API_TIMEOUT = LongProperty("detect.api.timeout", 300000).apply {
            info("Detect Api Timeout", "3.0.0")
            help("Timeout for response from Black Duck regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.")
            groups(DetectGroup.ProjectInfo, DetectGroup.Project)
            deprecated("This property is now deprecated. Please use --detect.report.timeout in the future. NOTE the new property is in SECONDS not MILLISECONDS.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_URL = NullableStringProperty("blackduck.hub.url").apply {
            info("Blackduck Hub Url", "3.0.0")
            help("URL of the Hub server.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.url in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_TIMEOUT = IntegerProperty("blackduck.hub.timeout", 120).apply {
            info("Blackduck Hub Timeout", "3.0.0")
            help("The time to wait for rest connections to complete in seconds.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.timeout in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_USERNAME = NullableStringProperty("blackduck.hub.username").apply {
            info("Blackduck Hub Username", "3.0.0")
            help("Hub username.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.username in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PASSWORD = NullableStringProperty("blackduck.hub.password").apply {
            info("Blackduck Hub Password", "3.0.0")
            help("Hub password.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.password in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_API_TOKEN = NullableStringProperty("blackduck.hub.api.token").apply {
            info("Blackduck Hub Api Token", "3.1.0")
            help("Hub API Token.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.api.token in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_HOST = NullableStringProperty("blackduck.hub.proxy.host").apply {
            info("Blackduck Hub Proxy Host", "3.0.0")
            help("Proxy host.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.host in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_PORT = NullableStringProperty("blackduck.hub.proxy.port").apply {
            info("Blackduck Hub Proxy Port", "3.0.0")
            help("Proxy port.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.port in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_USERNAME = NullableStringProperty("blackduck.hub.proxy.username").apply {
            info("Blackduck Hub Proxy Username", "3.0.0")
            help("Proxy username.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.username in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_PASSWORD = NullableStringProperty("blackduck.hub.proxy.password").apply {
            info("Blackduck Hub Proxy Password", "3.0.0")
            help("Proxy password.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.password in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_NTLM_DOMAIN = NullableStringProperty("blackduck.hub.proxy.ntlm.domain").apply {
            info("Blackduck Hub Proxy Ntlm Domain", "3.1.0")
            help("NTLM Proxy domain.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.ntlm.domain in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_IGNORED_HOSTS = NullableStringProperty("blackduck.hub.proxy.ignored.hosts").apply {
            info("Blackduck Hub Proxy Ignored Hosts", "3.2.0")
            help("A comma-separated list of host patterns that should not use the proxy.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.ignored.hosts in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION = NullableStringProperty("blackduck.hub.proxy.ntlm.workstation").apply {
            info("Blackduck Hub Proxy Ntlm Workstation", "3.1.0")
            help("NTLM Proxy workstation.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Proxy)
            deprecated("This property is changing. Please use --blackduck.proxy.ntlm.workstation in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_TRUST_CERT = BooleanProperty("blackduck.hub.trust.cert", false).apply {
            info("Blackduck Hub Trust Cert", "3.0.0")
            help("If true, automatically trusts the certificate for the current run of Detect only.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --blackduck.trust.cert in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val BLACKDUCK_HUB_OFFLINE_MODE = BooleanProperty("blackduck.hub.offline.mode", false).apply {
            info("Blackduck Hub Offline Mode", "3.0.0")
            help("This disables any Hub communication. If true, Detect does not upload BDIO files, does not check policies, and does not download and install the signature scanner.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Offline)
            deprecated("This property is changing. Please use --blackduck.offline.mode in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_DISABLE_WITHOUT_HUB = BooleanProperty("detect.disable.without.hub", false).apply {
            info("Detect Disable Without Hub", "4.0.0")
            help("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.")
            groups(DetectGroup.BlackduckServer)
            deprecated("This property is changing. Please use --detect.ignore.connection.failures in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)

        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_DISABLE_WITHOUT_BLACKDUCK = BooleanProperty("detect.disable.without.blackduck", false).apply {
            info("Check For Valid Black Duck Connection", "4.2.0")
            help("If true, during initialization Detect will check for Black Duck connectivity and exit with status code 0 if it cannot connect.")
            groups(DetectGroup.BlackduckServer, DetectGroup.Blackduck, DetectGroup.Default)
            deprecated("This property is changing. Please use --detect.ignore.connection.failures in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_SUPPRESS_CONFIGURATION_OUTPUT = BooleanProperty("detect.suppress.configuration.output", false).apply {
            info("Detect Suppress Configuration Output", "3.0.0")
            help("If true, the default behavior of printing your configuration properties at startup will be suppressed.")
            groups(DetectGroup.Logging)
            deprecated("This property is being removed. Configuration can no longer be suppressed individually. Log level can be used.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_SUPPRESS_RESULTS_OUTPUT = BooleanProperty("detect.suppress.results.output", false).apply {
            info("Detect Suppress Results Output", "3.0.0")
            help("If true, the default behavior of printing the Detect Results will be suppressed.")
            groups(DetectGroup.Logging)
            deprecated("This property is being removed. Results can no longer be suppressed individually. Log level can be used.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_EXCLUDED_BOM_TOOL_TYPES = NullableStringProperty("detect.excluded.bom.tool.types").apply {
            info("Detect Excluded Bom Tool Types", "3.0.0")
            help("By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.")
            groups(DetectGroup.Detector, DetectGroup.SourceScan)
            deprecated("This property is changing. Please use --detect.excluded.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS = BooleanProperty("detect.bom.tool.search.exclusion.defaults", true).apply {
            info("Detect Bom Tool Search Exclusion Defaults", "3.2.0")
            help("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the bom tool search: bin, build, .git, .gradle, node_modules, out, packages, target")
            groups(DetectGroup.Paths, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.detector.search.exclusion.defaults in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BOM_TOOL_SEARCH_EXCLUSION = NullableStringListProperty("detect.bom.tool.search.exclusion").apply {
            info("Detect Bom Tool Search Exclusion", "3.2.0")
            help("A comma-separated list of directory names to exclude from the bom tool search.")
            groups(DetectGroup.Paths, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.detector.search.exclusion in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_INCLUDED_BOM_TOOL_TYPES = NullableStringProperty("detect.included.bom.tool.types").apply {
            info("Detect Included Bom Tool Types", "3.0.0")
            help("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
            groups(DetectGroup.Detector, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.included.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_PROJECT_BOM_TOOL = NullableStringProperty("detect.project.bom.tool").apply {
            info("Detect Project Bom Tool", "4.0.0")
            help("The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.")
            groups(DetectGroup.Paths, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.project.detector in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BOM_TOOL_SEARCH_DEPTH = IntegerProperty("detect.bom.tool.search.depth", 0).apply {
            info("Detect Bom Tool Search Depth", "3.2.0")
            help("Depth of subdirectories within the source directory to search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.")
            groups(DetectGroup.Paths, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.detector.search.depth in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_REQUIRED_BOM_TOOL_TYPES = NullableStringProperty("detect.required.bom.tool.types").apply {
            info("Detect Required Bom Tool Types", "4.3.0")
            help("If set, Detect will fail if it does not find the bom tool types supplied here.")
            groups(DetectGroup.Detector, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.required.detector.types in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BOM_TOOL_SEARCH_CONTINUE = BooleanProperty("detect.bom.tool.search.continue", false).apply {
            info("Detect Bom Tool Search Continue", "3.2.0")
            help("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. " +
                    "If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project .")
            groups(DetectGroup.Paths, DetectGroup.Detector)
            deprecated("This property is changing. Please use --detect.detector.search.continue in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_GRADLE_INSPECTOR_REPOSITORY_URL = NullableStringProperty("detect.gradle.inspector.repository.url").apply {
            info("Detect Gradle Inspector Repository Url", "3.0.0")
            help("The respository gradle should use to look for the gradle inspector dependencies.")
            groups(DetectGroup.Gradle)
            deprecated("In the future, the gradle inspector will no longer be downloaded from a custom repository, please use Detect Air Gap instead.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_NUGET_INSPECTOR_NAME = StringProperty("detect.nuget.inspector.name", "IntegrationNugetInspector").apply {
            info("Detect Nuget Inspector Name", "3.0.0")
            help("Name of the Nuget Inspector package and the Nuget Inspector exe. (Do not include '.exe'.)", "The nuget inspector (previously) could be hosted on a custom nuget feed. In this case, Detect needed to know the name of the package to pull and the name of the exe file (which has to match). In the future, Detect will only retreive it from Artifactory or from Air Gap so a custom name is no longer supported.")
            groups(DetectGroup.Nuget)
            deprecated("In the future, Detect will not look for a custom named inspector.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_NUGET_PATH = NullableStringProperty("detect.nuget.path").apply {
            info("Detect Nuget Path", "3.0.0")
            help("The path to the Nuget executable. Nuget is used to download the classic inspectors nuget package.")
            groups(DetectGroup.Nuget)
            deprecated("In the future, Detect will no longer need a nuget executable as it will download the inspector from Artifactory exclusively.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN = BooleanProperty("detect.hub.signature.scanner.dry.run", false).apply {
            info("Detect Hub Signature Scanner Dry Run", "3.0.0")
            help("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.dry.run in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE = BooleanProperty("detect.hub.signature.scanner.snippet.mode", false).apply {
            info("Detect Hub Signature Scanner Snippet Mode", "3.0.0")
            help("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.snippet.mode in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = NullableStringListProperty("detect.hub.signature.scanner.exclusion.patterns").apply {
            info("Detect Hub Signature Scanner Exclusion Patterns", "3.0.0")
            help("A comma-separated list of values to be used with the Signature Scanner --exclude flag.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.patterns in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_PATHS = NullableStringListProperty("detect.hub.signature.scanner.paths").apply {
            info("Detect Hub Signature Scanner Paths", "3.0.0")
            help("These paths and only these paths will be scanned.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.paths in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = StringListProperty("detect.hub.signature.scanner.exclusion.name.patterns", listOf("node_modules")).apply {
            info("Detect Hub Signature Scanner Exclusion Name Patterns", "4.0.0")
            help("A comma-separated list of directory name patterns Detect will search for and add to the Signature Scanner --exclude flag values.", "Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner. " +
                    "These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.exclusion.name.patterns in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_MEMORY = IntegerProperty("detect.hub.signature.scanner.memory", 4096).apply {
            info("Detect Hub Signature Scanner Memory", "3.0.0")
            help("The memory for the scanner to use.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.memory in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_DISABLED = BooleanProperty("detect.hub.signature.scanner.disabled", false).apply {
            info("Detect Hub Signature Scanner Disabled", "3.0.0")
            help("Set to true to disable the Hub Signature Scanner.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.tools in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED = BooleanProperty("detect.blackduck.signature.scanner.disabled", false).apply {
            info("Detect Blackduck Signature Scanner Disabled", "4.2.0")
            help("Set to true to disable the Black Duck Signature Scanner.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Blackduck)
            deprecated("This property is changing. Please use --detect.tools in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = NullableStringProperty("detect.hub.signature.scanner.offline.local.path").apply {
            info("Detect Hub Signature Scanner Offline Local Path", "3.0.0")
            help("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Offline)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.offline.local.path in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH = NullableStringProperty("detect.hub.signature.scanner.local.path").apply {
            info("Detect Hub Signature Scanner Local Path", "4.2.0")
            help("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Offline)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.local.path in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_HOST_URL = NullableStringProperty("detect.hub.signature.scanner.host.url").apply {
            info("Detect Hub Signature Scanner Host Url", "3.0.0")
            help("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.host.url in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = IntegerProperty("detect.blackduck.signature.scanner.parallel.processors", 1).apply {
            info("Signature Scanner Parallel Processors", "4.2.0")
            help("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global)
            category(DetectCategory.Advanced)
            deprecated("This property is changing. Please use --detect.parallel.processors in the future. The --detect.parallel.processors property will take precedence over this property.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = IntegerProperty("detect.hub.signature.scanner.parallel.processors", 1).apply {
            info("Detect Hub Signature Scanner Parallel Processors", "3.0.0")
            help("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.parallel.processors in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS = NullableStringProperty("detect.hub.signature.scanner.arguments").apply {
            info("Detect Hub Signature Scanner Arguments", "4.0.0")
            help("Additional arguments to use when running the Hub signature scanner.")
            groups(DetectGroup.SignatureScanner)
            deprecated("This property is changing. Please use --detect.blackduck.signature.scanner.arguments in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_SWIP_ENABLED = BooleanProperty("detect.polaris.enabled", false).apply {
            info("Detect Polaris Enabled", "4.4.0")
            help("Set to false to disable the Synopsys Polaris Tool.")
            groups(DetectGroup.Polaris)
            deprecated("This property is changing. Please use --detect.tools and POLARIS in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION = EnumProperty("logging.level.com.blackducksoftware.integration", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList()).apply {
            info("Logging Level", "3.0.0")
            help("The logging level of Detect.")
            groups(DetectGroup.Logging, DetectGroup.Global)
            deprecated("This property is changing. Please use --logging.level.com.synopsys.integration in the future.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_MAVEN_SCOPE = NullableStringProperty("detect.maven.scope").apply {
            info("Dependency Scope Included", "3.0.0")
            help("The name of a Maven scope. Output will be limited to dependencies with this scope.", "If set, Detect will include only dependencies of the given Maven scope.")
            groups(DetectGroup.Maven, DetectGroup.SourceScan)
            deprecated("This property is changing. Please use --detect.maven.included.scope in the future.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE = BooleanProperty("detect.blackduck.signature.scanner.snippet.mode", false).apply {
            info("Snippet Scanning", "4.2.0")
            help("If set to true, the signature scanner will, if supported by your Black Duck version, run in snippet scanning mode.")
            groups(DetectGroup.SignatureScanner, DetectGroup.Global, DetectGroup.SourceScan)
            deprecated("This property is now deprecated. Please use --detect.blackduck.signature.scanner.snippet.matching in the future. NOTE the new property is one of a particular set of values. You will need to consult the documentation for the Signature Scanner in Black Duck for details.", DetectMajorVersion.SIX, DetectMajorVersion.SEVEN)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val POLARIS_URL = NullableStringProperty("polaris.url").apply {
            info("Polaris Url", "4.1.0")
            help("The url of your polaris instance.")
            groups(DetectGroup.Polaris, DetectGroup.Default, DetectGroup.Global)
            deprecated("This property is being removed. Detect will no longer invoke the Polaris CLI.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val POLARIS_ACCESS_TOKEN = NullableStringProperty("polaris.access.token").apply {
            info("Polaris Access Token", "5.3.0")
            help("The access token for your polaris instance.")
            groups(DetectGroup.Polaris, DetectGroup.Default, DetectGroup.Global)
            deprecated("This property is being removed. Detect will no longer invoke the Polaris CLI.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val POLARIS_ARGUMENTS = NullableStringProperty("polaris.arguments").apply {
            info("Polaris Arguments", "5.3.0")
            help("Additional arguments to pass to polaris separated by space. The polaris.command takes precedence.")
            groups(DetectGroup.Polaris, DetectGroup.Default, DetectGroup.SourceScan)
            deprecated("This property is being removed. Detect will no longer invoke the Polaris CLI.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        @Deprecated(DEPRECATED_PROPERTY_MESSAGE)
        val POLARIS_COMMAND = NullableStringProperty("polaris.command").apply {
            info("Polaris Command", "6.0.0")
            help("A replacement command to pass to polaris separated by space. Include the analyze or setup command itself. If specified, polaris.arguments will be ignored and this will take precedence.")
            groups(DetectGroup.Polaris, DetectGroup.Default, DetectGroup.SourceScan)
            deprecated("This property is being removed. Detect will no longer invoke the Polaris CLI.", DetectMajorVersion.SEVEN, DetectMajorVersion.EIGHT)
        }

        //#endregion Deprecated Properties

        //#region Accessor for all properties
        val properties = values()

        private fun values(): List<Property> {
            val clazz = DetectProperties::class
            val companionClass = clazz.companionObject!!
            val companion = clazz.companionObjectInstance!!
            val members = mutableListOf<Property>()
            for (member in companionClass.memberProperties) {
                when (val value = member.getter.call(companion)) {
                    is Property -> members.add(value)
                }
            }
            return members
        }

        //#endregion Accessor for all properties
    }

}