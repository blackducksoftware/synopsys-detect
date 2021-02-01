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

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.DetectWorkflow;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckRunOptions;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

class ProductDeciderTest {
    private String VALID_URL = "http://example";

    @Test
    public void shouldRunPolarisWhenConfigValid() {
        File userHome = Mockito.mock(File.class);
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        DetectToolFilter detectToolFilter = mockToolFilterForPolaris(true);
        DetectConfigurationFactory detectConfigurationFactory = mockDetectConfigurationFactoryForPolaris(true);

        PolarisDecision polarisDecision = new ProductDecider().determinePolaris(detectConfigurationFactory, userHome, detectToolFilter, blackDuckRunOptions);
        Assertions.assertTrue(polarisDecision.shouldRun());
    }

    @Test
    public void shouldNotRunPolarisWhenConfigInvalid() {
        File userHome = Mockito.mock(File.class);
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        DetectToolFilter detectToolFilter = mockToolFilterForPolaris(true);
        DetectConfigurationFactory detectConfigurationFactory = mockDetectConfigurationFactoryForPolaris(false);

        PolarisDecision polarisDecision = new ProductDecider().determinePolaris(detectConfigurationFactory, userHome, detectToolFilter, blackDuckRunOptions);
        Assertions.assertFalse(polarisDecision.shouldRun());
    }

    @Test
    public void shouldNotRunPolarisWhenExcluded() {
        File userHome = Mockito.mock(File.class);
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        DetectToolFilter detectToolFilter = mockToolFilterForPolaris(false);
        DetectConfigurationFactory detectConfigurationFactory = mockDetectConfigurationFactoryForPolaris(true);

        PolarisDecision polarisDecision = new ProductDecider().determinePolaris(detectConfigurationFactory, userHome, detectToolFilter, blackDuckRunOptions);
        Assertions.assertFalse(polarisDecision.shouldRun());
    }

    @Test
    public void shouldRunBlackDuckOfflineWhenOverride() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(null, null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOfflineWhenInstallUrl() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(null, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOfflineWhenInstallPath() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(Mockito.mock(Path.class), null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOnline() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.PERSISTENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(null, null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertFalse(productDecision.isOffline());
    }

    @Test
    public void shouldNotRunPolarisBlackDuckRapidMode() {
        File userHome = Mockito.mock(File.class);
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.TRANSIENT_SCAN);
        DetectToolFilter detectToolFilter = mockToolFilterForPolaris(true);
        DetectConfigurationFactory detectConfigurationFactory = mockDetectConfigurationFactoryForPolaris(true);

        PolarisDecision polarisDecision = new ProductDecider().determinePolaris(detectConfigurationFactory, userHome, detectToolFilter, blackDuckRunOptions);
        Assertions.assertFalse(polarisDecision.shouldRun());
    }

    @Test
    public void shouldNotRunBlackduckRapidModeAndOffline() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.TRANSIENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(Mockito.mock(Path.class), null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertFalse(productDecision.shouldRun());
    }

    @Test
    public void shouldNotRunBlackduckRapidModeAndBDIO2Disabled() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.TRANSIENT_SCAN);
        RunOptions runOptions = createTestRunOptions(false);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(Mockito.mock(Path.class), null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertFalse(productDecision.shouldRun());
    }

    @Test
    public void shouldRunBlackduckRapidModeAndBDIO2Enabled() {
        BlackDuckRunOptions blackDuckRunOptions = new BlackDuckRunOptions(DetectWorkflow.TRANSIENT_SCAN);
        RunOptions runOptions = createTestRunOptions(true);
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, null);
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = blackDuckSignatureScannerOptions(Mockito.mock(Path.class), null);
        BlackDuckDecision productDecision = new ProductDecider().determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions);

        Assertions.assertTrue(productDecision.shouldRun());
    }

    private RunOptions createTestRunOptions(boolean useBdio2) {
        return new RunOptions(false, null, null, null, null, useBdio2);
    }

    private DetectToolFilter mockToolFilterForPolaris(boolean includesPolaris) {
        DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.POLARIS)).thenReturn(includesPolaris);
        return detectToolFilter;
    }

    private BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions(Path offlineScannerInstallPath, String userProvidedScannerInstallUrl) {
        return new BlackDuckSignatureScannerOptions(Bds.listOf(), Bds.listOf(), Bds.listOf(), offlineScannerInstallPath, null, userProvidedScannerInstallUrl, 1024, 1, false, null, false, null, null, null, 1, null, false, false);
    }

    private BlackDuckConnectionDetails blackDuckConnectionDetails(boolean offline, String blackduckUrl) {
        return new BlackDuckConnectionDetails(offline, blackduckUrl, null, null, null);
    }

    private DetectConfigurationFactory mockDetectConfigurationFactoryForPolaris(boolean returnsValid) {
        PolarisServerConfigBuilder polarisServerConfigBuilder = new MockPolarisServerConfigBuilder(returnsValid);
        DetectConfigurationFactory detectConfigurationFactory = Mockito.mock(DetectConfigurationFactory.class);
        Mockito.when(detectConfigurationFactory.createPolarisServerConfigBuilder(Mockito.any())).thenReturn(
            polarisServerConfigBuilder
        );
        return detectConfigurationFactory;
    }

    static class MockPolarisServerConfigBuilder extends PolarisServerConfigBuilder {
        private final boolean isValid;

        MockPolarisServerConfigBuilder(boolean isValid) {
            this.isValid = isValid;
        }

        @Override
        protected void validate(BuilderStatus builderStatus) {
            if (!isValid) {
                builderStatus.addErrorMessage("Invalid polaris config!");
            }
        }
    }
}
