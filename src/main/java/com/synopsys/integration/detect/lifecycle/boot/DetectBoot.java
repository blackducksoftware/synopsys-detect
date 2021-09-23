/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyUtil;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.enumeration.DetectTargetType;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
import com.synopsys.integration.detect.configuration.validation.DeprecationResult;
import com.synopsys.integration.detect.configuration.validation.DetectConfigurationBootManager;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.RunDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.AirGapType;
import com.synopsys.integration.detect.workflow.airgap.AirGapTypeDecider;
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

    private final EventSystem eventSystem;
    private final Gson gson;
    private final DetectBootFactory detectBootFactory;
    private final DetectArgumentState detectArgumentState;
    private final List<PropertySource> propertySources;

    public DetectBoot(EventSystem eventSystem, Gson gson, DetectBootFactory detectBootFactory, DetectArgumentState detectArgumentState, List<PropertySource> propertySources) {
        this.eventSystem = eventSystem;
        this.gson = gson;
        this.detectBootFactory = detectBootFactory;
        this.detectArgumentState = detectArgumentState;
        this.propertySources = propertySources;
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

        SortedMap<String, String> maskedRawPropertyValues = collectMaskedRawPropertyValues(detectConfiguration);
        Set<String> propertyKeys = new HashSet<>(DetectProperties.allProperties().getPropertyKeys());

        publishCollectedPropertyValues(maskedRawPropertyValues);

        logger.debug("Configuration processed completely.");

        DetectConfigurationBootManager detectConfigurationBootManager = detectBootFactory.createDetectConfigurationBootManager(detectConfiguration);
        DeprecationResult deprecationResult = detectConfigurationBootManager.checkForDeprecations(detectConfiguration);
        detectConfigurationBootManager.printConfiguration(maskedRawPropertyValues, propertyKeys, deprecationResult.getAdditionalNotes());

        Optional<DetectUserFriendlyException> possiblePropertyParseError = detectConfigurationBootManager.validateForPropertyParseErrors();
        if (possiblePropertyParseError.isPresent()) {
            return Optional.of(DetectBootResult.exception(possiblePropertyParseError.get(), detectConfiguration));
        }

        logger.debug("Initializing Detect.");

        Configuration freemarkerConfiguration = detectBootFactory.createFreemarkerConfiguration();
        PathResolver pathResolver = detectBootFactory.createPathResolver();
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver, gson);
        DirectoryManager directoryManager = detectBootFactory.createDirectoryManager(detectConfigurationFactory);

        DiagnosticSystem diagnosticSystem = null;
        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, detectConfiguration);
        if (diagnosticDecision.shouldCreateDiagnosticSystem()) {
            diagnosticSystem = detectBootFactory.createDiagnosticSystem(diagnosticDecision.isExtended(), detectConfiguration, directoryManager, maskedRawPropertyValues, propertyKeys);
        }

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            try {
                AirGapType airGapType = new AirGapTypeDecider().decide(detectArgumentState);
                AirGapCreator airGapCreator = detectBootFactory.createAirGapCreator(detectConfigurationFactory.createConnectionDetails(), detectConfigurationFactory.createDetectExecutableOptions(), freemarkerConfiguration);
                String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty())
                                                    .orElse(null);

                File airGapZip = airGapCreator.createAirGapZip(airGapType, directoryManager.getRunHomeDirectory(), gradleInspectorVersion);

                return Optional.of(DetectBootResult.exit(detectConfiguration, airGapZip, directoryManager, diagnosticSystem));
            } catch (DetectUserFriendlyException e) {
                return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
            }
        }

        logger.info("");

        ProductRunData productRunData;
        try {

            ProductDecider productDecider = new ProductDecider();
            BlackDuckDecision blackDuckDecision = productDecider.decideBlackDuck(detectConfigurationFactory.createBlackDuckConnectionDetails(), detectConfigurationFactory.createBlackDuckSignatureScannerOptions(),
                detectConfigurationFactory.createScanMode(), detectConfigurationFactory.createBdioOptions());
            RunDecision runDecision = new RunDecision(detectConfigurationFactory.createDetectTarget() == DetectTargetType.IMAGE); //TODO: Move to proper decision home. -jp
            DetectToolFilter detectToolFilter = detectConfigurationFactory.createToolFilter(runDecision, blackDuckDecision);

            logger.debug("Decided what products will be run. Starting product boot.");

            ProductBoot productBoot = detectBootFactory.createProductBoot(detectConfigurationFactory);
            productRunData = productBoot.boot(blackDuckDecision, detectToolFilter);
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
            detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem, pathResolver, detectableProxyInfo);
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, detectConfiguration, directoryManager, diagnosticSystem));
        }

        BootSingletons bootSingletons = detectBootFactory.createRunDependencies(productRunData, detectConfiguration, detectableOptionFactory, detectConfigurationFactory, directoryManager, freemarkerConfiguration);
        return Optional.of(DetectBootResult.run(bootSingletons, detectConfiguration, productRunData, directoryManager, diagnosticSystem));
    }

    private SortedMap<String, String> collectMaskedRawPropertyValues(PropertyConfiguration propertyConfiguration) throws IllegalAccessException {
        return new TreeMap<>(propertyConfiguration.getMaskedRawValueMap(new HashSet<>(DetectProperties.allProperties().getProperties()), DetectPropertyUtil.PASSWORDS_AND_TOKENS_PREDICATE));
    }

    private void publishCollectedPropertyValues(Map<String, String> maskedRawPropertyValues) {
        eventSystem.publishEvent(Event.RawMaskedPropertyValuesCollected, new TreeMap<>(maskedRawPropertyValues));
    }

}
