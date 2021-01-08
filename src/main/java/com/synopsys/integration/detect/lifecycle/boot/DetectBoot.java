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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
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
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.help.print.DetectInfoPrinter;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
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
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticsDecider;
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

    public Optional<DetectBootResult> boot() throws DetectUserFriendlyException, IOException, IllegalAccessException {
        Configuration freemarkerConfiguration = detectBootFactory.createFreemarkerConfiguration();

        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(DEFAULT_PRINT_STREAM, DetectProperties.allProperties(), Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            HelpJsonManager helpJsonManager = detectBootFactory.createHelpJsonManager();
            helpJsonManager.createHelpJsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()));
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        // FIXME: This prints three lines of code.
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(DEFAULT_PRINT_STREAM, detectInfo);

        if (detectArgumentState.isInteractive()) {
            InteractiveManager interactiveManager = detectBootFactory.createInteractiveManager(propertySources);
            MapPropertySource interactivePropertySource = interactiveManager.executeInteractiveMode();
            propertySources.add(0, interactivePropertySource);
        }

        PropertyConfiguration detectConfiguration = new PropertyConfiguration(propertySources);

        logger.debug("Configuration processed completely.");

        DetectConfigurationPrinter detectConfigurationPrinter = detectBootFactory.createDetectConfigurationPrinter();
        Optional<DetectBootResult> configurationResult = detectConfigurationPrinter.printConfiguration(detectConfiguration);
        if (configurationResult.isPresent()) {
            return configurationResult;
        }

        logger.debug("Initializing Detect.");

        PathResolver pathResolver = detectBootFactory.createPathResolver(detectConfiguration.getValueOrDefault(DetectProperties.DETECT_RESOLVE_TILDE_IN_PATHS.getProperty()));
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
        DirectoryManager directoryManager = detectBootFactory.createDirectoryManager(detectConfigurationFactory);
        DiagnosticSystem diagnosticSystem = detectBootFactory.createDiagnosticSystem(new DiagnosticsDecider(detectArgumentState), detectConfiguration, directoryManager)
                                                .orElse(null);

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            File airGapZip;
            try {
                DetectOverrideableFilter inspectorFilter = DetectOverrideableFilter.createArgumentValueFilter(detectArgumentState);
                AirGapCreator airGapCreator = detectBootFactory.createAirGapCreator(detectConfigurationFactory, freemarkerConfiguration);
                String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty())
                                                    .orElse(null);

                String airGapSuffix = inspectorFilter.getIncludedSet()
                                          .stream()
                                          .sorted()
                                          .collect(Collectors.joining("-"));

                airGapZip = airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix, gradleInspectorVersion);
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


        ProductRunData productRunData;
        try {
            ProductBoot productBoot = detectBootFactory.createProductBoot(detectConfigurationFactory);
            productRunData = productBoot.boot(productDecision);
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

        //Finished, populate the detect context
        detectContext.registerBean(detectBootFactory.getDetectRun());
        detectContext.registerBean(detectBootFactory.getEventSystem());
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

}
