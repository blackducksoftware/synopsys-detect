/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.lifecycle.boot;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.DetectInfoUtility;
import com.synopsys.integration.detect.RunBeanConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationManager;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.DetectPropertyMap;
import com.synopsys.integration.detect.configuration.DetectPropertySource;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.help.DetectArgumentState;
import com.synopsys.integration.detect.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.help.html.HelpHtmlWriter;
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
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.property.SpringPropertySource;
import com.synopsys.integration.detect.util.TildeInPathResolver;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticManager;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.RelevantFileTracker;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectableProfiler;
import com.synopsys.integration.detect.workflow.report.DetectConfigurationReporter;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.exception.IntegrationException;

import freemarker.template.Configuration;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DetectBootFactory detectBootFactory;

    public DetectBoot(DetectBootFactory detectBootFactory) {
        this.detectBootFactory = detectBootFactory;
    }

    public DetectBootResult boot(DetectRun detectRun, final String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext) throws DetectUserFriendlyException, IntegrationException {
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
            return DetectBootResult.exit(detectConfiguration);
        }

        if (detectArgumentState.isHelpHtmlDocument()) {
            printHelpHtmlDocument(options, detectInfo, configuration);
            return DetectBootResult.exit(detectConfiguration);
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            printHelpJsonDocument(options, detectInfo, configuration, gson);
            return DetectBootResult.exit(detectConfiguration);
        }

        printDetectInfo(detectInfo);

        if (detectArgumentState.isInteractive()) {
            startInteractiveMode(detectOptionManager, detectConfiguration, gson, objectMapper);
        }

        processDetectConfiguration(detectInfo, detectRun, detectConfiguration, options);

        detectOptionManager.postConfigurationProcessedInit();

        logger.info("Configuration processed completely.");

        printConfiguration(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT, PropertyAuthority.None), options);

        logger.info("Initializing detect.");

        DetectConfigurationFactory factory = new DetectConfigurationFactory(detectConfiguration);
        DirectoryManager directoryManager = new DirectoryManager(factory.createDirectoryOptions(), detectRun);
        DiagnosticManager diagnosticManager = createDiagnostics(detectOptionManager.getDetectOptions(), detectRun, detectInfo, detectArgumentState, eventSystem, directoryManager);

        checkForInvalidOptions(detectOptionManager);

        if (detectOptionManager.checkForAnyFailureProperties()) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION));
            return DetectBootResult.exit(detectConfiguration);
        }

        logger.info("Main boot completed. Deciding what detect should do.");

        ProductDecider productDecider = new ProductDecider();
        ProductDecision productDecision = productDecider.decide(detectConfiguration, directoryManager.getUserHome());

        logger.info("Decided what products will be run. Starting product boot.");

        ProductBootFactory productBootFactory = new ProductBootFactory(detectConfiguration, detectInfo, eventSystem, detectOptionManager);
        ProductBoot productBoot = new ProductBoot();
        ProductRunData productRunData = productBoot.boot(productDecision, detectConfiguration, new BlackDuckConnectivityChecker(), new PolarisConnectivityChecker(), productBootFactory);
        if (productRunData == null){
            logger.info("No products to run, detect is complete.");
            return DetectBootResult.exit(detectConfiguration);
        }

        //TODO: Only need this if in diagnostic or online (for phone home):
        DetectableProfiler profiler = new DetectableProfiler(eventSystem);

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
        detectContext.registerBean(diagnosticManager);

        detectContext.registerBean(gson);
        detectContext.registerBean(objectMapper);
        detectContext.registerBean(xml);
        detectContext.registerBean(configuration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        //detectContext.registerConfiguration(DetectorBeanConfiguration.class); TODO: Fix
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        return DetectBootResult.run(detectConfiguration, productRunData);
    }

    private void printAppropriateHelp(List<DetectOption> detectOptions, DetectArgumentState detectArgumentState) {
        HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, detectOptions, detectArgumentState);
    }

    private void printHelpHtmlDocument(List<DetectOption> detectOptions, DetectInfo detectInfo, Configuration configuration) {
        HelpHtmlWriter helpHtmlWriter = new HelpHtmlWriter(configuration);
        helpHtmlWriter.writeHtmlDocument(String.format("hub-detect-%s-help.html", detectInfo.getDetectVersion()), detectOptions);
    }

    private void printHelpJsonDocument(List<DetectOption> detectOptions, DetectInfo detectInfo, Configuration configuration, Gson gson) {
        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(configuration, gson);
        helpJsonWriter.writeGsonDocument(String.format("hub-detect-%s-help.json", detectInfo.getDetectVersion()), detectOptions);
    }

    private void printDetectInfo(DetectInfo detectInfo) {
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);
    }

    private void printConfiguration(boolean fullConfiguration, List<DetectOption> detectOptions) {
        DetectConfigurationReporter detectConfigurationReporter = new DetectConfigurationReporter();
        InfoLogReportWriter infoLogReportWriter = new InfoLogReportWriter();
        if (!fullConfiguration) {
            detectConfigurationReporter.print(infoLogReportWriter, detectOptions);
        }
        detectConfigurationReporter.printWarnings(infoLogReportWriter, detectOptions);
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

    private void checkForInvalidOptions(DetectOptionManager detectOptionManager) throws DetectUserFriendlyException {
        final List<DetectOption.OptionValidationResult> invalidDetectOptionResults = detectOptionManager.getAllInvalidOptionResults();
        if (!invalidDetectOptionResults.isEmpty()) {
            throw new DetectUserFriendlyException(invalidDetectOptionResults.get(0).getValidationMessage(), ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private DiagnosticManager createDiagnostics(List<DetectOption> detectOptions, DetectRun detectRun, DetectInfo detectInfo, DetectArgumentState detectArgumentState, EventSystem eventSystem, DirectoryManager directoryManager) {

        if (detectArgumentState.isDiagnostic() || detectArgumentState.isDiagnosticExtended()) {
            boolean extendedMode = detectArgumentState.isDiagnosticExtended();
            RelevantFileTracker relevantFileTracker = new RelevantFileTracker(detectArgumentState.isDiagnostic(), detectArgumentState.isDiagnosticExtended(), directoryManager);
            DiagnosticSystem diagnosticSystem = new DiagnosticSystem(extendedMode, detectOptions, detectRun, detectInfo, relevantFileTracker, directoryManager, eventSystem);
            return DiagnosticManager.createWithDiagnostics(diagnosticSystem);
        } else {
            return DiagnosticManager.createWithoutDiagnostics();
        }
    }


}
