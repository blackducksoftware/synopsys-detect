/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect;

import java.util.Optional;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.lifecycle.boot.BootFactory;
import com.blackducksoftware.integration.hub.detect.lifecycle.boot.BootManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.boot.BootResult;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.run.RunResult;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeUtility;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ShutdownManager;
import com.blackducksoftware.integration.hub.detect.workflow.ConnectivityManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.log.Slf4jIntLogger;

//@SpringBootApplication
//@Configuration
//@Import({ OldBeanConfiguration.class })
//@EnableAspectJAutoProxy
public class Application implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    private ConfigurableEnvironment environment;

    @Autowired
    public Application(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public static void main(final String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.logStartupInfo(false);
        builder.run(args);
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        final long startTime = System.currentTimeMillis();

        //Events, Status and Exit Codes are required even if boot fails.
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);

        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);

        ReportManager reportManager = ReportManager.createDefault(eventSystem);

        //Before boot even begins, we create a new Spring context for Detect to work within.
        logger.info("Preparing detect.");
        DetectRun detectRun = DetectRun.createDefault();
        DetectContext detectContext = new DetectContext(detectRun);

        BootResult bootResult = null;
        Optional<RunResult> runResult = Optional.empty();
        try {
            logger.info("Detect boot begin.");
            BootManager bootManager = new BootManager(new BootFactory());
            bootResult = bootManager.boot(detectRun, applicationArguments.getSourceArgs(), environment, eventSystem, detectContext);
            logger.info("Detect boot completed.");
        } catch (final Exception e) {
            logger.error("Detect boot failed.");
            exitCodeManager.requestExitCode(e);
        }
        if (bootResult != null && bootResult.bootType == BootResult.BootType.CONTINUE) {
            logger.info("Detect will attempt to run.");
            RunManager runManager = new RunManager(detectContext);
            try {
                logger.info("Detect run begin: " + detectRun.getRunId());
                runResult = Optional.ofNullable(runManager.run());
                logger.info("Detect run completed.");
            } catch (final Exception e) {
                logger.error("Detect run failed: " + e.getMessage());
                logger.debug("An exception was thrown during the detect run.", e);
                exitCodeManager.requestExitCode(e);
            }
            try {
                logger.info("Detect will attempt to shutdown.");
                DiagnosticManager diagnosticManager = detectContext.getBean(DiagnosticManager.class);
                DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);
                DetectConfiguration detectConfiguration = detectContext.getBean(DetectConfiguration.class);
                ConnectivityManager connectivityManager = detectContext.getBean(ConnectivityManager.class);
                ShutdownManager shutdownManager = new ShutdownManager(connectivityManager, statusManager, exitCodeManager, directoryManager, detectConfiguration, reportManager, diagnosticManager);
                logger.info("Detect shutdown begin.");
                shutdownManager.shutdown(runResult);
                logger.info("Detect shutdown completed.");
            } catch (final Exception e) {
                logger.error("Detect shutdown failed.");
                exitCodeManager.requestExitCode(e);
            }
        } else {
            logger.debug("Detect will NOT attempt to run.");
        }

        logger.info("All detect actions completed.");

        //Determine how detect should actually exit
        boolean printOutput = true;
        boolean shouldForceSuccess = false;
        if (bootResult != null && bootResult.detectConfiguration != null) {
            printOutput = !bootResult.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT, PropertyAuthority.None);
            shouldForceSuccess = bootResult.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_FORCE_SUCCESS, PropertyAuthority.None);
        }

        //Generally, when requesting a failure status, an exit code is also requested, but if it is not, we default to an unknown error.
        if (statusManager.hasAnyFailure()) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_UNKNOWN_ERROR, "A failure status was requested by one or more of Detect's tools."));
        }

        //Find the final (as requested) exit code
        ExitCodeType finalExitCode = exitCodeManager.getWinningExitCode();

        //Print detect's status
        if (printOutput) {
            reportManager.printDetectorIssues();
            statusManager.logDetectResults(new Slf4jIntLogger(logger), finalExitCode);
        }

        //Print duration of run
        final long endTime = System.currentTimeMillis();
        logger.info(String.format("Detect duration: %s", DurationFormatUtils.formatPeriod(startTime, endTime, "HH'h' mm'm' ss's' SSS'ms'")));

        //Exit with formal exit code
        if (finalExitCode != ExitCodeType.SUCCESS && shouldForceSuccess) {
            logger.warn(String.format("Forcing success: Exiting with exit code 0. Ignored exit code was %s.", finalExitCode.getExitCode()));
            System.exit(0);
        } else if (finalExitCode != ExitCodeType.SUCCESS) {
            logger.error(String.format("Exiting with code %s - %s", finalExitCode.getExitCode(), finalExitCode.toString()));
        }

        System.exit(finalExitCode.getExitCode());
    }
}
