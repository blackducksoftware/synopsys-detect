package com.synopsys.integration.detect.workflow.blackduck;

import com.synopsys.integration.blackduck.api.generated.view.BomStatusScanView;
import com.synopsys.integration.blackduck.api.generated.enumeration.BomStatusScanStatusType;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.wait.ResilientJob;

public class DetectBomScanWaitJob implements ResilientJob<BomStatusScanView> {
    private final BlackDuckApiClient blackDuckApiClient;
    private final HttpUrl scanUrl;
    private BomStatusScanView scanResponse;
    
    private boolean complete;
    private static final String JOB_NAME = "BOM Scan Wait Job ";

    public DetectBomScanWaitJob(BlackDuckApiClient blackDuckApiClient, HttpUrl scanUrl) {
        this.blackDuckApiClient = blackDuckApiClient;
        this.scanUrl = scanUrl;
        complete = false;
    }

    @Override
    public void attemptJob() throws IntegrationException {
        BomStatusScanView initialResponse = 
                blackDuckApiClient.getResponse(scanUrl, BomStatusScanView.class);
        
        if (initialResponse.getStatus() != BomStatusScanStatusType.BUILDING) {
            complete = true;
            scanResponse = initialResponse;
        }
    }

    @Override
    public boolean wasJobCompleted() {
        return complete;
    }

    @Override
    public BomStatusScanView onTimeout() throws IntegrationTimeoutException {
        throw new IntegrationTimeoutException("Error waiting for scan to be considered for including in BOM. Timeout may have occurred.");
    }

    @Override
    public BomStatusScanView onCompletion() throws IntegrationException {
        return scanResponse;
    }

    @Override
    public String getName() {
        return JOB_NAME + scanUrl;
    }

}