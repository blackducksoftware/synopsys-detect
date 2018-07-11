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

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.PackageManagerFinder;
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
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrExtractor;
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
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;

public class BomToolFactory {
    private final DetectConfigWrapper detectConfigWrapper;
    private final DetectFileFinder detectFileFinder;
    private final StandardExecutableFinder standardExecutableFinder;

    private final ClangExtractor cLangExtractor;
    private final PackageManagerFinder cLangPackageManagerFinder;
    private final ComposerLockExtractor composerLockExtractor;
    private final CondaCliExtractor condaCliExtractor;
    private final CpanCliExtractor cpanCliExtractor;
    private final DockerExtractor dockerExtractor;
    private final DockerInspectorManager dockerInspectorManager;
    private final GemlockExtractor gemlockExtractor;
    private final GoDepExtractor goDepExtractor;
    private final GoInspectorManager goInspectorManager;
    private final GoVndrExtractor goVndrExtractor;
    private final GradleExecutableFinder gradleFinder;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final GradleInspectorManager gradleInspectorManager;
    private final MavenCliExtractor mavenCliExtractor;
    private final MavenExecutableFinder mavenExecutableFinder;
    private final NpmCliExtractor npmCliExtractor;
    private final NpmExecutableFinder npmExecutableFinder;
    private final NpmLockfileExtractor npmLockfileExtractor;
    private final NugetInspectorExtractor nugetInspectorExtractor;
    private final NugetInspectorManager nugetInspectorManager;
    private final PackratLockExtractor packratLockExtractor;
    private final PearCliExtractor pearCliExtractor;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final PipInspectorManager pipInspectorManager;
    private final PipenvExtractor pipenvExtractor;
    private final PodlockExtractor podlockExtractor;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final RebarExtractor rebarExtractor;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;
    private final YarnLockExtractor yarnLockExtractor;

    public BomToolFactory(final DetectConfigWrapper detectConfigWrapper, final DetectFileFinder detectFileFinder, final StandardExecutableFinder standardExecutableFinder, final ClangExtractor cLangExtractor,
            final PackageManagerFinder cLangPackageManagerFinder,
            final ComposerLockExtractor composerLockExtractor, final CondaCliExtractor condaCliExtractor, final CpanCliExtractor cpanCliExtractor, final DockerExtractor dockerExtractor,
            final DockerInspectorManager dockerInspectorManager, final GemlockExtractor gemlockExtractor, final GoDepExtractor goDepExtractor, final GoInspectorManager goInspectorManager,
            final GoVndrExtractor goVndrExtractor, final GradleExecutableFinder gradleFinder, final GradleInspectorExtractor gradleInspectorExtractor, final GradleInspectorManager gradleInspectorManager,
            final MavenCliExtractor mavenCliExtractor, final MavenExecutableFinder mavenExecutableFinder, final NpmCliExtractor npmCliExtractor, final NpmExecutableFinder npmExecutableFinder,
            final NpmLockfileExtractor npmLockfileExtractor, final NugetInspectorExtractor nugetInspectorExtractor, final NugetInspectorManager nugetInspectorManager,
            final PackratLockExtractor packratLockExtractor, final PearCliExtractor pearCliExtractor, final PipInspectorExtractor pipInspectorExtractor, final PipInspectorManager pipInspectorManager,
            final PipenvExtractor pipenvExtractor, final PodlockExtractor podlockExtractor, final PythonExecutableFinder pythonExecutableFinder, final RebarExtractor rebarExtractor,
            final SbtResolutionCacheExtractor sbtResolutionCacheExtractor, final YarnLockExtractor yarnLockExtractor) {
        this.detectConfigWrapper = detectConfigWrapper;
        this.detectFileFinder = detectFileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.cLangExtractor = cLangExtractor;
        this.cLangPackageManagerFinder = cLangPackageManagerFinder;
        this.composerLockExtractor = composerLockExtractor;
        this.condaCliExtractor = condaCliExtractor;
        this.cpanCliExtractor = cpanCliExtractor;
        this.dockerExtractor = dockerExtractor;
        this.dockerInspectorManager = dockerInspectorManager;
        this.gemlockExtractor = gemlockExtractor;
        this.goDepExtractor = goDepExtractor;
        this.goInspectorManager = goInspectorManager;
        this.goVndrExtractor = goVndrExtractor;
        this.gradleFinder = gradleFinder;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.gradleInspectorManager = gradleInspectorManager;
        this.mavenCliExtractor = mavenCliExtractor;
        this.mavenExecutableFinder = mavenExecutableFinder;
        this.npmCliExtractor = npmCliExtractor;
        this.npmExecutableFinder = npmExecutableFinder;
        this.npmLockfileExtractor = npmLockfileExtractor;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
        this.nugetInspectorManager = nugetInspectorManager;
        this.packratLockExtractor = packratLockExtractor;
        this.pearCliExtractor = pearCliExtractor;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.pipInspectorManager = pipInspectorManager;
        this.pipenvExtractor = pipenvExtractor;
        this.podlockExtractor = podlockExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
        this.rebarExtractor = rebarExtractor;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
        this.yarnLockExtractor = yarnLockExtractor;
    }

    public ClangBomTool createCLangBomTool(final BomToolEnvironment environment) {
        return new ClangBomTool(environment, detectFileFinder, cLangPackageManagerFinder, cLangExtractor);
    }

    public ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        return new ComposerLockBomTool(environment, detectFileFinder, composerLockExtractor);
    }

    public CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        return new CondaCliBomTool(environment, detectFileFinder, standardExecutableFinder, condaCliExtractor);
    }

    public CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        return new CpanCliBomTool(environment, detectFileFinder, standardExecutableFinder, cpanCliExtractor);
    }

    public DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        final String tar = detectConfigWrapper.getProperty(DetectProperty.DETECT_DOCKER_TAR);
        final String image = detectConfigWrapper.getProperty(DetectProperty.DETECT_DOCKER_IMAGE);
        final boolean dockerRequired = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED);

        return new DockerBomTool(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, dockerExtractor);
    }

    public GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        return new GemlockBomTool(environment, detectFileFinder, gemlockExtractor);
    }

    public GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        return new GoCliBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
    }

    public GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        return new GoLockBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
    }

    public GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        return new GoVndrBomTool(environment, detectFileFinder, goVndrExtractor);
    }

    public GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        return new GradleInspectorBomTool(environment, detectFileFinder, gradleFinder, gradleInspectorManager, gradleInspectorExtractor);
    }

    public MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        return new MavenPomBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
    }

    public MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        return new MavenPomWrapperBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
    }

    public NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        return new NpmCliBomTool(environment, detectFileFinder, npmExecutableFinder, npmCliExtractor);
    }

    public NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        return new NpmPackageLockBomTool(environment, detectFileFinder, npmLockfileExtractor);
    }

    public NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        return new NugetProjectBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
    }

    public NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        return new NpmShrinkwrapBomTool(environment, detectFileFinder, npmLockfileExtractor);
    }

    public NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        return new NugetSolutionBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
    }

    public PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        return new PackratLockBomTool(environment, detectFileFinder, packratLockExtractor);
    }

    public PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        return new PearCliBomTool(environment, detectFileFinder, standardExecutableFinder, pearCliExtractor);
    }

    public PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        return new PipenvBomTool(environment, detectFileFinder, pythonExecutableFinder, pipenvExtractor);
    }

    public PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        final String requirementsFile = detectConfigWrapper.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH);
        return new PipInspectorBomTool(environment, requirementsFile, detectFileFinder, pythonExecutableFinder, pipInspectorManager, pipInspectorExtractor);
    }

    public PodlockBomTool createPodLockBomTool(final BomToolEnvironment environment) {
        return new PodlockBomTool(environment, detectFileFinder, podlockExtractor);
    }

    public RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        return new RebarBomTool(environment, detectFileFinder, standardExecutableFinder, rebarExtractor);
    }

    public SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        return new SbtResolutionCacheBomTool(environment, detectFileFinder, sbtResolutionCacheExtractor);
    }

    public YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        final boolean productionDependenciesOnly = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_YARN_PROD_ONLY);
        return new YarnLockBomTool(environment, productionDependenciesOnly, detectFileFinder, standardExecutableFinder, yarnLockExtractor);
    }

}
