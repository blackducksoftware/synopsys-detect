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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.property.PropertyMap;
import com.blackducksoftware.integration.hub.detect.property.SpringPropertySource;
import com.synopsys.detect.doctor.configuration.DoctorArgumentState;
import com.synopsys.detect.doctor.configuration.DoctorArgumentStateParser;
import com.synopsys.detect.doctor.configuration.DoctorConfiguration;
import com.synopsys.detect.doctor.configuration.DoctorProperty;
import com.synopsys.detect.doctor.logparser.DetectLogParseResult;
import com.synopsys.detect.doctor.logparser.DetectLogParser;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;
import com.synopsys.detect.doctor.logparser.LoggedDetectProperty;

import ch.qos.logback.core.db.dialect.PostgreSQLDialect;

@SpringBootApplication
public class BomToolApplication implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(BomToolApplication.class);

    private ConfigurableEnvironment configurableEnvironment;

    public static void main(final String[] args) {
        new SpringApplicationBuilder(BomToolApplication.class).logStartupInfo(false).run(args);
    }

    @Autowired
    public BomToolApplication(ConfigurableEnvironment configurableEnvironment){
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

        if (state.getIsExtraction()){
            File log = new File(doctorConfiguration.getProperty(DoctorProperty.DETECT_LOG_FILE));
            DetectLogParser logParser = new DetectLogParser();

            DetectLogParseResult result = logParser.parse(log);
            for (LoggedDetectProperty property : result.loggedConfiguration.loggedPropertyList){
                logger.info("Parsed property " + property.key + " found value " + property.value);
            }

            logger.info("Detect log parsed.");

            String extractionId = doctorConfiguration.getProperty(DoctorProperty.DETECT_EXTRACTION_ID);
            LoggedDetectExtraction extraction = null;
            for (LoggedDetectExtraction possibleExtraction : result.loggedConfiguration.extractions){
                if (possibleExtraction.extractionIdentifier.equals(extractionId)){
                    extraction = possibleExtraction;
                }
            }

            if (extraction != null){
                logger.info("Found extraction with id: " + extractionId);

            }

            File outputFolder = new File(doctorConfiguration.getProperty(DoctorProperty.DETECT_OUTPUT_FOLDER));
        }
    }
}
