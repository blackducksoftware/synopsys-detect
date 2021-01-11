/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.lifecycle.boot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.property.types.path.TildeInPathResolver;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.configuration.source.SpringConfigurationPropertySource;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.validation.DetectConfigurationValidator;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.interactive.InteractiveModeDecisionTree;
import com.synopsys.integration.detect.interactive.InteractivePropertySourceBuilder;
import com.synopsys.integration.detect.interactive.InteractiveWriter;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.GradleInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorInstaller;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.airgap.DockerAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.GradleAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.NugetAirGapCreator;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsConfigurationService;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticsDecider;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticsDecision;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.util.OperatingSystemType;

import freemarker.template.Configuration;

//Responsible for creating a few classes boot needs
public class DetectBootFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRun detectRun;
    private final DetectInfo detectInfo;
    private final Gson gson;
    private final EventSystem eventSystem;
    private final BlackDuckConnectivityChecker blackDuckConnectivityChecker;

    public DetectBootFactory(DetectRun detectRun, DetectInfo detectInfo, Gson gson, EventSystem eventSystem) {
        this.detectRun = detectRun;
        this.detectInfo = detectInfo;
        this.gson = gson;
        this.eventSystem = eventSystem;

        this.blackDuckConnectivityChecker = new BlackDuckConnectivityChecker();
    }

    public DetectBoot createDetectBoot(ConfigurableEnvironment environment, String[] sourceArgs, DetectContext detectContext) {
        List<PropertySource> propertySources;

        try {
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, false));
        } catch (RuntimeException e) {
            logger.error("An unknown property source was found, detect will still continue.", e);
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, true));
        }

        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);

        return new DetectBoot(this, detectArgumentState, propertySources, detectContext);
    }


    public ObjectMapper createObjectMapper() {
        return BlackDuckServicesFactory.createDefaultObjectMapper();
    }

    public Configuration createFreemarkerConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(Application.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    public DocumentBuilder createXmlDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PathResolver createPathResolver(Boolean resolveTildes) {
        PathResolver pathResolver;

        if (detectInfo.getCurrentOs() != OperatingSystemType.WINDOWS && Boolean.TRUE.equals(resolveTildes)) {
            logger.info("Tilde's will be automatically resolved to USER HOME.");
            pathResolver = new TildeInPathResolver(SystemUtils.USER_HOME);
        } else {
            pathResolver = new SimplePathResolver();
        }

        return pathResolver;
    }

    @Deprecated
    public List<PropertySource> initializePropertySources(ConfigurableEnvironment environment) {
        List<PropertySource> propertySources;
        try {
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, false));
        } catch (RuntimeException e) {
            logger.error("An unknown property source was found, detect will still continue.", e);
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, true));
        }
        return propertySources;
    }

    public Optional<DiagnosticSystem> createDiagnosticSystem(DiagnosticsDecider diagnosticsDecider, PropertyConfiguration detectConfiguration, DirectoryManager directoryManager) {
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(detectConfiguration);
        if (diagnosticsDecision.isConfiguredForDiagnostic) {
            return Optional.of(new DiagnosticSystem(diagnosticsDecider.decide(detectConfiguration).isDiagnosticExtended, detectConfiguration, detectRun, detectInfo, directoryManager, eventSystem));
        }
        return Optional.empty();
    }

    public AirGapCreator createAirGapCreator(DetectConfigurationFactory detectConfigurationFactory, Configuration configuration) throws DetectUserFriendlyException {
        ConnectionDetails connectionDetails = detectConfigurationFactory.createConnectionDetails();
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionDetails);
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);

        FileFinder fileFinder = new WildcardFileFinder();
        DirectoryExecutableFinder directoryExecutableFinder = DirectoryExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SystemPathExecutableFinder systemPathExecutableFinder = new SystemPathExecutableFinder(directoryExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(directoryExecutableFinder, systemPathExecutableFinder, detectConfigurationFactory.createExecutablePaths());

        GradleInspectorInstaller gradleInspectorInstaller = new GradleInspectorInstaller(artifactResolver);
        DetectExecutableRunner runner = DetectExecutableRunner.newDebug(eventSystem);
        GradleAirGapCreator gradleAirGapCreator = new GradleAirGapCreator(detectExecutableResolver, gradleInspectorInstaller, runner, configuration);

        NugetAirGapCreator nugetAirGapCreator = new NugetAirGapCreator(new NugetInspectorInstaller(artifactResolver));
        DockerAirGapCreator dockerAirGapCreator = new DockerAirGapCreator(new DockerInspectorInstaller(artifactResolver));

        return new AirGapCreator(new AirGapPathFinder(), eventSystem, gradleAirGapCreator, nugetAirGapCreator, dockerAirGapCreator);
    }

    public HelpJsonManager createHelpJsonManager() {
        return new HelpJsonManager(gson);
    }

    public DirectoryManager createDirectoryManager(DetectConfigurationFactory detectConfigurationFactory) throws IOException {
        return new DirectoryManager(detectConfigurationFactory.createDirectoryOptions(), detectRun);
    }

    public DetectConfigurationValidator createDetectConfigurationValidator() {
        return new DetectConfigurationValidator(eventSystem, detectInfo);
    }

    public DetectorProfiler createDetectorProfiler() {
        return new DetectorProfiler(eventSystem);
    }

    public ProductBootFactory createProductBootFactory(DetectConfigurationFactory detectConfigurationFactory) {
        return new ProductBootFactory(detectInfo, eventSystem, detectConfigurationFactory);
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public DetectRun getDetectRun() {
        return detectRun;
    }

    public ProductBoot createProductBoot(DetectConfigurationFactory detectConfigurationFactory) {
        ProductBootOptions productBootOptions = detectConfigurationFactory.createProductBootOptions();
        return new ProductBoot(blackDuckConnectivityChecker, new PolarisConnectivityChecker(), createAnalyticsConfigurationService(), createProductBootFactory(detectConfigurationFactory), productBootOptions);
    }

    public AnalyticsConfigurationService createAnalyticsConfigurationService() {
        return new AnalyticsConfigurationService(gson);
    }

    public InteractiveManager createInteractiveManager(List<PropertySource> propertySources) {
        InteractiveWriter writer = InteractiveWriter.defaultWriter(System.console(), System.in, System.out);
        InteractivePropertySourceBuilder propertySourceBuilder = new InteractivePropertySourceBuilder(writer);
        InteractiveModeDecisionTree interactiveModeDecisionTree = new InteractiveModeDecisionTree(blackDuckConnectivityChecker, propertySources);
        return new InteractiveManager(propertySourceBuilder, writer, interactiveModeDecisionTree);
    }

}
