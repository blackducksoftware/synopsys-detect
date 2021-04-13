/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;

public class ProductDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BlackDuckDecision decideBlackDuck(BlackDuckConnectionDetails blackDuckConnectionDetails, BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, BlackduckScanMode scanMode, BdioOptions bdioOptions) {
        boolean offline = blackDuckConnectionDetails.getOffline();
        Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();
        Optional<String> signatureScannerHostUrl = blackDuckSignatureScannerOptions.getUserProvidedScannerInstallUrl();
        Optional<Path> signatureScannerOfflineLocalPath = blackDuckSignatureScannerOptions.getOfflineLocalScannerInstallPath();
        if (offline && scanMode == BlackduckScanMode.RAPID) {
            logger.debug("Black Duck will NOT run: Black Duck offline mode is set to true and Black Duck {} scan is enabled which requires online mode", BlackduckScanMode.RAPID.name());
            return BlackDuckDecision.skip();
        } else if (scanMode == BlackduckScanMode.RAPID && !bdioOptions.isBdio2Enabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (scanMode == BlackduckScanMode.INTELLIGENT && !bdioOptions.isBdio2Enabled() && !bdioOptions.isLegacyUploadEnabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (signatureScannerHostUrl.isPresent()) {
            logger.info("A Black Duck signature scanner url was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (signatureScannerOfflineLocalPath.isPresent()) {
            logger.info("A local Black Duck signature scanner path was provided, which requires Black Duck offline mode.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckUrl.isPresent()) {
            logger.debug("Black Duck will run ONLINE: A Black Duck url was found.");
            return BlackDuckDecision.runOnline(scanMode);
        } else if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.");
            return BlackDuckDecision.runOffline();
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.");
            return BlackDuckDecision.skip();
        }
    }

}
