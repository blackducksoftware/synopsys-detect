/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.boot;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityResult;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.PolarisConnectivityResult;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class ProductBootTest {
    @Test
    public void bothProductsSkippedThrows() {
        Assertions.assertThrows(DetectUserFriendlyException.class, () -> {
            testBoot(BlackDuckDecision.skip(), PolarisDecision.skip());
        });
    }

    @Test
    public void blackDuckConnectionFailureThrows() {
        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");
        Assertions.assertThrows(DetectUserFriendlyException.class, () -> {
            testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, new HashMap<>());
        });

    }

    @Test
    public void polarisConnectionFailureThrows() {
        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.failure("Failed to connect");
        Assertions.assertThrows(DetectUserFriendlyException.class, () -> {
            testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, new HashMap<>());
        });
    }

    @Test
    public void blackDuckFailureWithIgnoreReturnsFalse() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_IGNORE_CONNECTION_FAILURES, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assertions.assertFalse(productRunData.shouldUseBlackDuckProduct());
        Assertions.assertFalse(productRunData.shouldUsePolarisProduct());
    }

    @Test
    public void blackDuckConnectionFailureWithTestThrows() {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.failure("Failed to connect");

        Assertions.assertThrows(DetectUserFriendlyException.class, () -> {
            testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);
        });
    }

    @Test
    public void polarisConnectionFailureWithTestThrows() {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.failure("Failed to connect");

        Assertions.assertThrows(DetectUserFriendlyException.class, () -> {
            testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, properties);
        });
    }

    @Test()
    public void blackDuckConnectionSuccessWithTestReturnsNull() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.success(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assertions.assertNull(productRunData);
    }

    @Test()
    public void polarisConnectionSuccessWithTestReturnsNull() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_TEST_CONNECTION, true);

        final PolarisConnectivityResult connectivityResult = PolarisConnectivityResult.success();

        final ProductRunData productRunData = testBoot(BlackDuckDecision.skip(), PolarisDecision.runOnline(null), null, connectivityResult, properties);

        Assertions.assertNull(productRunData);
    }

    @Test()
    public void blackDuckOnlyWorks() throws DetectUserFriendlyException {
        final HashMap<DetectProperty, Boolean> properties = new HashMap<>();

        final BlackDuckConnectivityResult connectivityResult = BlackDuckConnectivityResult.success(Mockito.mock(BlackDuckServicesFactory.class), Mockito.mock(BlackDuckServerConfig.class));

        final ProductRunData productRunData = testBoot(BlackDuckDecision.runOnline(), PolarisDecision.skip(), connectivityResult, null, properties);

        Assertions.assertTrue(productRunData.shouldUseBlackDuckProduct());
        Assertions.assertFalse(productRunData.shouldUsePolarisProduct());
    }

    @Test()
    public void polarisOnlyWorks() throws DetectUserFriendlyException {
        final PolarisDecision polarisDecision = PolarisDecision.runOnline(Mockito.mock(PolarisServerConfig.class));

        final PolarisConnectivityResult polarisConnectivityResult = Mockito.mock(PolarisConnectivityResult.class);
        Mockito.when(polarisConnectivityResult.isSuccessfullyConnected()).thenReturn(true);

        final ProductRunData productRunData = testBoot(BlackDuckDecision.skip(), polarisDecision, null, polarisConnectivityResult, new HashMap<>());

        Assertions.assertFalse(productRunData.shouldUseBlackDuckProduct());
        Assertions.assertTrue(productRunData.shouldUsePolarisProduct());
    }

    private ProductRunData testBoot(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision) throws DetectUserFriendlyException {
        return testBoot(blackDuckDecision, polarisDecision, null, null, new HashMap<>());
    }

    private ProductRunData testBoot(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision, final BlackDuckConnectivityResult blackDuckconnectivityResult, final PolarisConnectivityResult polarisConnectivityResult,
        final Map<DetectProperty, Boolean> properties) throws DetectUserFriendlyException {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        properties.forEach((key, value) -> Mockito.when(detectConfiguration.getBooleanProperty(key, PropertyAuthority.NONE)).thenReturn(value));

        final ProductBootFactory productBootFactory = Mockito.mock(ProductBootFactory.class);
        Mockito.when(productBootFactory.createPhoneHomeManager(Mockito.any())).thenReturn(null);

        final ProductDecision productDecision = new ProductDecision(blackDuckDecision, polarisDecision);

        final ProductBoot productBoot = new ProductBoot();

        final BlackDuckConnectivityChecker blackDuckConnectivityChecker = Mockito.mock(BlackDuckConnectivityChecker.class);
        Mockito.when(blackDuckConnectivityChecker.determineConnectivity(Mockito.any())).thenReturn(blackDuckconnectivityResult);

        final PolarisConnectivityChecker polarisConnectivityChecker = Mockito.mock(PolarisConnectivityChecker.class);
        Mockito.when(polarisConnectivityChecker.determineConnectivity(Mockito.any())).thenReturn(polarisConnectivityResult);

        return productBoot.boot(productDecision, detectConfiguration, blackDuckConnectivityChecker, polarisConnectivityChecker, productBootFactory);
    }
}
