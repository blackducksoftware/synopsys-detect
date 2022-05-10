package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckMultipleRequest;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.exception.IntegrationRestException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.ResilientJob;

public class DetectRapidScanWaitJob implements ResilientJob<List<DeveloperScanComponentResultView>> {
    private final BlackDuckApiClient blackDuckApiClient;
    private final List<HttpUrl> remainingUrls;

    private static final String JOB_NAME = "Waiting for Rapid Scans";

    private boolean complete;

    public DetectRapidScanWaitJob(BlackDuckApiClient blackDuckApiClient, List<HttpUrl> resultUrl) {
        this.blackDuckApiClient = blackDuckApiClient;
        this.remainingUrls = new ArrayList<>();
        remainingUrls.addAll(resultUrl);
    }

    @Override
    public void attemptJob() throws IntegrationException {
        if (remainingUrls.isEmpty()) {
            complete = true;
            return;
        }

        List<HttpUrl> completed = new ArrayList<>();
        for (HttpUrl url : remainingUrls) {
            if (isComplete(url)) {
                completed.add(url);
            }
        }

        remainingUrls.removeAll(completed);
        complete = remainingUrls.isEmpty();
    }

    private boolean isComplete(HttpUrl url) throws IntegrationException {
        BlackDuckResponseRequest request = new DetectRapidScanRequestBuilder()
            .createResponseRequest(url);
        try (Response response = blackDuckApiClient.execute(request)) {
            return response.isStatusCodeSuccess();
        } catch (IntegrationRestException ex) {
            if (HttpStatus.SC_NOT_FOUND == ex.getHttpStatusCode()) {
                return false;
            } else {
                throw ex;
            }
        } catch (IOException ex) {
            throw new BlackDuckIntegrationException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean wasJobCompleted() {
        return complete;
    }

    @Override
    public List<DeveloperScanComponentResultView> onTimeout() throws IntegrationTimeoutException {
        throw new IntegrationTimeoutException("Error getting developer scan result. Timeout may have occurred.");
    }

    @Override
    public List<DeveloperScanComponentResultView> onCompletion() throws IntegrationException {
        List<DeveloperScanComponentResultView> allComponents = new ArrayList<>();
        for (HttpUrl url : remainingUrls) {
            allComponents.addAll(getScanResultsForUrl(url));
        }
        return allComponents;
    }

    private List<DeveloperScanComponentResultView> getScanResultsForUrl(HttpUrl url) throws IntegrationException {
        BlackDuckMultipleRequest<DeveloperScanComponentResultView> request =
            new DetectRapidScanRequestBuilder()
                .createRequest(url);
        return blackDuckApiClient.getAllResponses(request);
    }

    @Override
    public String getName() {
        return JOB_NAME;
    }
}