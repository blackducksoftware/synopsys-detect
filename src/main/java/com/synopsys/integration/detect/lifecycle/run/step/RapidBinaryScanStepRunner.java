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
package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detect.workflow.bdba.BdbaStatusScanView;
import com.synopsys.integration.detect.workflow.bdba.BinaryRapidScanWaitJob;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContent;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.ResilientJobConfig;
import com.synopsys.integration.wait.ResilientJobExecutor;
import com.synopsys.integration.wait.tracker.WaitIntervalTracker;
import com.synopsys.integration.wait.tracker.WaitIntervalTrackerFactory;

public class RapidBinaryScanStepRunner {
    
    private IntHttpClient httpClient;
    private Gson gson;
    private UUID scanId;
    private static final int DEFAULT_TIMEOUT = 300;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public RapidBinaryScanStepRunner(Gson gson, UUID scanId) {
        this.gson = gson;
        this.scanId = scanId;
        
        httpClient = new IntHttpClient(
                new SilentIntLogger(),
                gson,
                Math.toIntExact(DEFAULT_TIMEOUT),
                true,
                ProxyInfo.NO_PROXY_INFO
            );
    }

    public Response submitScan() throws IntegrationException {
        // TODO have to be told somehow where file is, using existing property in arguments?
        BodyContent content = StringBodyContent.json("{\"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
        
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        
        Request request = new Request(new HttpUrl("http://localhost:9001/scan/" + scanId), HttpMethod.POST, null, queryParams, headers, content);
        return httpClient.execute(request);   
    }

    public BdbaStatusScanView pollForResults() throws InterruptedException, IntegrationException {
        WaitIntervalTracker waitIntervalTracker = WaitIntervalTrackerFactory.createProgressive(DEFAULT_TIMEOUT, 60);
        ResilientJobConfig waitJobConfig = new ResilientJobConfig(new Slf4jIntLogger(logger), System.currentTimeMillis(), waitIntervalTracker);
        BinaryRapidScanWaitJob waitJob = new BinaryRapidScanWaitJob(httpClient, scanId, gson);
        ResilientJobExecutor jobExecutor = new ResilientJobExecutor(waitJobConfig);
        return jobExecutor.executeJob(waitJob);
    }

    public void getBdio() {
        // TODO Auto-generated method stub
        
    }
}
