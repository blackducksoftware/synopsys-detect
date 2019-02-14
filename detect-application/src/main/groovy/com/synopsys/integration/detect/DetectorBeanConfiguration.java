/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect;

import javax.xml.parsers.DocumentBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.configuration.ConnectionManager;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectorOptionFactory;
import com.synopsys.integration.detect.detector.DetectorFactory;
import com.synopsys.integration.detect.util.executable.CacheableExecutableFinder;
import com.synopsys.integration.detect.util.executable.ExecutableFinder;
import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.file.AirGapManager;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

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

    @Bean
    public DetectorOptionFactory detectorOptionFactory() {
        return new DetectorOptionFactory(detectConfiguration);
    }

    //DetectorFactory
    //This is the ONLY class that should be taken from the Configuration manually.
    //Detectors should be accessed using the DetectorFactory which will create them through Spring.

    @Bean
    public DetectorFactory detectorFactory() {
        return new DetectorFactory();
    }

    //Detector-Only Dependencies
    //All detector support classes. These are classes not actually used outside of the bom tools but are necessary for some bom tools.
/* TODO: Resurrect
    @Bean
    public BazelExtractor bazelExtractor() {
        BazelQueryXmlOutputParser parse = new BazelQueryXmlOutputParser(new XPathParser());
        BazelExternalIdExtractionSimpleRules rules = new BazelExternalIdExtractionSimpleRules(detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_TARGET, PropertyAuthority.None));
        BazelCodeLocationBuilder codeLocationGenerator = new BazelCodeLocationBuilder(externalIdFactory);
        BazelExternalIdExtractionFullRuleJsonProcessor bazelExternalIdExtractionFullRuleJsonProcessor = new BazelExternalIdExtractionFullRuleJsonProcessor(gson);
        return new BazelExtractor(detectConfiguration, executableRunner, parse, rules, codeLocationGenerator, bazelExternalIdExtractionFullRuleJsonProcessor);
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
    public BazelExecutableFinder bazelExecutableFinder() {
        return new BazelExecutableFinder(executableRunner, directoryManager, executableFinder, detectConfiguration);
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

    //BomTools
    //Should be scoped to Prototype so a new Detector is created every time one is needed.
    //Should only be accessed through the DetectorFactory.

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public BazelDetector bazelDetector(final DetectorEnvironment environment) {
        return new BazelDetector(environment, bazelExtractor(), bazelExecutableFinder(), detectConfiguration);
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public BitbakeDetector bitbakeBomTool(final DetectorEnvironment environment) {
        return new BitbakeDetector(environment, detectFileFinder, detectorOptionFactory().createBitbakeDetectorOptions(), bitbakeExtractor(), cacheableExecutableFinder);
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ClangDetector clangBomTool(final DetectorEnvironment environment) {
        return new ClangDetector(environment, executableRunner, detectFileFinder, clangLinuxPackageManagers(), clangExtractor(), detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None), clangLinuxPackageManager());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ComposerLockDetector composerLockBomTool(final DetectorEnvironment environment) {
        return new ComposerLockDetector(environment, detectFileFinder, composerLockExtractor());
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
    public MavenPomDetectable mavenPomBomTool(final DetectorEnvironment environment) {
        return new MavenPomDetectable(environment, detectFileFinder, mavenExecutableFinder(), mavenCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public MavenPomWrapperDetectable mavenPomWrapperBomTool(final DetectorEnvironment environment) {
        return new MavenPomWrapperDetectable(environment, detectFileFinder, mavenExecutableFinder(), mavenCliExtractor());
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
    public PipInspectorDetectable pipInspectorBomTool(final DetectorEnvironment environment) {
        //final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None);
        return new PipInspectorDetectable(environment, detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None), detectFileFinder, pythonExecutableFinder(), pipInspectorManager(),
            pipInspectorExtractor());
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
    public YarnLockDetectable yarnLockBomTool(final DetectorEnvironment environment) {
        return new YarnLockDetectable(environment, detectFileFinder, cacheableExecutableFinder, yarnLockExtractor());
    }
    */
}
