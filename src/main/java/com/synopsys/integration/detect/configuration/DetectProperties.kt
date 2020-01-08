package com.synopsys.integration.detect.configuration

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType
import com.synopsys.integration.detect.config.*
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionPhaseType
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching
import com.synopsys.integration.detect.DetectTool
import com.synopsys.integration.detect.workflow.bdio.AggregateMode
import com.synopsys.integration.detectable.detectables.bazel.BazelWorkspace
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule
import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.log.LogLevel
import java.awt.SystemColor.text
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMemberProperties
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
        val BLACKDUCK_API_TOKEN = OptionalStringProperty("blackduck.api.token")
        val BLACKDUCK_OFFLINE_MODE = RequiredBooleanProperty("blackduck.offline.mode", false)
        val BLACKDUCK_PASSWORD = OptionalStringProperty("blackduck.password")
        val BLACKDUCK_PROXY_HOST = OptionalStringProperty("blackduck.proxy.host")
        val BLACKDUCK_PROXY_IGNORED_HOSTS = OptionalStringProperty("blackduck.proxy.ignored.hosts")
        val BLACKDUCK_PROXY_NTLM_DOMAIN = OptionalStringProperty("blackduck.proxy.ntlm.domain")
        val BLACKDUCK_PROXY_NTLM_WORKSTATION = OptionalStringProperty("blackduck.proxy.ntlm.workstation")
        val BLACKDUCK_PROXY_PASSWORD = OptionalStringProperty("blackduck.proxy.password")
        val BLACKDUCK_PROXY_PORT = OptionalStringProperty("blackduck.proxy.port")
        val BLACKDUCK_PROXY_USERNAME = OptionalStringProperty("blackduck.proxy.username")
        val BLACKDUCK_TIMEOUT = RequiredIntegerProperty("blackduck.timeout", 120)
        val BLACKDUCK_TRUST_CERT = RequiredBooleanProperty("blackduck.trust.cert", false)
        val BLACKDUCK_URL = OptionalStringProperty("blackduck.url")
        val BLACKDUCK_USERNAME = OptionalStringProperty("blackduck.username")
        val DETECT_PARALLEL_PROCESSORS = RequiredIntegerProperty("detect.parallel.processors", 1)
        val DETECT_BASH_PATH = OptionalStringProperty("detect.bash.path")
        val DETECT_BAZEL_PATH = OptionalStringProperty("detect.bazel.path")
        val DETECT_BAZEL_TARGET = OptionalStringProperty("detect.bazel.target")
        val DETECT_BAZEL_CQUERY_OPTIONS = RequiredStringArrayProperty("detect.bazel.cquery.options", emptyList())
        val DETECT_BAZEL_DEPENDENCY_RULE = RequiredEnumProperty("detect.bazel.dependency.type", WorkspaceRule.UNSPECIFIED, WorkspaceRule::valueOf, WorkspaceRule.values().toList())
        val DETECT_BDIO_OUTPUT_PATH = OptionalStringProperty("detect.bdio.output.path")
        val DETECT_BDIO2_ENABLED = RequiredBooleanProperty("detect.bdio2.enabled", false)
        val DETECT_BINARY_SCAN_FILE = OptionalStringProperty("detect.binary.scan.file.path")
        val DETECT_BINARY_SCAN_FILE_NAME_PATTERNS = OptionalStringArrayProperty("detect.binary.scan.file.name.patterns")
        val DETECT_BITBAKE_BUILD_ENV_NAME = RequiredStringProperty("detect.bitbake.build.env.name", "oe-init-build-env")
        val DETECT_BITBAKE_PACKAGE_NAMES = OptionalStringArrayProperty("detect.bitbake.package.names")
        val DETECT_BITBAKE_SOURCE_ARGUMENTS = RequiredStringArrayProperty("detect.bitbake.source.arguments", emptyList())
        val DETECT_BITBAKE_SEARCH_DEPTH = RequiredIntegerProperty("detect.bitbake.search.depth", 1)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS = OptionalStringProperty("detect.blackduck.signature.scanner.arguments")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN = RequiredBooleanProperty("detect.blackduck.signature.scanner.dry.run", false)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = RequiredStringArrayProperty("detect.blackduck.signature.scanner.exclusion.name.patterns", listOf("node_modules"))
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH = RequiredIntegerProperty("detect.blackduck.signature.scanner.exclusion.pattern.search.depth", 4)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = OptionalStringArrayProperty("detect.blackduck.signature.scanner.exclusion.patterns")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL = OptionalStringProperty("detect.blackduck.signature.scanner.host.url")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH = OptionalStringProperty("detect.blackduck.signature.scanner.local.path")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY = RequiredIntegerProperty("detect.blackduck.signature.scanner.memory", 4096)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = OptionalStringProperty("detect.blackduck.signature.scanner.offline.local.path")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS = OptionalStringArrayProperty("detect.blackduck.signature.scanner.paths")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING = RequiredExtendedEnumProperty("detect.blackduck.signature.scanner.snippet.matching", ExtendedValue(ExtendedSnippetMode.NONE), ExtendedSnippetMode::valueOf, SnippetMatching::valueOf, ExtendedSnippetMode.values().toList(), SnippetMatching.values().toList())
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE = RequiredBooleanProperty("detect.blackduck.signature.scanner.upload.source.mode", false)
        val DETECT_BOM_AGGREGATE_NAME = OptionalStringProperty("detect.bom.aggregate.name")
        val DETECT_BOM_AGGREGATE_REMEDIATION_MODE = RequiredEnumProperty("detect.bom.aggregate.remediation.mode", AggregateMode.TRANSITIVE, AggregateMode::valueOf, AggregateMode.values().toList())
        val DETECT_BUILDLESS = RequiredBooleanProperty("detect.detector.buildless", false)
        val DETECT_CLEANUP = RequiredBooleanProperty("detect.cleanup", true)
        val DETECT_CLONE_PROJECT_VERSION_NAME = OptionalStringProperty("detect.clone.project.version.name")
        val DETECT_CLONE_PROJECT_VERSION_LATEST = OptionalBooleanProperty("detect.clone.project.version.latest")
        val DETECT_CODE_LOCATION_NAME = OptionalStringProperty("detect.code.location.name")
        val DETECT_CONDA_ENVIRONMENT_NAME = OptionalStringProperty("detect.conda.environment.name")
        val DETECT_CONDA_PATH = OptionalStringProperty("detect.conda.path")
        val DETECT_CPAN_PATH = OptionalStringProperty("detect.cpan.path")
        val DETECT_CPANM_PATH = OptionalStringProperty("detect.cpanm.path")
        val DETECT_DEFAULT_PROJECT_VERSION_SCHEME = RequiredEnumProperty("detect.default.project.version.scheme", DefaultVersionNameScheme.DEFAULT, DefaultVersionNameScheme::valueOf, DefaultVersionNameScheme.values().toList())
        val DETECT_DEFAULT_PROJECT_VERSION_TEXT = RequiredStringProperty("detect.default.project.version.text", "Default Detect Version")
        val DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT = RequiredStringProperty("detect.default.project.version.timeformat", "yyyy-MM-dd\'T\'HH:mm:ss.SSS")
        val DETECT_DETECTOR_SEARCH_DEPTH = RequiredIntegerProperty("detect.detector.search.depth", 0)
        val DETECT_DETECTOR_SEARCH_CONTINUE = RequiredBooleanProperty("detect.detector.search.continue", false)
        val DETECT_DETECTOR_SEARCH_EXCLUSION = OptionalStringArrayProperty("detect.detector.search.exclusion")
        val DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS = OptionalStringArrayProperty("detect.detector.search.exclusion.patterns")
        val DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS = OptionalStringArrayProperty("detect.detector.search.exclusion.paths")
        val DETECT_DETECTOR_SEARCH_EXCLUSION_FILES = RequiredStringArrayProperty("detect.detector.search.exclusion.files", emptyList())
        val DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS = RequiredBooleanProperty("detect.detector.search.exclusion.defaults", true)
        val DETECT_IGNORE_CONNECTION_FAILURES = RequiredBooleanProperty("detect.ignore.connection.failures", false)
        val DETECT_DOCKER_IMAGE = OptionalStringProperty("detect.docker.image")
        val DETECT_DOCKER_IMAGE_ID = OptionalStringProperty("detect.docker.image.id")
        val DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH = OptionalStringProperty("detect.docker.inspector.air.gap.path")
        val DETECT_DOCKER_INSPECTOR_PATH = OptionalStringProperty("detect.docker.inspector.path")
        val DETECT_DOCKER_INSPECTOR_VERSION = OptionalStringProperty("detect.docker.inspector.version")
        val DETECT_DOCKER_PATH = OptionalStringProperty("detect.docker.path")
        val DETECT_DOCKER_PATH_REQUIRED = RequiredBooleanProperty("detect.docker.path.required", false)
        val DETECT_DOCKER_PLATFORM_TOP_LAYER_ID = OptionalStringProperty("detect.docker.platform.top.layer.id")
        val DETECT_DOCKER_TAR = OptionalStringProperty("detect.docker.tar")
        val DETECT_DOTNET_PATH = OptionalStringProperty("detect.dotnet.path")
        val DETECT_EXCLUDED_DETECTOR_TYPES = RequiredFilterableEnumListProperty("detect.excluded.detector.types", emptyList(), DetectorType::valueOf, DetectorType.values().toList())
        val DETECT_FORCE_SUCCESS = RequiredBooleanProperty("detect.force.success", false)
        val DETECT_GIT_PATH = OptionalStringProperty("detect.git.path")
        val DETECT_GO_PATH = OptionalStringProperty("detect.go.path")
        val DETECT_GRADLE_BUILD_COMMAND = OptionalStringProperty("detect.gradle.build.command")
        val DETECT_GRADLE_EXCLUDED_CONFIGURATIONS = OptionalStringProperty("detect.gradle.excluded.configurations")
        val DETECT_GRADLE_EXCLUDED_PROJECTS = OptionalStringProperty("detect.gradle.excluded.projects")
        val DETECT_GRADLE_INCLUDED_CONFIGURATIONS = OptionalStringProperty("detect.gradle.included.configurations")
        val DETECT_GRADLE_INCLUDED_PROJECTS = OptionalStringProperty("detect.gradle.included.projects")
        val DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH = OptionalStringProperty("detect.gradle.inspector.air.gap.path")
        val DETECT_GRADLE_INSPECTOR_VERSION = OptionalStringProperty("detect.gradle.inspector.version")
        val DETECT_GRADLE_PATH = OptionalStringProperty("detect.gradle.path")
        val DETECT_HEX_REBAR3_PATH = OptionalStringProperty("detect.hex.rebar3.path")
        val DETECT_INCLUDED_DETECTOR_TYPES = RequiredFilterableEnumListProperty("detect.included.detector.types", emptyList(), DetectorType::valueOf, DetectorType.values().toList())
        val DETECT_JAVA_PATH = OptionalStringProperty("detect.java.path")
        val DETECT_MAVEN_BUILD_COMMAND = OptionalStringProperty("detect.maven.build.command")
        val DETECT_MAVEN_EXCLUDED_MODULES = OptionalStringProperty("detect.maven.excluded.modules")
        val DETECT_MAVEN_INCLUDED_MODULES = OptionalStringProperty("detect.maven.included.modules")
        val DETECT_MAVEN_PATH = OptionalStringProperty("detect.maven.path")
        val DETECT_MAVEN_INCLUDED_SCOPES = OptionalStringProperty("detect.maven.included.scopes")
        val DETECT_MAVEN_EXCLUDED_SCOPES = OptionalStringProperty("detect.maven.excluded.scopes")
        val DETECT_MAVEN_INCLUDE_PLUGINS = RequiredBooleanProperty("detect.maven.include.plugins", false)
        val DETECT_NOTICES_REPORT = RequiredBooleanProperty("detect.notices.report", false)
        val DETECT_NOTICES_REPORT_PATH = OptionalStringProperty("detect.notices.report.path")
        val DETECT_NPM_ARGUMENTS = OptionalStringProperty("detect.npm.arguments")
        val DETECT_NPM_INCLUDE_DEV_DEPENDENCIES = RequiredBooleanProperty("detect.npm.include.dev.dependencies", true)
        val DETECT_NPM_PATH = OptionalStringProperty("detect.npm.path")
        val DETECT_NUGET_CONFIG_PATH = OptionalStringProperty("detect.nuget.config.path")
        val DETECT_NUGET_EXCLUDED_MODULES = OptionalStringProperty("detect.nuget.excluded.modules")
        val DETECT_NUGET_IGNORE_FAILURE = RequiredBooleanProperty("detect.nuget.ignore.failure", false)
        val DETECT_NUGET_INCLUDED_MODULES = OptionalStringProperty("detect.nuget.included.modules")
        val DETECT_NUGET_INSPECTOR_AIR_GAP_PATH = OptionalStringProperty("detect.nuget.inspector.air.gap.path")
        val DETECT_NUGET_INSPECTOR_VERSION = OptionalStringProperty("detect.nuget.inspector.version")
        val DETECT_NUGET_PACKAGES_REPO_URL = RequiredStringArrayProperty("detect.nuget.packages.repo.url", listOf("https://api.nuget.org/v3/index.json"))
        val DETECT_OUTPUT_PATH = OptionalStringProperty("detect.output.path")
        val DETECT_TOOLS_OUTPUT_PATH = OptionalStringProperty("detect.tools.output.path")
        val DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES = RequiredBooleanProperty("detect.packagist.include.dev.dependencies", true)
        val DETECT_PEAR_ONLY_REQUIRED_DEPS = RequiredBooleanProperty("detect.pear.only.required.deps", false)
        val DETECT_PEAR_PATH = OptionalStringProperty("detect.pear.path")
        val DETECT_PIP_PROJECT_NAME = OptionalStringProperty("detect.pip.project.name")
        val DETECT_PIP_PROJECT_VERSION_NAME = OptionalStringProperty("detect.pip.project.version.name")
        val DETECT_PIP_REQUIREMENTS_PATH = RequiredStringArrayProperty("detect.pip.requirements.path", emptyList())
        val DETECT_PIP_ONLY_PROJECT_TREE = RequiredBooleanProperty("detect.pip.only.project.tree", false)
        val DETECT_PIPENV_PATH = OptionalStringProperty("detect.pipenv.path")
        val DETECT_SWIFT_PATH = OptionalStringProperty("detect.swift.path")
        val DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES = RequiredFilterableEnumListProperty("detect.policy.check.fail.on.severities", emptyList(), PolicySeverityType::valueOf, PolicySeverityType.values().toList())
        val DETECT_PROJECT_APPLICATION_ID = OptionalStringProperty("detect.project.application.id")
        val DETECT_CUSTOM_FIELDS_PROJECT = OptionalStringProperty("detect.custom.fields.project")
        val DETECT_CUSTOM_FIELDS_VERSION = OptionalStringProperty("detect.custom.fields.version")
        val DETECT_PROJECT_CLONE_CATEGORIES = RequiredEnumListProperty("detect.project.clone.categories", listOf(ProjectCloneCategoriesType.COMPONENT_DATA, ProjectCloneCategoriesType.VULN_DATA), ProjectCloneCategoriesType::valueOf, ProjectCloneCategoriesType.values().toList())
        val DETECT_PROJECT_CODELOCATION_PREFIX = OptionalStringProperty("detect.project.codelocation.prefix")
        val DETECT_PROJECT_CODELOCATION_SUFFIX = OptionalStringProperty("detect.project.codelocation.suffix")
        val DETECT_PROJECT_CODELOCATION_UNMAP = RequiredBooleanProperty("detect.project.codelocation.unmap", false)
        val DETECT_PROJECT_DESCRIPTION = OptionalStringProperty("detect.project.description")
        val DETECT_PROJECT_USER_GROUPS = RequiredStringArrayProperty("detect.project.user.groups", emptyList())
        val DETECT_PROJECT_TAGS = RequiredStringArrayProperty("detect.project.tags", emptyList())
        val DETECT_PROJECT_DETECTOR = OptionalStringProperty("detect.project.detector")
        val DETECT_PROJECT_LEVEL_ADJUSTMENTS = RequiredBooleanProperty("detect.project.level.adjustments", true)
        val DETECT_PROJECT_NAME = OptionalStringProperty("detect.project.name")
        val DETECT_PARENT_PROJECT_NAME = OptionalStringProperty("detect.parent.project.name")
        val DETECT_PARENT_PROJECT_VERSION_NAME = OptionalStringProperty("detect.parent.project.version.name")
        val DETECT_PROJECT_TIER = OptionalIntegerProperty("detect.project.tier")
        val DETECT_PROJECT_TOOL = RequiredEnumListProperty("detect.project.tool", listOf(DetectTool.DOCKER, DetectTool.DETECTOR, DetectTool.BAZEL), DetectTool::valueOf, DetectTool.values().toList())
        val DETECT_PROJECT_VERSION_DISTRIBUTION = RequiredEnumProperty("detect.project.version.distribution", ProjectVersionDistributionType.EXTERNAL, ProjectVersionDistributionType::valueOf, ProjectVersionDistributionType.values().toList())
        val DETECT_PROJECT_VERSION_NAME = OptionalStringProperty("detect.project.version.name")
        val DETECT_PROJECT_VERSION_NICKNAME = OptionalStringProperty("detect.project.version.nickname")
        val DETECT_PROJECT_VERSION_NOTES = OptionalStringProperty("detect.project.version.notes")
        val DETECT_PROJECT_VERSION_PHASE = RequiredEnumProperty("detect.project.version.phase", ProjectVersionPhaseType.DEVELOPMENT, ProjectVersionPhaseType::valueOf, ProjectVersionPhaseType.values().toList())
        val DETECT_PROJECT_VERSION_UPDATE = RequiredBooleanProperty("detect.project.version.update", false)
        val DETECT_PYTHON_PATH = OptionalStringProperty("detect.python.path")
        val DETECT_PYTHON_PYTHON3 = RequiredBooleanProperty("detect.python.python3", false)
        val DETECT_REPORT_TIMEOUT = RequiredLongProperty("detect.report.timeout", 300)
        val DETECT_REQUIRED_DETECTOR_TYPES = OptionalStringProperty("detect.required.detector.types")
        val DETECT_RESOLVE_TILDE_IN_PATHS = RequiredBooleanProperty("detect.resolve.tilde.in.paths", true)
        val DETECT_RISK_REPORT_PDF = RequiredBooleanProperty("detect.risk.report.pdf", false)
        val DETECT_RISK_REPORT_PDF_PATH = OptionalStringProperty("detect.risk.report.pdf.path")
        val DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES = RequiredBooleanProperty("detect.ruby.include.runtime.dependencies", true)
        val DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES = RequiredBooleanProperty("detect.ruby.include.dev.dependencies", false)
        val DETECT_SBT_EXCLUDED_CONFIGURATIONS = OptionalStringProperty("detect.sbt.excluded.configurations")
        val DETECT_SBT_INCLUDED_CONFIGURATIONS = OptionalStringProperty("detect.sbt.included.configurations")
        val DETECT_SBT_REPORT_DEPTH = RequiredIntegerProperty("detect.sbt.report.search.depth", 3)
        val DETECT_SCAN_OUTPUT_PATH = OptionalStringProperty("detect.scan.output.path")
        val DETECT_SOURCE_PATH = OptionalStringProperty("detect.source.path")
        val DETECT_TEST_CONNECTION = RequiredBooleanProperty("detect.test.connection", false)
        val DETECT_TOOLS = RequiredFilterableEnumListProperty("detect.tools", emptyList(), DetectTool::valueOf, DetectTool.values().toList())
        val DETECT_TOOLS_EXCLUDED = RequiredFilterableEnumListProperty("detect.tools.excluded", emptyList(), DetectTool::valueOf, DetectTool.values().toList())
        val DETECT_YARN_PROD_ONLY = RequiredBooleanProperty("detect.yarn.prod.only", false)
        val LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION = RequiredEnumProperty("logging.level.com.synopsys.integration", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList())
        val LOGGING_LEVEL_DETECT = RequiredEnumProperty("logging.level.detect", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList())
        val DETECT_WAIT_FOR_RESULTS = RequiredBooleanProperty("detect.wait.for.results", false)
        val DETECT_BITBAKE_REFERENCE_IMPL = RequiredStringProperty("detect.bitbake.reference.impl", "default-poky-linux")
        val DETECT_API_TIMEOUT = RequiredLongProperty("detect.api.timeout", 300000)
        val BLACKDUCK_HUB_URL = OptionalStringProperty("blackduck.hub.url")
        val BLACKDUCK_HUB_TIMEOUT = RequiredIntegerProperty("blackduck.hub.timeout", 120)
        val BLACKDUCK_HUB_USERNAME = OptionalStringProperty("blackduck.hub.username")
        val BLACKDUCK_HUB_PASSWORD = OptionalStringProperty("blackduck.hub.password")
        val BLACKDUCK_HUB_API_TOKEN = OptionalStringProperty("blackduck.hub.api.token")
        val BLACKDUCK_HUB_PROXY_HOST = OptionalStringProperty("blackduck.hub.proxy.host")
        val BLACKDUCK_HUB_PROXY_PORT = OptionalStringProperty("blackduck.hub.proxy.port")
        val BLACKDUCK_HUB_PROXY_USERNAME = OptionalStringProperty("blackduck.hub.proxy.username")
        val BLACKDUCK_HUB_PROXY_PASSWORD = OptionalStringProperty("blackduck.hub.proxy.password")
        val BLACKDUCK_HUB_PROXY_NTLM_DOMAIN = OptionalStringProperty("blackduck.hub.proxy.ntlm.domain")
        val BLACKDUCK_HUB_PROXY_IGNORED_HOSTS = OptionalStringProperty("blackduck.hub.proxy.ignored.hosts")
        val BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION = OptionalStringProperty("blackduck.hub.proxy.ntlm.workstation")
        val BLACKDUCK_HUB_TRUST_CERT = RequiredBooleanProperty("blackduck.hub.trust.cert", false)
        val BLACKDUCK_HUB_OFFLINE_MODE = RequiredBooleanProperty("blackduck.hub.offline.mode", false)
        val DETECT_DISABLE_WITHOUT_HUB = RequiredBooleanProperty("detect.disable.without.hub", false)
        val DETECT_DISABLE_WITHOUT_BLACKDUCK = RequiredBooleanProperty("detect.disable.without.blackduck", false)
        val DETECT_SUPPRESS_CONFIGURATION_OUTPUT = RequiredBooleanProperty("detect.suppress.configuration.output", false)
        val DETECT_SUPPRESS_RESULTS_OUTPUT = RequiredBooleanProperty("detect.suppress.results.output", false)
        val DETECT_EXCLUDED_BOM_TOOL_TYPES = OptionalStringProperty("detect.excluded.bom.tool.types")
        val DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS = RequiredBooleanProperty("detect.bom.tool.search.exclusion.defaults", true)
        val DETECT_BOM_TOOL_SEARCH_EXCLUSION = OptionalStringArrayProperty("detect.bom.tool.search.exclusion")
        val DETECT_INCLUDED_BOM_TOOL_TYPES = OptionalStringProperty("detect.included.bom.tool.types")
        val DETECT_PROJECT_BOM_TOOL = OptionalStringProperty("detect.project.bom.tool")
        val DETECT_BOM_TOOL_SEARCH_DEPTH = RequiredIntegerProperty("detect.bom.tool.search.depth", 0)
        val DETECT_REQUIRED_BOM_TOOL_TYPES = OptionalStringProperty("detect.required.bom.tool.types")
        val DETECT_BOM_TOOL_SEARCH_CONTINUE = RequiredBooleanProperty("detect.bom.tool.search.continue", false)
        val DETECT_GRADLE_INSPECTOR_REPOSITORY_URL = OptionalStringProperty("detect.gradle.inspector.repository.url")
        val DETECT_NUGET_INSPECTOR_NAME = RequiredStringProperty("detect.nuget.inspector.name", "IntegrationNugetInspector")
        val DETECT_NUGET_PATH = OptionalStringProperty("detect.nuget.path")
        val DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN = RequiredBooleanProperty("detect.hub.signature.scanner.dry.run", false)
        val DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE = RequiredBooleanProperty("detect.hub.signature.scanner.snippet.mode", false)
        val DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS = OptionalStringArrayProperty("detect.hub.signature.scanner.exclusion.patterns")
        val DETECT_HUB_SIGNATURE_SCANNER_PATHS = OptionalStringArrayProperty("detect.hub.signature.scanner.paths")
        val DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS = RequiredStringArrayProperty("detect.hub.signature.scanner.exclusion.name.patterns", listOf("node_modules"))
        val DETECT_HUB_SIGNATURE_SCANNER_MEMORY = RequiredIntegerProperty("detect.hub.signature.scanner.memory", 4096)
        val DETECT_HUB_SIGNATURE_SCANNER_DISABLED = RequiredBooleanProperty("detect.hub.signature.scanner.disabled", false)
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED = RequiredBooleanProperty("detect.blackduck.signature.scanner.disabled", false)
        val DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH = OptionalStringProperty("detect.hub.signature.scanner.offline.local.path")
        val DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH = OptionalStringProperty("detect.hub.signature.scanner.local.path")
        val DETECT_HUB_SIGNATURE_SCANNER_HOST_URL = OptionalStringProperty("detect.hub.signature.scanner.host.url")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = RequiredIntegerProperty("detect.blackduck.signature.scanner.parallel.processors", 1)
        val DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS = RequiredIntegerProperty("detect.hub.signature.scanner.parallel.processors", 1)
        val DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS = OptionalStringProperty("detect.hub.signature.scanner.arguments")
        val DETECT_SWIP_ENABLED = RequiredBooleanProperty("detect.polaris.enabled", false)
        val LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION = RequiredEnumProperty("logging.level.com.blackducksoftware.integration", LogLevel.INFO, LogLevel::fromString, LogLevel.values().toList())
        val DETECT_MAVEN_SCOPE = OptionalStringProperty("detect.maven.scope")
        val DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE = RequiredBooleanProperty("detect.blackduck.signature.scanner.snippet.mode", false)
        val POLARIS_URL = OptionalStringProperty("polaris.url")
        val POLARIS_ACCESS_TOKEN = OptionalStringProperty("polaris.access.token")
        val POLARIS_ARGUMENTS = OptionalStringProperty("polaris.arguments")
        val POLARIS_COMMAND = OptionalStringProperty("polaris.command")

        init {
            val properties = listOf(
                    BLACKDUCK_API_TOKEN
                            .info("Black Duck API Token", "4.2.0")
                            .help("The API token used to authenticate with the Black Duck Server.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default),

                    BLACKDUCK_OFFLINE_MODE
                            .info("Offline Mode", "4.2.0")
                            .help("This can disable any Black Duck communication - if true, Detect will not upload BDIO files, it will not check policies, and it will not download and install the signature scanner.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Offline, Group.Default),
                    BLACKDUCK_PASSWORD
                            .info("Black Duck Password", "4.2.0")
                            .help("Black Duck password.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default),
                    BLACKDUCK_PROXY_HOST
                            .info("Proxy Host", "4.2.0")
                            .help("Hostname for proxy server.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_IGNORED_HOSTS
                            .info("Bypass Proxy Hosts", "4.2.0")
                            .help("A comma separated list of regular expression host patterns that should not use the proxy.", "These patterns must adhere to Java regular expressions: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_NTLM_DOMAIN
                            .info("NTLM Proxy Domain", "4.2.0")
                            .help("NTLM Proxy domain.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_NTLM_WORKSTATION
                            .info("NTLM Proxy Workstation", "4.2.0")
                            .help("NTLM Proxy workstation.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_PASSWORD
                            .info("Proxy Password", "4.2.0")
                            .help("Proxy password.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_PORT
                            .info("Proxy Port", "4.2.0")
                            .help("Proxy port.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_PROXY_USERNAME
                            .info("Proxy Username", "4.2.0")
                            .help("Proxy username.")
                            .groups(Group.Proxy, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_TIMEOUT
                            .info("Black Duck Timeout", "4.2.0")
                            .help("The time to wait for network connections to complete (in seconds).")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_TRUST_CERT
                            .info("Trust All SSL Certificates", "4.2.0")
                            .help("If true, automatically trust the certificate for the current run of Detect only.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default)
                            .category(Category.Advanced),
                    BLACKDUCK_URL
                            .info("Black Duck URL", "4.2.0")
                            .help("URL of the Black Duck server.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default),
                    BLACKDUCK_USERNAME
                            .info("Black Duck Username", "4.2.0")
                            .help("Black Duck username.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default),
                    DETECT_PARALLEL_PROCESSORS
                            .info("Detect Parallel Processors", "6.0.0")
                            .help("The number of threads to run processes in parallel, defaults to 1, but if you specify less than or equal to 0, the number of processors on the machine will be used.")
                            .groups(Group.General, Group.Global)
                            .category(Category.Advanced),
                    DETECT_BASH_PATH
                            .info("Bash Executable", "3.0.0")
                            .help("Path to the Bash executable.", "If set, Detect will use the given Bash executable instead of searching for one.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_BAZEL_PATH
                            .info("Bazel Executable", "5.2.0")
                            .help("The path to the Bazel executable.")
                            .groups(Group.Bazel, Group.Global),
                    DETECT_BAZEL_TARGET
                            .info("Bazel Target", "5.2.0")
                            .help("The Bazel target (for example, //foo:foolib) for which dependencies are collected. For Detect to run Bazel, this property must be set.")
                            .groups(Group.Bazel, Group.SourceScan),
                    DETECT_BAZEL_CQUERY_OPTIONS
                            .info("Bazel cquery additional options", "6.1.0")
                            .help("A comma-separated list of additional options to pass to the bazel cquery command.")
                            .groups(Group.Bazel, Group.SourceScan),
                    DETECT_BAZEL_DEPENDENCY_RULE
                            .info("Bazel workspace external dependency rule", "6.0.0")
                            .help("The Bazel workspace rule used to pull in external dependencies. If not set, Detect will attempt to determine the rule from the contents of the WORKSPACE file.")
                            .groups(Group.Bazel, Group.SourceScan),
                    DETECT_BDIO_OUTPUT_PATH
                            .info("BDIO Output Directory", "3.0.0")
                            .help("The path to the output directory for all BDIO files.", "If not set, the BDIO files are placed in a 'BDIO' subdirectory of the output directory.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_BDIO2_ENABLED
                            .info("BDIO 2 Enabled", "6.1.0")
                            .help("The version of BDIO files to generate.", "If set to false, BDIO version 1 will be generated. If set to true, BDIO version 2 will be generated.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_BINARY_SCAN_FILE
                            .info("Binary Scan Target", "4.2.0")
                            .help("If specified, this file and this file only will be uploaded for binary scan analysis. This property takes precedence over detect.binary.scan.file.name.patterns.")
                            .groups(Group.SignatureScanner, Group.SourcePath),
                    DETECT_BINARY_SCAN_FILE_NAME_PATTERNS
                            .info("Binary Scan Filename Patterns", "6.0.0")
                            .help("If specified, all files in the source directory whose names match these file name patterns will be zipped and uploaded for binary scan analysis. This property will not be used if detect.binary.scan.file.path is specified.")
                            .groups(Group.SignatureScanner, Group.SourcePath),
                    DETECT_BITBAKE_BUILD_ENV_NAME
                            .info("BitBake Init Script Name", "4.4.0")
                            .help("The name of the build environment init script.")
                            .groups(Group.Bitbake, Group.SourceScan),
                    DETECT_BITBAKE_PACKAGE_NAMES
                            .info("BitBake Package Names", "4.4.0")
                            .help("A comma-separated list of package names from which dependencies are extracted.")
                            .groups(Group.Bitbake, Group.SourceScan),
                    DETECT_BITBAKE_SOURCE_ARGUMENTS
                            .info("BitBake Source Arguments", "6.0.0")
                            .help("A comma-separated list of arguments to supply when sourcing the build environment init script.")
                            .groups(Group.Bitbake, Group.SourceScan),
                    DETECT_BITBAKE_SEARCH_DEPTH
                            .info("BitBake Search Depth", "6.1.0")
                            .help("The depth at which Detect will search for the recipe-depends.dot or package-depends.dot files.")
                            .groups(Group.Bitbake, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS
                            .info("Signature Scanner Arguments", "4.2.0")
                            .help("Additional arguments to use when running the Black Duck signature scanner.", "For example: Suppose you are running in bash on Linux and want to use the signature scanner's ability to read a list of directories to exclude from a file (using the signature scanner --exclude-from option). You tell the signature scanner read excluded directories from a file named excludes.txt in your home directory with: --detect.blackduck.signature.scanner.arguments='--exclude-from \${HOME}/excludes.txt'")
                            .groups(Group.SignatureScanner, Group.Global),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN
                            .info("Signature Scanner Dry Run", "4.2.0")
                            .help("If set to true, the signature scanner results are not uploaded to Black Duck, and the scanner results are written to disk.")
                            .groups(Group.SignatureScanner, Group.Global),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS
                            .info("Directory Name Exclusion Patterns", "4.2.0")
                            .help("A comma-separated list of directory name patterns for which Detect searches and adds to the signature scanner --exclude flag values.", "These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters). Detect will recursively search within the scan targets for files/directories that match these patterns and will create the corresponding exclusion patterns (paths relative to the scan target directory) for the signature scanner (Black Duck scan CLI). Please note that the signature scanner will only exclude directories; matched filenames will be passed to the signature scanner but will have no effect. These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns and passed as --exclude values. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.name.patterns=blackduck-common, --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-common', --detect.blackduck.signature.scanner.exclusion.name.patterns='blackduck-*'. Use this property when you want Detect to convert the given patterns to actual paths. Use detect.blackduck.signature.scanner.exclusion.patterns to pass patterns directly to the signature scanner as-is.")
                            .groups(Group.SignatureScanner, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERN_SEARCH_DEPTH
                            .info("Exclusion Patterns Search Depth", "5.0.0")
                            .help("Enables you to adjust the depth to which Detect will search when creating signature scanner exclusion patterns.")
                            .groups(Group.SignatureScanner, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS
                            .info("Exclusion Patterns", "4.2.0")
                            .help("A comma-separated list of values to be used with the Signature Scanner --exclude flag.", "Each pattern provided is passed to the signature scanner (Black Duck scan CLI) as a value for an --exclude option. The signature scanner requires that these exclusion patterns start and end with a forward slash (/) and may not contain double asterisks (**). These patterns will be added to the paths created from detect.blackduck.signature.scanner.exclusion.name.patterns and passed as --exclude values. Use this property to pass patterns directly to the signature scanner as-is. For example: suppose you are running in bash on Linux, and have a subdirectory named blackduck-common that you want to exclude from signature scanning. Any of the following would exclude it: --detect.blackduck.signature.scanner.exclusion.patterns=/blackduck-common/, --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-common/', --detect.blackduck.signature.scanner.exclusion.patterns='/blackduck-*/'. Use detect.blackduck.signature.scanner.exclusion.name.patterns when you want Detect to convert the given patterns to actual paths.")
                            .groups(Group.SignatureScanner, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL
                            .info("Signature Scanner Host URL", "4.2.0")
                            .help("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Black Duck's urls for different operating systems.")
                            .groups(Group.SignatureScanner, Group.Global)
                            .category(Category.Advanced),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH
                            .info("Signature Scanner Local Path", "4.2.0")
                            .help("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
                            .groups(Group.SignatureScanner, Group.Global),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY
                            .info("Signature Scanner Memory", "4.2.0")
                            .help("The memory for the scanner to use.")
                            .groups(Group.SignatureScanner, Group.Global)
                            .category(Category.Advanced),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH
                            .info("Signature Scanner Local Path (Offline)", "4.2.0")
                            .help("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
                            .groups(Group.SignatureScanner, Group.Global)
                            .category(Category.Advanced),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS
                            .info("Signature Scanner Target Paths", "4.2.0")
                            .help("These paths and only these paths will be scanned.")
                            .groups(Group.SignatureScanner, Group.Global),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MATCHING
                            .info("Snippet Matching", "5.5.0")
                            .help("Use this value to enable the various snippet scanning modes. For a full explanation, please refer to the 'Running a component scan using the Signature Scanner command line' section in your Black Duck server's online help.")
                            .groups(Group.SignatureScanner, Group.Global, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_UPLOAD_SOURCE_MODE
                            .info("Upload source mode", "5.4.0")
                            .help("If set to true, the signature scanner will, if supported by your Black Duck version, upload source code to Black Duck.")
                            .groups(Group.SignatureScanner, Group.Global, Group.SourceScan),
                    DETECT_BOM_AGGREGATE_NAME
                            .info("Aggregate BDIO File Name", "3.0.0")
                            .help("If set, this will aggregate all the BOMs to create a single BDIO file with the name provided.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_BOM_AGGREGATE_REMEDIATION_MODE
                            .info("BDIO Aggregate Remediation Mode", "6.1.0")
                            .help("If an aggregate BDIO file is being generated and this property is set to DIRECT, the aggregate BDIO file will exclude code location nodes from the top layer of the dependency tree to preserve the correct identification of direct dependencies in the resulting Black Duck BOM. When this property is set to TRANSITIVE (the default), component source information is preserved by including code location nodes at the top of the dependency tree, but all components will appear as TRANSITIVE in the BOM.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_BUILDLESS
                            .info("Buildless Mode", "5.4.0")
                            .help("If set to true, only Detector's capable of running without a build will be run.")
                            .groups(Group.General, Group.Global),
                    DETECT_CLEANUP
                            .info("Cleanup Output", "3.2.0")
                            .help("If true, the files created by Detect will be cleaned up.")
                            .groups(Group.Cleanup, Group.Global),
                    DETECT_CLONE_PROJECT_VERSION_NAME
                            .info("Clone Project Version Name", "4.2.0")
                            .help("The name of the project version to clone this project version from. Respects the given Clone Categories in detect.project.clone.categories or as set on the Black Duck server.")
                            .groups(Group.Project, Group.Global, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_CLONE_PROJECT_VERSION_LATEST
                            .info("Clone Latest Project Version", "5.6.0")
                            .help("If set to true, detect will attempt to use the latest project version as the clone for this project. The project must exist and have at least one version.")
                            .groups(Group.Project, Group.Global, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_CODE_LOCATION_NAME
                            .info("Scan Name", "4.0.0")
                            .help("An override for the name Detect will use for the scan file it creates. If supplied and multiple scans are found, Detect will append an index to each scan name.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_CONDA_ENVIRONMENT_NAME
                            .info("Anaconda Environment Name", "3.0.0")
                            .help("The name of the anaconda environment used by your project.")
                            .groups(Group.Conda, Group.SourceScan),
                    DETECT_CONDA_PATH
                            .info("Conda Executable", "3.0.0")
                            .help("The path to the conda executable.")
                            .groups(Group.Conda, Group.Global),
                    DETECT_CPAN_PATH
                            .info("cpan Executable", "3.0.0")
                            .help("The path to the cpan executable.")
                            .groups(Group.Cpan, Group.Global),
                    DETECT_CPANM_PATH
                            .info("cpanm Executable", "3.0.0")
                            .help("The path to the cpanm executable.")
                            .groups(Group.Cpan, Group.Global),
                    DETECT_DEFAULT_PROJECT_VERSION_SCHEME
                            .info("Default Project Version Name Scheme", "3.0.0")
                            .help("The scheme to use when the package managers can not determine a version, either 'text' or 'timestamp'.")
                            .groups(Group.Project, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DEFAULT_PROJECT_VERSION_TEXT
                            .info("Default Project Version Name Text", "3.0.0")
                            .help("The text to use as the default project version.")
                            .groups(Group.Project, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT
                            .info("Default Project Version Name Timestamp Format", "3.0.0")
                            .help("The timestamp format to use as the default project version.")
                            .groups(Group.Project, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_DEPTH
                            .info("Detector Search Depth", "3.2.0")
                            .help("Depth of subdirectories within the source directory to which Detect will search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan),
                    DETECT_DETECTOR_SEARCH_CONTINUE
                            .info("Detector Search Continue", "3.2.0")
                            .help("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_EXCLUSION
                            .info("Detector Directory Exclusions", "3.2.0")
                            .help("A comma-separated list of directory names to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name appear in this list will not be searched.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_EXCLUSION_PATTERNS
                            .info(" Detector Directory Patterns Exclusions", "3.2.0")
                            .help("A comma-separated list of directory name patterns to exclude from detector search.", "While searching the source directory to determine which detectors to run, subdirectories whose name match a pattern in this list will not be searched. These patterns are file system glob patterns ('?' is a wildcard for a single character, '*' is a wildcard for zero or more characters). For example, suppose you're running in bash on Linux, you've set --detect.detector.search.depth=1, and have a subdirectory named blackduck-common (a gradle project) that you want to exclude from the detector search. Any of the following would exclude it: --detect.detector.search.exclusion.patterns=blackduck-common, --detect.detector.search.exclusion.patterns='blackduck-common', --detect.detector.search.exclusion.patterns='blackduck-*'")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_EXCLUSION_PATHS
                            .info(" Detector Directory Path Exclusions", "5.5.0")
                            .help("A comma-separated list of directory paths to exclude from detector search. (E.g. 'foo/bar/biz' will only exclude the 'biz' directory if the parent directory structure is 'foo/bar/'.)", "This property performs the same basic function as detect.detector.search.exclusion, but lets you be more specific.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_EXCLUSION_FILES
                            .info(" Detector File Exclusions", "6.0.0")
                            .help("A comma-separated list of file names to exclude from detector search.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_DETECTOR_SEARCH_EXCLUSION_DEFAULTS
                            .info("Detector Exclude Default Directories", "3.2.0")
                            .help("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the detector search: bin, build, .git, .gradle, node_modules, out, packages, target.")
                            .groups(Group.Paths, Group.Detector, Group.Global, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_IGNORE_CONNECTION_FAILURES
                            .info("Detect Ignore Connection Failures", "5.3.0")
                            .help("If true, Detect will ignore any products that it cannot connect to.", "If true, when Detect attempts to boot a product it will also check if it can communicate with it - if it cannot, it will not run the product.")
                            .groups(Group.General, Group.BlackduckServer, Group.Polaris)
                            .category(Category.Advanced),
                    DETECT_DOCKER_IMAGE
                            .info("Docker Image Name", "3.0.0")
                            .help("The Docker image name to inspect. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.")
                            .groups(Group.Docker, Group.SourcePath),
                    DETECT_DOCKER_IMAGE_ID
                            .info("Docker Image ID", "6.1.0")
                            .help("The Docker image ID to inspect.")
                            .groups(Group.Docker, Group.SourcePath),
                    DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH
                            .info("Docker Inspector AirGap Path", "3.0.0")
                            .help("The path to the directory containing the Docker Inspector jar and images.")
                            .groups(Group.Docker, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DOCKER_INSPECTOR_PATH
                            .info("Docker Inspector .jar File Path", "3.0.0")
                            .help("This is used to override using the hosted Docker Inspector .jar file by binary repository url. You can use a local Docker Inspector .jar file at this path.")
                            .groups(Group.Docker, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DOCKER_INSPECTOR_VERSION
                            .info("Docker Inspector Version", "3.0.0")
                            .help("Version of the Docker Inspector to use. By default Detect will attempt to automatically determine the version to use.")
                            .groups(Group.Docker, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DOCKER_PATH
                            .info("Docker Executable", "3.0.0")
                            .help("Path to the docker executable.")
                            .groups(Group.Docker, Group.Global),
                    DETECT_DOCKER_PATH_REQUIRED
                            .info("Run Without Docker in Path", "4.0.0")
                            .help("If set to true, Detect will attempt to run the Docker Inspector only if it finds a docker client executable.")
                            .groups(Group.Docker, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DOCKER_PLATFORM_TOP_LAYER_ID
                            .info("Platform Top Layer ID", "6.1.0")
                            .help("To exclude components from platform layers from the results, assign to this property the ID of the top layer of the platform image. Get the platform top layer ID from the output of 'docker inspect platformimage:tag'. The platform top layer ID is the last item in RootFS.Layers. For more information, see 'Isolating application components' in the Docker Inspector documentation.", "If you are interested in components from the application layers of your image, but not interested in components from the underlying platform layers, you can exclude components from platform layers from the results by using this property to specify the boundary between platform layers and application layers. ")
                            .groups(Group.Docker, Group.Global)
                            .category(Category.Advanced),
                    DETECT_DOCKER_TAR
                            .info("Docker Image Archive File", "3.0.0")
                            .help("A saved Docker image - must be a .tar file. For Detect to run Docker Inspector, either this property or detect.docker.tar must be set. Docker Inspector finds packages installed by the Linux package manager in Linux-based images.")
                            .groups(Group.Docker, Group.SourcePath),
                    DETECT_DOTNET_PATH
                            .info("dotnet Executable", "4.4.0")
                            .help("The path to the dotnet executable.")
                            .groups(Group.Nuget, Group.Global),
                    DETECT_EXCLUDED_DETECTOR_TYPES
                            .info("Detector Types Excluded", "3.0.0")
                            .help("By default, all detectors will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all detectors, specify  \"ALL\". Exclusion rules always win.", "If Detect runs one or more detector on your project that you would like to exclude, you can use this property to prevent Detect from running them.")
                            .groups(Group.Detector, Group.Global)
                            .category(Category.Advanced),
                    DETECT_FORCE_SUCCESS
                            .info("Force Success", "3.0.0")
                            .help("If true, Detect will always exit with code 0.")
                            .groups(Group.General, Group.Global)
                            .category(Category.Advanced),
                    DETECT_GIT_PATH
                            .info("Git Executable", "5.5.0")
                            .help("Path of the git executable")
                            .groups(Group.Paths, Group.Global),
                    DETECT_GO_PATH
                            .info("Go Executable", "3.0.0")
                            .help("Path to the Go executable.")
                            .groups(Group.Go, Group.Global),
                    DETECT_GRADLE_BUILD_COMMAND
                            .info("Gradle Build Command", "3.0.0")
                            .help("Gradle command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the gradle (or gradlew) command with one task: dependencies. You can use this property to insert one or more additional gradle command line arguments (options or tasks) before the dependencies argument.")
                            .groups(Group.Gradle, Group.SourceScan),
                    DETECT_GRADLE_EXCLUDED_CONFIGURATIONS
                            .info("Gradle Exclude Configurations", "3.0.0")
                            .help("A comma-separated list of Gradle configurations to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle configurations specified via this property.")
                            .groups(Group.Gradle, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_GRADLE_EXCLUDED_PROJECTS
                            .info("Gradle Exclude Projects", "3.0.0")
                            .help("A comma-separated list of Gradle sub-projects to exclude.", "As Detect examines the Gradle project for dependencies, Detect will skip any Gradle sub-projects specified via this property.")
                            .groups(Group.Gradle, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_GRADLE_INCLUDED_CONFIGURATIONS
                            .info("Gradle Include Configurations", "3.0.0")
                            .help("A comma-separated list of Gradle configurations to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those Gradle configurations specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
                            .groups(Group.Gradle, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_GRADLE_INCLUDED_PROJECTS
                            .info("Gradle Include Projects", "3.0.0")
                            .help("A comma-separated list of Gradle sub-projects to include.", "As Detect examines the Gradle project for dependencies, if this property is set, Detect will include only those sub-projects specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
                            .groups(Group.Gradle, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH
                            .info("Gradle Inspector AirGap Path", "3.0.0")
                            .help("The path to the directory containing the air gap dependencies for the gradle inspector.", "Use this property when running Detect on a Gradle project in 'air gap' mode (offline). Download and unzip the Detect air gap zip file, and point this property to the packaged-inspectors/gradle directory.")
                            .groups(Group.Gradle, Group.Global)
                            .category(Category.Advanced),
                    DETECT_GRADLE_INSPECTOR_VERSION
                            .info("Gradle Inspector Version", "3.0.0")
                            .help("The version of the Gradle Inspector that Detect should use. By default, Detect will try to automatically determine the correct Gradle Inspector version.", "The Detect Gradle detector uses a separate program, the Gradle Inspector, to discover dependencies from Gradle projects. Detect automatically downloads the Gradle Inspector as needed. Use the property to use a specific version of the Gradle Inspector.")
                            .groups(Group.Gradle, Group.Global)
                            .category(Category.Advanced),
                    DETECT_GRADLE_PATH
                            .info("Gradle Executable", "3.0.0")
                            .help("The path to the Gradle executable (gradle or gradlew).", "If set, Detect will use the given Gradle executable instead of searching for one.")
                            .groups(Group.Gradle),
                    DETECT_HEX_REBAR3_PATH
                            .info("Rebar3 Executable", "3.0.0")
                            .help("The path to the rebar3 executable.")
                            .groups(Group.Hex, Group.Global),
                    DETECT_INCLUDED_DETECTOR_TYPES
                            .info("Detector Types Included", "3.0.0")
                            .help("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.", "If you want to limit Detect to a subset of its detectors, use this property to specify that subset.")
                            .groups(Group.Detector, Group.Global)
                            .category(Category.Advanced),
                    DETECT_JAVA_PATH
                            .info("Java Executable", "5.0.0")
                            .help("Path to the java executable.", "If set, Detect will use the given java executable instead of searching for one.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_MAVEN_BUILD_COMMAND
                            .info("Maven Build Command", "3.0.0")
                            .help("Maven command line arguments to add to the mvn/mvnw command line.", "By default, Detect runs the mvn (or mvnw) command with one argument: dependency:tree. You can use this property to insert one or more additional mvn command line arguments (goals, etc.) before the dependency:tree argument. For example: suppose you are running in bash on Linux, and want to point maven to your settings file (maven_dev_settings.xml in your home directory) and assign the value 'other' to property 'reason'. You could do this with: --detect.maven.build.command='--settings \${HOME}/maven_dev_settings.xml --define reason=other'")
                            .groups(Group.Maven, Group.SourceScan),
                    DETECT_MAVEN_EXCLUDED_MODULES
                            .info("Maven Modules Excluded", "3.0.0")
                            .help("A comma-separated list of Maven modules (sub-projects) to exclude.", "As Detect parses the mvn dependency:tree output for dependencies, Detect will skip any Maven modules specified via this property.")
                            .groups(Group.Maven, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_MAVEN_INCLUDED_MODULES
                            .info("Maven Modules Included", "3.0.0")
                            .help("A comma-separated list of Maven modules (sub-projects) to include.", "As Detect parses the mvn dependency:tree output for dependencies, if this property is set, Detect will include only those Maven modules specified via this property that are not excluded. Leaving this unset implies 'include all'. Exclusion rules always win.")
                            .groups(Group.Maven, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_MAVEN_PATH
                            .info("Maven Executable", "3.0.0")
                            .help("The path to the Maven executable (mvn or mvnw).", "If set, Detect will use the given Maven executable instead of searching for one.")
                            .groups(Group.Maven, Group.Global),
                    DETECT_MAVEN_INCLUDED_SCOPES
                            .info("Dependency Scope Included", "6.0.0")
                            .help("A comma separated list of Maven scopes. Output will be limited to dependencies within these scopes (overridden by exclude).", "If set, Detect will include only dependencies of the given Maven scope.")
                            .groups(Group.Maven, Group.SourceScan),
                    DETECT_MAVEN_EXCLUDED_SCOPES
                            .info("Dependency Scope Excluded", "6.0.0")
                            .help("A comma separated list of Maven scopes. Output will be limited to dependencies outside these scopes (overrides include).", "If set, Detect will include only dependencies outside of the given Maven scope.")
                            .groups(Group.Maven, Group.SourceScan),
                    DETECT_MAVEN_INCLUDE_PLUGINS
                            .info("Maven Include Plugins", "5.6.0")
                            .help("Whether or not detect will include the plugins section when parsing a pom.xml.")
                            .groups(Group.Maven, Group.Global)
                            .category(Category.Advanced),
                    DETECT_NOTICES_REPORT
                            .info("Generate Notices Report", "3.0.0")
                            .help("When set to true, a Black Duck notices report in text form will be created in your source directory.")
                            .groups(Group.Report, Group.Global),
                    DETECT_NOTICES_REPORT_PATH
                            .info("Notices Report Path", "3.0.0")
                            .help("The output directory for notices report. Default is the source directory.")
                            .groups(Group.Report, Group.Global, Group.ReportSetting),
                    DETECT_NPM_ARGUMENTS
                            .info("Additional NPM Command Arguments", "4.3.0")
                            .help("A space-separated list of additional arguments to add to the npm command line when running Detect against an NPM project.")
                            .groups(Group.Npm, Group.SourceScan),
                    DETECT_NPM_INCLUDE_DEV_DEPENDENCIES
                            .info("Include NPM Development Dependencies", "3.0.0")
                            .help("Set this value to false if you would like to exclude your dev dependencies when ran.")
                            .groups(Group.Npm, Group.Global, Group.SourceScan),
                    DETECT_NPM_PATH
                            .info("NPM Executable", "3.0.0")
                            .help("The path to the Npm executable.")
                            .groups(Group.Npm, Group.Global),
                    DETECT_NUGET_CONFIG_PATH
                            .info("Nuget Config File", "4.0.0")
                            .help("The path to the Nuget.Config file to supply to the nuget exe.")
                            .groups(Group.Nuget, Group.SourceScan),
                    DETECT_NUGET_EXCLUDED_MODULES
                            .info("Nuget Projects Excluded", "3.0.0")
                            .help("The names of the projects in a solution to exclude.")
                            .groups(Group.Nuget, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_NUGET_IGNORE_FAILURE
                            .info("Ignore Nuget Failures", "3.0.0")
                            .help("If true errors will be logged and then ignored.")
                            .groups(Group.Nuget, Group.Global)
                            .category(Category.Advanced),
                    DETECT_NUGET_INCLUDED_MODULES
                            .info("Nuget Modules Included", "3.0.0")
                            .help("The names of the projects in a solution to include (overrides exclude).")
                            .groups(Group.Nuget, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_NUGET_INSPECTOR_AIR_GAP_PATH
                            .info("Nuget Inspector AirGap Path", "3.0.0")
                            .help("The path to the directory containing the nuget inspector nupkg.")
                            .groups(Group.Nuget, Group.Global)
                            .category(Category.Advanced),
                    DETECT_NUGET_INSPECTOR_VERSION
                            .info("Nuget Inspector Version", "3.0.0")
                            .help("Version of the Nuget Inspector. By default Detect will run the latest version that is compatible with the Detect version.")
                            .groups(Group.Nuget, Group.Global)
                            .category(Category.Advanced),
                    DETECT_NUGET_PACKAGES_REPO_URL
                            .info("Nuget Packages Repository URL", "3.0.0")
                            .help("The source for nuget packages", "Set this to \"https://www.nuget.org/api/v2/\" if your are still using a nuget client expecting the v2 api.")
                            .groups(Group.Nuget, Group.Global),
                    DETECT_OUTPUT_PATH
                            .info("Detect Output Path", "3.0.0")
                            .help("The path to the output directory.", "If set, Detect will use the given directory to store files that it downloads and creates, instead of using the default location (~/blackduck).")
                            .groups(Group.Paths, Group.Global),
                    DETECT_TOOLS_OUTPUT_PATH
                            .info("Detect Tools Output Path", "5.6.0")
                            .help("The path to the tools directory where detect should download and/or access things like the Signature Scanner that it shares over multiple runs.", "If set, Detect will use the given directory instead of using the default location of output path plus tools.")
                            .groups(Group.Paths, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES
                            .info("Include Packagist Development Dependencies", "3.0.0")
                            .help("Set this value to false if you would like to exclude your dev requires dependencies when ran.")
                            .groups(Group.Packagist, Group.Global, Group.SourceScan),
                    DETECT_PEAR_ONLY_REQUIRED_DEPS
                            .info("Include Only Required Pear Dependencies", "3.0.0")
                            .help("Set to true if you would like to include only required packages.")
                            .groups(Group.Pear, Group.Global, Group.SourceScan),
                    DETECT_PEAR_PATH
                            .info("Pear Executable", "3.0.0")
                            .help("The path to the pear executable.")
                            .groups(Group.Pear, Group.Global),
                    DETECT_PIP_PROJECT_NAME
                            .info("PIP Project Name", "3.0.0")
                            .help("The name of your PIP project, to be used if your project's name cannot be correctly inferred from its setup.py file.")
                            .groups(Group.Pip, Group.SourceScan),
                    DETECT_PIP_PROJECT_VERSION_NAME
                            .info("PIP Project Version Name", "4.1.0")
                            .help("The version of your PIP project, to be used if your project's version name cannot be correctly inferred from its setup.py file.")
                            .groups(Group.Pip, Group.SourceScan),
                    DETECT_PIP_REQUIREMENTS_PATH
                            .info("PIP Requirements Path", "3.0.0")
                            .help("A comma-separated list of paths to requirements.txt files.")
                            .groups(Group.Pip, Group.SourceScan),
                    DETECT_PIP_ONLY_PROJECT_TREE
                            .info("PIP Include Only Project Tree", "6.1.0")
                            .help("By default, pipenv includes all dependencies found in the graph. Set to true to only include dependencies found underneath the dependency that matches the provided pip project and version name.")
                            .groups(Group.Pip, Group.SourceScan),
                    DETECT_PIPENV_PATH
                            .info("Pipenv Executable", "4.1.0")
                            .help("The path to the Pipenv executable.")
                            .groups(Group.Pip, Group.Global),
                    DETECT_SWIFT_PATH
                            .info("Swift Executable", "6.0.0")
                            .help("Path of the swift executable.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES
                            .info("Fail on Policy Violation Severities", "3.0.0")
                            .help("A comma-separated list of policy violation severities that will fail Detect. If this is not set, Detect will not fail due to policy violations. A value of ALL is equivalent to all of the other possible values except UNSPECIFIED.")
                            .groups(Group.Project, Group.Global, Group.ProjectSetting, Group.Policy),
                    DETECT_PROJECT_APPLICATION_ID
                            .info("Application ID", "5.2.0")
                            .help("Sets the 'Application ID' project setting.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_CUSTOM_FIELDS_PROJECT
                            .info("Custom Fields", "5.6.0")
                            .help("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.project[0].label='example' and detect.custom.fields.project[0].value='one,two'. Note that these will not show up in the detect configuration log.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_CUSTOM_FIELDS_VERSION
                            .info("Custom Fields", "5.6.0")
                            .help("A  list of custom fields with a label and comma-separated value starting from index 0. For example detect.custom.fields.version[0].label='example' and detect.custom.fields.version[0].value='one,two'. Note that these will not show up in the detect configuration log.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_CLONE_CATEGORIES
                            .info("Clone Project Categories", "4.2.0")
                            .help("An override for the Project Clone Categories that are used when cloning a version. If the project already exists, make sure to use --detect.project.version.update to make sure these are set.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_CODELOCATION_PREFIX
                            .info("Scan Name Prefix", "3.0.0")
                            .help("A prefix to the name of the scans created by Detect. Useful for running against the same projects on multiple machines.")
                            .groups(Group.Project, Group.ProjectSetting, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PROJECT_CODELOCATION_SUFFIX
                            .info("Scan Name Suffix", "3.0.0")
                            .help("A suffix to the name of the scans created by Detect.")
                            .groups(Group.Project, Group.ProjectSetting, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PROJECT_CODELOCATION_UNMAP
                            .info("Unmap All Other Scans for Project", "4.0.0")
                            .help("If set to true, unmaps all other scans mapped to the project version produced by the current run of Detect.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_DESCRIPTION
                            .info("Project Description", "4.0.0")
                            .help("If project description is specified, your project version will be created with this description.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_USER_GROUPS
                            .info("Project User Groups", "5.4.0")
                            .help("A comma-separated list of names of user groups to add to the project.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_TAGS
                            .info("Project Tags", "5.6.0")
                            .help("A comma-separated list of tags to add to the project.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_DETECTOR
                            .info("Project Name/Version Detector", "4.0.0")
                            .help("The detector that will be used to determine the project name and version when multiple detector types. This property should be used with the detect.project.tool.", "If Detect finds that multiple detectors apply, this property can be used to select the detector that will provide the project name and version. When using this property, you should also set detect.project.tool=DETECTOR")
                            .groups(Group.Paths, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PROJECT_LEVEL_ADJUSTMENTS
                            .info("Allow Project Level Adjustments", "3.0.0")
                            .help("An override for the Project level matches.")
                            .groups(Group.Project, Group.ProjectSetting, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PROJECT_NAME
                            .info("Project Name", "3.0.0")
                            .help("An override for the name to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable project name. If that fails, the final part of the directory path where the inspection is taking place will be used.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PARENT_PROJECT_NAME
                            .info("Parent Project Name", "3.0.0")
                            .help("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PARENT_PROJECT_VERSION_NAME
                            .info("Parent Project Version Name", "3.0.0")
                            .help("When a parent project and version name are specified, the created detect project will be added as a component to the specified parent project version.")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_TIER
                            .info("Project Tier", "3.1.0")
                            .help("If a Black Duck project tier is specified, your project will be created with this tier.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_TOOL
                            .info("Detector Tool Priority", "5.0.0")
                            .help("The tool priority for project name and version. The project name and version will be determined by the first tool in this list that provides them.", "This allows you to control which tool provides the project name and version when more than one tool are capable of providing it.")
                            .groups(Group.Paths, Group.Global)
                            .category(Category.Advanced),
                    DETECT_PROJECT_VERSION_DISTRIBUTION
                            .info("Version Distribution", "3.0.0")
                            .help("An override for the Project Version distribution")
                            .groups(Group.Project, Group.ProjectSetting)
                            .category(Category.Advanced),
                    DETECT_PROJECT_VERSION_NAME
                            .info("Version Name", "3.0.0")
                            .help("An override for the version to use for the Black Duck project. If not supplied, Detect will attempt to use the tools to figure out a reasonable version name. If that fails, the current date will be used.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_VERSION_NICKNAME
                            .info("Version Nickname", "5.2.0")
                            .help("If a project version nickname is specified, your project version will be created with this nickname.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_VERSION_NOTES
                            .info("Version Notes", "3.1.0")
                            .help("If project version notes are specified, your project version will be created with these notes.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_VERSION_PHASE
                            .info("Version Phase", "3.0.0")
                            .help("An override for the Project Version phase.")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PROJECT_VERSION_UPDATE
                            .info("Update Project Version", "4.0.0")
                            .help("If set to true, will update the Project Version with the configured properties. See detailed help for more information.", "When set to true, the following properties will be updated on the Project. Project tier (detect.project.tier) and Project Level Adjustments (detect.project.level.adjustments). The following properties will also be updated on the Version. Version notes (detect.project.version.notes), phase (detect.project.version.phase), distribution (detect.project.version.distribution).")
                            .groups(Group.Project, Group.ProjectSetting),
                    DETECT_PYTHON_PATH
                            .info("Python Executable", "3.0.0")
                            .help("The path to the Python executable.")
                            .groups(Group.Python, Group.Global),
                    DETECT_PYTHON_PYTHON3
                            .info("Use Python3", "3.0.0")
                            .help("If true will use Python 3 if available on class path.")
                            .groups(Group.Python, Group.Global),
                    DETECT_REPORT_TIMEOUT
                            .info("Report Generation Timeout", "5.2.0")
                            .help("The amount of time in seconds Detect will wait for scans to finish and to generate reports (i.e. risk and policy check). When changing this value, keep in mind the checking of policies might have to wait for scans to process which can take some time.")
                            .groups(Group.BlackduckServer, Group.Global),
                    DETECT_REQUIRED_DETECTOR_TYPES
                            .info("Required Detect Types", "4.3.0")
                            .help("The set of required detectors.", "If you want one or more detectors to be required (must be found to apply), use this property to specify the set of required detectors. If this property is set, and one (or more) of the given detectors is not found to apply, Detect will fail.")
                            .groups(Group.Detector, Group.Global),
                    DETECT_RESOLVE_TILDE_IN_PATHS
                            .info("Resolve Tilde in Paths", "3.0.0")
                            .help("If set to false Detect will not automatically resolve the '~/' prefix in a mac or linux path to the user's home directory.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_RISK_REPORT_PDF
                            .info("Generate Risk Report (PDF)", "3.0.0")
                            .help("When set to true, a Black Duck risk report in PDF form will be created.")
                            .groups(Group.Report, Group.Global, Group.ReportSetting),
                    DETECT_RISK_REPORT_PDF_PATH
                            .info("Risk Report Output Path", "3.0.0")
                            .help("The output directory for risk report in PDF. Default is the source directory.")
                            .groups(Group.Report, Group.Global),
                    DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES
                            .info("Ruby Runtime Dependencies", "5.4.0")
                            .help("If set to false, runtime dependencies will not be included when parsing *.gemspec files.")
                            .groups(Group.Ruby, Group.Global, Group.SourceScan),
                    DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES
                            .info("Ruby Development Dependencies", "5.4.0")
                            .help("If set to true, development dependencies will be included when parsing *.gemspec files.")
                            .groups(Group.Ruby, Group.Global, Group.SourceScan),
                    DETECT_SBT_EXCLUDED_CONFIGURATIONS
                            .info("SBT Configurations Excluded", "3.0.0")
                            .help("The names of the sbt configurations to exclude.")
                            .groups(Group.Sbt, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_SBT_INCLUDED_CONFIGURATIONS
                            .info("SBT Configurations Included", "3.0.0")
                            .help("The names of the sbt configurations to include.")
                            .groups(Group.Sbt, Group.SourceScan)
                            .category(Category.Advanced),
                    DETECT_SBT_REPORT_DEPTH
                            .info("SBT Report Search Depth", "4.3.0")
                            .help("Depth the sbt detector will use to search for report files.")
                            .groups(Group.Sbt, Group.SourceScan),
                    DETECT_SCAN_OUTPUT_PATH
                            .info("Scan Output Path", "3.0.0")
                            .help("The output directory for all signature scanner output files. If not set, the signature scanner output files will be in a 'scan' subdirectory of the output directory.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_SOURCE_PATH
                            .info("Source Path", "3.0.0")
                            .help("The path to the project directory to inspect.", "Detect will search the given directory for hints that indicate which package manager(s) the project uses, and will attempt to run the corresponding detector(s).")
                            .groups(Group.Paths, Group.SourcePath),
                    DETECT_TEST_CONNECTION
                            .info("Test Connection to Black Duck", "3.0.0")
                            .help("Test the connection to Black Duck with the current configuration.")
                            .groups(Group.BlackduckServer, Group.Global),
                    DETECT_TOOLS
                            .info("Detect Tools Included", "5.0.0")
                            .help("The tools Detect should allow in a comma-separated list. Tools in this list (as long as they are not also in the excluded list) will be allowed to run if all criteria of the tool are met. Exclusion rules always win.", "This property and detect.tools.excluded provide control over which tools Detect runs.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_TOOLS_EXCLUDED
                            .info("Detect Tools Excluded", "5.0.0")
                            .help("The tools Detect should not allow, in a comma-separated list. Excluded tools will not be run even if all criteria for the tool is met. Exclusion rules always win.", "This property and detect.tools provide control over which tools Detect runs.")
                            .groups(Group.Paths, Group.Global),
                    DETECT_YARN_PROD_ONLY
                            .info("Include Yarn Production Dependencies Only", "4.0.0")
                            .help("Set this to true to only scan production dependencies.")
                            .groups(Group.Yarn, Group.Global, Group.SourceScan),
                    LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION
                            .info("Logging Level", "5.3.0")
                            .help("The logging level of Detect.")
                            .groups(Group.Logging, Group.Global),
                    LOGGING_LEVEL_DETECT
                            .info("Logging Level Shorthand", "5.5.0")
                            .help("Shorthand for the logging level of detect. Equivalent to setting logging.level.com.synopsys.integration.")
                            .groups(Group.Logging, Group.Global),
                    DETECT_WAIT_FOR_RESULTS
                            .info("Wait For Results", "5.5.0")
                            .help("If set to true, Detect will wait for Synopsys products until results are available or the blackduck.timeout is exceeded.")
                            .groups(Group.General, Group.Global),
                    DETECT_BITBAKE_REFERENCE_IMPL
                            .info("Reference implementation", "4.4.0")
                            .help("The reference implementation of the Yocto project. These characters are stripped from the discovered target architecture.")
                            .groups(Group.Bitbake, Group.SourceScan),
                    DETECT_API_TIMEOUT
                            .info("Detect Api Timeout", "3.0.0")
                            .help("Timeout for response from Black Duck regarding your project (i.e. risk reports and policy check). When changing this value, keep in mind the checking of policies might have to wait for a new scan to process which can take some time.")
                            .groups(Group.ProjectInfo, Group.Project),
                    BLACKDUCK_HUB_URL
                            .info("Blackduck Hub Url", "3.0.0")
                            .help("URL of the Hub server.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_TIMEOUT
                            .info("Blackduck Hub Timeout", "3.0.0")
                            .help("The time to wait for rest connections to complete in seconds.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_USERNAME
                            .info("Blackduck Hub Username", "3.0.0")
                            .help("Hub username.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_PASSWORD
                            .info("Blackduck Hub Password", "3.0.0")
                            .help("Hub password.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_API_TOKEN
                            .info("Blackduck Hub Api Token", "3.1.0")
                            .help("Hub API Token.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_PROXY_HOST
                            .info("Blackduck Hub Proxy Host", "3.0.0")
                            .help("Proxy host.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_PORT
                            .info("Blackduck Hub Proxy Port", "3.0.0")
                            .help("Proxy port.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_USERNAME
                            .info("Blackduck Hub Proxy Username", "3.0.0")
                            .help("Proxy username.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_PASSWORD
                            .info("Blackduck Hub Proxy Password", "3.0.0")
                            .help("Proxy password.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_NTLM_DOMAIN
                            .info("Blackduck Hub Proxy Ntlm Domain", "3.1.0")
                            .help("NTLM Proxy domain.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_IGNORED_HOSTS
                            .info("Blackduck Hub Proxy Ignored Hosts", "3.2.0")
                            .help("A comma-separated list of host patterns that should not use the proxy.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION
                            .info("Blackduck Hub Proxy Ntlm Workstation", "3.1.0")
                            .help("NTLM Proxy workstation.")
                            .groups(Group.BlackduckServer, Group.Proxy),
                    BLACKDUCK_HUB_TRUST_CERT
                            .info("Blackduck Hub Trust Cert", "3.0.0")
                            .help("If true, automatically trusts the certificate for the current run of Detect only.")
                            .groups(Group.BlackduckServer),
                    BLACKDUCK_HUB_OFFLINE_MODE
                            .info("Blackduck Hub Offline Mode", "3.0.0")
                            .help("This disables any Hub communication. If true, Detect does not upload BDIO files, does not check policies, and does not download and install the signature scanner.")
                            .groups(Group.BlackduckServer, Group.Offline),
                    DETECT_DISABLE_WITHOUT_HUB
                            .info("Detect Disable Without Hub", "4.0.0")
                            .help("If true, during initialization Detect will check for Hub connectivity and exit with status code 0 if it cannot connect.")
                            .groups(Group.BlackduckServer),
                    DETECT_DISABLE_WITHOUT_BLACKDUCK
                            .info("Check For Valid Black Duck Connection", "4.2.0")
                            .help("If true, during initialization Detect will check for Black Duck connectivity and exit with status code 0 if it cannot connect.")
                            .groups(Group.BlackduckServer, Group.Blackduck, Group.Default),
                    DETECT_SUPPRESS_CONFIGURATION_OUTPUT
                            .info("Detect Suppress Configuration Output", "3.0.0")
                            .help("If true, the default behavior of printing your configuration properties at startup will be suppressed.")
                            .groups(Group.Logging),
                    DETECT_SUPPRESS_RESULTS_OUTPUT
                            .info("Detect Suppress Results Output", "3.0.0")
                            .help("If true, the default behavior of printing the Detect Results will be suppressed.")
                            .groups(Group.Logging),
                    DETECT_EXCLUDED_BOM_TOOL_TYPES
                            .info("Detect Excluded Bom Tool Types", "3.0.0")
                            .help("By default, all tools will be included. If you want to exclude specific detectors, specify the ones to exclude here. If you want to exclude all tools, specify \"ALL\". Exclusion rules always win.")
                            .groups(Group.Detector, Group.SourceScan),
                    DETECT_BOM_TOOL_SEARCH_EXCLUSION_DEFAULTS
                            .info("Detect Bom Tool Search Exclusion Defaults", "3.2.0")
                            .help("If true, the bom tool search will exclude the default directory names. See the detailed help for more information.", "If true, these directories will be excluded from the bom tool search: bin, build, .git, .gradle, node_modules, out, packages, target")
                            .groups(Group.Paths, Group.Detector),
                    DETECT_BOM_TOOL_SEARCH_EXCLUSION
                            .info("Detect Bom Tool Search Exclusion", "3.2.0")
                            .help("A comma-separated list of directory names to exclude from the bom tool search.")
                            .groups(Group.Paths, Group.Detector),
                    DETECT_INCLUDED_BOM_TOOL_TYPES
                            .info("Detect Included Bom Tool Types", "3.0.0")
                            .help("By default, all tools will be included. If you want to include only specific tools, specify the ones to include here. Exclusion rules always win.")
                            .groups(Group.Detector, Group.Detector),
                    DETECT_PROJECT_BOM_TOOL
                            .info("Detect Project Bom Tool", "4.0.0")
                            .help("The detector to choose when multiple detector types are found and one needs to be chosen for project name and version. This property should be used with the detect.project.tool.")
                            .groups(Group.Paths, Group.Detector),
                    DETECT_BOM_TOOL_SEARCH_DEPTH
                            .info("Detect Bom Tool Search Depth", "3.2.0")
                            .help("Depth of subdirectories within the source directory to search for files that indicate whether a detector applies.", "A value of 0 (the default) tells Detect not to search any subdirectories, a value of 1 tells Detect to search first-level subdirectories, etc.")
                            .groups(Group.Paths, Group.Detector),
                    DETECT_REQUIRED_BOM_TOOL_TYPES
                            .info("Detect Required Bom Tool Types", "4.3.0")
                            .help("If set, Detect will fail if it does not find the bom tool types supplied here.")
                            .groups(Group.Detector, Group.Detector),
                    DETECT_BOM_TOOL_SEARCH_CONTINUE
                            .info("Detect Bom Tool Search Continue", "3.2.0")
                            .help("If true, the bom tool search will continue to look for nested bom tools of the same type to the maximum search depth, see the detailed help for more information.", "If true, Detect will find Maven projects that are in subdirectories of a Maven project and Gradle projects that are in subdirectories of Gradle projects, etc. If false, Detect will only find bom tools in subdirectories of a project if they are of a different type such as an Npm project in a subdirectory of a Gradle project.")
                            .groups(Group.Paths, Group.Detector),
                    DETECT_GRADLE_INSPECTOR_REPOSITORY_URL
                            .info("Detect Gradle Inspector Repository Url", "3.0.0")
                            .help("The respository gradle should use to look for the gradle inspector dependencies.")
                            .groups(Group.Gradle),
                    DETECT_NUGET_INSPECTOR_NAME
                            .info("Detect Nuget Inspector Name", "3.0.0")
                            .help("Name of the Nuget Inspector package and the Nuget Inspector exe. (Do not include '.exe'.)", "The nuget inspector (previously) could be hosted on a custom nuget feed. In this case, Detect needed to know the name of the package to pull and the name of the exe file (which has to match). In the future, Detect will only retreive it from Artifactory or from Air Gap so a custom name is no longer supported.")
                            .groups(Group.Nuget),
                    DETECT_NUGET_PATH
                            .info("Detect Nuget Path", "3.0.0")
                            .help("The path to the Nuget executable. Nuget is used to download the classic inspectors nuget package.")
                            .groups(Group.Nuget),
                    DETECT_HUB_SIGNATURE_SCANNER_DRY_RUN
                            .info("Detect Hub Signature Scanner Dry Run", "3.0.0")
                            .help("If set to true, the signature scanner results will not be uploaded to the Hub and the scanner results will be written to disk.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_SNIPPET_MODE
                            .info("Detect Hub Signature Scanner Snippet Mode", "3.0.0")
                            .help("If set to true, the signature scanner will, if supported by your Hub version, run in snippet scanning mode.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_PATTERNS
                            .info("Detect Hub Signature Scanner Exclusion Patterns", "3.0.0")
                            .help("A comma-separated list of values to be used with the Signature Scanner --exclude flag.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_PATHS
                            .info("Detect Hub Signature Scanner Paths", "3.0.0")
                            .help("These paths and only these paths will be scanned.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS
                            .info("Detect Hub Signature Scanner Exclusion Name Patterns", "4.0.0")
                            .help("A comma-separated list of directory name patterns Detect will search for and add to the Signature Scanner --exclude flag values.", "Detect will recursively search within the scan targets for files/directories that match these file name patterns and will create the corresponding exclusion patterns for the signature scanner. These patterns will be added to the patterns provided by detect.blackduck.signature.scanner.exclusion.patterns.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_MEMORY
                            .info("Detect Hub Signature Scanner Memory", "3.0.0")
                            .help("The memory for the scanner to use.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_DISABLED
                            .info("Detect Hub Signature Scanner Disabled", "3.0.0")
                            .help("Set to true to disable the Hub Signature Scanner.")
                            .groups(Group.SignatureScanner),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_DISABLED
                            .info("Detect Blackduck Signature Scanner Disabled", "4.2.0")
                            .help("Set to true to disable the Black Duck Signature Scanner.")
                            .groups(Group.SignatureScanner, Group.Blackduck),
                    DETECT_HUB_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH
                            .info("Detect Hub Signature Scanner Offline Local Path", "3.0.0")
                            .help("To use a local signature scanner and force offline, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
                            .groups(Group.SignatureScanner, Group.Offline),
                    DETECT_HUB_SIGNATURE_SCANNER_LOCAL_PATH
                            .info("Detect Hub Signature Scanner Local Path", "4.2.0")
                            .help("To use a local signature scanner, specify the path where the signature scanner was unzipped. This will likely look similar to 'scan.cli-x.y.z' and includes the 'bin, icon, jre, and lib' directories of the expanded scan.cli.")
                            .groups(Group.SignatureScanner, Group.Offline),
                    DETECT_HUB_SIGNATURE_SCANNER_HOST_URL
                            .info("Detect Hub Signature Scanner Host Url", "3.0.0")
                            .help("If this url is set, an attempt will be made to use it to download the signature scanner. The server url provided must respect the Hub's urls for different operating systems.")
                            .groups(Group.SignatureScanner),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS
                            .info("Signature Scanner Parallel Processors", "4.2.0")
                            .help("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
                            .groups(Group.SignatureScanner, Group.Global)
                            .category(Category.Advanced),
                    DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS
                            .info("Detect Hub Signature Scanner Parallel Processors", "3.0.0")
                            .help("The number of scans to run in parallel, defaults to 1, but if you specify -1, the number of processors on the machine will be used.")
                            .groups(Group.SignatureScanner),
                    DETECT_HUB_SIGNATURE_SCANNER_ARGUMENTS
                            .info("Detect Hub Signature Scanner Arguments", "4.0.0")
                            .help("Additional arguments to use when running the Hub signature scanner.")
                            .groups(Group.SignatureScanner),
                    DETECT_SWIP_ENABLED
                            .info("Detect Polaris Enabled", "4.4.0")
                            .help("Set to false to disable the Synopsys Polaris Tool.")
                            .groups(Group.Polaris),
                    LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION
                            .info("Logging Level", "3.0.0")
                            .help("The logging level of Detect.")
                            .groups(Group.Logging, Group.Global),
                    DETECT_MAVEN_SCOPE
                            .info("Dependency Scope Included", "3.0.0")
                            .help("The name of a Maven scope. Output will be limited to dependencies with this scope.", "If set, Detect will include only dependencies of the given Maven scope.")
                            .groups(Group.Maven, Group.SourceScan),
                    DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE
                            .info("Snippet Scanning", "4.2.0")
                            .help("If set to true, the signature scanner will, if supported by your Black Duck version, run in snippet scanning mode.")
                            .groups(Group.SignatureScanner, Group.Global, Group.SourceScan),
                    POLARIS_URL
                            .info("Polaris Url", "4.1.0")
                            .help("The url of your polaris instance.")
                            .groups(Group.Polaris, Group.Default, Group.Global),
                    POLARIS_ACCESS_TOKEN
                            .info("Polaris Access Token", "5.3.0")
                            .help("The access token for your polaris instance.")
                            .groups(Group.Polaris, Group.Default, Group.Global),
                    POLARIS_ARGUMENTS
                            .info("Polaris Arguments", "5.3.0")
                            .help("Additional arguments to pass to polaris separated by space. The polaris.command takes precedence.")
                            .groups(Group.Polaris, Group.Default, Group.SourceScan),
                    POLARIS_COMMAND
                            .info("Polaris Command", "6.0.0")
                            .help("A replacement command to pass to polaris separated by space. Include the analyze or setup command itself. If specified, polaris.arguments will be ignored and this will take precedence.")
                            .groups(Group.Polaris, Group.Default, Group.SourceScan)
            )
        }
    }
}