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
package com.blackducksoftware.integration.hub.detect;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorFactory;
import com.blackducksoftware.integration.hub.detect.detector.bazel.BazelDetector;
import com.blackducksoftware.integration.hub.detect.detector.bazel.BazelExtractor;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.BitbakeDetector;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.BitbakeExtractor;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.BitbakeListTasksParser;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.GraphParserTransformer;
import com.blackducksoftware.integration.hub.detect.detector.clang.ApkPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.ClangCompileCommandParser;
import com.blackducksoftware.integration.hub.detect.detector.clang.ClangDetector;
import com.blackducksoftware.integration.hub.detect.detector.clang.ClangExtractor;
import com.blackducksoftware.integration.hub.detect.detector.clang.ClangLinuxPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.CodeLocationAssembler;
import com.blackducksoftware.integration.hub.detect.detector.clang.DependenciesListFileManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.DpkgPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.clang.RpmPackageManager;
import com.blackducksoftware.integration.hub.detect.detector.cocoapods.PodlockDetector;
import com.blackducksoftware.integration.hub.detect.detector.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.detector.cocoapods.PodlockParser;
import com.blackducksoftware.integration.hub.detect.detector.conda.CondaCliDetector;
import com.blackducksoftware.integration.hub.detect.detector.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.detector.conda.CondaListParser;
import com.blackducksoftware.integration.hub.detect.detector.cpan.CpanCliDetector;
import com.blackducksoftware.integration.hub.detect.detector.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.detector.cpan.CpanListParser;
import com.blackducksoftware.integration.hub.detect.detector.cran.PackratLockDetector;
import com.blackducksoftware.integration.hub.detect.detector.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.detector.cran.PackratPackager;
import com.blackducksoftware.integration.hub.detect.detector.go.DepPackager;
import com.blackducksoftware.integration.hub.detect.detector.go.GoCliDetector;
import com.blackducksoftware.integration.hub.detect.detector.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.detector.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.detector.go.GoLockDetector;
import com.blackducksoftware.integration.hub.detect.detector.go.GoVendorDetector;
import com.blackducksoftware.integration.hub.detect.detector.go.GoVendorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.go.GoVndrDetector;
import com.blackducksoftware.integration.hub.detect.detector.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleInspectorDetector;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.detector.hex.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.detector.hex.RebarDetector;
import com.blackducksoftware.integration.hub.detect.detector.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.detector.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.detector.maven.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.detector.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.detector.maven.MavenPomDetector;
import com.blackducksoftware.integration.hub.detect.detector.maven.MavenPomWrapperDetector;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmCliDetector;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmCliParser;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmLockfileParser;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmPackageLockDetector;
import com.blackducksoftware.integration.hub.detect.detector.npm.NpmShrinkwrapDetector;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetProjectDetector;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetSolutionDetector;
import com.blackducksoftware.integration.hub.detect.detector.packagist.ComposerLockDetector;
import com.blackducksoftware.integration.hub.detect.detector.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.detector.packagist.PackagistParser;
import com.blackducksoftware.integration.hub.detect.detector.pear.PearCliDetector;
import com.blackducksoftware.integration.hub.detect.detector.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.detector.pear.PearParser;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipInspectorDetector;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipInspectorTreeParser;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipenvDetector;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.detector.pip.PipenvGraphParser;
import com.blackducksoftware.integration.hub.detect.detector.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.detector.rubygems.GemlockDetector;
import com.blackducksoftware.integration.hub.detect.detector.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.detector.sbt.SbtResolutionCacheDetector;
import com.blackducksoftware.integration.hub.detect.detector.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.detector.yarn.YarnListParser;
import com.blackducksoftware.integration.hub.detect.detector.yarn.YarnLockDetector;
import com.blackducksoftware.integration.hub.detect.detector.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.detector.yarn.YarnLockParser;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactResolver;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

import freemarker.template.Configuration;

//@Configuration is used here to allow 'EnableAspectJAutoProxy' because it could not be enabled it otherwise.
//This configuration is NOT loaded when the application starts, but only manually when a DetectRun is needed.
//Spring scanning should not be invoked as this should not be loaded during boot.
@org.springframework.context.annotation.Configuration
public class DetectorBeanConfiguration {
    //Provided Dependencies
    @Autowired
    public Gson gson;
    @Autowired
    public Configuration configuration;
    @Autowired
    public DocumentBuilder documentBuilder;
    @Autowired
    public ExecutableRunner executableRunner;
    @Autowired
    public AirGapManager airGapManager;
    @Autowired
    public ExecutableFinder executableFinder;
    @Autowired
    public ExternalIdFactory externalIdFactory;
    @Autowired
    public DetectFileFinder detectFileFinder;
    @Autowired
    public DirectoryManager directoryManager;
    @Autowired
    public DetectConfiguration detectConfiguration;
    @Autowired
    public ConnectionManager connectionManager;
    @Autowired
    public CacheableExecutableFinder cacheableExecutableFinder;
    @Autowired
    public ArtifactResolver artifactResolver;
    @Autowired
    public DetectInfo detectInfo;

    //DetectorFactory
    //This is the ONLY class that should be taken from the Configuration manually.
    //Detectors should be accessed using the DetectorFactory which will create them through Spring.

    @Bean
    public DetectorFactory detectorFactory() {
        return new DetectorFactory();
    }

    //Detector-Only Dependencies
    //All detector support classes. These are classes not actually used outside of the bom tools but are necessary for some bom tools.

    @Bean
    public DependenciesListFileManager clangDependenciesListFileParser() {
        return new DependenciesListFileManager(executableRunner, clangCompileCommandParser());
    }

    @Bean
    ClangCompileCommandParser clangCompileCommandParser() {
        return new ClangCompileCommandParser();
    }

    @Bean
    public CodeLocationAssembler codeLocationAssembler() {
        return new CodeLocationAssembler(externalIdFactory);
    }

    @Bean
    public BazelExtractor bazelExtractor() {
        return new BazelExtractor(executableRunner);
    }

    @Bean
    public ClangExtractor clangExtractor() {
        return new ClangExtractor(detectConfiguration, executableRunner, gson, detectFileFinder, directoryManager, clangDependenciesListFileParser(), codeLocationAssembler());
    }

    public List<ClangLinuxPackageManager> clangLinuxPackageManagers() {
        final List<ClangLinuxPackageManager> clangLinuxPackageManagers = new ArrayList<>();
        clangLinuxPackageManagers.add(new ApkPackageManager());
        clangLinuxPackageManagers.add(new DpkgPackageManager());
        clangLinuxPackageManagers.add(new RpmPackageManager());
        return clangLinuxPackageManagers;
    }

    @Bean
    public PodlockParser podlockParser() {
        return new PodlockParser(externalIdFactory);
    }

    @Bean
    public PodlockExtractor podlockExtractor() {
        return new PodlockExtractor(podlockParser(), externalIdFactory);
    }

    @Bean
    public CondaListParser condaListParser() {
        return new CondaListParser(gson, externalIdFactory);
    }

    @Bean
    public CondaCliExtractor condaCliExtractor() {
        return new CondaCliExtractor(condaListParser(), externalIdFactory, executableRunner, detectConfiguration, directoryManager);
    }

    @Bean
    public CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory);
    }

    @Bean
    public CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), externalIdFactory, executableRunner, directoryManager);
    }

    @Bean
    public PackratPackager packratPackager() {
        return new PackratPackager(externalIdFactory);
    }

    @Bean
    public PackratLockExtractor packratLockExtractor() {
        return new PackratLockExtractor(packratPackager(), externalIdFactory, detectFileFinder);
    }

    @Bean
    public GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(depPackager(), externalIdFactory);
    }

    @Bean
    public GoInspectorManager goInspectorManager() {
        return new GoInspectorManager(directoryManager, executableFinder, executableRunner, detectConfiguration);
    }

    @Bean
    public GoVndrExtractor goVndrExtractor() {
        return new GoVndrExtractor(externalIdFactory);
    }

    @Bean
    public GoVendorExtractor goVendorExtractor() {
        return new GoVendorExtractor(gson, externalIdFactory);
    }

    @Bean
    public DepPackager depPackager() {
        return new DepPackager(executableRunner, externalIdFactory, detectConfiguration);
    }

    @Bean
    public GradleReportParser gradleReportParser() {
        return new GradleReportParser(externalIdFactory);
    }

    @Bean
    public GradleExecutableFinder gradleExecutableFinder() {
        return new GradleExecutableFinder(executableFinder, detectConfiguration);
    }

    @Bean
    public GradleInspectorExtractor gradleInspectorExtractor() {
        return new GradleInspectorExtractor(executableRunner, detectFileFinder, gradleReportParser(), detectConfiguration);
    }

    @Bean
    public GradleInspectorManager gradleInspectorManager() throws ParserConfigurationException {
        return new GradleInspectorManager(directoryManager, airGapManager, configuration, detectConfiguration, artifactResolver);
    }

    @Bean
    public Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser(externalIdFactory);
    }

    @Bean
    public RebarExtractor rebarExtractor() {
        return new RebarExtractor(executableRunner, rebar3TreeParser());
    }

    @Bean
    public MavenCodeLocationPackager mavenCodeLocationPackager() {
        return new MavenCodeLocationPackager(externalIdFactory);
    }

    @Bean
    public MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner, mavenCodeLocationPackager(), detectConfiguration);
    }

    @Bean
    public MavenExecutableFinder mavenExecutableFinder() {
        return new MavenExecutableFinder(executableFinder, detectConfiguration);
    }

    @Bean
    public NpmCliParser npmCliDependencyFinder() {
        return new NpmCliParser(externalIdFactory);
    }

    @Bean
    public NpmLockfileParser npmLockfilePackager() {
        return new NpmLockfileParser(gson, externalIdFactory);
    }

    @Bean
    public NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner, npmCliDependencyFinder(), detectConfiguration);
    }

    @Bean
    public NpmLockfileExtractor npmLockfileExtractor() {
        return new NpmLockfileExtractor(npmLockfilePackager(), detectConfiguration);
    }

    @Bean
    public NpmExecutableFinder npmExecutableFinder() {
        return new NpmExecutableFinder(directoryManager, executableFinder, executableRunner, detectConfiguration);
    }

    @Bean
    public NugetInspectorPackager nugetInspectorPackager() {
        return new NugetInspectorPackager(gson, externalIdFactory);
    }

    @Bean
    public NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(nugetInspectorPackager(), detectFileFinder, detectConfiguration);
    }

    @Bean
    public NugetInspectorManager nugetInspectorManager() {
        return new NugetInspectorManager(directoryManager, executableFinder, executableRunner, detectConfiguration, airGapManager, artifactResolver, detectInfo, detectFileFinder);
    }

    @Bean
    public PackagistParser packagistParser() {
        return new PackagistParser(externalIdFactory, detectConfiguration);
    }

    @Bean
    public ComposerLockExtractor composerLockExtractor() {
        return new ComposerLockExtractor(packagistParser());
    }

    @Bean
    public PearParser pearDependencyFinder() {
        return new PearParser(externalIdFactory, detectConfiguration);
    }

    @Bean
    public PearCliExtractor pearCliExtractor() {
        return new PearCliExtractor(detectFileFinder, externalIdFactory, pearDependencyFinder(), executableRunner, directoryManager);
    }

    @Bean
    public PipenvGraphParser pipenvGraphParser() {
        return new PipenvGraphParser(externalIdFactory);
    }

    @Bean
    public PipenvExtractor pipenvExtractor() {
        return new PipenvExtractor(executableRunner, pipenvGraphParser(), detectConfiguration);
    }

    @Bean
    public PipInspectorTreeParser pipInspectorTreeParser() {
        return new PipInspectorTreeParser(externalIdFactory);
    }

    @Bean
    public PipInspectorExtractor pipInspectorExtractor() {
        return new PipInspectorExtractor(executableRunner, pipInspectorTreeParser(), detectConfiguration);
    }

    @Bean
    public PipInspectorManager pipInspectorManager() {
        return new PipInspectorManager(directoryManager);
    }

    @Bean
    public PythonExecutableFinder pythonExecutableFinder() {
        return new PythonExecutableFinder(executableFinder, detectConfiguration);
    }

    @Bean
    public GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory);
    }

    @Bean
    public SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        return new SbtResolutionCacheExtractor(detectFileFinder, externalIdFactory, detectConfiguration);
    }

    @Bean
    public YarnListParser yarnListParser() {
        return new YarnListParser(externalIdFactory, yarnLockParser());
    }

    @Bean
    public YarnLockParser yarnLockParser() {
        return new YarnLockParser();
    }

    @Bean
    public YarnLockExtractor yarnLockExtractor() {
        return new YarnLockExtractor(externalIdFactory, yarnListParser(), executableRunner, detectConfiguration);
    }

    @Bean
    public GraphParserTransformer graphParserTransformer() {
        return new GraphParserTransformer();
    }

    @Bean
    public BitbakeExtractor bitbakeExtractor() {
        return new BitbakeExtractor(executableFinder, executableRunner, detectConfiguration, directoryManager, detectFileFinder, graphParserTransformer(), bitbakeListTasksParser());
    }

    @Bean
    public BitbakeListTasksParser bitbakeListTasksParser() {
        return new BitbakeListTasksParser();
    }

    //BomTools
    //Should be scoped to Prototype so a new Detector is created every time one is needed.
    //Should only be accessed through the DetectorFactory.


    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public BazelDetector bazelDetector(final DetectorEnvironment environment) {
        return new BazelDetector(environment, executableRunner, detectFileFinder, bazelExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public BitbakeDetector bitbakeBomTool(final DetectorEnvironment environment) {
        return new BitbakeDetector(environment, detectFileFinder, detectConfiguration, bitbakeExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ClangDetector clangBomTool(final DetectorEnvironment environment) {
        return new ClangDetector(environment, executableRunner, detectFileFinder, clangLinuxPackageManagers(), clangExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ComposerLockDetector composerLockBomTool(final DetectorEnvironment environment) {
        return new ComposerLockDetector(environment, detectFileFinder, composerLockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public CondaCliDetector condaBomTool(final DetectorEnvironment environment) {
        return new CondaCliDetector(environment, detectFileFinder, cacheableExecutableFinder, condaCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public CpanCliDetector cpanCliBomTool(final DetectorEnvironment environment) {
        return new CpanCliDetector(environment, detectFileFinder, cacheableExecutableFinder, cpanCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GemlockDetector gemlockBomTool(final DetectorEnvironment environment) {
        return new GemlockDetector(environment, detectFileFinder, gemlockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoCliDetector goCliBomTool(final DetectorEnvironment environment) {
        return new GoCliDetector(environment, detectFileFinder, cacheableExecutableFinder, goInspectorManager(), goDepExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoLockDetector goLockBomTool(final DetectorEnvironment environment) {
        return new GoLockDetector(environment, detectFileFinder, cacheableExecutableFinder, goInspectorManager(), goDepExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoVndrDetector goVndrBomTool(final DetectorEnvironment environment) {
        return new GoVndrDetector(environment, detectFileFinder, goVndrExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoVendorDetector goVendorBomTool(final DetectorEnvironment environment) {
        return new GoVendorDetector(environment, detectFileFinder, goVendorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GradleInspectorDetector gradleInspectorBomTool(final DetectorEnvironment environment) throws ParserConfigurationException {
        return new GradleInspectorDetector(environment, directoryManager, detectFileFinder, gradleExecutableFinder(), gradleInspectorManager(), gradleInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public MavenPomDetector mavenPomBomTool(final DetectorEnvironment environment) {
        return new MavenPomDetector(environment, detectFileFinder, mavenExecutableFinder(), mavenCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public MavenPomWrapperDetector mavenPomWrapperBomTool(final DetectorEnvironment environment) {
        return new MavenPomWrapperDetector(environment, detectFileFinder, mavenExecutableFinder(), mavenCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmCliDetector npmCliBomTool(final DetectorEnvironment environment) {
        return new NpmCliDetector(environment, detectFileFinder, npmExecutableFinder(), npmCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmPackageLockDetector npmPackageLockBomTool(final DetectorEnvironment environment) {
        return new NpmPackageLockDetector(environment, detectFileFinder, npmLockfileExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NugetProjectDetector nugetProjectBomTool(final DetectorEnvironment environment) {
        return new NugetProjectDetector(environment, directoryManager, detectFileFinder, nugetInspectorManager(), nugetInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmShrinkwrapDetector npmShrinkwrapBomTool(final DetectorEnvironment environment) {
        return new NpmShrinkwrapDetector(environment, detectFileFinder, npmLockfileExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NugetSolutionDetector nugetSolutionBomTool(final DetectorEnvironment environment) {
        return new NugetSolutionDetector(environment, detectFileFinder, nugetInspectorManager(), nugetInspectorExtractor(), directoryManager);
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PackratLockDetector packratLockBomTool(final DetectorEnvironment environment) {
        return new PackratLockDetector(environment, detectFileFinder, packratLockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PearCliDetector pearCliBomTool(final DetectorEnvironment environment) {
        return new PearCliDetector(environment, detectFileFinder, cacheableExecutableFinder, pearCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PipenvDetector pipenvBomTool(final DetectorEnvironment environment) {
        return new PipenvDetector(environment, detectFileFinder, pythonExecutableFinder(), pipenvExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PipInspectorDetector pipInspectorBomTool(final DetectorEnvironment environment) {
        //final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None);
        return new PipInspectorDetector(environment, detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None), detectFileFinder, pythonExecutableFinder(), pipInspectorManager(),
            pipInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PodlockDetector podLockBomTool(final DetectorEnvironment environment) {
        return new PodlockDetector(environment, detectFileFinder, podlockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public RebarDetector rebarBomTool(final DetectorEnvironment environment) {
        return new RebarDetector(environment, detectFileFinder, cacheableExecutableFinder, rebarExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public SbtResolutionCacheDetector sbtResolutionCacheBomTool(final DetectorEnvironment environment) {
        return new SbtResolutionCacheDetector(environment, detectFileFinder, sbtResolutionCacheExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public YarnLockDetector yarnLockBomTool(final DetectorEnvironment environment) {
        return new YarnLockDetector(environment, detectFileFinder, cacheableExecutableFinder, yarnLockExtractor());
    }
}
