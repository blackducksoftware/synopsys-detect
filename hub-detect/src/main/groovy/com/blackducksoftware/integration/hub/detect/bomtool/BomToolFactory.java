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
package com.blackducksoftware.integration.hub.detect.bomtool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.blackducksoftware.integration.hub.detect.configuration.BomToolConfig;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class BomToolFactory {
    private final PodlockExtractor podlockExtractor;
    private final CondaCliExtractor condaCliExtractor;
    private final CpanCliExtractor cpanCliExtractor;
    private final PackratLockExtractor packratLockExtractor;
    private final DockerExtractor dockerExtractor;
    private final GoVndrExtractor goVndrExtractor;
    private final GoDepsExtractor goDepsExtractor;
    private final GoDepExtractor goDepExtractor;
    private final GradleInspectorExtractor gradleInspectorExtractor;
    private final RebarExtractor rebarExtractor;
    private final MavenCliExtractor mavenCliExtractor;
    private final NpmCliExtractor npmCliExtractor;
    private final NpmLockfileExtractor npmLockfileExtractor;
    private final NugetInspectorExtractor nugetInspectorExtractor;
    private final ComposerLockExtractor composerLockExtractor;
    private final PearCliExtractor pearCliExtractor;
    private final PipenvExtractor pipenvExtractor;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final GemlockExtractor gemlockExtractor;
    private final SbtResolutionCacheExtractor sbtResolutionCacheExtractor;
    private final YarnLockExtractor yarnLockExtractor;
    private final DetectFileFinder detectFileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final DockerInspectorManager dockerInspectorManager;
    private final PipInspectorManager pipInspectorManager;
    private final GoInspectorManager goInspectorManager;
    private final NugetInspectorManager nugetInspectorManager;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final GradleExecutableFinder gradleFinder;
    private final GradleInspectorManager gradleInspectorManager;
    private final MavenExecutableFinder mavenExecutableFinder;
    private final NpmExecutableFinder npmExecutableFinder;
    private final BomToolConfig bomToolConfig;

    @Autowired
    public BomToolFactory(final PodlockExtractor podlockExtractor, final CondaCliExtractor condaCliExtractor, final CpanCliExtractor cpanCliExtractor, final PackratLockExtractor packratLockExtractor,
            final DockerExtractor dockerExtractor, final GoVndrExtractor goVndrExtractor, final GoDepsExtractor goDepsExtractor, final GoDepExtractor goDepExtractor,
            final GradleInspectorExtractor gradleInspectorExtractor, final RebarExtractor rebarExtractor, final MavenCliExtractor mavenCliExtractor, final NpmCliExtractor npmCliExtractor,
            final NpmLockfileExtractor npmLockfileExtractor, final NugetInspectorExtractor nugetInspectorExtractor, final ComposerLockExtractor composerLockExtractor, final PearCliExtractor pearCliExtractor,
            final PipenvExtractor pipenvExtractor, final PipInspectorExtractor pipInspectorExtractor, final GemlockExtractor gemlockExtractor, final SbtResolutionCacheExtractor sbtResolutionCacheExtractor,
            final YarnLockExtractor yarnLockExtractor, final DetectFileFinder detectFileFinder, final StandardExecutableFinder standardExecutableFinder, final DockerInspectorManager dockerInspectorManager,
            final PipInspectorManager pipInspectorManager, final GoInspectorManager goInspectorManager, final NugetInspectorManager nugetInspectorManager, final PythonExecutableFinder pythonExecutableFinder,
            final GradleExecutableFinder gradleFinder, final GradleInspectorManager gradleInspectorManager, final MavenExecutableFinder mavenExecutableFinder, final NpmExecutableFinder npmExecutableFinder,
            final BomToolConfig bomToolConfig) {
        this.podlockExtractor = podlockExtractor;
        this.condaCliExtractor = condaCliExtractor;
        this.cpanCliExtractor = cpanCliExtractor;
        this.packratLockExtractor = packratLockExtractor;
        this.dockerExtractor = dockerExtractor;
        this.goVndrExtractor = goVndrExtractor;
        this.goDepsExtractor = goDepsExtractor;
        this.goDepExtractor = goDepExtractor;
        this.gradleInspectorExtractor = gradleInspectorExtractor;
        this.rebarExtractor = rebarExtractor;
        this.mavenCliExtractor = mavenCliExtractor;
        this.npmCliExtractor = npmCliExtractor;
        this.npmLockfileExtractor = npmLockfileExtractor;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
        this.composerLockExtractor = composerLockExtractor;
        this.pearCliExtractor = pearCliExtractor;
        this.pipenvExtractor = pipenvExtractor;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.gemlockExtractor = gemlockExtractor;
        this.sbtResolutionCacheExtractor = sbtResolutionCacheExtractor;
        this.yarnLockExtractor = yarnLockExtractor;
        this.detectFileFinder = detectFileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.dockerInspectorManager = dockerInspectorManager;
        this.pipInspectorManager = pipInspectorManager;
        this.goInspectorManager = goInspectorManager;
        this.nugetInspectorManager = nugetInspectorManager;
        this.pythonExecutableFinder = pythonExecutableFinder;
        this.gradleFinder = gradleFinder;
        this.gradleInspectorManager = gradleInspectorManager;
        this.mavenExecutableFinder = mavenExecutableFinder;
        this.npmExecutableFinder = npmExecutableFinder;
        this.bomToolConfig = bomToolConfig;
    }

    public BomToolSearchRuleSet createStrategies(final BomToolEnvironment environment) {
        final BomToolSearchRuleSetBuilder searchRuleSet = new BomToolSearchRuleSetBuilder(environment);

        searchRuleSet.addBomTool(createCocoapodsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createCondaBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createCpanCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPackratLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createDockerBomTool(environment)).nestable(false).maxDepth(0);

        searchRuleSet.addBomTool(createGoCliBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoDepsBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoLockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createGoVndrBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_DEPS);
        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_LOCK);
        searchRuleSet.yield(BomToolType.GO_CLI).to(BomToolType.GO_VNDR);

        searchRuleSet.addBomTool(createGradleInspectorBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createRebarBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createMavenPomBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createMavenPomWrapperBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createYarnLockBomTool(environment)).defaultNested();

        searchRuleSet.addBomTool(createNpmPackageLockBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNpmShrinkwrapBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNpmCliBomTool(environment)).defaultNested();

        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_PACKAGELOCK);
        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.NPM_SHRINKWRAP);

        searchRuleSet.yield(BomToolType.NPM_CLI).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_PACKAGELOCK).to(BomToolType.YARN_LOCK);
        searchRuleSet.yield(BomToolType.NPM_SHRINKWRAP).to(BomToolType.YARN_LOCK);

        searchRuleSet.addBomTool(createNugetSolutionBomTool(environment)).defaultNested();
        searchRuleSet.addBomTool(createNugetProjectBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.NUGET_PROJECT_INSPECTOR).to(BomToolType.NUGET_SOLUTION_INSPECTOR);

        searchRuleSet.addBomTool(createComposerLockBomTool(environment)).defaultNotNested();

        searchRuleSet.addBomTool(createPipenvBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPipInspectorBomTool(environment)).defaultNotNested();

        searchRuleSet.yield(BomToolType.PIP_INSPECTOR).to(BomToolType.PIP_ENV);

        searchRuleSet.addBomTool(createGemlockBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createSbtResolutionCacheBomTool(environment)).defaultNotNested();
        searchRuleSet.addBomTool(createPearCliBomTool(environment)).defaultNotNested();

        return searchRuleSet.build();
    }

    private PodlockBomTool createCocoapodsBomTool(final BomToolEnvironment environment) {
        final PodlockBomTool bomTool = new PodlockBomTool(environment, detectFileFinder, podlockExtractor);
        return bomTool;
    }

    private CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        final CondaCliBomTool bomTool = new CondaCliBomTool(environment, detectFileFinder, standardExecutableFinder, condaCliExtractor);
        return bomTool;
    }

    private CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        final CpanCliBomTool bomTool = new CpanCliBomTool(environment, detectFileFinder, standardExecutableFinder, cpanCliExtractor);
        return bomTool;
    }

    private PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        final PackratLockBomTool bomTool = new PackratLockBomTool(environment, detectFileFinder, packratLockExtractor);
        return bomTool;
    }

    private DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        final String tar = bomToolConfig.getDockerTar();
        final String image = bomToolConfig.getDockerImage();
        final boolean dockerRequired = bomToolConfig.getDockerPathRequired();

        final DockerBomTool bomTool = new DockerBomTool(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, dockerExtractor);
        return bomTool;
    }

    private GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        final GoCliBomTool bomTool = new GoCliBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    private GoDepsBomTool createGoDepsBomTool(final BomToolEnvironment environment) {
        final GoDepsBomTool bomTool = new GoDepsBomTool(environment, detectFileFinder, goDepsExtractor);
        return bomTool;
    }

    private GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        final GoLockBomTool bomTool = new GoLockBomTool(environment, detectFileFinder, standardExecutableFinder, goInspectorManager, goDepExtractor);
        return bomTool;
    }

    private GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        final GoVndrBomTool bomTool = new GoVndrBomTool(environment, detectFileFinder, goVndrExtractor);
        return bomTool;
    }

    private GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        final GradleInspectorBomTool bomTool = new GradleInspectorBomTool(environment, detectFileFinder, gradleFinder, gradleInspectorManager, gradleInspectorExtractor);
        return bomTool;
    }

    private RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        final RebarBomTool bomTool = new RebarBomTool(environment, detectFileFinder, standardExecutableFinder, rebarExtractor);
        return bomTool;
    }

    private MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        final MavenPomBomTool bomTool = new MavenPomBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    private MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        final MavenPomWrapperBomTool bomTool = new MavenPomWrapperBomTool(environment, detectFileFinder, mavenExecutableFinder, mavenCliExtractor);
        return bomTool;
    }

    private NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        final NpmCliBomTool bomTool = new NpmCliBomTool(environment, detectFileFinder, npmExecutableFinder, npmCliExtractor);
        return bomTool;
    }

    private NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        final NpmPackageLockBomTool bomTool = new NpmPackageLockBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    private NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        final NpmShrinkwrapBomTool bomTool = new NpmShrinkwrapBomTool(environment, detectFileFinder, npmLockfileExtractor);
        return bomTool;
    }

    private NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        final NugetSolutionBomTool bomTool = new NugetSolutionBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    private NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        final NugetProjectBomTool bomTool = new NugetProjectBomTool(environment, detectFileFinder, nugetInspectorManager, nugetInspectorExtractor);
        return bomTool;
    }

    private ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        final ComposerLockBomTool bomTool = new ComposerLockBomTool(environment, detectFileFinder, composerLockExtractor);
        return bomTool;
    }

    private PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        final PearCliBomTool bomTool = new PearCliBomTool(environment, detectFileFinder, standardExecutableFinder, pearCliExtractor);
        return bomTool;
    }

    private PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        final PipenvBomTool bomTool = new PipenvBomTool(environment, detectFileFinder, pythonExecutableFinder, pipenvExtractor);
        return bomTool;
    }

    private PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        final String requirementsFile = bomToolConfig.getRequirementsFilePath();
        final PipInspectorBomTool bomTool = new PipInspectorBomTool(environment, requirementsFile, detectFileFinder, pythonExecutableFinder, pipInspectorManager, pipInspectorExtractor);
        return bomTool;
    }

    private GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        final GemlockBomTool bomTool = new GemlockBomTool(environment, detectFileFinder, gemlockExtractor);
        return bomTool;
    }

    private SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        final SbtResolutionCacheBomTool bomTool = new SbtResolutionCacheBomTool(environment, detectFileFinder, sbtResolutionCacheExtractor);
        return bomTool;
    }

    private YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        final boolean productionDependenciesOnly = bomToolConfig.getYarnProductionDependenciesOnly();
        final YarnLockBomTool bomTool = new YarnLockBomTool(environment, productionDependenciesOnly, detectFileFinder, standardExecutableFinder, yarnLockExtractor);
        return bomTool;
    }
}
