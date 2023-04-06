package com.synopsys.integration.detect.workflow.blackduck.developer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanWaitJobFull;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.wait.ResilientJobConfig;
import com.synopsys.integration.wait.ResilientJobExecutor;
import com.synopsys.integration.wait.tracker.WaitIntervalTracker;
import com.synopsys.integration.wait.tracker.WaitIntervalTrackerFactory;

public class RapidModeWaitOperation {
    public static final int DEFAULT_WAIT_INTERVAL_IN_SECONDS = 1;

    private final BlackDuckApiClient blackDuckApiClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RapidModeWaitOperation(BlackDuckApiClient blackDuckApiClient) {
        this.blackDuckApiClient = blackDuckApiClient;
    }

    public List<DeveloperScansScanView> waitForFullScans(List<HttpUrl> uploadedScans, long timeoutInSeconds, int waitIntervalInSeconds, BlackduckScanMode mode, int maxWaitInSeconds)
            throws IntegrationException, InterruptedException {
            WaitIntervalTracker waitIntervalTracker = WaitIntervalTrackerFactory.createProgressive(timeoutInSeconds, maxWaitInSeconds);
            ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), System.currentTimeMillis(), waitIntervalTracker);
            DetectRapidScanWaitJobFull waitJob = new DetectRapidScanWaitJobFull(blackDuckApiClient, uploadedScans, mode);
            ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
            return jobExecutor.executeJob(waitJob);
        }
}
