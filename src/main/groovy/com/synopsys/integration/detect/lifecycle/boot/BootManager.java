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
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.DetectInfoUtility;
import com.synopsys.integration.detect.DetectorBeanConfiguration;
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
import com.synopsys.integration.detect.lifecycle.run.RunDecider;
import com.synopsys.integration.detect.lifecycle.run.RunDecision;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.property.SpringPropertySource;
import com.synopsys.integration.detect.util.TildeInPathResolver;
import com.synopsys.integration.detect.workflow.ConnectivityManager;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticManager;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.diagnostic.RelevantFileTracker;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.BomToolProfiler;
import com.synopsys.integration.detect.workflow.report.DetectConfigurationReporter;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.PolarisEnvironmentCheck;
import com.synopsys.integration.util.IntEnvironmentVariables;

import freemarker.template.Configuration;

public class BootManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BootFactory bootFactory;

    public BootManager(BootFactory bootFactory) {
        this.bootFactory = bootFactory;
    }

    public BootResult boot(DetectRun detectRun, final String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext) throws DetectUserFriendlyException, IntegrationException {
        Gson gson = bootFactory.createGson();
        ObjectMapper objectMapper = bootFactory.createObjectMapper();
        DocumentBuilder xml = bootFactory.createXmlDocumentBuilder();
        Configuration configuration = bootFactory.createConfiguration();

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
            return BootResult.exit(detectConfiguration);
        }

        if (detectArgumentState.isHelpHtmlDocument()) {
            printHelpHtmlDocument(options, detectInfo, configuration);
            return BootResult.exit(detectConfiguration);
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            printHelpJsonDocument(options, detectInfo, configuration, gson);
            return BootResult.exit(detectConfiguration);
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
            return BootResult.exit(detectConfiguration);
        }

        logger.info("Main boot completed. Deciding what detect should do.");
        Properties properties = new Properties();
        properties.setProperty("user.home", directoryManager.getUserHome().getAbsolutePath());
        PolarisEnvironmentCheck polarisEnvironmentCheck = new PolarisEnvironmentCheck(new IntEnvironmentVariables(), properties);

        RunDecider runDecider = new RunDecider();
        RunDecision runDecision = runDecider.decide(detectConfiguration, polarisEnvironmentCheck);

        boolean willRunSomething = runDecision.willRunBlackduck() || runDecision.willRunPolaris();
        if (!willRunSomething) {
            throw new DetectUserFriendlyException("Your environment was not sufficiently configured to run blackduck or polaris. Please configure your environment for at least one product.", ExitCodeType.FAILURE_CONFIGURATION);
        }

        ConnectivityManager connectivityManager;
        if (runDecision.willRunBlackduck()) {
            boolean offline = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
            if (offline) {
                logger.info("Detect is in offline mode.");
                connectivityManager = ConnectivityManager.offline();
            } else {
                logger.info("Detect is in online mode.");
                //check my connectivity
                ConnectivityChecker connectivityChecker = new ConnectivityChecker();
                ConnectivityResult connectivityResult = connectivityChecker.determineConnectivity(detectConfiguration, detectOptionManager, detectInfo, gson, objectMapper, eventSystem);

            if (connectivityResult.isSuccessfullyConnected()) {
                logger.info("Detect is capable of communicating with server.");
                connectivityManager = ConnectivityManager.online(connectivityResult.getBlackDuckServicesFactory(), connectivityResult.getPhoneHomeManager(), connectivityResult.getBlackDuckServerConfig());
            } else {
                logger.info("Detect is NOT capable of communicating with server.");
                logger.info("Please double check the Detect documentation: https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/622633/Hub+Detect");
                if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK, PropertyAuthority.None)) {
                    logger.info(connectivityResult.getFailureReason());
                    logger.info(String.format("%s is set to 'true' so Detect will simply exit.", DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK.getPropertyName()));
                    return BootResult.exit(detectConfiguration);
                } else {
                    throw new DetectUserFriendlyException("Could not communicate with Black Duck: " + connectivityResult.getFailureReason(), ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
                }
            }
        }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION, PropertyAuthority.None)) {
                logger.info(String.format("%s is set to 'true' so Detect will not run.", DetectProperty.DETECT_TEST_CONNECTION.getPropertyName()));
                return BootResult.exit(detectConfiguration);
            }
        } else {
            connectivityManager = null;
        }

        //TODO: Only need this if in diagnostic or online (for phone home):
        BomToolProfiler profiler = new BomToolProfiler(eventSystem);

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
        detectContext.registerBean(connectivityManager);

        detectContext.registerBean(gson);
        detectContext.registerBean(objectMapper);
        detectContext.registerBean(xml);
        detectContext.registerBean(configuration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.registerConfiguration(DetectorBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        BootResult result = new BootResult();
        result.bootType = BootResult.BootType.CONTINUE;
        result.detectConfiguration = detectConfiguration;
        result.runDecision = runDecision;
        return result;
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
