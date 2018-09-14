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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.RunManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.boot.BootManager;
import com.blackducksoftware.integration.hub.detect.workflow.boot.BootResult;
import com.blackducksoftware.integration.hub.detect.workflow.boot.CleanupManager;
import com.blackducksoftware.integration.hub.detect.workflow.boot.DetectRunContext;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;

@SpringBootApplication
//@Import({ OldBeanConfiguration.class })
public class SpringlessApplication implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(SpringlessApplication.class);

    private ConfigurableEnvironment environment;

    @Autowired
    public SpringlessApplication(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public static void main(final String[] args) {
        new SpringApplicationBuilder(SpringlessApplication.class).logStartupInfo(false).run(args);
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        final long startTime = System.currentTimeMillis();

        DetectRunContext runContext = null;
        try {
            BootResult bootResult = bootDetect();
            runContext = bootResult.detectRunContext;
            if (bootResult.bootType == BootResult.BootType.CONTINUE){
                RunResult runResult = runDetect();
            }
        } finally {
            cleanupDetect(runContext);
            printStatus();
        }

         System.exit();



        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeType detectExitCode = ExitCodeType.SUCCESS;
        BootResult bootResult = null;
        try {
            AnnotationConfigApplicationContext bootContext = new AnnotationConfigApplicationContext(DetectSharedBeanConfiguration.class, DetectBootBeanConfiguration.class);
            BootManager bootManager = bootContext.getBean(BootManager.class);
            bootResult = bootManager.boot(applicationArguments.getSourceArgs(), environment);
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        try {
            if (bootResult != null && bootResult.bootType == BootResult.BootType.CONTINUE){
                RunResult runResult;
                AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext(DetectSharedBeanConfiguration.class, DetectRunBeanConfiguration.class);
                runContext.getBeanFactory().registerSingleton(bootResult.detectRunContext.getClass().getSimpleName(), bootResult.detectRunContext);
                RunManager runManager = runContext.getBean(RunManager.class);
                runManager.run();
            }
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        try {
            if (bootResult != null){
                CleanupManager cleanupManager = new CleanupManager();
                cleanupManager.cleanup(bootResult.detectRunContext);
            }
        } catch (final Exception e){
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        boolean printOutput = bootResult.detectRunContext.detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_RESULTS_OUTPUT);
        if (!printOutput) {
            //bootResult.detectRunContext.detectSummaryManager();
            //detectSummaryManager.logDetectResults(new Slf4jIntLogger(logger), currentExitCodeType);
        }

        final long endTime = System.currentTimeMillis();
        logger.info(String.format("Hub-Detect run duration: %s", DurationFormatUtils.formatPeriod(startTime, endTime, "HH'h' mm'm' ss's' SSS'ms'")));

        if (detectExitCode != ExitCodeType.SUCCESS && bootResult.bootType == null) {
            logger.warn(String.format("Forcing success: Exiting with exit code 0. Ignored exit code was %s.", detectExitCode.getExitCode()));
            System.exit(0);
        } else if (detectExitCode != ExitCodeType.SUCCESS) {
            logger.error(String.format("Exiting with code %s - %s", detectExitCode.getExitCode(), detectExitCode.toString()));
        }

        System.exit(detectExitCode.getExitCode());
    }


}
