package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanWaitJob;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.wait.ResilientJobConfig;
import com.synopsys.integration.wait.ResilientJobExecutor;

public class RapidModeWaitOperation {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 1;

    private final BlackDuckApiClient blackDuckApiClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RapidModeWaitOperation(BlackDuckApiClient blackDuckApiClient) {
        this.blackDuckApiClient = blackDuckApiClient;
    }

    public List<DeveloperScansScanView> waitForScans(List<HttpUrl> uploadedScans, long timeoutInSeconds, int waitIntervalInSeconds)
        throws IntegrationException, InterruptedException {
        ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), timeoutInSeconds, System.currentTimeMillis(), waitIntervalInSeconds);
        DetectRapidScanWaitJob waitJob = new DetectRapidScanWaitJob(blackDuckApiClient, uploadedScans);
        ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
        return jobExecutor.executeJob(waitJob);
    }
}
