/*
 * Copyright (C) 2023 Synopsys Inc.
 * http://www.synopsys.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Synopsys ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Synopsys.
 */
package com.synopsys.integration.detect.workflow.bdba;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.exception.IntegrationTimeoutException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.exception.IntegrationRestException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.ResilientJob;

public class BinaryRapidScanWaitJob implements ResilientJob<BdbaStatusScanView>{
    
    private IntHttpClient httpClient;
    private UUID scanId;
    private BdbaStatusScanView scanStatus;
    private Gson gson;
    private String bdbaBaseUrl;
    
    private boolean complete;
    private static final String JOB_NAME = "Binary Rapid Scan Wait Job ";

    public BinaryRapidScanWaitJob(IntHttpClient httpClient, UUID scanId, Gson gson, String bdbaBaseUrl) {
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

             if (initialResponse.getStatus().equals("ready")) {
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
        throw new IntegrationTimeoutException("Error waiting for BDBA worker to response to scan status request. Timeout may have occurred.");
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
