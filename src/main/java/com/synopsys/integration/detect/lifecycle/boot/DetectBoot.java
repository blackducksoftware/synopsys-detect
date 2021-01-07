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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

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
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
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

    private final DetectBootFactory detectBootFactory;

    public DetectBoot(DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public Optional<DetectBootResult> boot(String[] sourceArgs, ConfigurableEnvironment environment, DetectContext detectContext) throws DetectUserFriendlyException, IOException, IllegalAccessException {
        Configuration configuration = detectBootFactory.createConfiguration();

        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);

        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();

        DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        List<PropertySource> propertySources = detectBootFactory.initializePropertySources(environment);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(System.out, DetectProperties.allProperties(), Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources)));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            HelpJsonManager helpJsonManager = detectBootFactory.createHelpJsonManager();
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

        DetectConfigurationPrinter detectConfigurationPrinter = detectBootFactory.createDetectConfigurationPrinter();
        Optional<DetectBootResult> configurationResult = detectConfigurationPrinter.printConfiguration(detectConfiguration);
        if (configurationResult.isPresent()) {
            return configurationResult;
        }

        logger.debug("Initializing Detect.");

        // Building code pieces
        PathResolver pathResolver = detectBootFactory.createPathResolver(detectConfiguration.getValueOrDefault(DetectProperties.DETECT_RESOLVE_TILDE_IN_PATHS.getProperty()));
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration,pathResolver);
        DirectoryManager directoryManager = detectBootFactory.createDirectoryManager(detectConfigurationFactory);
        DiagnosticSystem diagnosticSystem = detectBootFactory.createDiagnosticSystem(new DiagnosticsDecider(detectArgumentState), detectConfiguration, directoryManager)
                                                .orElse(null);
        // end building code pieces

        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            File airGapZip;
            try {
                DetectOverrideableFilter inspectorFilter = new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
                AirGapCreator airGapCreator = detectBootFactory.createAirGapCreator(detectConfigurationFactory, configuration);
                String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty())
                                                    .orElse(null);
                String airGapSuffix = inspectorFilter.getIncludedSet().stream().sorted().collect(Collectors.joining("-"));

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
            ProductBootFactory productBootFactory = detectBootFactory.createProductBootFactory(detectConfigurationFactory);
            ProductBootOptions productBootOptions = detectConfigurationFactory.createProductBootOptions();
            ProductBoot productBoot = detectBootFactory.createProductBoot();
            productRunData = productBoot.boot(productDecision, productBootOptions, productBootFactory);
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
        detectContext.registerBean(configuration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        return Optional.of(DetectBootResult.run(detectConfiguration, productRunData, directoryManager, diagnosticSystem));
    }


}
