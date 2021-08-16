/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanWaitJobCompleter;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanWaitJobCondition;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.wait.WaitJob;
import com.synopsys.integration.wait.WaitJobConfig;

public class RapidModeWaitOperation {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 1;

    private final BlackDuckApiClient blackDuckApiClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RapidModeWaitOperation(BlackDuckApiClient blackDuckApiClient) {
        this.blackDuckApiClient = blackDuckApiClient;
    }

    public List<DeveloperScanComponentResultView> waitForScans(List<HttpUrl> uploadedScans, long timeoutInSeconds, int waitIntervalInSeconds) throws IntegrationException, InterruptedException {
        WaitJobConfig waitJobConfig = new WaitJobConfig(new Slf4jIntLogger(logger), "Waiting for Rapid Scans", timeoutInSeconds, System.currentTimeMillis(), waitIntervalInSeconds);
        DetectRapidScanWaitJobCondition waitJobCondition = new DetectRapidScanWaitJobCondition(blackDuckApiClient, uploadedScans);
        DetectRapidScanWaitJobCompleter waitJobCompleter = new DetectRapidScanWaitJobCompleter(blackDuckApiClient, uploadedScans);

        WaitJob<List<DeveloperScanComponentResultView>> waitJob = new WaitJob<>(waitJobConfig, waitJobCondition, waitJobCompleter);

        try {
            return waitJob.waitFor();
        } catch (IntegrationTimeoutException e) {
            throw new BlackDuckIntegrationException(e.getMessage());
        }
    }
}
