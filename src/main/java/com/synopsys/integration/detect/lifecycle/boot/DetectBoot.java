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
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandArgumentParser;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.RunBeanConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyUtil;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
import com.synopsys.integration.detect.configuration.validation.DeprecationResult;
import com.synopsys.integration.detect.configuration.validation.DetectConfigurationBootManager;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticDecision;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.rest.proxy.ProxyInfo;

import freemarker.template.Configuration;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final PrintStream DEFAULT_PRINT_STREAM = System.out;

    private final DetectBootFactory detectBootFactory;
    private final DetectArgumentState detectArgumentState;
    private final List<PropertySource> propertySources;
    private final DetectContext detectContext;

    public DetectBoot(DetectBootFactory detectBootFactory, DetectArgumentState detectArgumentState, List<PropertySource> propertySources, DetectContext detectContext) {
        this.detectBootFactory = detectBootFactory;
        this.detectArgumentState = detectArgumentState;
        this.propertySources = propertySources;
        this.detectContext = detectContext;
    }

    public Optional<DetectBootResult> boot(String detectVersion) throws IOException, IllegalAccessException {
        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(DEFAULT_PRINT_STREAM, DetectProperties.allProperties().getProperties(), Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            HelpJsonManager helpJsonManager = detectBootFactory.createHelpJsonManager();
            helpJsonManager.createHelpJsonDocument(String.format("synopsys-detect-%s-help.json", detectVersion));
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        DEFAULT_PRINT_STREAM.println();
        DEFAULT_PRINT_STREAM.println("Detect Version: " + detectVersion);
        DEFAULT_PRINT_STREAM.println();

        if (detectArgumentState.isInteractive()) {
            InteractiveManager interactiveManager = detectBootFactory.createInteractiveManager(propertySources);
            MapPropertySource interactivePropertySource = interactiveManager.executeInteractiveMode();
            propertySources.add(0, interactivePropertySource);
        }

        PropertyConfiguration detectConfiguration = new PropertyConfiguration(propertySources);
        EventSystem eventSystem = detectBootFactory.getEventSystem();

        SortedMap<String, String> maskedRawPropertyValues = collectMaskedRawPropertyValues(detectConfiguration);
        Set<String> propertyKeys = new HashSet(DetectProperties.allProperties().getPropertyKeys());

        publishCollectedPropertyValues(maskedRawPropertyValues, eventSystem);

        logger.debug("Configuration processed completely.");

        DetectConfigurationBootManager detectConfigurationBootManager = detectBootFactory.createDetectConfigurationBootManager(detectConfiguration);
        DeprecationResult deprecationResult = detectConfigurationBootManager.checkForDeprecations(detectConfiguration);

        Boolean suppressConfigurationOutput = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_SUPPRESS_CONFIGURATION_OUTPUT.getProperty());
        if (Boolean.FALSE.equals(suppressConfigurationOutput)) {
            detectConfigurationBootManager.printConfiguration(maskedRawPropertyValues, propertyKeys, deprecationResult.getAdditionalNotes());
        }

        Optional<DetectUserFriendlyException> possiblePropertyParseError = detectConfigurationBootManager.validateForPropertyParseErrors();
        if (possiblePropertyParseError.isPresent()) {
            return Optional.of(DetectBootResult.exception(possiblePropertyParseError.get(), detectConfiguration));
        }

        if (deprecationResult.shouldFailFromDeprecations()) {
            detectConfigurationBootManager.printFailingPropertiesMessages(deprecationResult.getDeprecationMessages());

            return Optional.of(DetectBootResult.exit(detectConfiguration));
        }

        logger.debug("Initializing Detect.");

        Configuration freemarkerConfiguration = detectBootFactory.createFreemarkerConfiguration();
        PathResolver pathResolver = detectBootFactory.createPathResolver(detectConfiguration.getValueOrDefault(DetectProperties.DETECT_RESOLVE_TILDE_IN_PATHS.getProperty()));
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
        DirectoryManager directoryManager = detectBootFactory.createDirectoryManager(detectConfigurationFactory);

        DiagnosticSystem diagnosticSystem = null;
        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, detectConfiguration);
        if (diagnosticDecision.shouldCreateDiagnosticSystem()) {
            diagnosticSystem = detectBootFactory.createDiagnosticSystem(diagnosticDecision.isExtended(), detectConfiguration, directoryManager, maskedRawPropertyValues, propertyKeys);
        }

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            try {
                DetectOverrideableFilter inspectorFilter = DetectOverrideableFilter.createArgumentValueFilter(detectArgumentState);
                AirGapCreator airGapCreator = detectBootFactory.createAirGapCreator(detectConfigurationFactory.createConnectionDetails(), detectConfigurationFactory.createDetectExecutableOptions(), freemarkerConfiguration);
                String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty())
                                                    .orElse(null);

                String airGapSuffix = inspectorFilter.getIncludedSet()
                                          .stream()
                                          .sorted()
                                          .collect(Collectors.joining("-"));

                File airGapZip = airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix, gradleInspectorVersion);

                return Optional.of(DetectBootResult.exit(detectConfiguration, airGapZip, directoryManager, diagnosticSystem));
            } catch (DetectUserFriendlyException e) {
                return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
            }
        }

        logger.info("");

        ProductRunData productRunData;
        try {
            RunOptions runOptions = detectConfigurationFactory.createRunOptions();
            DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();
            ProductDecider productDecider = new ProductDecider();
            ProductDecision productDecision = productDecider.decide(detectConfigurationFactory, directoryManager.getUserHome(), detectToolFilter);

            logger.debug("Decided what products will be run. Starting product boot.");

            ProductBoot productBoot = detectBootFactory.createProductBoot(detectConfigurationFactory);
            productRunData = productBoot.boot(productDecision);
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
        }

        if (productRunData == null) {
            logger.info("No products to run, Detect is complete.");
            return Optional.of(DetectBootResult.exit(detectConfiguration, directoryManager, diagnosticSystem));
        }

        DetectableOptionFactory detectableOptionFactory;
        try {
            ProxyInfo detectableProxyInfo = detectConfigurationFactory.createBlackDuckProxyInfo();
            detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem, pathResolver, detectableProxyInfo, new ScanCommandArgumentParser());
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
        }

        //Finished, populate the detect context
        detectContext.registerBean(detectBootFactory.getDetectRun());
        detectContext.registerBean(eventSystem);
        detectContext.registerBean(detectBootFactory.createDetectorProfiler());

        detectContext.registerBean(detectConfiguration);
        detectContext.registerBean(detectableOptionFactory);
        detectContext.registerBean(detectConfigurationFactory);

        detectContext.registerBean(directoryManager);
        detectContext.registerBean(detectBootFactory.createObjectMapper());
        detectContext.registerBean(detectBootFactory.createXmlDocumentBuilder());
        detectContext.registerBean(freemarkerConfiguration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        return Optional.of(DetectBootResult.run(detectConfiguration, productRunData, directoryManager, diagnosticSystem));
    }

    private SortedMap<String, String> collectMaskedRawPropertyValues(PropertyConfiguration propertyConfiguration) throws IllegalAccessException {
        return new TreeMap(propertyConfiguration.getMaskedRawValueMap(new HashSet<>(DetectProperties.allProperties().getProperties()), DetectPropertyUtil.PASSWORDS_AND_TOKENS_PREDICATE));
    }

    private void publishCollectedPropertyValues(Map<String, String> maskedRawPropertyValues, EventSystem eventSystem) {
        eventSystem.publishEvent(Event.RawMaskedPropertyValuesCollected, new TreeMap(maskedRawPropertyValues));
    }

}
