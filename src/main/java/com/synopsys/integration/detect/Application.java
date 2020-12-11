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
package com.synopsys.integration.detect;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.DetectBoot;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootResult;
import com.synopsys.integration.detect.lifecycle.run.RunManager;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeUtility;
import com.synopsys.integration.detect.lifecycle.shutdown.ShutdownManager;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.report.ReportListener;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutputManager;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.DetectStatusManager;
import com.synopsys.integration.log.Slf4jIntLogger;

public class Application implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    private static boolean SHOULD_EXIT = true;

    private final ConfigurableEnvironment environment;

    @Autowired
    public Application(ConfigurableEnvironment environment) {
        this.environment = environment;
        environment.setIgnoreUnresolvableNestedPlaceholders(true);
    }

    public static boolean shouldExit() {
        return SHOULD_EXIT;
    }

    public static void setShouldExit(boolean shouldExit) {
        SHOULD_EXIT = shouldExit;
    }

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        builder.logStartupInfo(false);
        builder.run(args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        long startTime = System.currentTimeMillis();

        //Events, Status and Exit Codes are required even if boot fails.
        EventSystem eventSystem = new EventSystem();
        DetectStatusManager statusManager = new DetectStatusManager(eventSystem);

        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeManager exitCodeManager = new ExitCodeManager(eventSystem, exitCodeUtility);

        ReportListener.createDefault(eventSystem);
        FormattedOutputManager formattedOutputManager = new FormattedOutputManager(eventSystem);

        //Before boot even begins, we create a new Spring context for Detect to work within.
        logger.debug("Initializing detect.");
        DetectRun detectRun = DetectRun.createDefault();
        DetectContext detectContext = new DetectContext(detectRun);

        Gson gson = BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create();
        DetectInfo detectInfo = DetectInfoUtility.createDefaultDetectInfo();
        detectContext.registerBean(gson);
        detectContext.registerBean(detectInfo);

        boolean printOutput = true;
        boolean shouldForceSuccess = false;

        Optional<DetectBootResult> detectBootResultOptional = bootApplication(detectRun, applicationArguments, eventSystem, detectContext, exitCodeManager);

        if (detectBootResultOptional.isPresent()) {
            DetectBootResult detectBootResult = detectBootResultOptional.get();

            printOutput = detectBootResult.getDetectConfiguration()
                              .map(configuration -> !configuration.getValueOrDefault(DetectProperties.DETECT_SUPPRESS_RESULTS_OUTPUT.getProperty()))
                              .orElse(Boolean.TRUE);

            shouldForceSuccess = detectBootResult.getDetectConfiguration()
                                     .map(configuration -> configuration.getValueOrDefault(DetectProperties.DETECT_FORCE_SUCCESS.getProperty()))
                                     .orElse(Boolean.FALSE);

            runApplication(detectContext, detectRun, eventSystem, exitCodeManager, detectBootResult);

            //Create status output file.
            logger.info("");
            detectBootResult.getDirectoryManager()
                .ifPresent(directoryManager -> createStatusOutputFile(formattedOutputManager, detectInfo, directoryManager));

        } else {
            logger.info("Will not create status file, detect did not boot.");
        }
        
        shutdownApplication(detectBootResultOptional, exitCodeManager);

        logger.debug("All Detect actions completed.");

        //Generally, when requesting a failure status, an exit code is also requested, but if it is not, we default to an unknown error.
        if (statusManager.hasAnyFailure()) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_UNKNOWN_ERROR, "A failure status was requested by one or more of Detect's tools."));
        }

        //Find the final (as requested) exit code
        ExitCodeType finalExitCode = exitCodeManager.getWinningExitCode();

        //Print detect's status
        if (printOutput) {
            statusManager.logDetectResults(new Slf4jIntLogger(logger), finalExitCode);
        }

        //Print duration of run
        long endTime = System.currentTimeMillis();
        String duration = DurationFormatUtils.formatPeriod(startTime, endTime, "HH'h' mm'm' ss's' SSS'ms'");
        logger.info("Detect duration: {}", duration);

        exitApplication(finalExitCode, shouldForceSuccess);
    }

    private Optional<DetectBootResult> bootApplication(DetectRun detectRun, ApplicationArguments applicationArguments, EventSystem eventSystem, DetectContext detectContext, ExitCodeManager exitCodeManager) {
        Optional<DetectBootResult> bootResult = Optional.empty();
        try {
            logger.debug("Detect boot begin.");
            DetectBoot detectBoot = new DetectBoot(new DetectBootFactory());
            bootResult = detectBoot.boot(detectRun, applicationArguments.getSourceArgs(), environment, eventSystem, detectContext);
            logger.debug("Detect boot completed.");
        } catch (Exception e) {
            logger.error("Detect boot failed.");
            exitCodeManager.requestExitCode(e);
        }
        return bootResult;
    }

    private void runApplication(DetectContext detectContext, DetectRun detectRun, EventSystem eventSystem, ExitCodeManager exitCodeManager, DetectBootResult detectBootResult) {
        Optional<ProductRunData> optionalProductRunData = detectBootResult.getProductRunData();
        if (detectBootResult.getBootType() == DetectBootResult.BootType.RUN && optionalProductRunData.isPresent()) {
            logger.debug("Detect will attempt to run.");
            ProductRunData productRunData = optionalProductRunData.get();
            RunManager runManager = new RunManager(detectContext);
            try {
                logger.debug("Detect run begin: {}", detectRun.getRunId());
                runManager.run(productRunData);
                logger.debug("Detect run completed.");
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    logger.error("Detect run failed: {}", e.getMessage());
                } else {
                    logger.error("Detect run failed: {}", e.getClass().getSimpleName());
                }
                logger.debug("An exception was thrown during the detect run.", e);
                exitCodeManager.requestExitCode(e);
            }
        } else {
            logger.debug("Detect will NOT attempt to run.");
            detectBootResult.getException().ifPresent(exitCodeManager::requestExitCode);
            detectBootResult.getException().ifPresent(e -> DetectIssue.publish(eventSystem, DetectIssueType.EXCEPTION, e.getMessage()));
        }
    }

    private void createStatusOutputFile(FormattedOutputManager formattedOutputManager, DetectInfo detectInfo, DirectoryManager directoryManager) {
        logger.info("");
        try {
            File statusFile = new File(directoryManager.getStatusOutputDirectory(), "status.json");
            logger.info("Creating status file: {}", statusFile);

            Gson formattedGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            String json = formattedGson.toJson(formattedOutputManager.createFormattedOutput(detectInfo));
            FileUtils.writeStringToFile(statusFile, json, Charset.defaultCharset());
        } catch (Exception e) {
            logger.warn("There was a problem writing the status output file. The detect run was not affected.");
            logger.debug("The problem creating the status file was: ", e);
        }
    }

    private void shutdownApplication(Optional<DetectBootResult> detectBootResult, ExitCodeManager exitCodeManager) {
        try {
            logger.debug("Detect shutdown begin.");
            ShutdownManager shutdownManager = new ShutdownManager();
            shutdownManager.shutdown(
                detectBootResult.flatMap(DetectBootResult::getProductRunData),
                detectBootResult.flatMap(DetectBootResult::getAirGapZip),
                detectBootResult.flatMap(DetectBootResult::getDetectConfiguration),
                detectBootResult.flatMap(DetectBootResult::getDirectoryManager),
                detectBootResult.flatMap(DetectBootResult::getDiagnosticSystem));
            logger.debug("Detect shutdown completed.");
        } catch (Exception e) {
            logger.error("Detect shutdown failed.");
            exitCodeManager.requestExitCode(e);
        }
    }

    private void exitApplication(ExitCodeType finalExitCode, boolean shouldForceSuccess) {
        //Exit with formal exit code
        if (finalExitCode != ExitCodeType.SUCCESS && shouldForceSuccess) {
            logger.warn("Forcing success: Exiting with exit code 0. Ignored exit code was {}.", finalExitCode.getExitCode());
            System.exit(0);
        } else if (finalExitCode != ExitCodeType.SUCCESS) {
            logger.error("Exiting with code {} - {}", finalExitCode.getExitCode(), finalExitCode);
        }

        if (SHOULD_EXIT) {
            System.exit(finalExitCode.getExitCode());
        } else {
            logger.info("Would normally exit({}) but it is overridden.", finalExitCode.getExitCode());
        }
    }
}
