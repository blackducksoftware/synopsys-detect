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
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.boot.DetectBoot;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootResult;
import com.synopsys.integration.detect.lifecycle.exit.ExitManager;
import com.synopsys.integration.detect.lifecycle.exit.ExitOptions;
import com.synopsys.integration.detect.lifecycle.exit.ExitResult;
import com.synopsys.integration.detect.lifecycle.run.RunManager;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.shutdown.CleanupUtility;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeUtility;
import com.synopsys.integration.detect.lifecycle.shutdown.ShutdownDecider;
import com.synopsys.integration.detect.lifecycle.shutdown.ShutdownDecision;
import com.synopsys.integration.detect.lifecycle.shutdown.ShutdownManager;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.report.ReportListener;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutputManager;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.DetectStatusManager;

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
        ExitManager exitManager = new ExitManager(eventSystem, exitCodeManager, statusManager);

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
            printOutput = detectBootResult.shouldPrintOutput();
            shouldForceSuccess = detectBootResult.shouldForceSuccess();

            runApplication(detectContext, detectRun, eventSystem, exitCodeManager, detectBootResult);

            //Create status output file.
            logger.info("");
            detectBootResult.getDirectoryManager()
                .ifPresent(directoryManager -> createStatusOutputFile(formattedOutputManager, detectInfo, directoryManager));

            shutdownApplication(detectBootResult, exitCodeManager);
        } else {
            logger.info("Will not create status file, detect did not boot.");
        }

        logger.debug("All Detect actions completed.");

        exitApplication(exitManager, startTime, printOutput, shouldForceSuccess);
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

    private void shutdownApplication(DetectBootResult detectBootResult, ExitCodeManager exitCodeManager) {
        try {
            logger.debug("Detect shutdown begin.");
            ShutdownDecision shutdownDecision = new ShutdownDecider().decideShutdown(detectBootResult);
            ShutdownManager shutdownManager = new ShutdownManager(new CleanupUtility());
            shutdownManager.shutdown(detectBootResult, shutdownDecision);
            logger.debug("Detect shutdown completed.");
        } catch (Exception e) {
            logger.error("Detect shutdown failed.");
            exitCodeManager.requestExitCode(e);
        }
    }

    private void exitApplication(ExitManager exitManager, long startTime, boolean printOutput, boolean shouldForceSuccess) {
        ExitOptions exitOptions = new ExitOptions(startTime, printOutput, shouldForceSuccess, SHOULD_EXIT);
        ExitResult exitResult = exitManager.exit(exitOptions);

        if (exitResult.shouldForceSuccess()) {
            System.exit(0);
        } else if (exitResult.shouldPerformExit()) {
            System.exit(exitResult.getExitCodeType().getExitCode());
        }
    }
}
