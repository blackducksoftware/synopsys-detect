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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ApkPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangLinuxPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DpkgPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.RpmPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomWrapperBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmPackageLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmShrinkwrapBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetProjectBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetSolutionBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockBomTool;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public class BomToolFactory {
    private final DetectConfiguration detectConfiguration;
    private final DetectFileFinder detectFileFinder;
    private final ExecutableRunner executableRunner;

    private final ExtractorFactory extractorFactory;
    private final ExecutableFinderFactory executableFinderFactory;
    private final InspectorManagerFactory inspectorManagerFactory;

    public BomToolFactory(final DetectConfiguration detectConfiguration, final DetectFileFinder detectFileFinder, final ExecutableRunner executableRunner,
        final ExtractorFactory extractorFactory, final ExecutableFinderFactory executableFinderFactory, final InspectorManagerFactory inspectorManagerFactory) {
        this.detectConfiguration = detectConfiguration;
        this.detectFileFinder = detectFileFinder;
        this.executableRunner = executableRunner;
        this.extractorFactory = extractorFactory;
        this.executableFinderFactory = executableFinderFactory;
        this.inspectorManagerFactory = inspectorManagerFactory;
    }

    public List<ClangLinuxPackageManager> clangLinuxPackageManagers() {
        final List<ClangLinuxPackageManager> clangLinuxPackageManagers = new ArrayList<>();
        clangLinuxPackageManagers.add(new ApkPackageManager());
        clangLinuxPackageManagers.add(new DpkgPackageManager());
        clangLinuxPackageManagers.add(new RpmPackageManager());
        return clangLinuxPackageManagers;
    }

    public ClangBomTool createClangBomTool(final BomToolEnvironment environment) {
        return new ClangBomTool(environment, executableRunner, detectFileFinder, clangLinuxPackageManagers(), extractorFactory.clangExtractor());
    }

    public ComposerLockBomTool createComposerLockBomTool(final BomToolEnvironment environment) {
        return new ComposerLockBomTool(environment, detectFileFinder, extractorFactory.composerLockExtractor());
    }

    public CondaCliBomTool createCondaBomTool(final BomToolEnvironment environment) {
        return new CondaCliBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), extractorFactory.condaCliExtractor());
    }

    public CpanCliBomTool createCpanCliBomTool(final BomToolEnvironment environment) {
        return new CpanCliBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), extractorFactory.cpanCliExtractor());
    }

    public DockerBomTool createDockerBomTool(final BomToolEnvironment environment) {
        final String tar = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_TAR);
        final String image = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_IMAGE);
        final boolean dockerRequired = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED);
    //TODO: KILL DOCKER
        return null;
        //return new DockerBomTool(environment, dockerInspectorManager, standardExecutableFinder, dockerRequired, image, tar, extractorFactory.dockerExtractor());
    }

    public GemlockBomTool createGemlockBomTool(final BomToolEnvironment environment) {
        return new GemlockBomTool(environment, detectFileFinder, extractorFactory.gemlockExtractor());
    }

    public GoCliBomTool createGoCliBomTool(final BomToolEnvironment environment) {
        return new GoCliBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), inspectorManagerFactory.goInspectorManager(), extractorFactory.goDepExtractor());
    }

    public GoLockBomTool createGoLockBomTool(final BomToolEnvironment environment) {
        return new GoLockBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), inspectorManagerFactory.goInspectorManager(), extractorFactory.goDepExtractor());
    }

    public GoVndrBomTool createGoVndrBomTool(final BomToolEnvironment environment) {
        return new GoVndrBomTool(environment, detectFileFinder, extractorFactory.goVndrExtractor());
    }

    public GradleInspectorBomTool createGradleInspectorBomTool(final BomToolEnvironment environment) {
        return new GradleInspectorBomTool(environment, detectFileFinder, executableFinderFactory.gradleExecutableFinder(), inspectorManagerFactory.gradleInspectorManager(), extractorFactory.gradleInspectorExtractor());
    }

    public MavenPomBomTool createMavenPomBomTool(final BomToolEnvironment environment) {
        return new MavenPomBomTool(environment, detectFileFinder, executableFinderFactory.mavenExecutableFinder(), extractorFactory.mavenCliExtractor());
    }

    public MavenPomWrapperBomTool createMavenPomWrapperBomTool(final BomToolEnvironment environment) {
        return new MavenPomWrapperBomTool(environment, detectFileFinder, executableFinderFactory.mavenExecutableFinder(), extractorFactory.mavenCliExtractor());
    }

    public NpmCliBomTool createNpmCliBomTool(final BomToolEnvironment environment) {
        return new NpmCliBomTool(environment, detectFileFinder, executableFinderFactory.npmExecutableFinder(), extractorFactory.npmCliExtractor());
    }

    public NpmPackageLockBomTool createNpmPackageLockBomTool(final BomToolEnvironment environment) {
        return new NpmPackageLockBomTool(environment, detectFileFinder, extractorFactory.npmLockfileExtractor());
    }

    public NugetProjectBomTool createNugetProjectBomTool(final BomToolEnvironment environment) {
        return new NugetProjectBomTool(environment, detectFileFinder, inspectorManagerFactory.nugetInspectorManager(), extractorFactory.nugetInspectorExtractor());
    }

    public NpmShrinkwrapBomTool createNpmShrinkwrapBomTool(final BomToolEnvironment environment) {
        return new NpmShrinkwrapBomTool(environment, detectFileFinder, extractorFactory.npmLockfileExtractor());
    }

    public NugetSolutionBomTool createNugetSolutionBomTool(final BomToolEnvironment environment) {
        return new NugetSolutionBomTool(environment, detectFileFinder, inspectorManagerFactory.nugetInspectorManager(), extractorFactory.nugetInspectorExtractor());
    }

    public PackratLockBomTool createPackratLockBomTool(final BomToolEnvironment environment) {
        return new PackratLockBomTool(environment, detectFileFinder, extractorFactory.packratLockExtractor());
    }

    public PearCliBomTool createPearCliBomTool(final BomToolEnvironment environment) {
        return new PearCliBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), extractorFactory.pearCliExtractor());
    }

    public PipenvBomTool createPipenvBomTool(final BomToolEnvironment environment) {
        return new PipenvBomTool(environment, detectFileFinder, executableFinderFactory.pythonExecutableFinder(), extractorFactory.pipenvExtractor());
    }

    public PipInspectorBomTool createPipInspectorBomTool(final BomToolEnvironment environment) {
        final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH);
        return new PipInspectorBomTool(environment, requirementsFile, detectFileFinder, executableFinderFactory.pythonExecutableFinder(), inspectorManagerFactory.pipInspectorManager(), extractorFactory.pipInspectorExtractor());
    }

    public PodlockBomTool createPodLockBomTool(final BomToolEnvironment environment) {
        return new PodlockBomTool(environment, detectFileFinder, extractorFactory.podlockExtractor());
    }

    public RebarBomTool createRebarBomTool(final BomToolEnvironment environment) {
        return new RebarBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), extractorFactory.rebarExtractor());
    }

    public SbtResolutionCacheBomTool createSbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        return new SbtResolutionCacheBomTool(environment, detectFileFinder, extractorFactory.sbtResolutionCacheExtractor());
    }

    public YarnLockBomTool createYarnLockBomTool(final BomToolEnvironment environment) {
        return new YarnLockBomTool(environment, detectFileFinder, executableFinderFactory.standardExecutableFinder(), extractorFactory.yarnLockExtractor());
    }

}
