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

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

public class BomToolConfig extends BaseConfig {
    private String dockerInspectorAirGapPath;
    private String gradleInspectorAirGapPath;
    private String nugetInspectorAirGapPath;
    private List<String> bomToolSearchDirectoryExclusions;

    public void initialize(final ValueContainer valueContainer, final String dockerInspectorAirGapPath, final String gradleInspectorAirGapPath, final String nugetInspectorAirGapPath, final List<String> bomToolSearchDirectoryExclusions) {
        this.dockerInspectorAirGapPath = dockerInspectorAirGapPath;
        this.gradleInspectorAirGapPath = gradleInspectorAirGapPath;
        this.nugetInspectorAirGapPath = nugetInspectorAirGapPath;
        this.bomToolSearchDirectoryExclusions = bomToolSearchDirectoryExclusions;

        this.gradleInspectorVersion = valueContainer.getGradleInspectorVersion();
        this.gradleBuildCommand = valueContainer.getGradleBuildCommand();
        this.gradleExcludedConfigurationNames = valueContainer.getGradleExcludedConfigurationNames();
        this.gradleIncludedConfigurationNames = valueContainer.getGradleIncludedConfigurationNames();
        this.gradleExcludedProjectNames = valueContainer.getGradleExcludedProjectNames();
        this.gradleIncludedProjectNames = valueContainer.getGradleIncludedProjectNames();
        this.nugetConfigPath = valueContainer.getNugetConfigPath();
        this.nugetInspectorPackageName = valueContainer.getNugetInspectorPackageName();
        this.nugetInspectorPackageVersion = valueContainer.getNugetInspectorPackageVersion();
        this.nugetInspectorExcludedModules = valueContainer.getNugetInspectorExcludedModules();
        this.nugetInspectorIncludedModules = valueContainer.getNugetInspectorIncludedModules();
        this.nugetInspectorIgnoreFailure = valueContainer.getNugetInspectorIgnoreFailure();
        this.mavenScope = valueContainer.getMavenScope();
        this.mavenBuildCommand = valueContainer.getMavenBuildCommand();
        this.gradlePath = valueContainer.getGradlePath();
        this.mavenPath = valueContainer.getMavenPath();
        this.mavenExcludedModuleNames = valueContainer.getMavenExcludedModuleNames();
        this.mavenIncludedModuleNames = valueContainer.getMavenIncludedModuleNames();
        this.nugetPath = valueContainer.getNugetPath();
        this.pipProjectName = valueContainer.getPipProjectName();
        this.pipProjectVersionName = valueContainer.getPipProjectVersionName();
        this.pythonThreeOverride = valueContainer.getPythonThreeOverride();
        this.pipEnvPath = valueContainer.getPipenvPath();
        this.pythonPath = valueContainer.getPythonPath();
        this.npmPath = valueContainer.getNpmPath();
        this.npmIncludeDevDependencies = valueContainer.getNpmIncludeDevDependencies();
        this.npmNodePath = valueContainer.getNpmNodePath();
        this.pearPath = valueContainer.getPearPath();
        this.pearOnlyRequiredDependencies = valueContainer.getPearOnlyRequiredDependencies();
        this.requirementsFilePath = valueContainer.getRequirementsFilePath();
        this.goDepPath = valueContainer.getGoDepPath();
        this.goRunDepInit = valueContainer.getGoRunDepInit();
        this.dockerPath = valueContainer.getDockerPath();
        this.dockerPathRequired = valueContainer.getDockerPathRequired();
        this.dockerInspectorPath = valueContainer.getDockerInspectorPath();
        this.dockerInspectorVersion = valueContainer.getDockerInspectorVersion();
        this.dockerTar = valueContainer.getDockerTar();
        this.dockerImage = valueContainer.getDockerImage();
        this.bashPath = valueContainer.getBashPath();
        this.hubSignatureScannerDryRun = valueContainer.getHubSignatureScannerDryRun();
        this.hubSignatureScannerSnippetMode = valueContainer.getHubSignatureScannerSnippetMode();
        this.hubSignatureScannerExclusionPatterns = valueContainer.getHubSignatureScannerExclusionPatterns();
        this.hubSignatureScannerPaths = valueContainer.getHubSignatureScannerPaths();
        this.hubSignatureScannerExclusionNamePatterns = valueContainer.getHubSignatureScannerExclusionNamePatterns();
        this.hubSignatureScannerMemory = valueContainer.getHubSignatureScannerMemory();
        this.hubSignatureScannerDisabled = valueContainer.getHubSignatureScannerDisabled();
        this.hubSignatureScannerOfflineLocalPath = valueContainer.getHubSignatureScannerOfflineLocalPath();
        this.hubSignatureScannerHostUrl = valueContainer.getHubSignatureScannerHostUrl();
        this.hubSignatureScannerParallelProcessors = valueContainer.getHubSignatureScannerParallelProcessors();
        this.hubSignatureScannerArguments = valueContainer.getHubSignatureScannerArguments();
        this.packagistIncludeDevDependencies = valueContainer.getPackagistIncludeDevDependencies();
        this.perlPath = valueContainer.getPerlPath();
        this.cpanPath = valueContainer.getCpanPath();
        this.cpanmPath = valueContainer.getCpanmPath();
        this.sbtExcludedConfigurationNames = valueContainer.getSbtExcludedConfigurationNames();
        this.sbtIncludedConfigurationNames = valueContainer.getSbtIncludedConfigurationNames();
        this.condaPath = valueContainer.getCondaPath();
        this.condaEnvironmentName = valueContainer.getCondaEnvironmentName();
        this.nugetPackagesRepoUrl = valueContainer.getNugetPackagesRepoUrl();
        this.gradleInspectorRepositoryUrl = valueContainer.getGradleInspectorRepositoryUrl();
        this.hexRebar3Path = valueContainer.getHexRebar3Path();
        this.yarnPath = valueContainer.getYarnPath();
        this.yarnProductionDependenciesOnly = valueContainer.getYarnProductionDependenciesOnly();
    }

    public List<String> getBomToolSearchDirectoryExclusions() {
        return bomToolSearchDirectoryExclusions;
    }

    // properties start
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

    private String pipProjectVersionName;

    private Boolean pythonThreeOverride;

    private String pipEnvPath;

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

    @Deprecated
    public String getPipProjectName() {
        return pipProjectName;
    }

    @Deprecated
    public String getPipProjectVersionName() {
        return pipProjectVersionName;
    }

    public boolean getPythonThreeOverride() {
        return BooleanUtils.toBoolean(pythonThreeOverride);
    }

    public String getPipEnvPath() {
        return pipEnvPath;
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
