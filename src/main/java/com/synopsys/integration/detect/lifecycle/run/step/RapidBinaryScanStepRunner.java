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

import org.apache.http.HttpHeaders;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContent;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class RapidBinaryScanStepRunner {
    
    private IntHttpClient httpClient;
    
    public RapidBinaryScanStepRunner(Gson gson) {
        httpClient = new IntHttpClient(
                new SilentIntLogger(),
                gson,
                Math.toIntExact(300),
                true,
                ProxyInfo.NO_PROXY_INFO
            );
    }

    public Response submitScan() throws IntegrationException {
        BodyContent content = StringBodyContent.json("{\"url\":\"file:///foo/TEW-636APB-1002-Firmware.bin\"}");
        
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        
        Request request = new Request(new HttpUrl("http://localhost:9001/scan/123"), HttpMethod.POST, null, queryParams, headers, content);
        return httpClient.execute(request);   
    }

    public void pollForResults() {
        // TODO Auto-generated method stub
        
    }
}
