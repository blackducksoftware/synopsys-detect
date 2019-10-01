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

    private final DetectBootFactory detectBootFactory;

    public DetectBoot(final DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public DetectBootResult boot(final DetectRun detectRun, final String[] sourceArgs, final ConfigurableEnvironment environment, final EventSystem eventSystem, final DetectContext detectContext) {
        final Gson gson = detectBootFactory.createGson();
        final ObjectMapper objectMapper = detectBootFactory.createObjectMapper();
        final DocumentBuilder xml = detectBootFactory.createXmlDocumentBuilder();
        final Configuration configuration = detectBootFactory.createConfiguration();

        final DetectInfo detectInfo = DetectInfoUtility.createDefaultDetectInfo();

        final SpringPropertySource springPropertySource = new SpringPropertySource(environment);
        final DetectPropertySource propertySource = new DetectPropertySource(springPropertySource);
        final DetectPropertyMap propertyMap = new DetectPropertyMap();
        final DetectConfiguration detectConfiguration = new DetectConfiguration(propertySource, propertyMap);
        final DetectOptionManager detectOptionManager = new DetectOptionManager(detectConfiguration, detectInfo);

        final List<DetectOption> options = detectOptionManager.getDetectOptions();

        final DetectArgumentState detectArgumentState = parseDetectArgumentState(sourceArgs);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            printAppropriateHelp(options, detectArgumentState);
            return DetectBootResult.exit(detectConfiguration);
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            printHelpJsonDocument(options, detectInfo, gson);
            return DetectBootResult.exit(detectConfiguration);
        }

        printDetectInfo(detectInfo);

        if (detectArgumentState.isInteractive()) {
            startInteractiveMode(detectOptionManager, detectConfiguration, gson, objectMapper);
        }

        try {
            processDetectConfiguration(detectInfo, detectRun, detectConfiguration, options);
        } catch (final DetectUserFriendlyException e) {
            return DetectBootResult.exception(e, detectConfiguration);
        }

        detectOptionManager.postConfigurationProcessedInit();

        logger.debug("Configuration processed completely.");

        final Optional<DetectBootResult> configurationResult = printConfiguration(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT, PropertyAuthority.NONE), detectOptionManager, detectConfiguration,
            eventSystem, options);
        if (configurationResult.isPresent()) {
            return configurationResult.get();
        }

        logger.debug("Initializing Detect.");

        final DetectConfigurationFactory factory = new DetectConfigurationFactory(detectConfiguration);
        final DirectoryManager directoryManager = new DirectoryManager(factory.createDirectoryOptions(), detectRun);
        final Optional<DiagnosticSystem> diagnosticSystem = createDiagnostics(detectOptionManager.getDetectOptions(), detectRun, detectInfo, detectArgumentState, eventSystem, directoryManager);

        final DetectableOptionFactory detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem);

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            final DetectOverrideableFilter inspectorFilter = new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
            final String airGapSuffix = inspectorFilter.getIncludedSet().stream().sorted().collect(Collectors.joining("-"));
            final File airGapZip;
            try {
                airGapZip = createAirGapZip(inspectorFilter, detectConfiguration, directoryManager, gson, eventSystem, configuration, airGapSuffix);
            } catch (final DetectUserFriendlyException e) {
                return DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem);
            }
            return DetectBootResult.exit(detectConfiguration, airGapZip, directoryManager, diagnosticSystem);
        }

        final RunOptions runOptions = factory.createRunOptions();
        final DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();
        final ProductDecider productDecider = new ProductDecider();
        final ProductDecision productDecision;

        logger.info("");
        productDecision = productDecider.decide(detectConfiguration, directoryManager.getUserHome(), detectToolFilter);

        logger.debug("Decided what products will be run. Starting product boot.");

        final ProductBootFactory productBootFactory = new ProductBootFactory(detectConfiguration, detectInfo, eventSystem, detectOptionManager);
        final ProductBoot productBoot = new ProductBoot();
        final ProductRunData productRunData;
        try {
            productRunData = productBoot.boot(productDecision, detectConfiguration, new BlackDuckConnectivityChecker(), new PolarisConnectivityChecker(), productBootFactory);
        } catch (final DetectUserFriendlyException e) {
            return DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem);
        }

        if (productRunData == null) {
            logger.info("No products to run, Detect is complete.");
            return DetectBootResult.exit(detectConfiguration, directoryManager, diagnosticSystem);
        }

        //TODO: Only need this if in diagnostic or online (for phone home):
        final DetectorProfiler profiler = new DetectorProfiler(eventSystem);

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

    private void printAppropriateHelp(final List<DetectOption> detectOptions, final DetectArgumentState detectArgumentState) {
        final HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, detectOptions, detectArgumentState);
    }

    private void printHelpJsonDocument(final List<DetectOption> detectOptions, final DetectInfo detectInfo, final Gson gson) {
        final DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        final DetectorRuleSet build = ruleFactory.createRules(new DetectableFactory(), false);
        final DetectorRuleSet buildless = ruleFactory.createRules(new DetectableFactory(), true);
        final List<HelpJsonDetector> buildDetectors = build.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, build)).collect(Collectors.toList());
        final List<HelpJsonDetector> buildlessDetectors = buildless.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, buildless)).collect(Collectors.toList());

        final HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()), detectOptions, buildDetectors, buildlessDetectors);
    }

    private HelpJsonDetector convertDetectorRule(final DetectorRule rule, final DetectorRuleSet ruleSet) {
        final HelpJsonDetector helpData = new HelpJsonDetector();
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

    private void printDetectInfo(final DetectInfo detectInfo) {
        final DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);
    }

    private Optional<DetectBootResult> printConfiguration(final boolean fullConfiguration, final DetectOptionManager detectOptionManager, final DetectConfiguration detectConfiguration, final EventSystem eventSystem,
        final List<DetectOption> detectOptions) {

        //First print the entire configuration.
        final DetectConfigurationReporter detectConfigurationReporter = new DetectConfigurationReporter();
        final InfoLogReportWriter infoLogReportWriter = new InfoLogReportWriter();
        if (!fullConfiguration) {
            detectConfigurationReporter.print(infoLogReportWriter, detectOptions, true);
        }

        //Next check for options that are just plain bad, ie giving an detector type we don't know about.
        try {
            final List<DetectOption.OptionValidationResult> invalidDetectOptionResults = detectOptionManager.getAllInvalidOptionResults();
            if (!invalidDetectOptionResults.isEmpty()) {
                throw new DetectUserFriendlyException(invalidDetectOptionResults.get(0).getValidationMessage(), ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        } catch (final DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration));
        }

        //Check for deprecated fields that are still being used but should cause a failure.
        final List<DetectOption> failureProperties = detectOptionManager.findDeprecatedFailureProperties();
        if (failureProperties.size() > 0) {
            final ErrorLogReportWriter errorLogReportWriter = new ErrorLogReportWriter();
            detectConfigurationReporter.printFailures(errorLogReportWriter, failureProperties);
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION));
            return Optional.of(DetectBootResult.exit(detectConfiguration));
        }

        //Finally log all fields that are deprecated but still being used (that are not failure).
        detectConfigurationReporter.publishWarnings(eventSystem, detectOptions);

        return Optional.empty();
    }

    private void startInteractiveMode(final DetectOptionManager detectOptionManager, final DetectConfiguration detectConfiguration, final Gson gson, final ObjectMapper objectMapper) {
        final InteractiveManager interactiveManager = new InteractiveManager(detectOptionManager);
        final DefaultInteractiveMode defaultInteractiveMode = new DefaultInteractiveMode(detectOptionManager);
        interactiveManager.configureInInteractiveMode(defaultInteractiveMode);
    }

    private DetectArgumentState parseDetectArgumentState(final String[] sourceArgs) {
        final DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        final DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        return detectArgumentState;
    }

    private void processDetectConfiguration(final DetectInfo detectInfo, final DetectRun detectRun, final DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        final TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(DetectConfigurationManager.USER_HOME, detectInfo.getCurrentOs());
        final DetectConfigurationManager detectConfigurationManager = new DetectConfigurationManager(tildeInPathResolver, detectConfiguration);
        detectConfigurationManager.process(detectOptions);
    }

    private Optional<DiagnosticSystem> createDiagnostics(
        final List<DetectOption> detectOptions, final DetectRun detectRun, final DetectInfo detectInfo, final DetectArgumentState detectArgumentState, final EventSystem eventSystem, final DirectoryManager directoryManager) {
        if (detectArgumentState.isDiagnostic() || detectArgumentState.isDiagnosticExtended()) {
            final boolean extendedMode = detectArgumentState.isDiagnosticExtended();
            final DiagnosticSystem diagnosticSystem = new DiagnosticSystem(extendedMode, detectOptions, detectRun, detectInfo, directoryManager, eventSystem);
            return Optional.of(diagnosticSystem);
        } else {
            return Optional.empty();
        }
    }

    private File createAirGapZip(final DetectFilter inspectorFilter, final DetectConfiguration detectConfiguration, final DirectoryManager directoryManager, final Gson gson, final EventSystem eventSystem, final Configuration configuration,
        final String airGapSuffix)
        throws DetectUserFriendlyException {
        final ConnectionManager connectionManager = new ConnectionManager(detectConfiguration);
        final ArtifactResolver artifactResolver = new ArtifactResolver(connectionManager, gson);

        //TODO: This is awful, why is making this so convoluted.
        final SimpleFileFinder fileFinder = new SimpleFileFinder();
        final SimpleExecutableFinder simpleExecutableFinder = SimpleExecutableFinder.forCurrentOperatingSystem(fileFinder);
        final SimpleLocalExecutableFinder localExecutableFinder = new SimpleLocalExecutableFinder(simpleExecutableFinder);
        final SimpleSystemExecutableFinder simpleSystemExecutableFinder = new SimpleSystemExecutableFinder(simpleExecutableFinder);
        final SimpleExecutableResolver executableResolver = new SimpleExecutableResolver(new CachedExecutableResolverOptions(false), localExecutableFinder, simpleSystemExecutableFinder);
        final DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(executableResolver, detectConfiguration);
        final GradleInspectorInstaller gradleInspectorInstaller = new GradleInspectorInstaller(artifactResolver);
        final SimpleExecutableRunner simpleExecutableRunner = new SimpleExecutableRunner();
        final GradleAirGapCreator gradleAirGapCreator = new GradleAirGapCreator(detectExecutableResolver, gradleInspectorInstaller, simpleExecutableRunner, configuration);

        final NugetAirGapCreator nugetAirGapCreator = new NugetAirGapCreator(new NugetInspectorInstaller(artifactResolver));
        final DockerAirGapCreator dockerAirGapCreator = new DockerAirGapCreator(new DockerInspectorInstaller(artifactResolver));

        final AirGapCreator airGapCreator = new AirGapCreator(new AirGapPathFinder(), eventSystem, gradleAirGapCreator, nugetAirGapCreator, dockerAirGapCreator);
        return airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix);
    }
}
