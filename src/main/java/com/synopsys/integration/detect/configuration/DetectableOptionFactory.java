package com.synopsys.integration.detect.configuration;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
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
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.detectables.packagist.PackagistDependencyType;
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.PearDependencyType;
import com.synopsys.integration.detectable.detectables.pip.inspector.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pipenv.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.PnpmLockOptions;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyTypeV2;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.detectables.rubygems.GemspecDependencyType;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.parse.SbtResolutionCacheOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnDependencyType;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class DetectableOptionFactory {

    private final DetectPropertyConfiguration detectConfiguration;
    @Nullable
    private final DiagnosticSystem diagnosticSystem;
    private final ProxyInfo proxyInfo;

    public DetectableOptionFactory(DetectPropertyConfiguration detectConfiguration, @Nullable DiagnosticSystem diagnosticSystem, PathResolver pathResolver, ProxyInfo proxyInfo) {
        this.detectConfiguration = detectConfiguration;
        this.diagnosticSystem = diagnosticSystem;
        this.proxyInfo = proxyInfo;
    }

    public BazelDetectableOptions createBazelDetectableOptions() {
        String targetName = detectConfiguration.getNullableValue(DetectProperties.DETECT_BAZEL_TARGET);
        List<String> bazelCqueryAdditionalOptions = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_CQUERY_OPTIONS);
        Set<WorkspaceRule> workspaceRulesFromDeprecatedProperty = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_DEPENDENCY_RULE).representedValueSet();
        Set<WorkspaceRule> workspaceRulesFromProperty = detectConfiguration.getValue(DetectProperties.DETECT_BAZEL_WORKSPACE_RULES).representedValueSet();
        return new BazelDetectableOptions(targetName, workspaceRulesFromDeprecatedProperty, workspaceRulesFromProperty, bazelCqueryAdditionalOptions);
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

        EnumListFilter<PackagistDependencyType> packagistDependencyTypeFilter;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PACKAGIST_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<PackagistDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PACKAGIST_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            packagistDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean includedDevDependencies = Boolean.TRUE.equals(detectConfiguration.getValue(DetectProperties.DETECT_PACKAGIST_INCLUDE_DEV_DEPENDENCIES));
            if (includedDevDependencies) {
                packagistDependencyTypeFilter = EnumListFilter.excludeNone();
            } else {
                packagistDependencyTypeFilter = EnumListFilter.fromExcluded(PackagistDependencyType.DEV);
            }
        }

        return new ComposerLockDetectableOptions(packagistDependencyTypeFilter);
    }

    public CondaCliDetectableOptions createCondaOptions() {
        String environmentName = detectConfiguration.getNullableValue(DetectProperties.DETECT_CONDA_ENVIRONMENT_NAME);
        return new CondaCliDetectableOptions(environmentName);
    }

    public DartPubDepsDetectableOptions createDartPubDepsDetectableOptions() {
        EnumListFilter<DartPubDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PUB_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<DartPubDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PUB_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean excludeDevDependencies = detectConfiguration.getValue(DetectProperties.DETECT_PUD_DEPS_EXCLUDE_DEV);
            if (excludeDevDependencies) {
                dependencyTypeFilter = EnumListFilter.fromExcluded(DartPubDependencyType.DEV);
            }
        }
        return new DartPubDepsDetectableOptions(dependencyTypeFilter);
    }

    public MavenParseOptions createMavenParseOptions() {
        Boolean includePlugins = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_INCLUDE_PLUGINS);
        Boolean legacyMode = detectConfiguration.getValue(DetectProperties.DETECT_MAVEN_BUILDLESS_LEGACY_MODE);
        return new MavenParseOptions(includePlugins, legacyMode);
    }

    public DockerDetectableOptions createDockerDetectableOptions() {
        Boolean dockerPathRequired = detectConfiguration.getValue(DetectProperties.DETECT_DOCKER_PATH_REQUIRED);
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
            dockerPathRequired,
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
        EnumListFilter<GoModDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_GO_MOD_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<GoModDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_GO_MOD_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean dependencyVerificationEnabled = detectConfiguration.getValue(DetectProperties.DETECT_GO_ENABLE_VERIFICATION);
            if (dependencyVerificationEnabled) {
                dependencyTypeFilter = EnumListFilter.fromExcluded(GoModDependencyType.UNUSED);
            }
        }

        return new GoModCliDetectableOptions(dependencyTypeFilter);
    }

    public GradleInspectorOptions createGradleInspectorOptions() {
        List<String> excludedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_PROJECTS);
        List<String> includedProjectNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_PROJECTS);
        List<String> excludedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS);
        List<String> includedConfigurationNames = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDED_CONFIGURATIONS);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;

        EnumListFilter<GradleConfigurationType> dependencyTypeFilter;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_GRADLE_CONFIGURATION_TYPES_EXCLUDED)) {
            Set<GradleConfigurationType> excludedConfigurationTypes = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_CONFIGURATION_TYPES_EXCLUDED).representedValueSet();
            dependencyTypeFilter = EnumListFilter.fromExcluded(excludedConfigurationTypes);
        } else {
            boolean includeUnresolvedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_GRADLE_INCLUDE_UNRESOLVED_CONFIGURATIONS);
            if (includeUnresolvedConfigurations) {
                dependencyTypeFilter = EnumListFilter.excludeNone();
            } else {
                dependencyTypeFilter = EnumListFilter.fromExcluded(GradleConfigurationType.UNRESOLVED);
            }
        }

        String onlineInspectorVersion = detectConfiguration.getNullableValue(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION);
        GradleInspectorScriptOptions scriptOptions = new GradleInspectorScriptOptions(excludedProjectNames, includedProjectNames, excludedConfigurationNames, includedConfigurationNames, customRepository, onlineInspectorVersion);
        String gradleBuildCommand = detectConfiguration.getNullableValue(DetectProperties.DETECT_GRADLE_BUILD_COMMAND);
        return new GradleInspectorOptions(gradleBuildCommand, scriptOptions, proxyInfo, dependencyTypeFilter);
    }

    public LernaOptions createLernaOptions() {
        EnumListFilter<LernaPackageType> lernaPackageTypeFilter;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_LERNA_PACKAGE_TYPES_EXCLUDED)) {
            Set<LernaPackageType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_LERNA_PACKAGE_TYPES_EXCLUDED).representedValueSet();
            lernaPackageTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean includePrivate = Boolean.TRUE.equals(detectConfiguration.getValue(DetectProperties.DETECT_LERNA_INCLUDE_PRIVATE));
            if (includePrivate) {
                lernaPackageTypeFilter = EnumListFilter.excludeNone();
            } else {
                lernaPackageTypeFilter = EnumListFilter.fromExcluded(LernaPackageType.PRIVATE);
            }
        }

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
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = createConanDependencyTypeFilter();
        return new ConanCliOptions(lockfilePath, additionalArguments, dependencyTypeFilter, preferLongFormExternalIds);
    }

    // TODO: Remove in 8.0.0. This will be one line, no method necessary - JM 01/2022
    private EnumListFilter<ConanDependencyType> createConanDependencyTypeFilter() {
        Boolean includeBuildDependencies = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_INCLUDE_BUILD_DEPENDENCIES);
        Set<ConanDependencyType> excludedDependencyTypes;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_CONAN_DEPENDENCY_TYPES_EXCLUDED)) {
            excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        } else {
            excludedDependencyTypes = new LinkedHashSet<>();
            if (Boolean.FALSE.equals(includeBuildDependencies)) {
                excludedDependencyTypes.add(ConanDependencyType.BUILD);
            }
        }
        return EnumListFilter.fromExcluded(excludedDependencyTypes);
    }

    public ConanLockfileExtractorOptions createConanLockfileOptions() {
        Path lockfilePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_CONAN_LOCKFILE_PATH);
        Boolean preferLongFormExternalIds = detectConfiguration.getValue(DetectProperties.DETECT_CONAN_REQUIRE_PREV_MATCH);
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = createConanDependencyTypeFilter();
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
        Set<NpmDependencyType> excludedDependencyTypes;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_NPM_DEPENDENCY_TYPES_EXCLUDED)) {
            excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_NPM_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        } else {
            boolean excludeDevDependencies = Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES));
            boolean excludePeerDependencies = Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_NPM_INCLUDE_PEER_DEPENDENCIES));
            excludedDependencyTypes = new LinkedHashSet<>();
            if (excludeDevDependencies) {
                excludedDependencyTypes.add(NpmDependencyType.DEV);
            }
            if (excludePeerDependencies) {
                excludedDependencyTypes.add(NpmDependencyType.PEER);
            }
        }
        return EnumListFilter.fromExcluded(excludedDependencyTypes);
    }

    public PearCliDetectableOptions createPearCliDetectableOptions() {
        EnumListFilter<PearDependencyType> pearDependencyTypeFilter;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PEAR_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<PearDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PEAR_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            pearDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean onlyGatherRequired = Boolean.TRUE.equals(detectConfiguration.getValue(DetectProperties.DETECT_PEAR_ONLY_REQUIRED_DEPS));
            if (onlyGatherRequired) {
                pearDependencyTypeFilter = EnumListFilter.fromExcluded(PearDependencyType.OPTIONAL);
            } else {
                pearDependencyTypeFilter = EnumListFilter.excludeNone();
            }
        }
        return new PearCliDetectableOptions(pearDependencyTypeFilter);
    }

    public PipenvDetectableOptions createPipenvDetectableOptions() {
        String pipProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        String pipProjectVersionName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_VERSION_NAME);
        Boolean pipProjectTreeOnly = detectConfiguration.getValue(DetectProperties.DETECT_PIP_ONLY_PROJECT_TREE);
        return new PipenvDetectableOptions(pipProjectName, pipProjectVersionName, pipProjectTreeOnly);
    }

    public PipInspectorDetectableOptions createPipInspectorDetectableOptions() {
        String pipProjectName = detectConfiguration.getNullableValue(DetectProperties.DETECT_PIP_PROJECT_NAME);
        List<Path> requirementsFilePath = detectConfiguration.getPaths(DetectProperties.DETECT_PIP_REQUIREMENTS_PATH);
        return new PipInspectorDetectableOptions(pipProjectName, requirementsFilePath);
    }

    public PnpmLockOptions createPnpmLockOptions() {
        Set<PnpmDependencyType> excludedDependencyTypes = new LinkedHashSet<>(); // Converting types so the existing property doesn't lose functionality.
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_PNPM_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<PnpmDependencyTypeV2> pnpmDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PNPM_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            if (pnpmDependencyTypes.contains(PnpmDependencyTypeV2.DEV)) {
                excludedDependencyTypes.add(PnpmDependencyType.DEV);
            }
            if (pnpmDependencyTypes.contains(PnpmDependencyTypeV2.OPTIONAL)) {
                excludedDependencyTypes.add(PnpmDependencyType.OPTIONAL);
            }
        } else {
            Set<PnpmDependencyType> pnpmDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_PNPM_DEPENDENCY_TYPES).representedValueSet();
            if (!pnpmDependencyTypes.contains(PnpmDependencyType.APP)) {
                excludedDependencyTypes.add(PnpmDependencyType.APP);
            }
            if (!pnpmDependencyTypes.contains(PnpmDependencyType.DEV)) {
                excludedDependencyTypes.add(PnpmDependencyType.DEV);
            }
            if (!pnpmDependencyTypes.contains(PnpmDependencyType.OPTIONAL)) {
                excludedDependencyTypes.add(PnpmDependencyType.OPTIONAL);
            }
        }
        EnumListFilter<PnpmDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);

        return new PnpmLockOptions(dependencyTypeFilter);
    }

    public ProjectInspectorOptions createProjectInspectorOptions() {
        String additionalArguments = detectConfiguration.getNullableValue(DetectProperties.PROJECT_INSPECTOR_ARGUMENTS);
        return new ProjectInspectorOptions(additionalArguments);
    }

    public GemspecParseDetectableOptions createGemspecParseDetectableOptions() {
        Set<GemspecDependencyType> excludedDependencyTypes;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_RUBY_DEPENDENCY_TYPES_EXCLUDED)) {
            excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_RUBY_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
        } else {
            boolean excludeRuntimeDependencies = Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_RUNTIME_DEPENDENCIES));
            boolean excludeDevDependencies = Boolean.FALSE.equals(detectConfiguration.getValue(DetectProperties.DETECT_RUBY_INCLUDE_DEV_DEPENDENCIES));
            excludedDependencyTypes = new LinkedHashSet<>();
            if (excludeDevDependencies) {
                excludedDependencyTypes.add(GemspecDependencyType.DEV);
            }
            if (excludeRuntimeDependencies) {
                excludedDependencyTypes.add(GemspecDependencyType.RUNTIME);
            }
        }
        EnumListFilter<GemspecDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        return new GemspecParseDetectableOptions(dependencyTypeFilter);
    }

    public SbtResolutionCacheOptions createSbtResolutionCacheDetectableOptions() {
        String sbtCommandAdditionalArguments = detectConfiguration.getNullableValue(DetectProperties.DETECT_SBT_ARGUMENTS);
        List<String> includedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_INCLUDED_CONFIGURATIONS);
        List<String> excludedConfigurations = detectConfiguration.getValue(DetectProperties.DETECT_SBT_EXCLUDED_CONFIGURATIONS);
        Integer reportDepth = detectConfiguration.getValue(DetectProperties.DETECT_SBT_REPORT_DEPTH);
        return new SbtResolutionCacheOptions(sbtCommandAdditionalArguments, includedConfigurations, excludedConfigurations, reportDepth, getFollowSymLinks());
    }

    public YarnLockOptions createYarnLockOptions() {
        EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter;
        if (detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_YARN_DEPENDENCY_TYPES_EXCLUDED)) {
            Set<YarnDependencyType> excludedDependencyTypes = detectConfiguration.getValue(DetectProperties.DETECT_YARN_DEPENDENCY_TYPES_EXCLUDED).representedValueSet();
            yarnDependencyTypeFilter = EnumListFilter.fromExcluded(excludedDependencyTypes);
        } else {
            boolean useProductionOnly = Boolean.TRUE.equals(detectConfiguration.getValue(DetectProperties.DETECT_YARN_PROD_ONLY));
            if (useProductionOnly) {
                yarnDependencyTypeFilter = EnumListFilter.fromExcluded(YarnDependencyType.NON_PRODUCTION);
            } else {
                yarnDependencyTypeFilter = EnumListFilter.excludeNone();
            }
        }
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

    public NugetLocatorOptions createNugetInstallerOptions() {
        List<String> packagesRepoUrl = detectConfiguration.getValue(DetectProperties.DETECT_NUGET_PACKAGES_REPO_URL);
        String nugetInspectorVersion = detectConfiguration.getNullableValue(DetectProperties.DETECT_NUGET_INSPECTOR_VERSION);
        return new NugetLocatorOptions(packagesRepoUrl, nugetInspectorVersion);
    }

    private boolean getFollowSymLinks() {
        return detectConfiguration.getValue(DetectProperties.DETECT_FOLLOW_SYMLINKS);
    }
}
