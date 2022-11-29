package com.synopsys.integration.detect.workflow.blackduck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.api.core.response.UrlMultipleResponses;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionBomStatusView;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionBomStatusType;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsSetting;
import com.synopsys.integration.detect.workflow.blackduck.developer.blackduck.DetectRapidScanRequestBuilder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.ResilientJob;

public class DetectBomWaitJob implements ResilientJob<ProjectVersionBomStatusView> {
    private final BlackDuckApiClient blackDuckApiClient;
    private final HttpUrl bomUrl;
    private ProjectVersionBomStatusView bomResponse;
    
    private boolean complete;
    private static final String JOB_NAME = "BOM Wait Job";

    public DetectBomWaitJob(BlackDuckApiClient blackDuckApiClient, HttpUrl bomUrl) {
        this.blackDuckApiClient = blackDuckApiClient;
        this.bomUrl = bomUrl;
        complete = false;
    }

    @Override
    public void attemptJob() throws IntegrationException {
        ProjectVersionBomStatusView initialResponse = 
                blackDuckApiClient.getResponse(bomUrl, ProjectVersionBomStatusView.class);
        
        if (initialResponse.getStatus() == ProjectVersionBomStatusType.UP_TO_DATE ||
                initialResponse.getStatus() == ProjectVersionBomStatusType.UP_TO_DATE_WITH_ERRORS) {
            complete = true;
            bomResponse = initialResponse;
        }
    }

    @Override
    public boolean wasJobCompleted() {
        return complete;
    }

    @Override
    public ProjectVersionBomStatusView onTimeout() throws IntegrationTimeoutException {
        throw new IntegrationTimeoutException("Error waiting for BOM to complete. Timeout may have occurred.");
    }

    @Override
    public ProjectVersionBomStatusView onCompletion() throws IntegrationException {
        return bomResponse;
    }

    @Override
    public String getName() {
        return JOB_NAME;
    }

}
