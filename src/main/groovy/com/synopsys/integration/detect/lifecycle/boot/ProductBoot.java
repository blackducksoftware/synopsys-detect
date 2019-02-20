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
package com.synopsys.integration.detect.lifecycle.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.BootDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.PolarisRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ProductBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductRunData boot(BootDecision bootDecision, DetectConfiguration detectConfiguration, ProductBootFactory productBootFactory) throws DetectUserFriendlyException {
        if (!bootDecision.willRunAny()) {
            throw new DetectUserFriendlyException("Your environment was not sufficiently configured to run Black Duck or polaris. Please configure your environment for at least one product.", ExitCodeType.FAILURE_CONFIGURATION);
        }

        BlackDuckRunData blackDuckRunData = null;
        BlackDuckDecision blackDuckDecision = bootDecision.getBlackDuckDecision();
        if (blackDuckDecision.shouldRun()){
            if (blackDuckDecision.isOffline()){
                blackDuckRunData = BlackDuckRunData.offline();
            } else {
                if (blackDuckDecision.isSuccessfullyConnected()){
                    BlackDuckServicesFactory blackDuckServicesFactory = blackDuckDecision.getBlackDuckServicesFactory();
                    PhoneHomeManager phoneHomeManager = productBootFactory.createPhoneHomeManager(blackDuckServicesFactory);
                    blackDuckRunData = BlackDuckRunData.online(blackDuckServicesFactory, phoneHomeManager, blackDuckDecision.getBlackDuckServerConfig());
                } else {
                    if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK, PropertyAuthority.None)) {
                        logger.info(blackDuckDecision.getConnectionFailureReason());
                        logger.info(String.format("%s is set to 'true' so Detect will simply exit.", DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK.getPropertyName()));
                        return null;
                    } else {
                        throw new DetectUserFriendlyException("Could not communicate with Black Duck: " + blackDuckDecision.getConnectionFailureReason(), ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
                    }
                }
                if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION, PropertyAuthority.None)) {
                    logger.info(String.format("%s is set to 'true' so Detect will not run.", DetectProperty.DETECT_TEST_CONNECTION.getPropertyName()));
                    return null;
                }
            }
        }

        PolarisRunData polarisRunData = null;
        PolarisDecision polarisDecision = bootDecision.getPolarisDecision();
        if (polarisDecision.shouldRun()){
            polarisRunData = new PolarisRunData(polarisDecision.getPolarisServerConfig());
        }

        return new ProductRunData(polarisRunData, blackDuckRunData);
    }
}
