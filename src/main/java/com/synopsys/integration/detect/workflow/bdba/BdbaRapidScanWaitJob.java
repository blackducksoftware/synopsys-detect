package com.synopsys.integration.detect.workflow.bdba;

import java.util.UUID;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.ResilientJob;

public class BdbaRapidScanWaitJob implements ResilientJob<BdbaStatusScanView>{
    
    private IntHttpClient httpClient;
    private UUID scanId;
    private BdbaStatusScanView scanStatus;
    private Gson gson;
    private String bdbaBaseUrl;
    
    private boolean complete;
    private static final String JOB_NAME = "BDBA Stateless Scan Wait Job ";
    private static final String IN_PROGRESS = "inprogress";

    public BdbaRapidScanWaitJob(IntHttpClient httpClient, UUID scanId, Gson gson, String bdbaBaseUrl) {
        this.httpClient = httpClient;
        this.scanId = scanId;
        this.gson = gson;
        this.bdbaBaseUrl = bdbaBaseUrl;
        complete = false;
    }

    @Override
    public void attemptJob() throws IntegrationException {
         RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.GET);

         HttpUriRequest request = createRequestBuilder
             .setUri(bdbaBaseUrl + "/status/" + scanId)
             .build();
         
         Response response = httpClient.execute(request);
         
         if (response.isStatusCodeSuccess()) {
             String json = response.getContentString();
             BdbaStatusScanView initialResponse = gson.fromJson(json, BdbaStatusScanView.class);

             if (!initialResponse.getStatus().toLowerCase().equals(IN_PROGRESS)) {
                 complete = true;
                 scanStatus = initialResponse;
             }
         }
     }

    @Override
    public boolean wasJobCompleted() {
        return complete;
    }

    @Override
    public BdbaStatusScanView onTimeout() throws IntegrationTimeoutException {
        Response response;
        try {
            RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.DELETE);

            HttpUriRequest request = createRequestBuilder
                .setUri(bdbaBaseUrl + "/scan/" + scanId)
                .build();
            
            response = httpClient.execute(request);
        } catch (IntegrationException e) {
            throw new IntegrationTimeoutException("Timeout waiting for BDBA scan. Attempted to terminate BDBA scan but received error: " + e.getMessage());
        }
        
        if (response.isStatusCodeSuccess()) {
            throw new IntegrationTimeoutException("Error waiting for BDBA worker to respond to scan status request. Timeout occurred.");
        } else {
            throw new IntegrationTimeoutException("Timeout waiting for BDBA scan. Attempted to terminate BDBA scan but received status code of: " + response.getStatusCode());            
        }
    }

    @Override
    public BdbaStatusScanView onCompletion() throws IntegrationException {
        return scanStatus;
    }

    @Override
    public String getName() {
        return JOB_NAME + scanId;
    }
}
