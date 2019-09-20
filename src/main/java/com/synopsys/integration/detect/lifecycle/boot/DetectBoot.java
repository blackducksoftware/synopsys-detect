/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.DetectInfoUtility;
import com.synopsys.integration.detect.DetectableBeanConfiguration;
import com.synopsys.integration.detect.RunBeanConfiguration;
import com.synopsys.integration.detect.configuration.ConnectionManager;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationManager;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.DetectPropertyMap;
import com.synopsys.integration.detect.configuration.DetectPropertySource;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.help.DetectArgumentState;
import com.synopsys.integration.detect.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.help.json.HelpJsonDetector;
import com.synopsys.integration.detect.help.json.HelpJsonWriter;
import com.synopsys.integration.detect.help.print.DetectInfoPrinter;
import com.synopsys.integration.detect.help.print.HelpPrinter;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.interactive.mode.DefaultInteractiveMode;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.property.SpringPropertySource;
import com.synopsys.integration.detect.tool.detector.DetectableFactory;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.GradleInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorInstaller;
import com.synopsys.integration.detect.util.TildeInPathResolver;
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
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.RelevantFileTracker;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;
import com.synopsys.integration.detect.workflow.report.DetectConfigurationReporter;
import com.synopsys.integration.detect.workflow.report.writer.ErrorLogReportWriter;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detectable.detectable.executable.impl.CachedExecutableResolverOptions;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableResolver;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleLocalExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleSystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

import freemarker.template.Configuration;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DetectBootFactory detectBootFactory;

    public DetectBoot(DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public DetectBootResult boot(DetectRun detectRun, final String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext) {
        Gson gson = detectBootFactory.createGson();
        ObjectMapper objectMapper = detectBootFactory.createObjectMapper();
        DocumentBuilder xml = detectBootFactory.createXmlDocumentBuilder();
        Configuration configuration = detectBootFactory.createConfiguration();

        DetectInfo detectInfo = DetectInfoUtility.createDefaultDetectInfo();

        SpringPropertySource springPropertySource = new SpringPropertySource(environment);
        DetectPropertySource propertySource = new DetectPropertySource(springPropertySource);
        DetectPropertyMap propertyMap = new DetectPropertyMap();
        DetectConfiguration detectConfiguration = new DetectConfiguration(propertySource, propertyMap);
        DetectOptionManager detectOptionManager = new DetectOptionManager(detectConfiguration, detectInfo);

        final List<DetectOption> options = detectOptionManager.getDetectOptions();

        DetectArgumentState detectArgumentState = parseDetectArgumentState(sourceArgs);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            printAppropriateHelp(options, detectArgumentState);
            return DetectBootResult.exit(detectConfiguration, Optional.empty(), Optional.empty(), Optional.empty());
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            printHelpJsonDocument(options, detectInfo, configuration, gson);
            return DetectBootResult.exit(detectConfiguration, Optional.empty(), Optional.empty(), Optional.empty());
        }

        printDetectInfo(detectInfo);

        if (detectArgumentState.isInteractive()) {
            startInteractiveMode(detectOptionManager, detectConfiguration, gson, objectMapper);
        }

        try {
            processDetectConfiguration(detectInfo, detectRun, detectConfiguration, options);
        } catch (DetectUserFriendlyException e) {
            return DetectBootResult.exception(e, Optional.of(detectConfiguration), Optional.empty(), Optional.empty());
        }

        detectOptionManager.postConfigurationProcessedInit();

        logger.info("Configuration processed completely.");

        Optional<DetectBootResult> configurationResult = printConfiguration(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT, PropertyAuthority.None), detectOptionManager, detectConfiguration,
            eventSystem, options);
        if (configurationResult.isPresent()) {
            return configurationResult.get();
        }

        logger.info("Initializing Detect.");

        DetectConfigurationFactory factory = new DetectConfigurationFactory(detectConfiguration);
        DirectoryManager directoryManager = new DirectoryManager(factory.createDirectoryOptions(), detectRun);
        Optional<DiagnosticSystem> diagnosticSystem = createDiagnostics(detectOptionManager.getDetectOptions(), detectRun, detectInfo, detectArgumentState, eventSystem, directoryManager);

        DetectableOptionFactory detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem);

        logger.info("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            DetectOverrideableFilter inspectorFilter = new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
            String airGapSuffix = String.join("-", inspectorFilter.getIncludedSet().stream().sorted().collect(Collectors.toList()));
            File airGapZip = null;
            try {
                airGapZip = createAirGapZip(inspectorFilter, detectConfiguration, directoryManager, gson, eventSystem, configuration, airGapSuffix);
            } catch (DetectUserFriendlyException e) {
                return DetectBootResult.exception(e, Optional.of(detectConfiguration), Optional.of(directoryManager), diagnosticSystem);
            }
            return DetectBootResult.exit(detectConfiguration, Optional.ofNullable(airGapZip), Optional.of(directoryManager), diagnosticSystem);
        }

        final RunOptions runOptions = factory.createRunOptions();
        final DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();
        ProductDecider productDecider = new ProductDecider();
        ProductDecision productDecision;
        try {
            productDecision = productDecider.decide(detectConfiguration, directoryManager.getUserHome(), detectToolFilter);
        } catch (DetectUserFriendlyException e) {
            return DetectBootResult.exception(e, Optional.of(detectConfiguration), Optional.of(directoryManager), diagnosticSystem);
        }

        logger.info("Decided what products will be run. Starting product boot.");

        ProductBootFactory productBootFactory = new ProductBootFactory(detectConfiguration, detectInfo, eventSystem, detectOptionManager);
        ProductBoot productBoot = new ProductBoot();
        ProductRunData productRunData;
        try {
            productRunData = productBoot.boot(productDecision, detectConfiguration, new BlackDuckConnectivityChecker(), new PolarisConnectivityChecker(), productBootFactory);
        } catch (DetectUserFriendlyException e) {
            return DetectBootResult.exception(e, Optional.of(detectConfiguration), Optional.of(directoryManager), diagnosticSystem);
        }

        if (productRunData == null) {
            logger.info("No products to run, Detect is complete.");
            return DetectBootResult.exit(detectConfiguration, Optional.empty(), Optional.of(directoryManager), diagnosticSystem);
        }

        //TODO: Only need this if in diagnostic or online (for phone home):
        DetectorProfiler profiler = new DetectorProfiler(eventSystem);

        //lock the configuration, boot has completed.
        logger.debug("Configuration is now complete. No changes should occur to configuration.");
        detectConfiguration.lock();

        //Finished, populate the detect context
        detectContext.registerBean(detectRun);
        detectContext.registerBean(eventSystem);
        detectContext.registerBean(profiler);

        detectContext.registerBean(detectConfiguration);
        detectContext.registerBean(detectInfo);
        detectContext.registerBean(directoryManager);

        detectContext.registerBean(gson);
        detectContext.registerBean(objectMapper);
        detectContext.registerBean(xml);
        detectContext.registerBean(configuration);

        detectContext.registerBean(detectableOptionFactory);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.registerConfiguration(DetectableBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        return DetectBootResult.run(detectConfiguration, productRunData, directoryManager, diagnosticSystem);
    }

    private void printAppropriateHelp(List<DetectOption> detectOptions, DetectArgumentState detectArgumentState) {
        HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, detectOptions, detectArgumentState);
    }

    private void printHelpJsonDocument(List<DetectOption> detectOptions, DetectInfo detectInfo, Configuration configuration, Gson gson) {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        DetectorRuleSet build = ruleFactory.createRules(new DetectableFactory(), false);
        DetectorRuleSet buildless = ruleFactory.createRules(new DetectableFactory(), true);
        List<HelpJsonDetector> buildDetectors = build.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, build)).collect(Collectors.toList());
        List<HelpJsonDetector> buildlessDetectors = buildless.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, buildless)).collect(Collectors.toList());

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(configuration, gson);
        helpJsonWriter.writeGsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()), detectOptions, buildDetectors, buildlessDetectors);
    }

    private HelpJsonDetector convertDetectorRule(DetectorRule rule, DetectorRuleSet ruleSet) {
        HelpJsonDetector helpData = new HelpJsonDetector();
        helpData.detectorName = rule.getName();
        helpData.detectorDescriptiveName = rule.getDescriptiveName();
        helpData.detectorType = rule.getDetectorType().toString();
        helpData.maxDepth = rule.getMaxDepth();
        helpData.nestable = rule.isNestable();
        helpData.nestInvisible = rule.isNestInvisible();
        helpData.yieldsTo = ruleSet.getYieldsTo(rule).stream().map(DetectorRule::getDescriptiveName).collect(Collectors.toList());
        helpData.fallbackTo = ruleSet.getFallbackFrom(rule).map(DetectorRule::getDescriptiveName).orElse("");

        //Attempt to create the detectable.
        //Not currently possible. Need a full DetectableConfiguration to be able to make Detectables.
        //Detectable detectable = rule.createDetectable(null);
        //helpData.detectableGroup = detectable.getGroupName();
        //helpData.detectableName = detectable.getName();
        //helpData.detectableDescriptiveName = detectable.getName();
        return helpData;
    }

    private void printDetectInfo(DetectInfo detectInfo) {
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);
    }

    private Optional<DetectBootResult> printConfiguration(boolean fullConfiguration, DetectOptionManager detectOptionManager, DetectConfiguration detectConfiguration, EventSystem eventSystem, List<DetectOption> detectOptions) {

        //First print the entire configuration.
        DetectConfigurationReporter detectConfigurationReporter = new DetectConfigurationReporter();
        InfoLogReportWriter infoLogReportWriter = new InfoLogReportWriter();
        if (!fullConfiguration) {
            detectConfigurationReporter.print(infoLogReportWriter, detectOptions);
        }

        //Next check for options that are just plain bad, ie giving an detector type we don't know about.
        try {
            final List<DetectOption.OptionValidationResult> invalidDetectOptionResults = detectOptionManager.getAllInvalidOptionResults();
            if (!invalidDetectOptionResults.isEmpty()) {
                throw new DetectUserFriendlyException(invalidDetectOptionResults.get(0).getValidationMessage(), ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, Optional.of(detectConfiguration), Optional.empty(), Optional.empty()));
        }

        //Check for deprecated fields that are still being used but should cause a failure.
        List<DetectOption> failureProperties = detectOptionManager.findDeprecatedFailureProperties();
        if (failureProperties.size() > 0) {
            ErrorLogReportWriter errorLogReportWriter = new ErrorLogReportWriter();
            detectConfigurationReporter.printFailures(errorLogReportWriter, failureProperties);
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION));
            return Optional.of(DetectBootResult.exit(detectConfiguration, Optional.empty(), Optional.empty(), Optional.empty()));
        }

        //Finally log all fields that are deprecated but still being used (that are not failure).
        detectConfigurationReporter.printWarnings(infoLogReportWriter, detectOptions);

        return Optional.empty();
    }

    private void startInteractiveMode(DetectOptionManager detectOptionManager, DetectConfiguration detectConfiguration, Gson gson, ObjectMapper objectMapper) {
        InteractiveManager interactiveManager = new InteractiveManager(detectOptionManager);
        DefaultInteractiveMode defaultInteractiveMode = new DefaultInteractiveMode(detectOptionManager);
        interactiveManager.configureInInteractiveMode(defaultInteractiveMode);
    }

    private DetectArgumentState parseDetectArgumentState(String[] sourceArgs) {
        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        final DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        return detectArgumentState;
    }

    private void processDetectConfiguration(DetectInfo detectInfo, DetectRun detectRun, DetectConfiguration detectConfiguration, List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(DetectConfigurationManager.USER_HOME, detectInfo.getCurrentOs());
        DetectConfigurationManager detectConfigurationManager = new DetectConfigurationManager(tildeInPathResolver, detectConfiguration);
        detectConfigurationManager.process(detectOptions, detectRun.getRunId());
    }

    private Optional<DiagnosticSystem> createDiagnostics(List<DetectOption> detectOptions, DetectRun detectRun, DetectInfo detectInfo, DetectArgumentState detectArgumentState, EventSystem eventSystem, DirectoryManager directoryManager) {
        if (detectArgumentState.isDiagnostic() || detectArgumentState.isDiagnosticExtended()) {
            boolean extendedMode = detectArgumentState.isDiagnosticExtended();
            RelevantFileTracker relevantFileTracker = new RelevantFileTracker(detectArgumentState.isDiagnostic(), detectArgumentState.isDiagnosticExtended(), directoryManager);
            DiagnosticSystem diagnosticSystem = new DiagnosticSystem(extendedMode, detectOptions, detectRun, detectInfo, relevantFileTracker, directoryManager, eventSystem);
            return Optional.of(diagnosticSystem);
        } else {
            return Optional.empty();
        }
    }

    private File createAirGapZip(DetectFilter inspectorFilter, DetectConfiguration detectConfiguration, DirectoryManager directoryManager, Gson gson, EventSystem eventSystem, Configuration configuration, String airGapSuffix)
        throws DetectUserFriendlyException {
        ConnectionManager connectionManager = new ConnectionManager(detectConfiguration);
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionManager, gson);

        //TODO: This is awful, why is making this so convoluted.
        SimpleFileFinder fileFinder = new SimpleFileFinder();
        SimpleExecutableFinder simpleExecutableFinder = SimpleExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SimpleLocalExecutableFinder localExecutableFinder = new SimpleLocalExecutableFinder(simpleExecutableFinder);
        SimpleSystemExecutableFinder simpleSystemExecutableFinder = new SimpleSystemExecutableFinder(simpleExecutableFinder);
        SimpleExecutableResolver executableResolver = new SimpleExecutableResolver(new CachedExecutableResolverOptions(false), localExecutableFinder, simpleSystemExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(executableResolver, detectConfiguration);
        GradleInspectorInstaller gradleInspectorInstaller = new GradleInspectorInstaller(artifactResolver);
        SimpleExecutableRunner simpleExecutableRunner = new SimpleExecutableRunner();
        GradleAirGapCreator gradleAirGapCreator = new GradleAirGapCreator(artifactResolver, detectExecutableResolver, gradleInspectorInstaller, simpleExecutableRunner, configuration);

        NugetAirGapCreator nugetAirGapCreator = new NugetAirGapCreator(new NugetInspectorInstaller(artifactResolver));
        DockerAirGapCreator dockerAirGapCreator = new DockerAirGapCreator(new DockerInspectorInstaller(artifactResolver));

        AirGapCreator airGapCreator = new AirGapCreator(new AirGapPathFinder(), eventSystem, gradleAirGapCreator, nugetAirGapCreator, dockerAirGapCreator);
        return airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix);
    }
}
