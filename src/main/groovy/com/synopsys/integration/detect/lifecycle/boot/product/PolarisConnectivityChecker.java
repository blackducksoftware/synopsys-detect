/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.integration.blackduck.api.component.ErrorResponse;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.response.CurrentVersionView;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.ConnectionResult;
import com.synopsys.integration.blackduck.rest.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisAccessTokenResolver;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.request.Response;

public class PolarisConnectivityChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PolarisConnectivityResult determineConnectivity(PolarisServerConfig polarisServerConfig)
        throws DetectUserFriendlyException {

        logger.info("Detect will check if it can communicate with the Polaris Server.");

        ConnectionResult connectionResult = attemptConnection(polarisServerConfig);

        if (connectionResult.isFailure()) {
            logger.error("Failed to connect to the Polaris server");
            logger.debug(String.format("The Polaris server responded with a status code of %d", connectionResult.getHttpStatusCode()));
            return PolarisConnectivityResult.failure(connectionResult.getFailureMessage().orElse("Could not reach the Polaris server or the credentials were invalid."));
        }

        logger.info("Connection to the Polaris server was successful");//TODO: Get a detailed reason of why canConnect failed.

        return PolarisConnectivityResult.success();
    }

    public ConnectionResult attemptConnection(PolarisServerConfig polarisServerConfig) {
        String errorMessage = null;
        int httpStatusCode = 0;

        try {
            AccessTokenPolarisHttpClient blackDuckHttpClient = polarisServerConfig.createPolarisHttpClient(new SilentIntLogger());
            try (Response response = blackDuckHttpClient.attemptAuthentication()) {
                // if you get an error response, you know that a connection could not be made
                httpStatusCode = response.getStatusCode();
                if (response.isStatusCodeError()) {
                    errorMessage = response.getContentString();
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        if (null != errorMessage) {
            logger.error(errorMessage);
            return ConnectionResult.FAILURE(httpStatusCode, errorMessage);
        }

        logger.info("A successful connection was made.");
        return ConnectionResult.SUCCESS(httpStatusCode);
    }
}