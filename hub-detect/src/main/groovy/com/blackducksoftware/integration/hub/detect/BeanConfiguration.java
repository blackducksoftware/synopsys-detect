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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory;
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper;
import com.blackducksoftware.integration.hub.bdio.BdioTransformer;
import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.Apk;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.CLangExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.CommandStringExecutor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.DependencyFileManager;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.Dpkg;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.PkgMgr;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.Rpm;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.PodlockParser;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cpan.CpanListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.cran.PackratPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties;
import com.blackducksoftware.integration.hub.detect.bomtool.go.DepPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.hex.RebarExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfileExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmLockfilePackager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetConfig;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.ComposerLockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.PackagistParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearCliExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pear.PearDependencyFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorTreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipenvGraphParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonExecutableFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.SbtResolutionCacheExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnListParser;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnLockExtractor;
import com.blackducksoftware.integration.hub.detect.configuration.AdditionalPropertyConfig;
import com.blackducksoftware.integration.hub.detect.configuration.ConfigurationManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.factory.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.help.ArgumentStateParser;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.mode.DefaultInteractiveMode;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.DetectProjectManager;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BomCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DockerCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DockerScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.ScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionManager;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionReporter;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.hub.BdioUploader;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.OfflineScanner;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.project.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.project.BomToolNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.blackducksoftware.integration.hub.detect.workflow.summary.DetectSummaryManager;
import com.blackducksoftware.integration.hub.detect.workflow.summary.StatusSummaryProvider;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;

@org.springframework.context.annotation.Configuration
public class BeanConfiguration {
    private final ConfigurableEnvironment configurableEnvironment;

    @Autowired
    public BeanConfiguration(final ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public SimpleBdioFactory simpleBdioFactory() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        return new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer, externalIdFactory(), gson());
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
    public Configuration configuration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(BeanConfiguration.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    @Bean
    public DocumentBuilder xmlDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

    @Bean
    public DetectInfo detectInfo() {
        return new DetectInfo();
    }

    @Bean
    public HelpPrinter helpPrinter() {
        return new HelpPrinter();
    }

    @Bean
    public TildeInPathResolver tildeInPathResolver() {
        return new TildeInPathResolver(detectInfo());
    }

    @Bean
    public DetectConfigWrapper detectConfigWrapper() {
        return new DetectConfigWrapper(configurableEnvironment);
    }

    @Bean
    public ConfigurationManager configurationManager() {
        return new ConfigurationManager(tildeInPathResolver(), detectConfigWrapper());
    }

    @Bean
    public DetectOptionManager detectOptionManager() {
        return new DetectOptionManager(detectConfigWrapper());
    }

    @Bean
    public HelpHtmlWriter helpHtmlWriter() {
        return new HelpHtmlWriter(detectOptionManager(), configuration());
    }

    @Bean
    public ArgumentStateParser argumentStateParser() {
        return new ArgumentStateParser();
    }

    @Bean
    public DefaultInteractiveMode defaultInteractiveMode() {
        return new DefaultInteractiveMode(hubServiceWrapper(), detectOptionManager());
    }

    @Bean
    public InteractiveManager interactiveManager() {
        return new InteractiveManager(detectOptionManager(), defaultInteractiveMode());
    }

    @Bean
    public AdditionalPropertyConfig additionalPropertyConfig() {
        return new AdditionalPropertyConfig(configurableEnvironment);
    }

    @Bean
    public HubServiceWrapper hubServiceWrapper() {
        return new HubServiceWrapper(detectConfigWrapper(), additionalPropertyConfig());
    }

    @Bean
    public DetectFileFinder detectFileFinder() {
        return new DetectFileFinder();
    }

    @Bean
    public DetectFileManager detectFileManager() {
        return new DetectFileManager(detectConfigWrapper());
    }

    @Bean
    public ExecutableRunner executableRunner() {
        return new ExecutableRunner(detectConfigWrapper());
    }

    @Bean
    public ExecutableManager executableManager() {
        return new ExecutableManager(detectFileFinder(), detectInfo());
    }

    @Bean
    public BomCodeLocationNameService bomCodeLocationNameService() {
        return new BomCodeLocationNameService(detectFileFinder());
    }

    @Bean
    public DockerCodeLocationNameService dockerCodeLocationNameService() {
        return new DockerCodeLocationNameService(detectFileFinder());
    }

    @Bean
    public DockerScanCodeLocationNameService dockerScanCodeLocationNameService() {
        return new DockerScanCodeLocationNameService(detectFileFinder());
    }

    @Bean
    public ScanCodeLocationNameService scanCodeLocationNameService() {
        return new ScanCodeLocationNameService(detectFileFinder());
    }

    @Bean
    public CodeLocationNameManager codeLocationNameManager() {
        return new CodeLocationNameManager(detectConfigWrapper(), bomCodeLocationNameService(), dockerCodeLocationNameService(), scanCodeLocationNameService(), dockerScanCodeLocationNameService());
    }

    @Bean
    public SearchSummaryReporter searchSummaryReporter() {
        return new SearchSummaryReporter();
    }

    @Bean
    public PhoneHomeManager phoneHomeManager() {
        return new PhoneHomeManager(detectInfo(), gson(), additionalPropertyConfig());
    }

    @Bean
    public BomToolFactory bomToolFactory() throws ParserConfigurationException {
        return new BomToolFactory(detectConfigWrapper(), detectFileFinder(), standardExecutableFinder(), cLangExtractor(), composerLockExtractor(), condaCliExtractor(), cpanCliExtractor(), dockerExtractor(), dockerInspectorManager(),
                gemlockExtractor(), goDepExtractor(), goInspectorManager(), goVndrExtractor(), gradleExecutableFinder(), gradleInspectorExtractor(), gradleInspectorManager(), mavenCliExtractor(), mavenExecutableFinder(), npmCliExtractor(),
                npmExecutableFinder(), npmLockfileExtractor(), nugetInspectorExtractor(), nugetInspectorManager(), packratLockExtractor(), pearCliExtractor(), pipInspectorExtractor(), pipInspectorManager(), pipenvExtractor(),
                podlockExtractor(), pythonExecutableFinder(), rebarExtractor(), sbtResolutionCacheExtractor(), yarnLockExtractor());
    }

    @Bean
    public BomToolSearchProvider bomToolSearchProvider() throws ParserConfigurationException {
        return new BomToolSearchProvider(bomToolFactory());
    }

    @Bean
    public SearchManager searchManager() throws ParserConfigurationException {
        return new SearchManager(searchSummaryReporter(), bomToolSearchProvider(), phoneHomeManager(), detectConfigWrapper());
    }

    @Bean
    public PreparationSummaryReporter preparationSummaryReporter() {
        return new PreparationSummaryReporter();
    }

    @Bean
    public ExtractionSummaryReporter extractionSummaryReporter() {
        return new ExtractionSummaryReporter();
    }

    @Bean
    public ExtractionReporter extractionReporter() {
        return new ExtractionReporter();
    }

    @Bean
    public ExtractionManager extractionManager() {
        return new ExtractionManager(preparationSummaryReporter(), extractionReporter());
    }

    @Bean
    public DetectCodeLocationManager detectCodeLocationManager() {
        return new DetectCodeLocationManager(codeLocationNameManager(), detectConfigWrapper());
    }

    @Bean
    public BdioManager bdioManager() {
        return new BdioManager(detectInfo(), simpleBdioFactory(), integrationEscapeUtil(), codeLocationNameManager(), detectConfigWrapper());
    }

    @Bean
    public BomToolNameVersionDecider bomToolNameVersionDecider() {
        return new BomToolNameVersionDecider();
    }

    @Bean
    public DetectProjectManager detectProjectManager() throws ParserConfigurationException {
        return new DetectProjectManager(searchManager(), extractionManager(), detectCodeLocationManager(), bdioManager(), extractionSummaryReporter(), bomToolNameVersionDecider(), detectConfigWrapper());
    }

    @Bean
    public OfflineScanner offlineScanner() {
        return new OfflineScanner(gson(), detectConfigWrapper());
    }

    @Bean
    public HubSignatureScanner hubSignatureScanner() {
        return new HubSignatureScanner(detectFileManager(), detectFileFinder(), offlineScanner(), codeLocationNameManager(), detectConfigWrapper());
    }

    @Bean
    public DetectSummaryManager statusSummary() throws ParserConfigurationException {
        final List<StatusSummaryProvider<?>> statusSummaryProviders = new ArrayList<>();
        statusSummaryProviders.add(detectProjectManager());
        statusSummaryProviders.add(hubSignatureScanner());

        return new DetectSummaryManager(statusSummaryProviders);
    }

    @Bean
    public PolicyChecker policyChecker() {
        return new PolicyChecker(detectConfigWrapper());
    }

    @Bean
    public BdioUploader bdioUploader() {
        return new BdioUploader(detectConfigWrapper());
    }

    @Bean
    public HubManager hubManager() {
        return new HubManager(bdioUploader(), codeLocationNameManager(), detectConfigWrapper(), hubServiceWrapper(), hubSignatureScanner(), policyChecker());
    }

    @Bean
    public StandardExecutableFinder standardExecutableFinder() {
        return new StandardExecutableFinder(executableManager(), detectConfigWrapper());
    }

    @Bean
    public CommandStringExecutor commandStringExecutor() {
        return new CommandStringExecutor(executableRunner());
    }

    @Bean
    public DependencyFileManager dependencyFileManager() {
        return new DependencyFileManager();
    }

    @Bean
    public Apk apk() {
        return new Apk();
    }

    @Bean
    public Dpkg dpkg() {
        return new Dpkg();
    }

    @Bean
    public Rpm rpm() {
        return new Rpm();
    }

    @Bean
    public CLangExtractor cLangExtractor() {
        final List<PkgMgr> pkgMgrs = new ArrayList<>();
        pkgMgrs.add(apk());
        pkgMgrs.add(dpkg());
        pkgMgrs.add(rpm());

        return new CLangExtractor(pkgMgrs, externalIdFactory(), commandStringExecutor(), dependencyFileManager(), detectFileManager());
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
        return new CondaCliExtractor(condaListParser(), externalIdFactory(), executableRunner(), detectConfigWrapper().getProperty(DetectProperty.DETECT_CONDA_ENVIRONMENT_NAME));
    }

    @Bean
    public CpanListParser cpanListParser() {
        return new CpanListParser(externalIdFactory());
    }

    @Bean
    public CpanCliExtractor cpanCliExtractor() {
        return new CpanCliExtractor(cpanListParser(), externalIdFactory(), executableRunner());
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
        return new DockerExtractor(detectFileFinder(), detectFileManager(), dockerProperties(), executableRunner(), bdioTransformer(), externalIdFactory(), gson(), hubSignatureScanner());
    }

    @Bean
    public DockerInspectorManager dockerInspectorManager() {
        return new DockerInspectorManager(detectFileManager(), executableManager(), executableRunner(), detectConfigWrapper());
    }

    @Bean
    public DockerProperties dockerProperties() {
        return new DockerProperties(detectConfigWrapper(), additionalPropertyConfig());
    }

    @Bean
    public GoDepExtractor goDepExtractor() {
        return new GoDepExtractor(depPackager(), externalIdFactory());
    }

    @Bean
    public GoInspectorManager goInspectorManager() {
        return new GoInspectorManager(detectFileManager(), executableManager(), executableRunner(), detectConfigWrapper());
    }

    @Bean
    public GoVndrExtractor goVndrExtractor() {
        return new GoVndrExtractor(externalIdFactory());
    }

    @Bean
    public DepPackager depPackager() {
        return new DepPackager(executableRunner(), externalIdFactory(), detectConfigWrapper().getBooleanProperty(DetectProperty.DETECT_GO_RUN_DEP_INIT));
    }

    @Bean
    public GradleReportParser gradleReportParser() {
        return new GradleReportParser(externalIdFactory());
    }

    @Bean
    public GradleExecutableFinder gradleExecutableFinder() {
        return new GradleExecutableFinder(executableManager(), detectConfigWrapper());
    }

    @Bean
    public GradleInspectorExtractor gradleInspectorExtractor() {
        return new GradleInspectorExtractor(executableRunner(), detectFileFinder(), detectFileManager(), gradleReportParser(), detectConfigWrapper().getProperty(DetectProperty.DETECT_GRADLE_BUILD_COMMAND));
    }

    @Bean
    public GradleInspectorManager gradleInspectorManager() throws ParserConfigurationException {
        return new GradleInspectorManager(detectFileManager(), configuration(), xmlDocumentBuilder(), detectConfigWrapper());
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
        final ExcludedIncludedFilter moduleFilter = new ExcludedIncludedFilter(detectConfigWrapper().getProperty(DetectProperty.DETECT_MAVEN_EXCLUDED_MODULES),
                detectConfigWrapper().getProperty(DetectProperty.DETECT_MAVEN_INCLUDED_MODULES));
        return new MavenCodeLocationPackager(externalIdFactory(), moduleFilter);
    }

    @Bean
    public MavenCliExtractor mavenCliExtractor() {
        return new MavenCliExtractor(executableRunner(), mavenCodeLocationPackager(), detectConfigWrapper().getProperty(DetectProperty.DETECT_MAVEN_BUILD_COMMAND), detectConfigWrapper().getProperty(DetectProperty.DETECT_MAVEN_SCOPE));
    }

    @Bean
    public MavenExecutableFinder mavenExecutableFinder() {
        return new MavenExecutableFinder(executableManager(), detectConfigWrapper());
    }

    @Bean
    public NpmCliDependencyFinder npmCliDependencyFinder() {
        return new NpmCliDependencyFinder(externalIdFactory());
    }

    @Bean
    public NpmLockfilePackager npmLockfilePackager() {
        return new NpmLockfilePackager(gson(), externalIdFactory());
    }

    @Bean
    public NpmCliExtractor npmCliExtractor() {
        return new NpmCliExtractor(executableRunner(), detectFileManager(), npmCliDependencyFinder(), detectConfigWrapper().getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES));
    }

    @Bean
    public NpmLockfileExtractor npmLockfileExtractor() {
        return new NpmLockfileExtractor(npmLockfilePackager(), detectConfigWrapper().getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES));
    }

    @Bean
    public NpmExecutableFinder npmExecutableFinder() {
        return new NpmExecutableFinder(executableManager(), executableRunner(), detectConfigWrapper());
    }

    @Bean
    public NugetInspectorPackager nugetInspectorPackager() {
        return new NugetInspectorPackager(gson(), externalIdFactory());
    }

    @Bean
    public NugetConfig nugetConfig() {
        return new NugetConfig(detectConfigWrapper());
    }

    @Bean
    public NugetInspectorExtractor nugetInspectorExtractor() {
        return new NugetInspectorExtractor(detectFileManager(), nugetInspectorPackager(), executableRunner(), detectFileFinder(), nugetConfig());
    }

    @Bean
    public NugetInspectorManager nugetInspectorManager() {
        return new NugetInspectorManager(detectFileManager(), executableManager(), executableRunner(), detectConfigWrapper());
    }

    @Bean
    public PackagistParser packagistParser() {
        return new PackagistParser(externalIdFactory(), detectConfigWrapper());
    }

    @Bean
    public ComposerLockExtractor composerLockExtractor() {
        return new ComposerLockExtractor(packagistParser());
    }

    @Bean
    public PearDependencyFinder pearDependencyFinder() {
        return new PearDependencyFinder(externalIdFactory(), detectConfigWrapper());
    }

    @Bean
    public PearCliExtractor pearCliExtractor() {
        return new PearCliExtractor(detectFileFinder(), externalIdFactory(), pearDependencyFinder(), executableRunner());
    }

    @Bean
    public PipenvGraphParser pipenvGraphParser() {
        return new PipenvGraphParser(externalIdFactory());
    }

    @Bean
    public PipenvExtractor pipenvExtractor() {
        return new PipenvExtractor(executableRunner(), pipenvGraphParser(), detectConfigWrapper().getProperty(DetectProperty.DETECT_PIP_PROJECT_NAME), detectConfigWrapper().getProperty(DetectProperty.DETECT_PIP_PROJECT_VERSION_NAME));
    }

    @Bean
    public PipInspectorTreeParser pipInspectorTreeParser() {
        return new PipInspectorTreeParser(externalIdFactory());
    }

    @Bean
    public PipInspectorExtractor pipInspectorExtractor() {
        return new PipInspectorExtractor(executableRunner(), pipInspectorTreeParser(), detectConfigWrapper().getProperty(DetectProperty.DETECT_PIP_PROJECT_NAME));
    }

    @Bean
    public PipInspectorManager pipInspectorManager() {
        return new PipInspectorManager(detectFileManager());
    }

    @Bean
    public PythonExecutableFinder pythonExecutableFinder() {
        return new PythonExecutableFinder(executableManager(), detectConfigWrapper());
    }

    @Bean
    public GemlockExtractor gemlockExtractor() {
        return new GemlockExtractor(externalIdFactory());
    }

    @Bean
    public SbtResolutionCacheExtractor sbtResolutionCacheExtractor() {
        final String included = detectConfigWrapper().getProperty(DetectProperty.DETECT_SBT_INCLUDED_CONFIGURATIONS);
        final String excluded = detectConfigWrapper().getProperty(DetectProperty.DETECT_SBT_EXCLUDED_CONFIGURATIONS);

        final int depth = detectConfigWrapper().getIntegerProperty(DetectProperty.DETECT_SEARCH_DEPTH);

        return new SbtResolutionCacheExtractor(detectFileFinder(), externalIdFactory(), new ExcludedIncludedFilter(excluded, included), depth);
    }

    @Bean
    public YarnListParser yarnListParser() {
        return new YarnListParser();
    }

    @Bean
    public YarnLockExtractor yarnLockExtractor() {
        return new YarnLockExtractor(externalIdFactory(), yarnListParser(), executableRunner(), detectConfigWrapper().getBooleanProperty(DetectProperty.DETECT_YARN_PROD_ONLY));
    }

}
