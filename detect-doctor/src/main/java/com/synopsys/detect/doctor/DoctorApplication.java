/**
 * detect-doctor
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
package com.synopsys.detect.doctor;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyMap;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.property.PropertyMap;
import com.blackducksoftware.integration.hub.detect.property.SpringPropertySource;
import com.synopsys.detect.doctor.configuration.DoctorArgumentState;
import com.synopsys.detect.doctor.configuration.DoctorArgumentStateParser;
import com.synopsys.detect.doctor.configuration.DoctorConfiguration;
import com.synopsys.detect.doctor.configuration.DoctorProperty;
import com.synopsys.detect.doctor.diagnosticparser.DetectRunInfo;
import com.synopsys.detect.doctor.diagnosticparser.DiagnosticParser;
import com.synopsys.detect.doctor.extraction.ExtractionHandler;
import com.synopsys.detect.doctor.logparser.DetectLogParseResult;
import com.synopsys.detect.doctor.logparser.DetectLogParser;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;
import com.synopsys.detect.doctor.run.DoctorDirectoryManager;
import com.synopsys.detect.doctor.run.DoctorRun;

@SpringBootApplication
public class DoctorApplication implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConfigurableEnvironment configurableEnvironment;

    public static void main(final String[] args) {
        new SpringApplicationBuilder(DoctorApplication.class).logStartupInfo(false).run(args);
    }

    @Autowired
    public DoctorApplication(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        PropertyMap<DoctorProperty> doctorPropertyPropertyMap = new PropertyMap<>();
        SpringPropertySource springPropertySource = new SpringPropertySource(configurableEnvironment);
        DoctorConfiguration doctorConfiguration = new DoctorConfiguration(springPropertySource, doctorPropertyPropertyMap);

        DoctorArgumentStateParser argumentStateParser = new DoctorArgumentStateParser();
        DoctorArgumentState state = argumentStateParser.parseArgs(applicationArguments.getSourceArgs());

        doctorConfiguration.init();

        DoctorRun doctorRun = DoctorRun.createDefault();

        logger.info("Doctor begin: " + doctorRun.getRunId());

        DoctorDirectoryManager doctorDirectoryManager = new DoctorDirectoryManager(doctorRun);

        logger.info("Doctor ready.");

        Optional<DetectRunInfo> detectRunInfo = Optional.empty();

        File diagnosticZip = new File(doctorConfiguration.getProperty(DoctorProperty.DETECT_DIAGNOSTIC_FILE));
        if (diagnosticZip.exists()) {
            logger.info("A diagnostic zip was found: " + diagnosticZip.getAbsolutePath());
            DiagnosticParser diagnosticParser = new DiagnosticParser();
            detectRunInfo = Optional.of(diagnosticParser.processDiagnosticZip(doctorDirectoryManager, diagnosticZip));
        } else {
            logger.info("No diagnostic zip provided, looking for the pieces.");
            File log = new File(doctorConfiguration.getProperty(DoctorProperty.DETECT_LOG_FILE));

            logger.info("Looking for log file at: " + log.getAbsolutePath());
            if (log.exists()) {
                logger.info("Found log file.");
                // detectRunInfo = Optional.of(new DetectRunInfo(log));
            } else {
                logger.info("No log file found.");
            }
        }

        if (detectRunInfo.isPresent()) {

            DetectLogParser logParser = new DetectLogParser();

            logger.info("Doctor can proceed, necessary pieces located.");

            File log = detectRunInfo.get().getLogFile();

            DetectLogParseResult result = logParser.parse(log);

            logger.info("Detect log parsed.");

            String extractionId = doctorConfiguration.getProperty(DoctorProperty.DETECT_EXTRACTION_ID);

            Set<String> extractions = new HashSet<>();

            LoggedDetectExtraction extraction = null;
            for (LoggedDetectExtraction possibleExtraction : result.loggedConfiguration.extractions) {
                extractions.add(possibleExtraction.extractionIdentifier);
                if (possibleExtraction.extractionIdentifier.equals(extractionId)) {
                    extraction = possibleExtraction;
                }
            }

            if (StringUtils.isBlank(extractionId)) {
                quit("Doctor needs an extraction to work with, options are: " + extractions.stream().collect(Collectors.joining(",")));
            }

            if (extraction == null) {
                quit("No extraction found for given id: " + extractionId);
            }

            logger.info("Found extraction with id: " + extractionId);
            logger.info("Doctor will attempt to diagnose!");

            logger.info("We begin by rebuilding the configuration.");
            Map<String, String> propertyMap = new HashMap<>();
            result.loggedConfiguration.loggedPropertyList.stream().forEach(it -> propertyMap.put(it.key, it.value));
            RehydratedPropertySource rehydratedPropertySource = new RehydratedPropertySource(propertyMap);
            DetectConfiguration detectConfiguration = new DetectConfiguration(new DetectPropertySource(rehydratedPropertySource), new DetectPropertyMap());

            ExtractionHandler extractionHandler = new ExtractionHandler();
            extractionHandler.processExtraction(extraction, detectRunInfo.get(), detectConfiguration);

        } else {
            quit("Neccessary pieces not found for doctor to proceed.");
        }

    }

    private void quit(String msg) {
        logger.error(msg);
        System.exit(-1);
    }
}
