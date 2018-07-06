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
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.detect.configuration.AdditionalPropertyConfig;
import com.blackducksoftware.integration.hub.detect.configuration.ConfigurationManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.ArgumentState;
import com.blackducksoftware.integration.hub.detect.help.ArgumentStateParser;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectConfigurationPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectInfoPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceWrapper;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ConsoleInteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.InteractiveReader;
import com.blackducksoftware.integration.hub.detect.interactive.reader.ScannerInteractiveReader;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectProjectManager;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.blackducksoftware.integration.hub.detect.workflow.summary.DetectSummaryManager;
import com.blackducksoftware.integration.log.SilentLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;

@SpringBootApplication
@Import({ BeanConfiguration.class })
public class Application implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    private final DetectOptionManager detectOptionManager;
    private final DetectInfo detectInfo;
    private final AdditionalPropertyConfig additionalPropertyConfig;
    private final DetectConfigWrapper detectConfigWrapper;
    private final ConfigurationManager configurationManager;
    private final DetectProjectManager detectProjectManager;
    private final HelpPrinter helpPrinter;
    private final HelpHtmlWriter helpHtmlWriter;
    private final HubManager hubManager;
    private final HubServiceWrapper hubServiceWrapper;
    private final HubSignatureScanner hubSignatureScanner;
    private final DetectSummaryManager detectSummaryManager;
    private final InteractiveManager interactiveManager;
    private final DetectFileManager detectFileManager;
    private final List<ExitCodeReporter> exitCodeReporters;
    private final PhoneHomeManager phoneHomeManager;
    private final ArgumentStateParser argumentStateParser;

    private ExitCodeType exitCodeType = ExitCodeType.SUCCESS;

    @Autowired
    public Application(final DetectOptionManager detectOptionManager, final DetectInfo detectInfo, final AdditionalPropertyConfig additionalPropertyConfig, final DetectConfigWrapper detectConfigWrapper,
            final ConfigurationManager configurationManager, final DetectProjectManager detectProjectManager, final HelpPrinter helpPrinter, final HelpHtmlWriter helpHtmlWriter, final HubManager hubManager,
            final HubServiceWrapper hubServiceWrapper, final HubSignatureScanner hubSignatureScanner, final DetectSummaryManager detectSummaryManager, final InteractiveManager interactiveManager, final DetectFileManager detectFileManager,
            final List<ExitCodeReporter> exitCodeReporters, final PhoneHomeManager phoneHomeManager, final ArgumentStateParser argumentStateParser) {
        this.detectOptionManager = detectOptionManager;
        this.detectInfo = detectInfo;
        this.additionalPropertyConfig = additionalPropertyConfig;
        this.detectConfigWrapper = detectConfigWrapper;
        this.configurationManager = configurationManager;
        this.detectProjectManager = detectProjectManager;
        this.helpPrinter = helpPrinter;
        this.helpHtmlWriter = helpHtmlWriter;
        this.hubManager = hubManager;
        this.hubServiceWrapper = hubServiceWrapper;
        this.hubSignatureScanner = hubSignatureScanner;
        this.detectSummaryManager = detectSummaryManager;
        this.interactiveManager = interactiveManager;
        this.detectFileManager = detectFileManager;
        this.exitCodeReporters = exitCodeReporters;
        this.phoneHomeManager = phoneHomeManager;
        this.argumentStateParser = argumentStateParser;
    }

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args);
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        final long startTime = System.currentTimeMillis();

        try {
            final boolean runDetect = initializeRun(applicationArguments.getSourceArgs());

            if (runDetect) {
                final DetectProject detectProject = detectProjectManager.createDetectProject();
                logger.info("Project Name: " + detectProject.getProjectName());
                logger.info("Project Version Name: " + detectProject.getProjectVersion());
                if (!detectConfigWrapper.getBooleanProperty(DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE)) {
                    final ProjectVersionView projectVersionView = hubManager.updateHubProjectVersion(detectProject);
                    hubManager.performPostHubActions(detectProject, projectVersionView);
                } else if (!detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_DISABLED)) {
                    hubSignatureScanner.scanPathsOffline(detectProject);
                }

                for (final ExitCodeReporter exitCodeReporter : exitCodeReporters) {
                    exitCodeType = ExitCodeType.getWinningExitCodeType(exitCodeType, exitCodeReporter.getExitCodeType());
                }
            }
        } catch (final Exception e) {
            populateExitCodeFromExceptionDetails(e);
        } finally {
            cleanupRun();
        }

        endRun(startTime);
    }

    private boolean initializeRun(final String[] sourceArgs) throws Exception {
        final boolean runDetect = true;
        final boolean exitDetect = false;

        detectInfo.init();
        additionalPropertyConfig.init();
        detectConfigWrapper.init();
        detectOptionManager.init();

        final List<DetectOption> options = detectOptionManager.getDetectOptions();

        final ArgumentState argumentState = argumentStateParser.parseArgs(sourceArgs);

        if (argumentState.isHelp || argumentState.isDeprecatedHelp || argumentState.isVerboseHelp) {
            helpPrinter.printAppropriateHelpMessage(System.out, options, argumentState);
            return exitDetect;
        }

        if (argumentState.isHelpDocument) {
            helpHtmlWriter.writeHelpMessage(String.format("hub-detect-%s-help.html", detectInfo.getDetectVersion()));
            return exitDetect;
        }

        if (argumentState.isInteractive) {
            final InteractiveReader interactiveReader;
            final Console console = System.console();

            if (console != null) {
                interactiveReader = new ConsoleInteractiveReader(console);
            } else {
                logger.warn("It may be insecure to enter passwords because you are running in a virtual console.");
                interactiveReader = new ScannerInteractiveReader(System.in);
            }

            final PrintStream interactivePrintStream = new PrintStream(System.out);
            interactiveManager.interact(interactiveReader, interactivePrintStream);
        }

        configurationManager.initialize(options);
        detectOptionManager.postInit();

        logger.info("Configuration processed completely.");

        if (!detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT)) {
            final DetectInfoPrinter infoPrinter = new DetectInfoPrinter();
            final DetectConfigurationPrinter detectConfigurationPrinter = new DetectConfigurationPrinter();

            infoPrinter.printInfo(System.out, detectInfo);
            detectConfigurationPrinter.print(System.out, options);
        }

        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_FAIL_CONFIG_WARNING) && options.stream().anyMatch(DetectOption::hasWarnings)) {
            throw new DetectUserFriendlyException("Failing because the configuration had warnings.", ExitCodeType.FAILURE_CONFIGURATION);
        }

        final List<DetectOption> unacceptableDetectOtions = detectOptionManager.findUnacceptableValues();
        if (unacceptableDetectOtions.size() > 0) {
            final DetectOption firstUnacceptableDetectOption = unacceptableDetectOtions.get(0);
            final String msg = String.format("%s: Unknown value '%s', acceptable values are %s",
                    firstUnacceptableDetectOption.getDetectProperty().getPropertyName(),
                    firstUnacceptableDetectOption.getResolvedValue(),
                    firstUnacceptableDetectOption.getAcceptableValues().stream().collect(Collectors.joining(",")));
            throw new DetectUserFriendlyException(msg, ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION)) {
            hubServiceWrapper.assertHubConnection(new SilentLogger());
            return exitDetect;
        }

        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_DISABLE_WITHOUT_HUB)) {
            try {
                logger.info("Testing Hub connection to see if Detect should run");
                hubServiceWrapper.assertHubConnection(new SilentLogger());
            } catch (final IntegrationException e) {
                logger.info("Not able to initialize Hub conection: " + e.getMessage());
                logger.debug("Stack trace: ", e);
                logger.info("Detect will not run");
                return exitDetect;
            }
        }

        if (detectConfigWrapper.getBooleanProperty(DetectProperty.BLACKDUCK_HUB_OFFLINE_MODE)) {
            phoneHomeManager.initOffline();
        } else {
            hubServiceWrapper.init();
            phoneHomeManager.init(hubServiceWrapper.createPhoneHomeService());
            phoneHomeManager.startPhoneHome();
        }

        return runDetect;
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

    private void cleanupRun() {
        try {
            phoneHomeManager.endPhoneHome();
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
        }

        if (!detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT)) {
            detectSummaryManager.logDetectResults(new Slf4jIntLogger(logger), exitCodeType);
        }

        detectFileManager.cleanupDirectories();
    }

    private void endRun(final long startTime) {
        final long endTime = System.currentTimeMillis();
        logger.info(String.format("Hub-Detect run duration: %s", DurationFormatUtils.formatPeriod(startTime, endTime, "HH'h' mm'm' ss's' SSS'ms'")));
        if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_FORCE_SUCCESS) && exitCodeType.getExitCode() != 0) {
            logger.warn(String.format("Forcing success: Exiting with 0. Desired exit code was %s.", exitCodeType.getExitCode()));
            System.exit(0);
        } else if (exitCodeType.getExitCode() != 0) {
            logger.error(String.format("Exiting with code %s - %s", exitCodeType.getExitCode(), exitCodeType.toString()));
        }
        System.exit(exitCodeType.getExitCode());
    }

}
