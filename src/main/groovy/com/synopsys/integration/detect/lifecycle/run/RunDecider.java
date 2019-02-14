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
package com.synopsys.integration.detect.lifecycle.run;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.polaris.common.PolarisEnvironmentCheck;

public class RunDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RunDecision decide(DetectConfiguration detectConfiguration, PolarisEnvironmentCheck polarisEnvironmentCheck) {
        boolean runBlackduck = false;
        boolean runPolaris = false;
        boolean offline = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        String hubUrl = detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None);
        if (offline || StringUtils.isNotBlank(hubUrl)) {
            logger.info("Either the Black Duck url was found or offline mode is set, will run Black Duck product.");
            runBlackduck = true;
        } else {
            logger.info("No Black Duck url was found and offline mode is not set, will NOT run Black Duck product.");
        }
        String polarisUrl = detectConfiguration.getProperty(DetectProperty.POLARIS_URL, PropertyAuthority.None);
        boolean libraryCanRun = polarisEnvironmentCheck.isAccessTokenConfigured();
        if (StringUtils.isBlank(polarisUrl)) {
            logger.info("No polaris url was found, cannot run polaris.");
            runPolaris = false;
        } else if (!libraryCanRun) {
            logger.info("No polaris access token was found even though a url was found, cannot run polaris.");
            runPolaris = false;
        } else {
            logger.info("A polaris access token and url was found, will run Polaris product.");
            runPolaris = true;
        }

        return new RunDecision(runBlackduck, runPolaris);
    }
}
