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
package com.synopsys.integration.detect.lifecycle.boot.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.PolarisRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class ProductBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductRunData boot(final ProductDecision productDecision, final DetectConfiguration detectConfiguration, final BlackDuckConnectivityChecker blackDuckConnectivityChecker, final PolarisConnectivityChecker polarisConnectivityChecker,
        final ProductBootFactory productBootFactory) throws DetectUserFriendlyException {
        if (!productDecision.willRunAny()) {
            throw new DetectUserFriendlyException("Your environment was not sufficiently configured to run Black Duck or Polaris. Please configure your environment for at least one product.", ExitCodeType.FAILURE_CONFIGURATION);
        }

        logger.info("Detect product boot start.");
        BlackDuckRunData blackDuckRunData = null;
        final BlackDuckDecision blackDuckDecision = productDecision.getBlackDuckDecision();
        if (blackDuckDecision.shouldRun()) {
            logger.info("Will boot Black Duck product.");
            if (blackDuckDecision.isOffline()) {
                blackDuckRunData = BlackDuckRunData.offline();
            } else {
                final BlackDuckServerConfig blackDuckServerConfig = productBootFactory.createBlackDuckServerConfig();
                final BlackDuckConnectivityResult blackDuckConnectivityResult = blackDuckConnectivityChecker.determineConnectivity(blackDuckServerConfig);

                if (blackDuckConnectivityResult.isSuccessfullyConnected()) {
                    final BlackDuckServicesFactory blackDuckServicesFactory = blackDuckConnectivityResult.getBlackDuckServicesFactory();
                    final PhoneHomeManager phoneHomeManager = productBootFactory.createPhoneHomeManager(blackDuckServicesFactory);
                    blackDuckRunData = BlackDuckRunData.online(blackDuckServicesFactory, phoneHomeManager, blackDuckConnectivityResult.getBlackDuckServerConfig());
                } else {
                    if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_IGNORE_CONNECTION_FAILURES, PropertyAuthority.None)) {
                        logger.info("Failed to connect to Black Duck: " + blackDuckConnectivityResult.getFailureReason());
                        logger.info(String.format("%s is set to 'true' so Detect will simply disable the Black Duck product.", DetectProperty.DETECT_IGNORE_CONNECTION_FAILURES.getPropertyName()));
                    } else {
                        throw new DetectUserFriendlyException("Could not communicate with Black Duck: " + blackDuckConnectivityResult.getFailureReason(), ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
                    }
                }
            }
        }

        PolarisRunData polarisRunData = null;
        final PolarisDecision polarisDecision = productDecision.getPolarisDecision();
        if (polarisDecision.shouldRun()) {
            logger.info("Will boot Polaris product.");
            final PolarisServerConfig polarisServerConfig = polarisDecision.getPolarisServerConfig();
            final PolarisConnectivityResult polarisConnectivityResult = polarisConnectivityChecker.determineConnectivity(polarisServerConfig);

            if (polarisConnectivityResult.isSuccessfullyConnected()) {
                polarisRunData = new PolarisRunData(polarisDecision.getPolarisServerConfig());
            } else {
                throw new DetectUserFriendlyException("Could not communicate with Polaris: " + polarisConnectivityResult.getFailureReason(), ExitCodeType.FAILURE_POLARIS_CONNECTIVITY);
            }
        }

        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION, PropertyAuthority.None)) {
            logger.info(String.format("%s is set to 'true' so Detect will not run.", DetectProperty.DETECT_TEST_CONNECTION.getPropertyName()));
            return null;
        }

        logger.info("Detect product boot completed.");
        return new ProductRunData(polarisRunData, blackDuckRunData);
    }
}
