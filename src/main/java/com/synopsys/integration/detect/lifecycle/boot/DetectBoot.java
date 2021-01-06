/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.RunBeanConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
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
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsConfigurationService;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticsDecider;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.rest.proxy.ProxyInfo;

import freemarker.template.Configuration;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectBootFactory detectBootFactory;

    public DetectBoot(DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public Optional<DetectBootResult> boot(DetectRun detectRun, String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext) throws DetectUserFriendlyException, IOException, IllegalAccessException {
        ObjectMapper objectMapper = detectBootFactory.createObjectMapper();
        DocumentBuilder xml = detectBootFactory.createXmlDocumentBuilder();
        Configuration configuration = detectBootFactory.createConfiguration();

        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);
        Gson gson = detectContext.getBean(Gson.class);

        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();

        DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        List<PropertySource> propertySources = detectBootFactory.initializePropertySources(environment);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(System.out, DetectProperties.allProperties(), Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            HelpJsonManager helpJsonManager = new HelpJsonManager(gson);
            helpJsonManager.createHelpJsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()));
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        // FIXME: This prints three lines of code.
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);

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

        // Building code pieces
        PathResolver pathResolver = detectBootFactory.createPathResolver(detectInfo.getCurrentOs(), detectConfiguration.getValueOrDefault(DetectProperties.DETECT_RESOLVE_TILDE_IN_PATHS.getProperty()));
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
        DirectoryManager directoryManager = new DirectoryManager(detectConfigurationFactory.createDirectoryOptions(), detectRun);

        DiagnosticSystem diagnosticSystem = detectBootFactory.createDiagnosticSystem(new DiagnosticsDecider(detectArgumentState), detectConfiguration,detectRun,detectInfo,directoryManager,eventSystem)
                                                .orElse(null);
        // end building code pieces

        // functional steps
        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            File airGapZip;
            try {
                DetectOverrideableFilter inspectorFilter = new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
                AirGapCreator airGapCreator = detectBootFactory.createAirGapCreator(detectConfiguration, pathResolver, gson, eventSystem, configuration);
                String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty())
                                                    .orElse(null);
                String airGapSuffix = inspectorFilter.getIncludedSet().stream().sorted().collect(Collectors.joining("-"));

                airGapZip = airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix, gradleInspectorVersion);
            } catch (DetectUserFriendlyException e) {
                return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
            }
            return Optional.of(DetectBootResult.exit(detectConfiguration, airGapZip, directoryManager, diagnosticSystem));
        }
        // end functional steps

        RunOptions runOptions = detectConfigurationFactory.createRunOptions();
        DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();

        logger.info("");
        ProductDecision productDecision = new ProductDecider().decide(detectConfigurationFactory, directoryManager.getUserHome(), detectToolFilter);

        logger.debug("Decided what products will be run. Starting product boot.");


        ProductRunData productRunData;
        try {
            ProductBootFactory productBootFactory = new ProductBootFactory(detectInfo, eventSystem, detectConfigurationFactory);
            ProductBootOptions productBootOptions = detectConfigurationFactory.createProductBootOptions();
            ProductBoot productBoot = new ProductBoot();
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

        // Building code pieces
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

    private Optional<DetectBootResult> printConfiguration(boolean fullConfiguration, PropertyConfiguration detectConfiguration, EventSystem eventSystem, DetectInfo detectInfo) throws IllegalAccessException {
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

}
