package com.synopsys.integration.detect.configuration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.WorkspaceRule;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDependencyType;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.clang.ClangDetectableOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.config.ConanCliOptions;
import com.synopsys.integration.detectable.detectables.conan.cli.config.ConanDependencyType;
import com.synopsys.integration.detectable.detectables.conan.lockfile.ConanLockfileExtractorOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDependencyType;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepsDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModDependencyType;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleConfigurationType;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaOptions;
import com.synopsys.integration.detectable.detectables.lerna.LernaPackageType;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.PackagistDependencyType;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearDependencyType;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.build.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipenvDependencyType;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockOptions;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.detectables.rubygems.GemspecDependencyType;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtDetectableOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnDependencyType;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class DetectableOptionFactory {

    private final DetectPropertyConfiguration detectConfiguration;
    @Nullable
    private final DiagnosticSystem diagnosticSystem;
    private final ProxyInfo proxyInfo;

    public DetectableOptionFactory(DetectPropertyConfiguration detectConfiguration, @Nullable DiagnosticSystem diagnosticSystem, ProxyInfo proxyInfo) {
        this.detectConfiguration = detectConfiguration;
        this.diagnosticSystem = diagnosticSystem;
        this.proxyInfo = proxyInfo;
    }

    public BazelDetectableOptions createBazelDetectableOptions() {
        String targetName = detectConfiguration.getNullableValue(DetectProperties.DETECT_BAZEL_TARGET);
        List<String> bazelCqueryAdditionalOptions = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_CQUERY_OPTIONS);
        Set<WorkspaceRule> workspaceRulesFromProperty = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_WORKSPACE_RULES).representedValueSet();
        return new BazelDetectableOptions(targetName, workspaceRulesFromProperty, bazelCqueryAdditionalOptions);
    }

    public BitbakeDetectableOptions createBitbakeDetectableOptions() {
        String buildEnvName = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_BUILD_ENV_NAME);
        List<String> sourceArguments = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SOURCE_ARGUMENTS);
        List<String> packageNames = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_PACKAGE_NAMES);
        Integer searchDepth = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_SEARCH_DEPTH);
        Set<BitbakeDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_BITBAKE_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<BitbakeDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new BitbakeDetectableOptions(buildEnvName, sourceArguments, packageNames, searchDepth, getFollowSymLinks(), dependencyTypeFilter);
    }

    public ClangDetectableOptions createClangDetectableOptions() {
        Boolean cleanup = detectConfiguration.getValue(DetectProperties.DETECT_CLEANUP);
        return new ClangDetectableOptions(cleanup);
    }

    public ComposerLockDetectableOptions createComposerLockDetectableOptions() {
        Set<PackagistDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PACKAGIST_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<PackagistDependencyType> packagistDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new ComposerLockDetectableOptions(packagistDependencyTypeFilter);
    }

    public CondaCliDetectableOptions createCondaOptions() {
        String environmentName = detectConfiguration.getNullableValue(DetectProperties.DETECT_CONDA_ENVIRONMENT_NAME);
        return new CondaCliDetectableOptions(environmentName);
    }

    public DartPubDepsDetectableOptions createDartPubDepsDetectableOptions() {
        Set<DartPubDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PUB_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<DartPubDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new DartPubDepsDetectableOptions(dependencyTypeFilter);
    }

    public DockerDetectableOptions createDockerDetectableOptions() {
        String suppliedDockerImage = detectConfiguration.getNullableValue(DetectProperties.DETECT_DOCKER_IMAGE);
        String dockerImageId = detectConfiguration.getNullableValue(DetectProperties.DETECT_DOCKER_IMAGE_ID);
        String suppliedDockerTar = detectConfiguration.getNullableValue(DetectProperties.DETECT_DOCKER_TAR);
        LogLevel dockerInspectorLoggingLevel;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.LOGGING_LEVEL_DETECT)) {
            dockerInspectorLoggingLevel = detectConfiguration.getValue(DetectProperties.LOGGING_LEVEL_DETECT);
        } else {
            dockerInspectorLoggingLevel = detectConfiguration.getValue(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION);
        }
        String dockerInspectorVersion = detectConfiguration.getNullableValue(DetectProperties.DETECT_DOCKER_INSPECTOR_VERSION);
        Map<String, String> additionalDockerProperties = detectConfiguration.getRaw(DetectProperties.DOCKER_PASSTHROUGH);
        if (diagnosticSystem != null) {
            additionalDockerProperties.putAll(diagnosticSystem.getAdditionalDockerProperties());
        }

        Path dockerInspectorPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_DOCKER_INSPECTOR_PATH);
        String dockerPlatformTopLayerId = detectConfiguration.getNullableValue(DetectProperties.DETECT_DOCKER_PLATFORM_TOP_LAYER_ID);
        return new DockerDetectableOptions(
            suppliedDockerImage,
            dockerImageId,
            suppliedDockerTar,
            dockerInspectorLoggingLevel,
            dockerInspectorVersion,
            additionalDockerProperties,
            dockerInspectorPath,
            dockerPlatformTopLayerId
        );
    }

    public GoModCliDetectableOptions createGoModCliDetectableOptions() {
        GoModDependencyType excludedDependencyType = detectConfiguration.getValue(DetectProperties.DETECT_GO_MOD_DEPENDENCY_TYPES_EXCLUDED);
        return new GoModCliDetectableOptions(excludedDependencyType);
    }

    public GradleInspectorOptions createGradleInspectorOptions() {
        List<String> excludedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECTS);
        List<String> includedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECTS);
        List<String> excludedProjectPaths = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECT_PATHS);
        List<String> includedProjectPaths = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECT_PATHS);
        List<String> excludedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS);
        List<String> includedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_CONFIGURATIONS);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;

        Set<GradleConfigurationType> excludedConfigurationTypes = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_CONFIGURATION_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<GradleConfigurationType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedConfigurationTypes);

        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(
            excludedProjectNames,
            includedProjectNames,
            excludedProjectPaths,
            includedProjectPaths,
            excludedConfigurationNames,
            includedConfigurationNames,
            customRepository
        );
        String gradleBuildCommand = detectConfiguration.getNullableValue(DetectProperties.DETECT_GRADLE_BUILD_COMMAND);
        return new GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo, dependencyTypeFilter);
    }

    public LernaOptions createLernaOptions() {
        Set<LernaPackageType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_LERNA_PACKAGE_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<LernaPackageType> lernaPackageTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);

        List<String> excludedPackages = detectConfiguration.getValue(DetectProperties.DETECT_LERNA_EXCLUDED_PACKAGES);
        List<String> includedPackages = detectConfiguration.getValue(DetectProperties.DETECT_LERNA_INCLUDED_PACKAGES);
        return new LernaOptions(lernaPackageTypeFilter, excludedPackages, includedPackages);
    }

    public MavenCliExtractorOptions createMavenCliOptions() {
        String mavenBuildCommand = detectConfiguration.getNullableValue(DetectProperties.DETECT_MAVEN_BUILD_COMMAND);
        List<String> mavenExcludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_SCOPES);
        List<String> mavenIncludedScopes = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_SCOPES);
        List<String> mavenExcludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_EXCLUDED_MODULES);
        List<String> mavenIncludedModules = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDED_MODULES);
        return new MavenCliExtractorOptions(mavenBuildCommand, mavenExcludedScopes, mavenIncludedScopes, mavenExcludedModules, mavenIncludedModules);
    }

    public ConanCliOptions createConanCliOptions() {
        Path lockfilePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_CONAN_LOCKFILE_PATH);
        String additionalArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_CONAN_ARGUMENTS);
        Boolean preferLongFormExternalIds = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_REQUIRE_PREV_MATCH);
        Set<ConanDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new ConanCliOptions(lockfilePath, additionalArguments, dependencyTypeFilter, preferLongFormExternalIds);
    }

    public ConanLockfileExtractorOptions createConanLockfileOptions() {
        Path lockfilePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_CONAN_LOCKFILE_PATH);
        Boolean preferLongFormExternalIds = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_REQUIRE_PREV_MATCH);
        Set<ConanDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new ConanLockfileExtractorOptions(lockfilePath, dependencyTypeFilter, preferLongFormExternalIds);
    }

    public NpmCliExtractorOptions createNpmCliExtractorOptions() {
        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = createNpmDependencyTypeFilter();
        String npmArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_NPM_ARGUMENTS);
        return new NpmCliExtractorOptions(npmDependencyTypeFilter, npmArguments);
    }

    public NpmLockfileOptions createNpmLockfileOptions() {
        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = createNpmDependencyTypeFilter();
        return new NpmLockfileOptions(npmDependencyTypeFilter);
    }

    public NpmPackageJsonParseDetectableOptions createNpmPackageJsonParseDetectableOptions() {
        EnumListFilter<NpmDependencyType> npmDependencyTypeFilter = createNpmDependencyTypeFilter();
        return new NpmPackageJsonParseDetectableOptions(npmDependencyTypeFilter);
    }

    private EnumListFilter<NpmDependencyType> createNpmDependencyTypeFilter() {
        Set<NpmDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_NPM_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        return EnumListFilter.fromExcluded(excludedDependencyTypes);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() {
        EnumListFilter<PearDependencyType> pearDependencyTypeFilter;
        Set<PearDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PEAR_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        pearDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new PearCliDetectableOptions(pearDependencyTypeFilter);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() {
        String pipProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        String pipProjectVersionName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME);
        Boolean pipProjectTreeOnly = detectConfiguration.getValue(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE);
        return new PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly);
    }

    public PipfileLockDetectableOptions createPipfileLockDetectableOptions() {
        Set<PipenvDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PIPFILE_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<PipenvDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new PipfileLockDetectableOptions(dependencyTypeFilter);
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions() {
        String pipProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        List<Path> requirementsFilePath = detectConfiguration.getPaths(DetectProperties.DETECT_PIP_REQUIREMENTS_PATH);
        return new PipInspectorDetectableOptions(pipProjectName, requirementsFilePath);
    }

    public PnpmLockOptions createPnpmLockOptions() {
        Set<PnpmDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PNPM_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<PnpmDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new PnpmLockOptions(dependencyTypeFilter);
    }

    public ProjectInspectorOptions createProjectInspectorOptions() {
        String globalArguments = detectConfiguration.getNullableValue(DetectProperties.PROJECT_INSPECTOR_GLOBAL_ARGUMENTS);
        String additionalArguments = detectConfiguration.getNullableValue(DetectProperties.PROJECT_INSPECTOR_ARGUMENTS);
        Path projectInspectorZipPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_PROJECT_INSPECTOR_PATH);
        return new ProjectInspectorOptions(projectInspectorZipPath, additionalArguments, globalArguments);
    }

    public GemspecParseDetectableOptions createGemspecParseDetectableOptions() {
        Set<GemspecDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<GemspecDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new GemspecParseDetectableOptions(dependencyTypeFilter);
    }

    public SbtDetectableOptions createSbtDetectableOptions() {
        String sbtCommandAdditionalArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_SBT_ARGUMENTS);
        return new SbtDetectableOptions(sbtCommandAdditionalArguments);
    }

    public YarnLockOptions createYarnLockOptions() {
        Set<YarnDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_YARN_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);

        List<String> excludedWorkspaces = detectConfiguration.getValue(DetectProperties.DETECT_YARN_EXCLUDED_WORKSPACES);
        List<String> includedWorkspaces = detectConfiguration.getValue(DetectProperties.DETECT_YARN_INCLUDED_WORKSPACES);
        return new YarnLockOptions(yarnDependencyTypeFilter, excludedWorkspaces, includedWorkspaces);
    }

    public NugetInspectorOptions createNugetInspectorOptions() {
        Boolean ignoreFailures = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_IGNORE_FAILURE);
        List<String> excludedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_EXCLUDED_MODULES);
        List<String> includedModules = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_INCLUDED_MODULES);
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL);
        Path nugetConfigPath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_NUGET_CONFIG_PATH);
        return new NugetInspectorOptions(ignoreFailures, excludedModules, includedModules, packagesRepoUrl, nugetConfigPath);
    }

    private boolean getFollowSymLinks() {
        return detectConfiguration.getValue(DetectProperties.DETECT_FOLLOW_SYMLINKS);
    }
}
