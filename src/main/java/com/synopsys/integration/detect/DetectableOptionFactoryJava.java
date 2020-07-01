package com.synopsys.integration.detect;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class DetectableOptionFactoryJava {

    private PropertyConfiguration detectConfiguration;
    @Nullable
    private DiagnosticSystem diagnosticSystem;
    private PathResolver pathResolver;
    private ProxyInfo proxyInfo;

    private Logger logger = LoggerFactory.getLogger(DetectableOptionFactory.class);

    public DetectableOptionFactoryJava(final PropertyConfiguration detectConfiguration, @Nullable final DiagnosticSystem diagnosticSystem, final PathResolver pathResolver, final ProxyInfo proxyInfo) {
        this.detectConfiguration = detectConfiguration;
        this.diagnosticSystem = diagnosticSystem;
        this.pathResolver = pathResolver;
        this.proxyInfo = proxyInfo;
    }
    
    public BazelDetectableOptions createBazelDetectableOptions() throws InvalidPropertyException {
        String targetName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BAZEL_TARGET()).orElse(null);
        List<String> bazelCqueryAdditionalOptions = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BAZEL_CQUERY_OPTIONS());

        WorkspaceRule bazelDependencyRule = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BAZEL_DEPENDENCY_RULE());
        return new BazelDetectableOptions(targetName, bazelDependencyRule, bazelCqueryAdditionalOptions);
    }

    public BitbakeDetectableOptions createBitbakeDetectableOptions() throws InvalidPropertyException {
        String buildEnvName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BITBAKE_BUILD_ENV_NAME());
        List<String> sourceArguments = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BITBAKE_SOURCE_ARGUMENTS());
        List<String> packageNames = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BITBAKE_PACKAGE_NAMES());
        Integer searchDepth = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_BITBAKE_SEARCH_DEPTH());
        return new BitbakeDetectableOptions(buildEnvName, sourceArguments, packageNames, searchDepth);
    }

    public ClangDetectableOptions createClangDetectableOptions() throws InvalidPropertyException {
        Boolean cleanup = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_CLEANUP());
        return new ClangDetectableOptions(cleanup);
    }

    public ComposerLockDetectableOptions createComposerLockDetectableOptions() throws InvalidPropertyException {
        Boolean includedDevDependencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES());
        return new ComposerLockDetectableOptions(includedDevDependencies);
    }

    public CondaCliDetectableOptions createCondaOptions() throws InvalidPropertyException {
        String environmentName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_CONDA_ENVIRONMENT_NAME()).orElse(null);
        return new CondaCliDetectableOptions(environmentName);
    }

    public MavenParseOptions createMavenParseOptions() throws InvalidPropertyException {
        Boolean includePlugins = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_INCLUDE_PLUGINS());
        return new MavenParseOptions(includePlugins);
    }

    public DockerDetectableOptions createDockerDetectableOptions() throws InvalidPropertyException {
        Boolean dockerPathRequired = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_PATH_REQUIRED());
        String suppliedDockerImage = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_IMAGE()).orElse(null);
        String dockerImageId = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_IMAGE_ID()).orElse(null);
        String suppliedDockerTar = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_TAR()).orElse(null);
        LogLevel dockerInspectorLoggingLevel = detectConfiguration.getValue(DetectProperties.Companion.getLOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION());
        String dockerInspectorVersion = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_INSPECTOR_VERSION()).orElse(null);
        Map<String, String> additionalDockerProperties = detectConfiguration.getRaw(DetectProperties.Companion.getDOCKER_PASSTHROUGH());
        if (diagnosticSystem != null) {
            additionalDockerProperties.putAll(diagnosticSystem.getAdditionalDockerProperties());
        }

        Path dockerInspectorPath = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_INSPECTOR_PATH()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        String dockerPlatformTopLayerId = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_DOCKER_PLATFORM_TOP_LAYER_ID()).orElse(null);
        return new DockerDetectableOptions(dockerPathRequired, suppliedDockerImage, dockerImageId, suppliedDockerTar, dockerInspectorLoggingLevel, dockerInspectorVersion, additionalDockerProperties, dockerInspectorPath, dockerPlatformTopLayerId);
    }

    public GradleInspectorOptions createGradleInspectorOptions() throws InvalidPropertyException {
        String excludedProjectNames = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_EXCLUDED_PROJECTS()).orElse(null);
        String includedProjectNames = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_INCLUDED_PROJECTS()).orElse(null);
        String excludedConfigurationNames = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_EXCLUDED_CONFIGURATIONS()).orElse(null);
        String includedConfigurationNames = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_INCLUDED_CONFIGURATIONS()).orElse(null);
        String configuredGradleInspectorRepositoryUrl = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_INSPECTOR_REPOSITORY_URL()).orElse(null);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;
        if (configuredGradleInspectorRepositoryUrl != null && StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            logger.warn("Using a custom gradle repository will not be supported in the future.");
            customRepository = configuredGradleInspectorRepositoryUrl;
        }

        String onlineInspectorVersion = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_INSPECTOR_VERSION()).orElse(null);
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, customRepository, onlineInspectorVersion);
        String gradleBuildCommand = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_GRADLE_BUILD_COMMAND()).orElse(null);
        return new GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo);
    }

    public LernaOptions createLernaOptions() throws InvalidPropertyException {
        Boolean includePrivate = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_LERNA_INCLUDE_PRIVATE());
        return new LernaOptions(includePrivate);
    }

    public MavenCliExtractorOptions createMavenCliOptions() throws InvalidPropertyException {
        String mavenBuildCommand = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_BUILD_COMMAND()).orElse(null);
        String mavenExcludedScopes = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_EXCLUDED_SCOPES()).orElse(null);
        String mavenIncludedScopes = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_INCLUDED_SCOPES()).orElse(null);
        String mavenExcludedModules = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_EXCLUDED_MODULES()).orElse(null);
        String mavenIncludedModules = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_MAVEN_INCLUDED_MODULES()).orElse(null);
        return new MavenCliExtractorOptions(mavenBuildCommand, mavenExcludedScopes, mavenIncludedScopes, mavenExcludedModules, mavenIncludedModules);
    }

    public NpmCliExtractorOptions createNpmCliExtractorOptions() throws InvalidPropertyException {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NPM_INCLUDE_DEV_DEPENDENCIES());
        String npmArguments = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NPM_ARGUMENTS()).orElse(null);
        return new NpmCliExtractorOptions(includeDevDependencies, npmArguments);
    }

    public NpmLockfileOptions createNpmLockfileOptions() throws InvalidPropertyException {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NPM_INCLUDE_DEV_DEPENDENCIES());
        return new NpmLockfileOptions(includeDevDependencies);
    }

    public NpmPackageJsonParseDetectableOptions createNpmPackageJsonParseDetectableOptions() throws InvalidPropertyException {
        Boolean includeDevDependencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NPM_INCLUDE_DEV_DEPENDENCIES());
        return new NpmPackageJsonParseDetectableOptions(includeDevDependencies);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() throws InvalidPropertyException {
        Boolean onlyGatherRequired = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PEAR_ONLY_REQUIRED_DEPS());
        return new PearCliDetectableOptions(onlyGatherRequired);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() throws InvalidPropertyException {
        String pipProjectName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PIP_PROJECT_NAME()).orElse(null);
        String pipProjectVersionName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PIP_PROJECT_VERSION_NAME()).orElse(null);
        Boolean pipProjectTreeOnly = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PIP_ONLY_PROJECT_TREE());
        return new PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly);
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions() throws InvalidPropertyException {
        String pipProjectName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PIP_PROJECT_NAME()).orElse(null);
        List<Path> requirementsFilePath = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PIP_REQUIREMENTS_PATH()).stream()
                                              .map(it -> it.resolvePath(pathResolver))
                                              .collect(Collectors.toList());
        return new PipInspectorDetectableOptions(pipProjectName, requirementsFilePath);
    }

    public GemspecParseDetectableOptions createGemspecParseDetectableOptions() throws InvalidPropertyException {
        Boolean includeRuntimeDependencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES());
        Boolean includeDevDeopendencies = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_RUBY_INCLUDE_DEV_DEPENDENCIES());
        return new GemspecParseDetectableOptions(includeRuntimeDependencies, includeDevDeopendencies);
    }

    public SbtResolutionCacheDetectableOptions createSbtResolutionCacheDetectableOptions() throws InvalidPropertyException {
        String includedConfigurations = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_SBT_INCLUDED_CONFIGURATIONS()).orElse(null);
        String excludedConfigurations = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_SBT_EXCLUDED_CONFIGURATIONS()).orElse(null);
        Integer reportDepth = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_SBT_REPORT_DEPTH());
        return new SbtResolutionCacheDetectableOptions(includedConfigurations, excludedConfigurations, reportDepth);
    }

    public YarnLockOptions createYarnLockOptions() throws InvalidPropertyException {
        Boolean useProductionOnly = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_YARN_PROD_ONLY());
        return new YarnLockOptions(useProductionOnly);
    }

    public NugetInspectorOptions createNugetInspectorOptions() throws InvalidPropertyException {
        Boolean ignoreFailures = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_IGNORE_FAILURE());
        String excludedModules = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_EXCLUDED_MODULES()).orElse(null);
        String includedModules = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_INCLUDED_MODULES()).orElse(null);
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_PACKAGES_REPO_URL());
        Path nugetConfigPath = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_CONFIG_PATH()).map(path -> path.resolvePath(pathResolver)).orElse(null);
        return new NugetInspectorOptions(ignoreFailures, excludedModules, includedModules, packagesRepoUrl, nugetConfigPath);
    }

    public NugetLocatorOptions createNugetInstallerOptions() throws InvalidPropertyException {
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_PACKAGES_REPO_URL());
        String nugetInspectorName = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_INSPECTOR_NAME());
        String nugetInspectorVersion = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_NUGET_INSPECTOR_VERSION()).orElse(null);
        return new NugetLocatorOptions(packagesRepoUrl, nugetInspectorName, nugetInspectorVersion);
    }

    public CachedExecutableResolverOptions createCachedExecutableResolverOptions() throws InvalidPropertyException {
        Boolean python3 = detectConfiguration.getValue(DetectProperties.Companion.getDETECT_PYTHON_PYTHON3());
        return new CachedExecutableResolverOptions(python3);
    }
}
