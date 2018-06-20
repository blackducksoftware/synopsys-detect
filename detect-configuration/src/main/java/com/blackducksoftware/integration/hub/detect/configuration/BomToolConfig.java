package com.blackducksoftware.integration.hub.detect.configuration;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BomToolConfig extends BaseConfig {
    private final Logger logger = LoggerFactory.getLogger(BomToolConfig.class);

    private String dockerInspectorAirGapPath;
    private String gradleInspectorAirGapPath;
    private String nugetInspectorAirGapPath;
    private List<String> bomToolSearchDirectoryExclusions;

    public void initialize(final ValueContainer valueContainer, final String dockerInspectorAirGapPath, final String gradleInspectorAirGapPath, final String nugetInspectorAirGapPath, final List<String> bomToolSearchDirectoryExclusions) {
        this.dockerInspectorAirGapPath = dockerInspectorAirGapPath;
        this.gradleInspectorAirGapPath = gradleInspectorAirGapPath;
        this.nugetInspectorAirGapPath = nugetInspectorAirGapPath;
        this.bomToolSearchDirectoryExclusions = bomToolSearchDirectoryExclusions;
    }

    public List<String> getBomToolSearchDirectoryExclusions() {
        return bomToolSearchDirectoryExclusions;
    }

    // properties start
    private String detectProjectBomTool;

    private Integer bomToolSearchDepth;

    private Boolean bomToolContinueSearch;

    private String[] bomToolSearchExclusion;

    private Boolean bomToolSearchExclusionDefaults;

    private String excludedBomToolTypes;

    private String includedBomToolTypes;

    private String gradleInspectorVersion;

    private String gradleBuildCommand;

    private String gradleExcludedConfigurationNames;

    private String gradleIncludedConfigurationNames;

    private String gradleExcludedProjectNames;

    private String gradleIncludedProjectNames;

    private String nugetConfigPath;

    private String nugetInspectorPackageName;

    private String nugetInspectorPackageVersion;

    private String nugetInspectorExcludedModules;

    private String nugetInspectorIncludedModules;

    private Boolean nugetInspectorIgnoreFailure;

    private String mavenScope;

    private String mavenBuildCommand;

    private String gradlePath;

    private String mavenPath;

    private String mavenExcludedModuleNames;

    private String mavenIncludedModuleNames;

    private String nugetPath;

    private String pipProjectName;

    private Boolean pythonThreeOverride;

    private String pythonPath;

    private String npmPath;

    private String npmIncludeDevDependencies;

    private String npmNodePath;

    private String pearPath;

    private Boolean pearOnlyRequiredDependencies;

    private String requirementsFilePath;

    private String goDepPath;

    private Boolean goRunDepInit;

    private String dockerPath;

    private Boolean dockerPathRequired;

    private String dockerInspectorPath;

    private String dockerInspectorVersion;

    private String dockerTar;

    private String dockerImage;

    private String bashPath;

    private Boolean hubSignatureScannerDryRun;

    private Boolean hubSignatureScannerSnippetMode;

    private String[] hubSignatureScannerExclusionPatterns;

    private String[] hubSignatureScannerPaths;

    private String[] hubSignatureScannerExclusionNamePatterns;

    private Integer hubSignatureScannerMemory;

    private Boolean hubSignatureScannerDisabled;

    private String hubSignatureScannerOfflineLocalPath;

    private String hubSignatureScannerHostUrl;

    private Integer hubSignatureScannerParallelProcessors;

    private String hubSignatureScannerArguments;

    private Boolean packagistIncludeDevDependencies;

    private String perlPath;

    private String cpanPath;

    private String cpanmPath;

    private String sbtExcludedConfigurationNames;

    private String sbtIncludedConfigurationNames;

    private String condaPath;

    private String condaEnvironmentName;

    private String[] nugetPackagesRepoUrl;

    private String gradleInspectorRepositoryUrl;

    private String hexRebar3Path;

    private String yarnPath;

    private String yarnProductionDependenciesOnly;

    public int getBomToolSearchDepth() {
        return convertInt(bomToolSearchDepth);
    }

    public String getDetectProjectBomTool() {
        return detectProjectBomTool;
    }

    public Boolean getBomToolContinueSearch() {
        return BooleanUtils.toBoolean(bomToolContinueSearch);
    }

    public String[] getBomToolSearchExclusion() {
        return bomToolSearchExclusion;
    }

    public Boolean getBomToolSearchExclusionDefaults() {
        return BooleanUtils.toBoolean(bomToolSearchExclusionDefaults);
    }

    public String getExcludedBomToolTypes() {
        return excludedBomToolTypes == null ? null : excludedBomToolTypes.toUpperCase();
    }

    public String getIncludedBomToolTypes() {
        return includedBomToolTypes == null ? null : includedBomToolTypes.toUpperCase();
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
        return nugetInspectorPackageName == null ? null : nugetInspectorPackageName.trim();
    }

    public String getNugetInspectorPackageVersion() {
        return nugetInspectorPackageVersion == null ? null : nugetInspectorPackageVersion.trim();
    }

    public String getNugetInspectorExcludedModules() {
        return nugetInspectorExcludedModules;
    }

    public String getNugetInspectorIncludedModules() {
        return nugetInspectorIncludedModules;
    }

    public boolean getNugetInspectorIgnoreFailure() {
        return BooleanUtils.toBoolean(nugetInspectorIgnoreFailure);
    }

    public String getMavenScope() {
        return mavenScope;
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

    public String getMavenBuildCommand() {
        return mavenBuildCommand;
    }

    public String getNugetPath() {
        return nugetPath;
    }

    public String getNpmPath() {
        return npmPath;
    }

    public boolean getNpmIncludeDevDependencies() {
        return BooleanUtils.toBoolean(npmIncludeDevDependencies);
    }

    public String getNpmNodePath() {
        return npmNodePath;
    }

    public String getPearPath() {
        return pearPath;
    }

    public boolean getPearOnlyRequiredDependencies() {
        return BooleanUtils.toBoolean(pearOnlyRequiredDependencies);
    }

    public String getPipProjectName() {
        return pipProjectName;
    }

    public boolean getPythonThreeOverride() {
        return BooleanUtils.toBoolean(pythonThreeOverride);
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public String getRequirementsFilePath() {
        return requirementsFilePath;
    }

    public String getGoDepPath() {
        return goDepPath;
    }

    public boolean getGoRunDepInit() {
        return BooleanUtils.toBoolean(goRunDepInit);
    }

    public String getDockerPath() {
        return dockerPath;
    }

    public boolean getDockerPathRequired() {
        return BooleanUtils.toBoolean(dockerPathRequired);
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

    public boolean getHubSignatureScannerDryRun() {
        return BooleanUtils.toBoolean(hubSignatureScannerDryRun);
    }

    public boolean getHubSignatureScannerSnippetMode() {
        return BooleanUtils.toBoolean(hubSignatureScannerSnippetMode);
    }

    public String[] getHubSignatureScannerPaths() {
        return hubSignatureScannerPaths;
    }

    public String[] getHubSignatureScannerExclusionPatterns() {
        return hubSignatureScannerExclusionPatterns;
    }

    public String[] getHubSignatureScannerExclusionNamePatterns() {
        return hubSignatureScannerExclusionNamePatterns;
    }

    public String getHubSignatureScannerOfflineLocalPath() {
        return hubSignatureScannerOfflineLocalPath;
    }

    public String getHubSignatureScannerHostUrl() {
        return hubSignatureScannerHostUrl;
    }

    public boolean getPackagistIncludeDevDependencies() {
        return BooleanUtils.toBoolean(packagistIncludeDevDependencies);
    }

    public int getHubSignatureScannerMemory() {
        return convertInt(hubSignatureScannerMemory);
    }

    public boolean getHubSignatureScannerDisabled() {
        return BooleanUtils.toBoolean(hubSignatureScannerDisabled);
    }

    public int getHubSignatureScannerParallelProcessors() {
        return convertInt(hubSignatureScannerParallelProcessors);
    }

    public String getHubSignatureScannerArguments() {
        return hubSignatureScannerArguments == null ? null : hubSignatureScannerArguments.trim();
    }

    public String getPerlPath() {
        return perlPath == null ? null : perlPath.trim();
    }

    public String getCpanPath() {
        return cpanPath == null ? null : cpanPath.trim();
    }

    public String getCpanmPath() {
        return cpanmPath == null ? null : cpanmPath.trim();
    }

    public String getSbtExcludedConfigurationNames() {
        return sbtExcludedConfigurationNames;
    }

    public String getSbtIncludedConfigurationNames() {
        return sbtIncludedConfigurationNames;
    }

    public String getCondaPath() {
        return condaPath == null ? null : condaPath.trim();
    }

    public String getCondaEnvironmentName() {
        return condaEnvironmentName == null ? null : condaEnvironmentName.trim();
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
        return gradleInspectorRepositoryUrl == null ? null : gradleInspectorRepositoryUrl.trim();
    }

    public String getHexRebar3Path() {
        return hexRebar3Path;
    }

    public String getYarnPath() {
        return yarnPath;
    }

    public Boolean getYarnProductionDependenciesOnly() {
        return BooleanUtils.toBoolean(yarnProductionDependenciesOnly);
    }

    // properties end
}
