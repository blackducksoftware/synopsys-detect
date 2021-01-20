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
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootResult;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.OnlineBlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ShutdownDecider {
    public ShutdownDecision decideShutdown(DetectBootResult detectBootResult) {
        Optional<ProductRunData> productRunData = detectBootResult.getProductRunData();
        DiagnosticSystem diagnosticSystem = detectBootResult.getDiagnosticSystem().orElse(null);

        PhoneHomeManager phoneHomeManager = null;
        if (productRunData.isPresent() && productRunData.get().shouldUseBlackDuckProduct()) {
            BlackDuckRunData blackDuckRunData = productRunData.get().getBlackDuckRunData();
            if(blackDuckRunData.isOnline()) {
                phoneHomeManager = ((OnlineBlackDuckRunData) blackDuckRunData).getPhoneHomeManager().orElse(null);
            }
        }

        CleanupDecision cleanupDecision = decideCleanup(detectBootResult.getDetectConfiguration().orElse(null), detectBootResult.getProductRunData().orElse(null), detectBootResult.getAirGapZip().orElse(null));
        return new ShutdownDecision(phoneHomeManager, diagnosticSystem, cleanupDecision);
    }

    public CleanupDecision decideCleanup(@Nullable PropertyConfiguration detectConfiguration, @Nullable ProductRunData productRunData, @Nullable File airGapZip) {
        if (detectConfiguration == null || !detectConfiguration.getValue(DetectProperties.DETECT_CLEANUP.getProperty())) {
            return CleanupDecision.skip();
        }
        boolean dryRun = detectConfiguration.getValue(DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN.getProperty());

        boolean offline = false;
        if (productRunData != null && productRunData.shouldUseBlackDuckProduct()) {
            offline = !productRunData.getBlackDuckRunData().isOnline();
        }

        boolean preserveScan = dryRun || offline;
        boolean preserveBdio = offline;
        boolean preserveAirGap = airGapZip != null;
        return new CleanupDecision(true, preserveScan, preserveBdio, preserveAirGap, airGapZip);
    }
}
