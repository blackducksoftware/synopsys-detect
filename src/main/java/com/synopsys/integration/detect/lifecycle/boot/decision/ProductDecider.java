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
package com.synopsys.integration.detect.lifecycle.boot.decision;

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

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckRunOptions;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class ProductDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductDecision decide(DetectConfigurationFactory detectConfigurationFactory, File userHome, DetectToolFilter detectToolFilter) throws DetectUserFriendlyException {
        BlackDuckConnectionDetails blackDuckConnectionDetails = detectConfigurationFactory.createBlackDuckConnectionDetails();
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        BlackDuckRunOptions blackDuckRunOptions = detectConfigurationFactory.createBlackDuckRunOptions();
        RunOptions runOptions = detectConfigurationFactory.createRunOptions();
        return new ProductDecision(determineBlackDuck(blackDuckConnectionDetails, blackDuckSignatureScannerOptions, blackDuckRunOptions, runOptions),
            determinePolaris(detectConfigurationFactory, userHome, detectToolFilter, blackDuckRunOptions));
    }

    public PolarisDecision determinePolaris(DetectConfigurationFactory detectConfigurationFactory, File userHome, DetectToolFilter detectToolFilter, BlackDuckRunOptions blackDuckRunOptions) {
        if (!detectToolFilter.shouldInclude(DetectTool.POLARIS)) {
            logger.debug("Polaris will NOT run because it is excluded.");
            return PolarisDecision.skip();
        }

        if (blackDuckRunOptions.shouldPerformRapidModeScan()) {
            logger.debug("Polaris will NOT run because BlackDuck {} scan configured.", BlackduckScanMode.RAPID_MODE.name());
            return PolarisDecision.skip();
        }

        PolarisServerConfigBuilder polarisServerConfigBuilder = detectConfigurationFactory.createPolarisServerConfigBuilder(userHome);
        BuilderStatus builderStatus = polarisServerConfigBuilder.validateAndGetBuilderStatus();
        if (!builderStatus.isValid()) {
            String polarisUrl = polarisServerConfigBuilder.getUrl();
            if (StringUtils.isBlank(polarisUrl)) {
                logger.debug("Polaris will NOT run: The Polaris url must be provided.");
            } else {
                logger.debug("Polaris will NOT run: " + builderStatus.getFullErrorMessage());
            }
            return PolarisDecision.skip();
        } else {
            logger.debug("Polaris will run: An access token and url were found.");
            return PolarisDecision.runOnline(polarisServerConfigBuilder.build());
        }
    }

    public BlackDuckDecision determineBlackDuck(BlackDuckConnectionDetails blackDuckConnectionDetails, BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, BlackDuckRunOptions blackDuckRunOptions, RunOptions runOptions) {
        boolean offline = blackDuckConnectionDetails.getOffline();
        Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();
        Optional<String> signatureScannerHostUrl = blackDuckSignatureScannerOptions.getUserProvidedScannerInstallUrl();
        Optional<Path> signatureScannerOfflineLocalPath = blackDuckSignatureScannerOptions.getOfflineLocalScannerInstallPath();
        if (offline && blackDuckRunOptions.shouldPerformRapidModeScan()) {
            logger.debug("Black Duck will NOT run: Black Duck offline mode is set to true and Black Duck {} scan is enabled which requires online mode", BlackduckScanMode.RAPID_MODE.name());
            return BlackDuckDecision.skip();
        } else if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckRunOptions.shouldPerformRapidModeScan() && !runOptions.shouldUseBdio2()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", BlackduckScanMode.RAPID_MODE.name());
            return BlackDuckDecision.skip();
        } else if (signatureScannerHostUrl.isPresent()) {
            logger.info("A Black Duck signature scanner url was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (signatureScannerOfflineLocalPath.isPresent()) {
            logger.info("A local Black Duck signature scanner path was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckUrl.isPresent()) {
            logger.debug("Black Duck will run: A Black Duck url was found.");
            return BlackDuckDecision.runOnline();
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.");
            return BlackDuckDecision.skip();
        }
    }

}
