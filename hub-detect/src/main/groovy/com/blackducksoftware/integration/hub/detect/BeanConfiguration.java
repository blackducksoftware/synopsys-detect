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

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.bitbake.BitbakeBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.bitbake.BitbakeExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.bitbake.BitbakeListTasksParser;
import com.blackducksoftware.integration.hub.detect.bomtool.bitbake.GraphParserTransformer;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ApkPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.ClangLinuxPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.CodeLocationAssembler;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DependenciesListFileManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DpkgPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.RpmPackageManager;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockParser;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerBomToolOptions;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties;
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager;
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
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPomWrapperBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliParser;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfilePackager;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmPackageLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmShrinkwrapBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetProjectBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetSolutionBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.PackagistParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorTreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvGraphParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockBomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockParser;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.boot.DetectRunDependencies;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckBinaryScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BlackDuckSignatureScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.PreparationSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.run.RunManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.blackducksoftware.integration.hub.detect.workflow.shutdown.ExitCodeManager;
import com.blackducksoftware.integration.hub.detect.workflow.shutdown.ExitCodeUtility;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.hub.bdio.BdioNodeFactory;
import com.synopsys.integration.hub.bdio.BdioPropertyHelper;
import com.synopsys.integration.hub.bdio.BdioTransformer;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.log.SilentLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;

import freemarker.template.Configuration;

//@Configuration is used here to allow 'EnableAspectJAutoProxy' because it could not be enabled it otherwise.
//This configuration is NOT loaded when the application starts, but only manually when a DetectRun is needed.
//Spring scanning should not be invoked as this should not be loaded during boot.
@org.springframework.context.annotation.Configuration
public class BeanConfiguration {
    private final DetectRunDependencies detectRunDependencies;

    @Autowired
    public BeanConfiguration(final DetectRunDependencies detectRunDependencies) {
        this.detectRunDependencies = detectRunDependencies;
    }

    //Beans from Boot
    @Bean
    public Gson gson() {
        return detectRunDependencies.gson;
    }

    @Bean
    public EventSystem eventSystem() {
        return detectRunDependencies.eventSystem;
    }

    @Bean
    public DetectConfiguration detectConfiguration() {
        return detectRunDependencies.detectConfiguration;
    }

    @Bean
    public DetectRun detectRun() {
        return detectRunDependencies.detectRun;
    }

    @Bean
    public DiagnosticManager diagnosticManager() {
        return detectRunDependencies.diagnosticManager;
    }

    @Bean
    public DetectInfo detectInfo() {
        return detectRunDependencies.detectInfo;
    }

    @Bean
    public DocumentBuilder xmlDocumentBuilder() {
        return detectRunDependencies.documentBuilder;
    }

    @Bean
    public HubServiceManager hubServiceManager() {
        return detectRunDependencies.hubServiceManager;
    }

    @Bean
    public Configuration configuration() {
        return detectRunDependencies.configuration;
    }

    @Bean
    public PhoneHomeManager phoneHomeManager() {
        return detectRunDependencies.phoneHomeManager;
    }

    //Regular Beans
    @Bean
    public DetectConfigurationFactory detectConfigurationFactory() {
        return new DetectConfigurationFactory(detectConfiguration());
    }

    @Bean
    public SimpleBdioFactory simpleBdioFactory() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        return new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer, externalIdFactory(), gson());
    }

    @Bean
    public ReportManager reportManager() {
        return new ReportManager(eventSystem(), detectRunDependencies.phoneHomeManager, diagnosticManager(), preparationSummaryReporter(), extractionSummaryReporter(), searchSummaryReporter());
    }

    @Bean
    public BdioTransformer bdioTransformer() {
        return new BdioTransformer();
    }

    @Bean
    public ExternalIdFactory externalIdFactory() {
        return new ExternalIdFactory();
    }

    @Bean
    public IntegrationEscapeUtil integrationEscapeUtil() {
        return new IntegrationEscapeUtil();
    }

    @Bean
    public DetectFileFinder detectFileFinder() {
        return new DetectFileFinder();
    }

    @Bean
    public DirectoryManager directoryManager() {
        return detectRunDependencies.directoryManager;
        //return new DirectoryManager(detectConfiguration(), detectRun(), diagnosticManager());
    }

    @Bean
    public AirGapManager airGapManager() {
        return detectRunDependencies.airGapManager;
        //return new DirectoryManager(detectConfiguration(), detectRun(), diagnosticManager());
    }

    @Bean
    public ExecutableRunner executableRunner() {
        return new ExecutableRunner();
    }

    @Bean
    public ExecutableManager executableManager() {
        return new ExecutableManager(detectFileFinder(), detectInfo());
    }

    @Bean
    public CodeLocationNameService codeLocationNameService() {
        return new CodeLocationNameService(detectFileFinder());
    }

    @Bean
    public CodeLocationService codeLocationService() {
        //TODO: Figure this out...
        return new CodeLocationService(hubServiceManager().createHubService(), new SilentLogger());
    }

    @Bean
    public CodeLocationNameManager codeLocationNameManager() {
        return new CodeLocationNameManager(detectConfiguration(), codeLocationNameService());
    }

    @Bean
    public SearchSummaryReporter searchSummaryReporter() {
        return new SearchSummaryReporter();
    }

    @Bean
    public BomToolFactory bomToolFactory() throws ParserConfigurationException {
        return new BomToolFactory();
        //return new BomToolFactory(detectConfiguration(), detectFileFinder(), executableRunner(), extractorFactory(), executableFinderFactory(), inspectorManagerFactory());
    }

    @Bean
    public BomToolSearchProvider bomToolSearchProvider() throws ParserConfigurationException {
        return new BomToolSearchProvider(bomToolFactory());
    }

    @Bean
    public BomToolSearchEvaluator bomToolSearchEvaluator() throws ParserConfigurationException {
        return new BomToolSearchEvaluator();
    }

    @Bean
    public SearchManager searchManager() {
        try {
            return new SearchManager(detectConfigurationFactory().createSearchOptions(directoryManager().getSourceDirectory()), bomToolSearchProvider(), bomToolSearchEvaluator(), eventSystem());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PreparationSummaryReporter preparationSummaryReporter() {
        return new PreparationSummaryReporter();
    }

    @Bean
    public ExtractionSummaryReporter extractionSummaryReporter() {
        return new ExtractionSummaryReporter();
    }

    public ExtractionManager extractionManager() {
        return new ExtractionManager();
    }

    @Bean
    public DetectCodeLocationManager detectCodeLocationManager() {
        return new DetectCodeLocationManager(codeLocationNameManager(), detectConfiguration(), directoryManager());
    }

    @Bean
    public BdioManager bdioManager() {
        return new BdioManager(detectInfo(), simpleBdioFactory(), integrationEscapeUtil(), codeLocationNameManager(), detectConfiguration(), codeLocationManager(), directoryManager());
    }

    @Bean
    public DetectCodeLocationManager codeLocationManager() {
        return new DetectCodeLocationManager(codeLocationNameManager(), detectConfiguration(), directoryManager());
    }

    @Bean
    public BomToolNameVersionDecider bomToolNameVersionDecider() {
        return new BomToolNameVersionDecider();
    }

    @Bean
    public BlackDuckSignatureScanner blackDuckSignatureScanner() {
        return new BlackDuckSignatureScanner(directoryManager(), detectFileFinder(), codeLocationNameManager(), detectConfiguration(), eventSystem());
    }

    @Bean
    public DetectStatusManager detectStatusManager() {
        return new DetectStatusManager(eventSystem());
    }

    @Bean
    public PolicyChecker policyChecker() {
        return new PolicyChecker(detectConfiguration());
    }

    @Bean
    public DetectBdioUploadService bdioUploader() {
        return new DetectBdioUploadService(detectConfiguration(), eventSystem(), codeLocationService());
    }

    @Bean
    public BlackDuckBinaryScanner blackDuckBinaryScanner() {
        return new BlackDuckBinaryScanner(codeLocationNameService());
    }

    @Bean
    public HubManager hubManager() {
        return new HubManager(bdioUploader(), codeLocationNameManager(), detectConfiguration(), hubServiceManager(), blackDuckSignatureScanner(), policyChecker(), blackDuckBinaryScanner());
    }

    @Bean
    public StandardExecutableFinder standardExecutableFinder() {
        return new StandardExecutableFinder(directoryManager(), executableManager(), detectConfiguration());
    }

    @Bean
    public DependenciesListFileManager clangDependenciesListFileParser() {
        return new DependenciesListFileManager(executableRunner());
    }

    @Bean
    public CodeLocationAssembler codeLocationAssembler() {
        return new CodeLocationAssembler(externalIdFactory());
    }

    @Bean
    public ClangExtractor clangExtractor() {
        return new ClangExtractor(executableRunner(), gson(), detectFileFinder(), directoryManager(), clangDependenciesListFileParser(), codeLocationAssembler());
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
        return new PodlockParser(externalIdFactory());
    }

    @Bean
    public PodlockExtractor podlockExtractor() {
        return new PodlockExtractor(podlockParser(), externalIdFactory());
    }

    @Bean
    public CondaListParser condaListParser() {
        return new CondaListParser(gson(), externalIdFactory());
    }

    @Bean
    public CondaCliExtractor condaCliExtractor() {
        return new CondaCliExtractor(condaListParser(), externalIdFactory(), executableRunner(), detectConfiguration(), directoryManager());
    }

    @Bean
    public CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory());
    }

    @Bean
    public CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), externalIdFactory(), executableRunner(), directoryManager());
    }

    @Bean
    public PackratPackager packratPackager() {
        return new PackratPackager(externalIdFactory());
    }

    @Bean
    public PackratLockExtractor packratLockExtractor() {
        return new PackratLockExtractor(packratPackager(), externalIdFactory(), detectFileFinder());
    }

    @Bean
    public DockerExtractor dockerExtractor() {
        return new DockerExtractor(detectFileFinder(), directoryManager(), dockerProperties(), executableRunner(), bdioTransformer(), externalIdFactory(), gson(), blackDuckSignatureScanner());
    }

    @Bean
    public ConnectionManager detectConfigurationUtility() {
        return new ConnectionManager(detectConfiguration());
    }

    @Bean
    public DockerInspectorManager dockerInspectorManager() {
        return new DockerInspectorManager(directoryManager(), airGapManager(), detectFileFinder(), detectConfiguration(), connectionManager(), mavenMetadataService());
    }

    @Bean
    public ConnectionManager connectionManager() {
        return new ConnectionManager(detectConfiguration());
    }

    @Bean
    public MavenMetadataService mavenMetadataService() {
        return new MavenMetadataService(xmlDocumentBuilder(), detectConfigurationUtility());
    }

    @Bean
    public DockerProperties dockerProperties() {
        return new DockerProperties(detectConfiguration());
    }

    @Bean
    public GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(depPackager(), externalIdFactory());
    }

    @Bean
    public GoInspectorManager goInspectorManager() {
        return new GoInspectorManager(directoryManager(), executableManager(), executableRunner(), detectConfiguration());
    }

    @Bean
    public GoVndrExtractor goVndrExtractor() {
        return new GoVndrExtractor(externalIdFactory());
    }

    @Bean
    public DepPackager depPackager() {
        return new DepPackager(executableRunner(), externalIdFactory(), detectConfiguration());
    }

    @Bean
    public GradleReportParser gradleReportParser() {
        return new GradleReportParser(externalIdFactory());
    }

    @Bean
    public GradleExecutableFinder gradleExecutableFinder() {
        return new GradleExecutableFinder(executableManager(), detectConfiguration());
    }

    @Bean
    public GradleInspectorExtractor gradleInspectorExtractor() {
        return new GradleInspectorExtractor(executableRunner(), detectFileFinder(), directoryManager(), gradleReportParser(), detectConfiguration());
    }

    @Bean
    public GradleInspectorManager gradleInspectorManager() throws ParserConfigurationException {
        return new GradleInspectorManager(directoryManager(), airGapManager(), configuration(), detectConfiguration(), mavenMetadataService());
    }

    @Bean
    public Rebar3TreeParser rebar3TreeParser() {
        return new Rebar3TreeParser(externalIdFactory());
    }

    @Bean
    public RebarExtractor rebarExtractor() {
        return new RebarExtractor(executableRunner(), rebar3TreeParser());
    }

    @Bean
    public MavenCodeLocationPackager mavenCodeLocationPackager() {
        return new MavenCodeLocationPackager(externalIdFactory());
    }

    @Bean
    public MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner(), mavenCodeLocationPackager(), detectConfiguration());
    }

    @Bean
    public MavenExecutableFinder mavenExecutableFinder() {
        return new MavenExecutableFinder(executableManager(), detectConfiguration());
    }

    @Bean
    public NpmCliParser npmCliDependencyFinder() {
        return new NpmCliParser(externalIdFactory());
    }

    @Bean
    public NpmLockfilePackager npmLockfilePackager() {
        return new NpmLockfilePackager(gson(), externalIdFactory());
    }

    @Bean
    public NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner(), npmCliDependencyFinder(), detectConfiguration());
    }

    @Bean
    public NpmLockfileExtractor npmLockfileExtractor() {
        return new NpmLockfileExtractor(npmLockfilePackager(), detectConfiguration());
    }

    @Bean
    public NpmExecutableFinder npmExecutableFinder() {
        return new NpmExecutableFinder(directoryManager(), executableManager(), executableRunner(), detectConfiguration());
    }

    @Bean
    public NugetInspectorPackager nugetInspectorPackager() {
        return new NugetInspectorPackager(gson(), externalIdFactory());
    }

    @Bean
    public NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(directoryManager(), nugetInspectorPackager(), executableRunner(), detectFileFinder(), detectConfiguration());
    }

    @Bean
    public NugetInspectorManager nugetInspectorManager() {
        return new NugetInspectorManager(directoryManager(), executableManager(), executableRunner(), detectConfiguration(), airGapManager());
    }

    @Bean
    public PackagistParser packagistParser() {
        return new PackagistParser(externalIdFactory(), detectConfiguration());
    }

    @Bean
    public ComposerLockExtractor composerLockExtractor() {
        return new ComposerLockExtractor(packagistParser());
    }

    @Bean
    public PearParser pearDependencyFinder() {
        return new PearParser(externalIdFactory(), detectConfiguration());
    }

    @Bean
    public PearCliExtractor pearCliExtractor() {
        return new PearCliExtractor(detectFileFinder(), externalIdFactory(), pearDependencyFinder(), executableRunner(), directoryManager());
    }

    @Bean
    public PipenvGraphParser pipenvGraphParser() {
        return new PipenvGraphParser(externalIdFactory());
    }

    @Bean
    public PipenvExtractor pipenvExtractor() {
        return new PipenvExtractor(executableRunner(), pipenvGraphParser(), detectConfiguration());
    }

    @Bean
    public PipInspectorTreeParser pipInspectorTreeParser() {
        return new PipInspectorTreeParser(externalIdFactory());
    }

    @Bean
    public PipInspectorExtractor pipInspectorExtractor() {
        return new PipInspectorExtractor(executableRunner(), pipInspectorTreeParser(), detectConfiguration());
    }

    @Bean
    public PipInspectorManager pipInspectorManager() {
        return new PipInspectorManager(directoryManager());
    }

    @Bean
    public PythonExecutableFinder pythonExecutableFinder() {
        return new PythonExecutableFinder(executableManager(), detectConfiguration());
    }

    @Bean
    public GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory());
    }

    @Bean
    public SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        return new SbtResolutionCacheExtractor(detectFileFinder(), externalIdFactory(), detectConfiguration());
    }

    @Bean
    public YarnListParser yarnListParser() {
        return new YarnListParser(externalIdFactory(), yarnLockParser());
    }

    @Bean
    public YarnLockParser yarnLockParser() {
        return new YarnLockParser();
    }

    @Bean
    public YarnLockExtractor yarnLockExtractor() {
        return new YarnLockExtractor(externalIdFactory(), yarnListParser(), executableRunner(), detectConfiguration());
    }

    @Bean
    public GraphParserTransformer graphParserTransformer() {
        return new GraphParserTransformer();
    }

    @Bean
    public BitbakeExtractor bitbakeExtractor() {
        return new BitbakeExtractor(executableManager(), executableRunner(), detectConfiguration(), directoryManager(), detectFileFinder(), graphParserTransformer(), bitbakeListTasksParser());
    }

    @Bean
    public BitbakeListTasksParser bitbakeListTasksParser() {
        return new BitbakeListTasksParser();
    }

    @Bean
    public ProjectNameVersionManager projectVersionManager() {
        ProjectNameVersionOptions options = detectConfigurationFactory().createProjectNameVersionOptions(directoryManager().getSourceDirectory().getName());
        return new ProjectNameVersionManager(options, bomToolNameVersionDecider());
    }

    @Bean
    public BomToolManager bomToolManager() {
        return new BomToolManager(searchManager(), extractionManager(), preparationManager(), eventSystem());
    }

    @Bean
    public PreparationManager preparationManager() {
        return new PreparationManager(eventSystem());
    }

    @Bean
    public RunManager runManager() {
        return new RunManager(phoneHomeManager(), detectConfiguration(), bomToolManager(), projectVersionManager(), detectProjectService(), detectCodeLocationUnmapService(), bdioManager(), detectBdioUploadService(), hubManager(),
            hubServiceManager().createHubService());
    }

    @Bean
    public DetectProjectService detectProjectService() {
        DetectProjectServiceOptions options = detectConfigurationFactory().createDetectProjectServiceOptions();
        return new DetectProjectService(hubServiceManager(), options);
    }

    @Bean
    public DetectCodeLocationUnmapService detectCodeLocationUnmapService() {
        return new DetectCodeLocationUnmapService(hubServiceManager().createHubService(), codeLocationService());
    }

    @Bean
    public DetectBdioUploadService detectBdioUploadService() {
        return new DetectBdioUploadService(detectConfiguration(), eventSystem(), codeLocationService());
    }

    @Bean
    public ExitCodeManager exitCodeManager() {
        return new ExitCodeManager(eventSystem(), new ExitCodeUtility());
    }

    //BomTools
    //Should be scoped to Prototype so a new BomTool is created every time one is needed.
    //The BomTool factory currently uses Spring to create the bom tools.
    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public BitbakeBomTool bitbakeBomTool(final BomToolEnvironment environment) {
        return new BitbakeBomTool(environment, detectFileFinder(), detectConfiguration(), bitbakeExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ClangBomTool clangBomTool(final BomToolEnvironment environment) {
        return new ClangBomTool(environment, executableRunner(), detectFileFinder(), clangLinuxPackageManagers(), clangExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public ComposerLockBomTool composerLockBomTool(final BomToolEnvironment environment) {
        return new ComposerLockBomTool(environment, detectFileFinder(), composerLockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public CondaCliBomTool condaBomTool(final BomToolEnvironment environment) {
        return new CondaCliBomTool(environment, detectFileFinder(), standardExecutableFinder(), condaCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public CpanCliBomTool cpanCliBomTool(final BomToolEnvironment environment) {
        return new CpanCliBomTool(environment, detectFileFinder(), standardExecutableFinder(), cpanCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public DockerBomTool dockerBomTool(final BomToolEnvironment environment, DockerBomToolOptions bomToolOptions) {
        return new DockerBomTool(environment, dockerInspectorManager(), standardExecutableFinder(), dockerExtractor(), bomToolOptions);
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GemlockBomTool gemlockBomTool(final BomToolEnvironment environment) {
        return new GemlockBomTool(environment, detectFileFinder(), gemlockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoCliBomTool goCliBomTool(final BomToolEnvironment environment) {
        return new GoCliBomTool(environment, detectFileFinder(), standardExecutableFinder(), goInspectorManager(), goDepExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoLockBomTool goLockBomTool(final BomToolEnvironment environment) {
        return new GoLockBomTool(environment, detectFileFinder(), standardExecutableFinder(), goInspectorManager(), goDepExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GoVndrBomTool goVndrBomTool(final BomToolEnvironment environment) {
        return new GoVndrBomTool(environment, detectFileFinder(), goVndrExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public GradleInspectorBomTool gradleInspectorBomTool(final BomToolEnvironment environment) throws ParserConfigurationException {
        return new GradleInspectorBomTool(environment, detectFileFinder(), gradleExecutableFinder(), gradleInspectorManager(), gradleInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public MavenPomBomTool mavenPomBomTool(final BomToolEnvironment environment) {
        return new MavenPomBomTool(environment, detectFileFinder(), mavenExecutableFinder(), mavenCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public MavenPomWrapperBomTool mavenPomWrapperBomTool(final BomToolEnvironment environment) {
        return new MavenPomWrapperBomTool(environment, detectFileFinder(), mavenExecutableFinder(), mavenCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmCliBomTool npmCliBomTool(final BomToolEnvironment environment) {
        return new NpmCliBomTool(environment, detectFileFinder(), npmExecutableFinder(), npmCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmPackageLockBomTool npmPackageLockBomTool(final BomToolEnvironment environment) {
        return new NpmPackageLockBomTool(environment, detectFileFinder(), npmLockfileExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NugetProjectBomTool nugetProjectBomTool(final BomToolEnvironment environment) {
        return new NugetProjectBomTool(environment, detectFileFinder(), nugetInspectorManager(), nugetInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NpmShrinkwrapBomTool npmShrinkwrapBomTool(final BomToolEnvironment environment) {
        return new NpmShrinkwrapBomTool(environment, detectFileFinder(), npmLockfileExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public NugetSolutionBomTool nugetSolutionBomTool(final BomToolEnvironment environment) {
        return new NugetSolutionBomTool(environment, detectFileFinder(), nugetInspectorManager(), nugetInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PackratLockBomTool packratLockBomTool(final BomToolEnvironment environment) {
        return new PackratLockBomTool(environment, detectFileFinder(), packratLockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PearCliBomTool pearCliBomTool(final BomToolEnvironment environment) {
        return new PearCliBomTool(environment, detectFileFinder(), standardExecutableFinder(), pearCliExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PipenvBomTool pipenvBomTool(final BomToolEnvironment environment) {
        return new PipenvBomTool(environment, detectFileFinder(), pythonExecutableFinder(), pipenvExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PipInspectorBomTool pipInspectorBomTool(final BomToolEnvironment environment) {
        //final String requirementsFile = detectConfiguration.getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None);
        return new PipInspectorBomTool(environment, detectConfiguration().getProperty(DetectProperty.DETECT_PIP_REQUIREMENTS_PATH, PropertyAuthority.None), detectFileFinder(), pythonExecutableFinder(), pipInspectorManager(),
            pipInspectorExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public PodlockBomTool podLockBomTool(final BomToolEnvironment environment) {
        return new PodlockBomTool(environment, detectFileFinder(), podlockExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public RebarBomTool rebarBomTool(final BomToolEnvironment environment) {
        return new RebarBomTool(environment, detectFileFinder(), standardExecutableFinder(), rebarExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public SbtResolutionCacheBomTool sbtResolutionCacheBomTool(final BomToolEnvironment environment) {
        return new SbtResolutionCacheBomTool(environment, detectFileFinder(), sbtResolutionCacheExtractor());
    }

    @Bean
    @Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
    public YarnLockBomTool yarnLockBomTool(final BomToolEnvironment environment) {
        return new YarnLockBomTool(environment, detectFileFinder(), standardExecutableFinder(), yarnLockExtractor());
    }
}
