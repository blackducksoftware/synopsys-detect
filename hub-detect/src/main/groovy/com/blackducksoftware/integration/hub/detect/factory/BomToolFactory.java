/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepsBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomWrapperBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmPackageLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmShrinkwrapBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetProjectBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetSolutionBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;

@Component
public class BomToolFactory {
    @Autowired
    PodlockExtractor podlockExtractor;

    @Autowired
    CondaCliExtractor condaCliExtractor;

    @Autowired
    CpanCliExtractor cpanCliExtractor;

    @Autowired
    PackratLockExtractor packratLockExtractor;

    @Autowired
    DockerExtractor dockerExtractor;

    @Autowired
    GoVndrExtractor goVndrExtractor;

    @Autowired
    GoDepsExtractor goDepsExtractor;

    @Autowired
    GoDepExtractor goDepExtractor;

    @Autowired
    GradleInspectorExtractor gradleInspectorExtractor;

    @Autowired
    RebarExtractor rebarExtractor;

    @Autowired
    MavenCliExtractor mavenCliExtractor;

    @Autowired
    NpmCliExtractor npmCliExtractor;

    @Autowired
    NpmLockfileExtractor npmLockfileExtractor;

    @Autowired
    NugetInspectorExtractor nugetInspectorExtractor;

    @Autowired
    ComposerLockExtractor composerLockExtractor;

    @Autowired
    PearCliExtractor pearCliExtractor;

    @Autowired
    PipenvExtractor pipenvExtractor;

    @Autowired
    PipInspectorExtractor pipInspectorExtractor;

    @Autowired
    GemlockExtractor gemlockExtractor;

    @Autowired
    SbtResolutionCacheExtractor sbtResolutionCacheExtractor;

    @Autowired
    YarnLockExtractor yarnLockExtractor;

    @Autowired
    DetectFileFinder detectFileFinder;

    @Autowired
    StandardExecutableFinder standardExecutableFinder;

    @Autowired
    DockerInspectorManager dockerInspectorManager;

    @Autowired
    PipInspectorManager pipInspectorManager;

    @Autowired
    GoInspectorManager goInspectorManager;

    @Autowired
    NugetInspectorManager nugetInspectorManager;

    @Autowired
    PythonExecutableFinder pythonExecutableFinder;

    @Autowired
    GradleExecutableFinder gradleFinder;

    @Autowired
    GradleInspectorManager gradleInspectorManager;

    @Autowired
    MavenExecutableFinder mavenExecutableFinder;

    @Autowired
    NpmExecutableFinder npmExecutableFinder;

    @Autowired
    DetectConfiguration detectConfiguration;

    public PodlockBomTool createCocoapodsBomTool(final BomToolEnvironment environment) {
        final PodlockBomTool bomTool = new PodlockBomTool(environment, detectFileFinder, podlockExtractor);
        return bomTool;
    }

    public CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        final CondaCliBomTool bomTool = new CondaCliBomTool(environment, detectFileFinder, standardExecutableFinder, condaCliExtractor);
        return bomTool;
    }

    public CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        final CpanCliBomTool bomTool = new CpanCliBomTool(environment, detectFileFinder, standardExecutableFinder, cpanCliExtractor);
        return bomTool;
    }

    public PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        final PackratLockBomTool bomTool = new PackratLockBomTool(environment, detectFileFinder, packratLockExtractor);
        return bomTool;
    }

    public DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        final String tar = detectConfiguration.getDockerTar();
        final String image = detectConfiguration.getDockerImage();
        final boolean dockerRequired = detectConfiguration.getDockerPathRequired();

        final DockerBomTool bomTool = new DockerBomTool(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, dockerExtractor);
        return bomTool;
    }

    public GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        final GoCliBomTool bomTool = new GoCliBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    public GoDepsBomTool createGoDepsBomTool(final BomToolEnvironment environment) {
        final GoDepsBomTool bomTool = new GoDepsBomTool(environment, detectFileFinder, goDepsExtractor);
        return bomTool;
    }

    public GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        final GoLockBomTool bomTool = new GoLockBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    public GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        final GoVndrBomTool bomTool = new GoVndrBomTool(environment, detectFileFinder, goVndrExtractor);
        return bomTool;
    }

    public GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        final GradleInspectorBomTool bomTool = new GradleInspectorBomTool(environment, detectFileFinder, gradleFinder, gradleInspectorManager, gradleInspectorExtractor);
        return bomTool;
    }

    public RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        final RebarBomTool bomTool = new RebarBomTool(environment, detectFileFinder, standardExecutableFinder, rebarExtractor);
        return bomTool;
    }

    public MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        final MavenPomBomTool bomTool = new MavenPomBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    public MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        final MavenPomWrapperBomTool bomTool = new MavenPomWrapperBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    public NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        final NpmCliBomTool bomTool = new NpmCliBomTool(environment, detectFileFinder, npmCliExtractor);
        return bomTool;
    }

    public NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        final NpmPackageLockBomTool bomTool = new NpmPackageLockBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    public NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        final NpmShrinkwrapBomTool bomTool = new NpmShrinkwrapBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    public NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        final NugetSolutionBomTool bomTool = new NugetSolutionBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    public NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        final NugetProjectBomTool bomTool = new NugetProjectBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    public ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        final ComposerLockBomTool bomTool = new ComposerLockBomTool(environment, detectFileFinder, composerLockExtractor);
        return bomTool;
    }

    public PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        final PearCliBomTool bomTool = new PearCliBomTool(environment, detectFileFinder, standardExecutableFinder, pearCliExtractor);
        return bomTool;
    }

    public PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        final PipenvBomTool bomTool = new PipenvBomTool(environment, detectFileFinder, pythonExecutableFinder, pipenvExtractor);
        return bomTool;
    }

    public PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        final String requirementsFile = detectConfiguration.getRequirementsFilePath();
        final PipInspectorBomTool bomTool = new PipInspectorBomTool(environment, requirementsFile, detectFileFinder, pythonExecutableFinder, pipInspectorManager, pipInspectorExtractor);
        return bomTool;
    }

    public GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        final GemlockBomTool bomTool = new GemlockBomTool(environment, detectFileFinder, gemlockExtractor);
        return bomTool;
    }

    public SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        final SbtResolutionCacheBomTool bomTool = new SbtResolutionCacheBomTool(environment, detectFileFinder, sbtResolutionCacheExtractor);
        return bomTool;
    }

    public YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        final boolean productionDependenciesOnly = detectConfiguration.getYarnProductionDependenciesOnly();
        final YarnLockBomTool bomTool = new YarnLockBomTool(environment, productionDependenciesOnly, detectFileFinder, standardExecutableFinder, yarnLockExtractor);
        return bomTool;
    }
}
