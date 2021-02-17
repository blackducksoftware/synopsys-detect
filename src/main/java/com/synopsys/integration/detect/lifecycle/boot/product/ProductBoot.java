/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.PolarisRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsConfigurationService;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsSetting;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class ProductBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckConnectivityChecker blackDuckConnectivityChecker;
    private final PolarisConnectivityChecker polarisConnectivityChecker;
    private final AnalyticsConfigurationService analyticsConfigurationService;
    private final ProductBootFactory productBootFactory;
    private final ProductBootOptions productBootOptions;

    public ProductBoot(BlackDuckConnectivityChecker blackDuckConnectivityChecker, PolarisConnectivityChecker polarisConnectivityChecker, AnalyticsConfigurationService analyticsConfigurationService, ProductBootFactory productBootFactory, ProductBootOptions productBootOptions) {
        this.blackDuckConnectivityChecker = blackDuckConnectivityChecker;
        this.polarisConnectivityChecker = polarisConnectivityChecker;
        this.analyticsConfigurationService = analyticsConfigurationService;
        this.productBootFactory = productBootFactory;
        this.productBootOptions = productBootOptions;
    }

    public ProductRunData boot(ProductDecision productDecision) throws DetectUserFriendlyException {
        if (!productDecision.willRunAny()) {
            throw new DetectUserFriendlyException("Your environment was not sufficiently configured to run Black Duck or Polaris. Please configure your environment for at least one product.  See online help at: https://detect.synopsys.com/doc/", ExitCodeType.FAILURE_CONFIGURATION);

        }

        logger.debug("Detect product boot start.");

        BlackDuckRunData blackDuckRunData = getBlackDuckRunData(productDecision, productBootFactory, blackDuckConnectivityChecker, productBootOptions, analyticsConfigurationService);

        PolarisRunData polarisRunData = getPolarisRunData(productDecision, polarisConnectivityChecker);

        if (productBootOptions.isTestConnections()) {
            logger.debug(String.format("%s is set to 'true' so Detect will not run.", DetectProperties.DETECT_TEST_CONNECTION.getProperty().getName()));
            return null;
        }

        logger.debug("Detect product boot completed.");
        return new ProductRunData(polarisRunData, blackDuckRunData);
    }

    @Nullable
    private BlackDuckRunData getBlackDuckRunData(ProductDecision productDecision, ProductBootFactory productBootFactory, BlackDuckConnectivityChecker blackDuckConnectivityChecker, ProductBootOptions productBootOptions, AnalyticsConfigurationService analyticsConfigurationService) throws DetectUserFriendlyException {
        BlackDuckDecision blackDuckDecision = productDecision.getBlackDuckDecision();

        if (!blackDuckDecision.shouldRun()) {
            return null;
        }

        if (blackDuckDecision.isOffline()) {
            return BlackDuckRunData.offline();
        }

        logger.debug("Will boot Black Duck product.");
        BlackDuckServerConfig blackDuckServerConfig = productBootFactory.createBlackDuckServerConfig();
        BlackDuckConnectivityResult blackDuckConnectivityResult = blackDuckConnectivityChecker.determineConnectivity(blackDuckServerConfig);

        if (blackDuckConnectivityResult.isSuccessfullyConnected()) {
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckConnectivityResult.getBlackDuckServicesFactory();

            if (shouldUsePhoneHome(analyticsConfigurationService, blackDuckServicesFactory.getBlackDuckApiClient())) {
                PhoneHomeManager phoneHomeManager = productBootFactory.createPhoneHomeManager(blackDuckServicesFactory);
                return BlackDuckRunData.online(blackDuckServicesFactory, phoneHomeManager, blackDuckConnectivityResult.getBlackDuckServerConfig());
            } else {
                logger.debug("Skipping phone home due to Black Duck global settings.");
                return BlackDuckRunData.onlineNoPhoneHome(blackDuckServicesFactory, blackDuckConnectivityResult.getBlackDuckServerConfig());
            }
        } else {
            if (productBootOptions.isIgnoreConnectionFailures()) {
                logger.info(String.format("Failed to connect to Black Duck: %s", blackDuckConnectivityResult.getFailureReason()));
                logger.info(String.format("%s is set to 'true' so Detect will simply disable the Black Duck product.", DetectProperties.DETECT_IGNORE_CONNECTION_FAILURES.getProperty().getName()));
                return null;
            } else {
                throw new DetectUserFriendlyException("Could not communicate with Black Duck: " + blackDuckConnectivityResult.getFailureReason(), ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
            }
        }
    }

    private boolean shouldUsePhoneHome(AnalyticsConfigurationService analyticsConfigurationService, BlackDuckApiClient blackDuckService) {
        try {
            AnalyticsSetting analyticsSetting = analyticsConfigurationService.fetchAnalyticsSetting(blackDuckService);
            return analyticsSetting.isEnabled();
        } catch (IntegrationException | IOException e) {
            logger.trace("Failed to check analytics setting on Black Duck. Likely this Black Duck instance does not support it.", e);
            return true; // Skip phone home will be applied at the library level.
        }
    }

    private PolarisRunData getPolarisRunData(ProductDecision productDecision, PolarisConnectivityChecker polarisConnectivityChecker) throws DetectUserFriendlyException {
        PolarisRunData polarisRunData = null;
        PolarisDecision polarisDecision = productDecision.getPolarisDecision();
        if (polarisDecision.shouldRun()) {
            logger.debug("Will boot Polaris product.");
            PolarisServerConfig polarisServerConfig = polarisDecision.getPolarisServerConfig();
            PolarisConnectivityResult polarisConnectivityResult = polarisConnectivityChecker.determineConnectivity(polarisServerConfig);

            if (polarisConnectivityResult.isSuccessfullyConnected()) {
                polarisRunData = new PolarisRunData(polarisDecision.getPolarisServerConfig());
            } else {
                throw new DetectUserFriendlyException("Could not communicate with Polaris: " + polarisConnectivityResult.getFailureReason(), ExitCodeType.FAILURE_POLARIS_CONNECTIVITY);
            }
        }
        return polarisRunData;
    }
}
