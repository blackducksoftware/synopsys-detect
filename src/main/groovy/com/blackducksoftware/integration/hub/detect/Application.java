/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect;

import java.io.Console;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory;
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper;
import com.blackducksoftware.integration.hub.bdio.BdioTransformer;
import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.ArgumentState;
import com.blackducksoftware.integration.hub.detect.help.ArgumentStateParser;
import com.blackducksoftware.integration.hub.detect.help.DetectBaseOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectConfigurationPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectInfoPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ConsoleInteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.InteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ScannerInteractiveReader;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.strategy.StrategyManager;
import com.blackducksoftware.integration.hub.detect.summary.DetectSummary;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.log.SilentLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;

@SpringBootApplication
public class Application implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private DetectOptionManager detectOptionManager;

    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectProjectManager detectProjectManager;

    @Autowired
    private HelpPrinter helpPrinter;

    @Autowired
    private HelpHtmlWriter helpHtmlWriter;

    @Autowired
    private HubManager hubManager;

    @Autowired
    private HubServiceWrapper hubServiceWrapper;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Autowired
    private DetectSummary detectSummary;

    @Autowired
    private InteractiveManager interactiveManager;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private List<ExitCodeReporter> exitCodeReporters;

    @Autowired
    private DetectPhoneHomeManager detectPhoneHomeManager;

    @Autowired
    private ArgumentStateParser argumentStateParser;

    @Autowired
    private StrategyManager strategyManager;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args);
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        final long start = System.currentTimeMillis();

        try {
            detectInfo.init();
            detectOptionManager.init();

            final List<DetectBaseOption> options = detectOptionManager.getDetectOptions();

            final String[] applicationArgs = applicationArguments.getSourceArgs();
            final ArgumentState argumentState = argumentStateParser.parseArgs(applicationArgs);

            if (argumentState.isHelp || argumentState.isDeprecatedHelp || argumentState.isVerboseHelp) {
                helpPrinter.printAppropriateHelpMessage(System.out, options, argumentState);
                return;
            }

            if (argumentState.isHelpDocument) {
                helpHtmlWriter.writeHelpMessage(String.format("hub-detect-%s-help.html", detectInfo.getDetectVersion()));
                return;
            }

            if (argumentState.isInteractive) {
                final InteractiveReader interactiveReader = createInteractiveReader();
                final PrintStream interactivePrintStream = new PrintStream(System.out);
                interactiveManager.interact(interactiveReader, interactivePrintStream);
            }

            detectConfiguration.init(options);
            detectOptionManager.postInit();

            logger.info("Configuration processed completely.");

            if (!detectConfiguration.getSuppressConfigurationOutput()) {
                final DetectInfoPrinter infoPrinter = new DetectInfoPrinter();
                final DetectConfigurationPrinter detectConfigurationPrinter = new DetectConfigurationPrinter();

                infoPrinter.printInfo(System.out, detectInfo);
                detectConfigurationPrinter.print(System.out, detectInfo, detectConfiguration, options);
            }

            if (detectConfiguration.getFailOnConfigWarning()) {
                final boolean foundConfigWarning = options.stream().anyMatch(option -> option.getWarnings().size() > 0);
                if (foundConfigWarning) {
                    throw new DetectUserFriendlyException("Failing because the configuration had warnings.", ExitCodeType.FAILURE_CONFIGURATION);
                }
            }

            final List<DetectBaseOption> unacceptableDetectOtions = detectOptionManager.findUnacceptableValues();
            if (unacceptableDetectOtions.size() > 0) {
                final DetectBaseOption firstUnacceptableDetectOption = unacceptableDetectOtions.get(0);
                final String msg = String.format("%s: Unknown value '%s', acceptable values are %s",
                        firstUnacceptableDetectOption.getKey(),
                        firstUnacceptableDetectOption.getResolvedValue(),
                        firstUnacceptableDetectOption.getAcceptableValues().stream().collect(Collectors.joining(",")));
                throw new DetectUserFriendlyException(msg, ExitCodeType.FAILURE_GENERAL_ERROR);
            }

            if (detectConfiguration.getTestConnection()) {
                hubServiceWrapper.assertHubConnection(new SilentLogger());
                return;
            }

            if (detectConfiguration.getDisableWithoutHub()) {
                try {
                    logger.info("Testing Hub connection to see if Detect should run");
                    hubServiceWrapper.assertHubConnection(new SilentLogger());
                } catch (final IntegrationException e) {
                    logger.info("Not able to initialize Hub conection: " + e.getMessage());
                    logger.debug("Stack trace: ", e);
                    logger.info("Detect will not run");
                    return;
                }
            }

            if (detectConfiguration.getHubOfflineMode()) {
                detectPhoneHomeManager.initOffline();
            } else {
                hubServiceWrapper.init();
            }

            strategyManager.init();

            final DetectProject detectProject = detectProjectManager.createDetectProject();
            final List<File> createdBdioFiles = detectProjectManager.createBdioFiles(detectProject);
            logger.info("Project Name: " + detectProject.getProjectName());
            logger.info("Project Version Name: " + detectProject.getProjectVersionName());
            if (!detectConfiguration.getHubOfflineMode()) {
                final ProjectVersionView projectVersionView = hubManager.updateHubProjectVersion(detectProject, createdBdioFiles);
                hubManager.performPostHubActions(detectProject, projectVersionView);
            } else if (!detectConfiguration.getHubSignatureScannerDisabled()) {
                hubSignatureScanner.scanPathsOffline(detectProject);
            }

            for (final ExitCodeReporter exitCodeReporter : exitCodeReporters) {
                exitCodeType = ExitCodeType.getWinningExitCodeType(exitCodeType, exitCodeReporter.getExitCodeType());
            }
        } catch (final Exception e) {
            populateExitCodeFromExceptionDetails(e);
        } finally {
            try {
                detectPhoneHomeManager.endPhoneHome();
            } catch (final Exception e) {
                logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
            }

            if (!detectConfiguration.getSuppressResultsOutput()) {
                detectSummary.logResults(new Slf4jIntLogger(logger), exitCodeType);
            }

            detectFileManager.cleanupDirectories();
        }

        final long end = System.currentTimeMillis();
        logger.info(String.format("Hub-Detect run duration: %s", DurationFormatUtils.formatPeriod(start, end, "HH'h' mm'm' ss's' SSS'ms'")));
        if (detectConfiguration.getForceSuccess() && exitCodeType.getExitCode() != 0) {
            logger.warn(String.format("Forcing success: Exiting with 0. Desired exit code was %s.", exitCodeType.getExitCode()));
            System.exit(0);
        } else if (exitCodeType.getExitCode() != 0) {
            logger.error(String.format("Exiting with code %s - %s", exitCodeType.getExitCode(), exitCodeType.toString()));
        }
        System.exit(exitCodeType.getExitCode());
    }

    private InteractiveReader createInteractiveReader() {
        final Console console = System.console();
        if (console != null) {
            return new ConsoleInteractiveReader(console);
        } else {
            logger.warn("It may be insecure to enter passwords because you are running in a virtual console.");
            return new ScannerInteractiveReader(System.in);
        }
    }

    private void populateExitCodeFromExceptionDetails(final Exception e) {
        if (e instanceof DetectUserFriendlyException) {
            if (e.getCause() != null) {
                logger.debug(e.getCause().getMessage(), e.getCause());
            }
            final DetectUserFriendlyException friendlyException = (DetectUserFriendlyException) e;
            exitCodeType = friendlyException.getExitCodeType();
        } else if (e instanceof IntegrationException) {
            logger.error("An unrecoverable error occurred - most likely this is due to your environment and/or configuration. Please double check the Hub Detect documentation: https://blackducksoftware.atlassian.net/wiki/x/Y7HtAg");
            logger.debug(e.getMessage(), e);
            exitCodeType = ExitCodeType.FAILURE_GENERAL_ERROR;
        } else {
            logger.error("An unknown/unexpected error occurred");
            logger.debug(e.getMessage(), e);
            exitCodeType = ExitCodeType.FAILURE_UNKNOWN_ERROR;
        }
        logger.error(e.getMessage());
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public SimpleBdioFactory simpleBdioFactory() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        return new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer, externalIdFactory(), gson());
    }

    @Bean
    public BdioTransformer bdioTransformer() {
        return new BdioTransformer();
    }

    @Bean
    public ExternalIdFactory externalIdFactory() {
        return new ExternalIdFactory();
    }

    @Bean
    public IntegrationEscapeUtil integrationEscapeUtil() {
        return new IntegrationEscapeUtil();
    }

    @Bean
    public Configuration configuration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(Application.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    @Bean
    public DocumentBuilder xmlDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

}
