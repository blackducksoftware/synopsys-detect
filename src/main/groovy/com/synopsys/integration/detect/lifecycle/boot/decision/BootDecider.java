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
package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.lifecycle.boot.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.BlackDuckConnectivityResult;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.util.BuilderStatus;

public class BootDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PolarisServerConfigBuilder createPolarisServerConfigBuilder(DetectConfiguration detectConfiguration, File userHome) {
        PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        Set<String> allPolarisKeys = new HashSet<>(polarisServerConfigBuilder.getAllPropertyKeys());
        Map<String, String> polarisProperties = detectConfiguration.getProperties(allPolarisKeys);
        polarisServerConfigBuilder.setLogger(new SilentIntLogger());
        polarisServerConfigBuilder.setFromProperties(polarisProperties);
        polarisServerConfigBuilder.setUserHomePath(userHome.getAbsolutePath());
        polarisServerConfigBuilder.setTimeoutSeconds(120);
        return polarisServerConfigBuilder;
    }

    public PolarisDecision determinePolaris(DetectConfiguration detectConfiguration, File userHome){
        PolarisServerConfigBuilder polarisServerConfigBuilder = createPolarisServerConfigBuilder(detectConfiguration, userHome);
        BuilderStatus builderStatus = polarisServerConfigBuilder.validateAndGetBuilderStatus();
        boolean polarisCanRun = builderStatus.isValid();

        if (!polarisCanRun) {
            logger.info("The supplied properties are not sufficient to run polaris - polaris will not run.");
            logger.info(builderStatus.getFullErrorMessage());
            return PolarisDecision.skip();
        } else {
            logger.info("A polaris access token and url were found, will run Polaris product.");
            return PolarisDecision.runOnline(polarisServerConfigBuilder.build());
        }
    }

    public BlackDuckDecision determineBlackDuck(DetectConfiguration detectConfiguration) {
        boolean offline = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        String hubUrl = detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None);
        if (offline) {
            return BlackDuckDecision.runOffline();
        } else if(StringUtils.isNotBlank(hubUrl)) {
            logger.info("Either the Black Duck url was found or offline mode is set, will run Black Duck product.");
            return BlackDuckDecision.runOnline();
        } else {
            logger.info("No Black Duck url was found and offline mode is not set, will NOT run Black Duck product.");
            return BlackDuckDecision.skip();
        }
    }

    public BootDecision decide(DetectConfiguration detectConfiguration, File userHome) throws DetectUserFriendlyException {
        return new BootDecision(determineBlackDuck(detectConfiguration), determinePolaris(detectConfiguration, userHome));
    }
}
