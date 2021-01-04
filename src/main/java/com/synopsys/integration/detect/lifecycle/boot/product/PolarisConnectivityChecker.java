/**
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.client.ConnectionResult;
import com.synopsys.integration.rest.response.Response;

public class PolarisConnectivityChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PolarisConnectivityResult determineConnectivity(final PolarisServerConfig polarisServerConfig) {

        logger.info("Detect will check if it can communicate with the Polaris Server.");

        final ConnectionResult connectionResult = attemptConnection(polarisServerConfig);

        if (connectionResult.isFailure()) {
            logger.error("Failed to connect to the Polaris server");
            logger.debug(String.format("The Polaris server responded with a status code of %d", connectionResult.getHttpStatusCode()));
            return PolarisConnectivityResult.failure(connectionResult.getFailureMessage().orElse("Could not reach the Polaris server or the credentials were invalid."));
        }

        logger.info("Connection to the Polaris server was successful");

        return PolarisConnectivityResult.success();
    }

    private ConnectionResult attemptConnection(final PolarisServerConfig polarisServerConfig) {
        String errorMessage = null;
        Exception caughtException = null;
        int httpStatusCode = 0;

        try {
            final AccessTokenPolarisHttpClient blackDuckHttpClient = polarisServerConfig.createPolarisHttpClient(new SilentIntLogger());
            try (final Response response = blackDuckHttpClient.attemptAuthentication()) {
                // if you get an error response, you know that a connection could not be made
                httpStatusCode = response.getStatusCode();
                if (response.isStatusCodeError()) {
                    errorMessage = response.getContentString();
                }
            }
        } catch (final Exception e) {
            errorMessage = e.getMessage();
            caughtException = e;
        }

        if (null != errorMessage) {
            logger.error(errorMessage);
            return ConnectionResult.FAILURE(httpStatusCode, errorMessage, caughtException);
        }

        logger.info("A successful connection was made.");
        return ConnectionResult.SUCCESS(httpStatusCode);
    }
}