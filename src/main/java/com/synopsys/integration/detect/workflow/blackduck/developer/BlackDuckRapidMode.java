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
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.blackduck.developermode.RapidScanService;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class BlackDuckRapidMode {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 1;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private BlackDuckRunData blackDuckRunData;
    private RapidScanService rapidScanService;
    private Long timeoutInSeconds;

    public BlackDuckRapidMode(BlackDuckRunData blackDuckRunData, RapidScanService rapidScanService, Long timeoutInSeconds) {
        this.blackDuckRunData = blackDuckRunData;
        this.rapidScanService = rapidScanService;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public List<DeveloperScanComponentResultView> run(BdioResult bdioResult) throws DetectUserFriendlyException {
        logger.info("Begin Rapid Mode Scan");
        if (!blackDuckRunData.isOnline()) {
            logger.warn("Black Duck isn't online skipping rapid mode scan.");
            return Collections.emptyList();
        }

        List<DeveloperScanComponentResultView> results = new LinkedList<>();
        try {
            for (UploadTarget uploadTarget : bdioResult.getUploadTargets()) {
                results.addAll(rapidScanService.performDeveloperScan(uploadTarget.getUploadFile(), timeoutInSeconds, DEFAULT_WAIT_INTERVAL_IN_SECONDS));
            }
            logger.debug("Rapid scan result count: {}", results.size());
        } catch (IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your Black Duck configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (BlackDuckIntegrationException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
        return results;
    }
}
