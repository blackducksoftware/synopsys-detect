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

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.ExitCodeUtility;
import com.blackducksoftware.integration.hub.detect.workflow.boot.BootFactory;
import com.blackducksoftware.integration.hub.detect.workflow.boot.BootManager;
import com.blackducksoftware.integration.hub.detect.workflow.boot.BootResult;
import com.blackducksoftware.integration.hub.detect.workflow.boot.CleanupManager;
import com.blackducksoftware.integration.hub.detect.workflow.boot.DetectRunContext;
import com.blackducksoftware.integration.hub.detect.workflow.run.RunManager;
import com.blackducksoftware.integration.hub.detect.workflow.run.RunResult;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

@SpringBootApplication
//@Import({ OldBeanConfiguration.class })
public class Application implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    private ConfigurableEnvironment environment;

    @Autowired
    public Application(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class).logStartupInfo(false).run(args);
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        final long startTime = System.currentTimeMillis();

        ExitCodeUtility exitCodeUtility = new ExitCodeUtility();
        ExitCodeType detectExitCode = ExitCodeType.SUCCESS;
        BootResult bootResult = null;
        try {
            BootManager bootManager = new BootManager(new BootFactory());
            bootResult = bootManager.boot(applicationArguments.getSourceArgs(), environment);
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        try {
            if (bootResult != null && bootResult.bootType == BootResult.BootType.CONTINUE) {
                RunResult runResult;
                final DetectRunContext detectRunContext = bootResult.detectRunContext;
                AnnotationConfigApplicationContext runContext = new AnnotationConfigApplicationContext();
                runContext.register(BeanConfiguration.class);
                runContext.registerBean(DetectRunContext.class, () -> { return detectRunContext; });
                runContext.refresh();

                ExternalIdFactory bomTool1 = runContext.getBean(ExternalIdFactory.class, ExternalIdFactory.class.getSimpleName());
                ExternalIdFactory bomTool2 = runContext.getBean(ExternalIdFactory.class, ExternalIdFactory.class.getSimpleName());

                RunManager runManager = runContext.getBean(RunManager.class);
                runManager.run();
            }
        } catch (final Exception e) {
            detectExitCode = exitCodeUtility.getExitCodeFromExceptionDetails(e);
        }

        try {
            if (bootResult != null) {
                CleanupManager cleanupManager = new CleanupManager();
                cleanupManager.cleanup(bootResult.detectRunContext);
            }
        } catch (final Exception e) {
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
