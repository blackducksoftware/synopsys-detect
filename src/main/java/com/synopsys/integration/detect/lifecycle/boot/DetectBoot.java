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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.property.types.path.TildeInPathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.configuration.source.SpringConfigurationPropertySource;
import com.synopsys.integration.detect.RunBeanConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonDetector;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonWriter;
import com.synopsys.integration.detect.configuration.help.print.DetectInfoPrinter;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.interactive.InteractiveModeDecisionTree;
import com.synopsys.integration.detect.interactive.InteractivePropertySourceBuilder;
import com.synopsys.integration.detect.interactive.InteractiveWriter;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.GradleInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorInstaller;
import com.synopsys.integration.detect.util.filter.DetectFilter;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
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
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.util.OperatingSystemType;

import freemarker.template.Configuration;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectBootFactory detectBootFactory;

    public DetectBoot(DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public Optional<DetectBootResult> boot(DetectRun detectRun, String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext)
        throws DetectUserFriendlyException, IOException, IllegalAccessException {
        ObjectMapper objectMapper = detectBootFactory.createObjectMapper();
        DocumentBuilder xml = detectBootFactory.createXmlDocumentBuilder();
        Configuration configuration = detectBootFactory.createConfiguration();

        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);
        Gson gson = detectContext.getBean(Gson.class);

        List<PropertySource> propertySources;
        try {
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, false));
        } catch (RuntimeException e) {
            logger.error("An unknown property source was found, detect will still continue.", e);
            propertySources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(environment, true));
        }

        DetectArgumentState detectArgumentState = parseDetectArgumentState(sourceArgs);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            printAppropriateHelp(DetectProperties.allProperties(), detectArgumentState);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            printHelpJsonDocument(DetectProperties.allProperties(), detectInfo, gson);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        printDetectInfo(detectInfo);

        if (detectArgumentState.isInteractive()) {
            InteractiveWriter writer = InteractiveWriter.defaultWriter(System.console(), System.in, System.out);
            InteractivePropertySourceBuilder propertySourceBuilder = new InteractivePropertySourceBuilder(writer);
            InteractiveManager interactiveManager = new InteractiveManager(propertySourceBuilder, writer);

            // TODO: Ideally we should be able to share the BlackDuckConnectivityChecker from elsewhere in the boot --rotte NOV 2020
            InteractiveModeDecisionTree interactiveModeDecisionTree = new InteractiveModeDecisionTree(new BlackDuckConnectivityChecker(), propertySources);
            MapPropertySource interactivePropertySource = interactiveManager.getInteractivePropertySource(interactiveModeDecisionTree);

            propertySources.add(0, interactivePropertySource);
        }
        PropertyConfiguration detectConfiguration = new PropertyConfiguration(propertySources);

        logger.debug("Configuration processed completely.");

        Boolean printFull = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_SUPPRESS_CONFIGURATION_OUTPUT.getProperty());
        Optional<DetectBootResult> configurationResult = printConfiguration(printFull, detectConfiguration, eventSystem, detectInfo);
        if (configurationResult.isPresent()) {
            return configurationResult;
        }

        logger.debug("Initializing Detect.");

        PathResolver pathResolver;
        if (detectInfo.getCurrentOs() != OperatingSystemType.WINDOWS && detectConfiguration.getValueOrDefault(DetectProperties.DETECT_RESOLVE_TILDE_IN_PATHS.getProperty())) {
            logger.info("Tilde's will be automatically resolved to USER HOME.");
            pathResolver = new TildeInPathResolver(SystemUtils.USER_HOME);
        } else {
            pathResolver = new SimplePathResolver();
        }
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
        DirectoryManager directoryManager = new DirectoryManager(detectConfigurationFactory.createDirectoryOptions(), detectRun);

        DiagnosticsDecision diagnosticsDecision = new DiagnosticsDecider(detectArgumentState, detectConfiguration).decide();
        DiagnosticSystem diagnosticSystem = null;
        if (diagnosticsDecision.isConfiguredForDiagnostic) {
            diagnosticSystem = new DiagnosticSystem(diagnosticsDecision.isDiagnosticExtended, detectConfiguration, detectRun, detectInfo, directoryManager, eventSystem);
        }

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            DetectOverrideableFilter inspectorFilter = new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
            String airGapSuffix = inspectorFilter.getIncludedSet().stream().sorted().collect(Collectors.joining("-"));
            File airGapZip;
            try {
                airGapZip = createAirGapZip(inspectorFilter, detectConfiguration, pathResolver, directoryManager, gson, eventSystem, configuration, airGapSuffix);
            } catch (DetectUserFriendlyException e) {
                return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
            }
            return Optional.of(DetectBootResult.exit(detectConfiguration, airGapZip, directoryManager, diagnosticSystem));
        }

        RunOptions runOptions = detectConfigurationFactory.createRunOptions();
        DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();

        logger.info("");
        ProductDecision productDecision = new ProductDecider().decide(detectConfigurationFactory, directoryManager.getUserHome(), detectToolFilter);

        logger.debug("Decided what products will be run. Starting product boot.");

        ProductBootFactory productBootFactory = new ProductBootFactory(detectInfo, eventSystem, detectConfigurationFactory);
        ProductBoot productBoot = new ProductBoot();
        ProductRunData productRunData;
        ProductBootOptions productBootOptions = detectConfigurationFactory.createProductBootOptions();
        try {
            productRunData = productBoot.boot(productDecision, productBootOptions, new BlackDuckConnectivityChecker(), new PolarisConnectivityChecker(), productBootFactory, new AnalyticsConfigurationService(gson));
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
        }

        if (productRunData == null) {
            logger.info("No products to run, Detect is complete.");
            return Optional.of(DetectBootResult.exit(detectConfiguration, directoryManager, diagnosticSystem));
        }

        ProxyInfo detectableProxyInfo;
        try {
            detectableProxyInfo = detectConfigurationFactory.createBlackDuckProxyInfo();
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
        }

        DetectableOptionFactory detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem, pathResolver, detectableProxyInfo);
        DetectorProfiler profiler = new DetectorProfiler(eventSystem);

        //Finished, populate the detect context
        detectContext.registerBean(detectRun);
        detectContext.registerBean(eventSystem);
        detectContext.registerBean(profiler);

        detectContext.registerBean(detectConfiguration);
        detectContext.registerBean(detectableOptionFactory);
        detectContext.registerBean(detectConfigurationFactory);

        detectContext.registerBean(directoryManager);
        detectContext.registerBean(objectMapper);
        detectContext.registerBean(xml);
        detectContext.registerBean(configuration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        return Optional.of(DetectBootResult.run(detectConfiguration, productRunData, directoryManager, diagnosticSystem));
    }

    private void printAppropriateHelp(List<Property> properties, DetectArgumentState detectArgumentState) {
        HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, properties, Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
    }

    private void printHelpJsonDocument(List<Property> properties, DetectInfo detectInfo, Gson gson) {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        // TODO: Is there a better way to build a fake set of rules?
        DetectDetectableFactory mockFactory = new DetectDetectableFactory(null, null, null, null, null, null, null);
        DetectorRuleSet build = ruleFactory.createRules(mockFactory, false);
        DetectorRuleSet buildless = ruleFactory.createRules(mockFactory, true);
        List<HelpJsonDetector> buildDetectors = build.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, build)).collect(Collectors.toList());
        List<HelpJsonDetector> buildlessDetectors = buildless.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, buildless)).collect(Collectors.toList());

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()), properties, buildDetectors, buildlessDetectors);
    }

    private HelpJsonDetector convertDetectorRule(DetectorRule rule, DetectorRuleSet ruleSet) {
        HelpJsonDetector helpData = new HelpJsonDetector();
        helpData.setDetectorName(rule.getName());
        helpData.setDetectorDescriptiveName(rule.getDescriptiveName());
        helpData.setDetectorType(rule.getDetectorType().toString());
        helpData.setMaxDepth(rule.getMaxDepth());
        helpData.setNestable(rule.isNestable());
        helpData.setNestInvisible(rule.isNestInvisible());
        helpData.setYieldsTo(ruleSet.getYieldsTo(rule).stream().map(DetectorRule::getDescriptiveName).collect(Collectors.toList()));
        helpData.setFallbackTo(ruleSet.getFallbackFrom(rule).map(DetectorRule::getDescriptiveName).orElse(""));

        //Attempt to create the detectable.
        //Not currently possible. Need a full DetectableConfiguration to be able to make Detectables.
        Class<Detectable> detectableClass = rule.getDetectableClass();
        Optional<DetectableInfo> infoSearch = Arrays.stream(detectableClass.getAnnotations())
                                                  .filter(annotation -> annotation instanceof DetectableInfo)
                                                  .map(annotation -> (DetectableInfo) annotation)
                                                  .findFirst();

        if (infoSearch.isPresent()) {
            DetectableInfo info = infoSearch.get();
            helpData.setDetectableLanguage(info.language());
            helpData.setDetectableRequirementsMarkdown(info.requirementsMarkdown());
            helpData.setDetectableForge(info.forge());
        }

        return helpData;
    }

    private void printDetectInfo(DetectInfo detectInfo) {
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);
    }

    private Optional<DetectBootResult> printConfiguration(boolean fullConfiguration, PropertyConfiguration detectConfiguration, EventSystem eventSystem,
        DetectInfo detectInfo) throws IllegalAccessException {

        Map<String, String> additionalNotes = new HashMap<>();

        List<Property> deprecatedProperties = DetectProperties.allProperties()
                                                  .stream()
                                                  .filter(property -> property.getPropertyDeprecationInfo() != null)
                                                  .collect(Collectors.toList());

        Map<String, List<String>> deprecationMessages = new HashMap<>();
        List<Property> usedFailureProperties = new ArrayList<>();
        for (Property property : deprecatedProperties) {
            if (detectConfiguration.wasKeyProvided(property.getKey())) {
                PropertyDeprecationInfo deprecationInfo = property.getPropertyDeprecationInfo();

                if (deprecationInfo == null) {
                    logger.debug("A deprecated property is missing deprecation info.");
                    continue;
                }

                additionalNotes.put(property.getKey(), "\t *** DEPRECATED ***");
                String deprecationMessage = deprecationInfo.getDeprecationText();

                deprecationMessages.put(property.getKey(), new ArrayList<>(Collections.singleton(deprecationMessage)));
                DetectIssue.publish(eventSystem, DetectIssueType.DEPRECATION, property.getKey(), "\t" + deprecationMessage);

                if (detectInfo.getDetectMajorVersion() >= deprecationInfo.getFailInVersion().getIntValue()) {
                    usedFailureProperties.add(property);
                }
            }
        }

        //First print the entire configuration.
        PropertyConfigurationHelpContext detectConfigurationReporter = new PropertyConfigurationHelpContext(detectConfiguration);
        InfoLogReportWriter infoLogReportWriter = new InfoLogReportWriter();
        if (!fullConfiguration) {
            detectConfigurationReporter.printCurrentValues(infoLogReportWriter::writeLine, DetectProperties.allProperties(), additionalNotes);
        }

        //Next check for options that are just plain bad, ie giving an detector type we don't know about.
        Map<String, List<String>> errorMap = detectConfigurationReporter.findPropertyParseErrors(DetectProperties.allProperties());
        if (errorMap.size() > 0) {
            Map.Entry<String, List<String>> entry = errorMap.entrySet().iterator().next();
            return Optional.of(DetectBootResult.exception(new DetectUserFriendlyException(entry.getKey() + ": " + entry.getValue().get(0), ExitCodeType.FAILURE_GENERAL_ERROR), detectConfiguration));
        }

        if (usedFailureProperties.size() > 0) {
            detectConfigurationReporter.printPropertyErrors(infoLogReportWriter::writeLine, DetectProperties.allProperties(), deprecationMessages);

            logger.warn(StringUtils.repeat("=", 60));
            logger.warn("Configuration is using deprecated properties that must be updated for this major version.");
            logger.warn("You MUST fix these deprecation issues for detect to proceed.");
            logger.warn("To ignore these messages and force detect to exit with success supply --" + DetectProperties.DETECT_FORCE_SUCCESS.getProperty().getKey() + "=true");
            logger.warn("This will not force detect to run, but it will pretend to have succeeded.");
            logger.warn(StringUtils.repeat("=", 60));

            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION));
            return Optional.of(DetectBootResult.exit(detectConfiguration));
        }

        return Optional.empty();
    }

    private DetectArgumentState parseDetectArgumentState(String[] sourceArgs) {
        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        return detectArgumentState;
    }

    private Optional<DiagnosticSystem> createDiagnostics(PropertyConfiguration propertyConfiguration, DetectRun detectRun, DetectInfo detectInfo, DiagnosticsDecider diagnosticsDecider, EventSystem eventSystem,
        DirectoryManager directoryManager) {
        DiagnosticSystem diagnosticSystem = null;

        return Optional.ofNullable(diagnosticSystem);
    }

    private File createAirGapZip(DetectFilter inspectorFilter, PropertyConfiguration detectConfiguration, PathResolver pathResolver, DirectoryManager directoryManager, Gson gson,
        EventSystem eventSystem,
        Configuration configuration,
        String airGapSuffix)
        throws DetectUserFriendlyException {
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
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

        AirGapCreator airGapCreator = new AirGapCreator(new AirGapPathFinder(), eventSystem, gradleAirGapCreator, nugetAirGapCreator, dockerAirGapCreator);
        String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty()).orElse(null);
        return airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix, gradleInspectorVersion);
    }
}
